package org.cyanx86.listeners;

import org.bukkit.Material;
import org.bukkit.block.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.*;

import org.cyanx86.OverCrafted;
import org.cyanx86.classes.GameRound;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;

import org.cyanx86.utils.Functions;
import org.jetbrains.annotations.NotNull;

public class PlayerListener implements Listener {

    // -- [[ ATTRIBUTES ]] --

    // -- PUBLIC --

    // -- PRIVATE --
    private final OverCrafted master = OverCrafted.getInstance();

    // -- [[ METHODS ]] --

    // -- PUBLIC --
    @EventHandler
    public void onPlayerMoves(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        GameRound round = master.getGameRoundManager().getGameRound();

        if (this.isNotRoundPlayerRequisites(player))
            return;

        if (!round.getGameArea().isPointInsideBoundaries(player.getLocation()))
            round.spawnPlayer(player, true);

        if (!round.isPlayerAbleToMove(player))
            event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerPlacesBlock(BlockPlaceEvent event) {
        if (this.isNotRoundPlayerRequisites(event.getPlayer()))
            return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerBreaksBlock(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (this.isNotRoundPlayerRequisites(player))
            return;

        Block block = event.getBlock();
        Map<Material, Material> materialMap = master.getOreBlocks().getOreMap();
        if (
            materialMap.containsKey(block.getType()) &&
            master.getGameRoundManager().getGameRound().getGameArea().isPointInsideBoundaries(block.getLocation())
        ) {
            ItemStack deliver = new ItemStack(
                materialMap.get(block.getType())
            );
            player.getInventory().addItem(deliver);
        }

        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerDamageFrameOrPainting(HangingBreakByEntityEvent event) {
        if (
            event.getRemover() instanceof Player player &&
            this.isNotRoundPlayerRequisites(player)
        )
            return;

        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerInteractItemFrame(PlayerInteractEntityEvent event) {
        if (
            this.isNotRoundPlayerRequisites(event.getPlayer()) ||
            !(event.getRightClicked() instanceof ItemFrame)
        )
            return;

        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerRemoveItemFrameContent(EntityDamageByEntityEvent event) {
        if (
            event.getDamager() instanceof Player player &&
            this.isNotRoundPlayerRequisites(player)
        )
            return;

        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerInteracts(PlayerInteractEvent event) {
        if (this.isNotRoundPlayerRequisites(event.getPlayer()))
            return;

        this.onPlayerInteractsWithChest(event);
        this.onPlayerInsertItemInFurnace(event);
    }

    @EventHandler
    public void onPlayerGetsDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player) || this.isNotRoundPlayerRequisites(player))
            return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerFoodLevelChange(FoodLevelChangeEvent event) {
        if (!(event.getEntity() instanceof Player player) || this.isNotRoundPlayerRequisites(player))
            return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerInventoryPickUpEvent(InventoryPickupItemEvent event) {
        Inventory inventory = event.getInventory();
        if(!(inventory.getHolder() instanceof Player player) || this.isNotRoundPlayerRequisites(player))
            return;

        ItemStack picked = event.getItem().getItemStack();
        if(Arrays.stream(inventory.getContents()).noneMatch(
            item -> item.getType().equals(picked.getType())
        ))
            return;

        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerInventoryMoveItem(InventoryMoveItemEvent event) {
        Inventory inventory = event.getDestination();
        if (!(inventory.getHolder() instanceof  Player player) || this.isNotRoundPlayerRequisites(player))
            return;

        ItemStack moved = event.getItem();
        if(Arrays.stream(inventory.getContents()).noneMatch(
                item -> item.getType().equals(moved.getType())
        ))
            return;

        event.setCancelled(true);
    }

    // -- PRIVATE --
    private boolean isNotRoundPlayerRequisites(Player player){
        GameRound round = master.getGameRoundManager().getGameRound();
        return (
            round == null ||
            round.getCurrentRoundState() == GameRound.ROUNDSTATE.ENDED ||
            !round.isPlayerInGame(player)
        );
    }

    // -- Chest interaction modifications
    private void onPlayerInteractsWithChest(PlayerInteractEvent event) {
        Block block = event.getClickedBlock();
        if (
            block == null ||
            !(
                block.getState() instanceof Chest &&
                event.getAction().equals(Action.RIGHT_CLICK_BLOCK)
            )
        )
            return;

        event.setCancelled(true);

        this.onPlayerDispenserIngredient(event, block);
        this.onPlayerDeliverRecipe(event, block);
    }

    private void onPlayerDispenserIngredient(PlayerInteractEvent event, @NotNull Block chest) {
        Material dropping = null;
        for (Entity entity : chest.getWorld().getNearbyEntities(chest.getLocation(), 2, 2, 2))
            if (
                entity instanceof ItemFrame itemframe &&
                entity.getLocation().getBlock().getRelative(((ItemFrame)entity).getAttachedFace()).equals(chest)
            ) {
                dropping = itemframe.getItem().getType();
                break;
            }
        if (dropping == null)
            return;

        ItemStack drop = new ItemStack(dropping);

        event.getPlayer().getInventory().addItem(drop);
    }

    private void onPlayerDeliverRecipe(PlayerInteractEvent event, @NotNull Block chest) {
        boolean isGlowItemFrame = false;
        for (Entity entity : chest.getWorld().getNearbyEntities(chest.getLocation(), 2, 2, 2))
            if (
                entity instanceof GlowItemFrame &&
                entity.getLocation().getBlock().getRelative(((GlowItemFrame)entity).getAttachedFace()).equals(chest)
            ) {
                isGlowItemFrame = true;
                break;
            }
        if (!isGlowItemFrame)
            return;

        Player player = event.getPlayer();

        ItemStack item = player.getInventory().getItem(EquipmentSlot.HAND);
        if (item == null)
            return;

        if (master.getGameRoundManager().getGameRound().dispatchOrder(item.getType())) {
            if (item.getAmount() == 1)
                player.getInventory().setItem(EquipmentSlot.HAND, new ItemStack(Material.AIR));
            else
                item.setAmount(item.getAmount() - 1);
        }
    }

    // -- Furnace interaction modifications
    private void onPlayerInsertItemInFurnace(PlayerInteractEvent event) {
        Block block = event.getClickedBlock();
        if (
            block == null ||
            !(
                (block.getState() instanceof Furnace furnace) &&
                event.getAction().equals(Action.RIGHT_CLICK_BLOCK) &&
                Objects.equals(event.getHand(), EquipmentSlot.HAND)
            )
        )
            return;

        event.setCancelled(true);

        ItemStack item = event.getItem();
        if (item == null)
            return;

        if (
            item.getType().isFuel() &&
            furnace.getBurnTime() == 0 &&
            furnace.getInventory().getFuel() == null
        )
            furnace.getInventory().setFuel(new ItemStack(Material.COAL, 1));
        else if (
            Functions.isSmeltable(item.getType(), furnace) &&
            furnace.getInventory().getSmelting() == null
        )
            furnace.getInventory().setSmelting(new ItemStack(item.getType(), 1));
        else
            return;

        if (item.getAmount() == 1)
            event.getPlayer().getInventory().setItem(EquipmentSlot.HAND, new ItemStack(Material.AIR));
        else
            item.setAmount(item.getAmount() - 1);
    }

}
