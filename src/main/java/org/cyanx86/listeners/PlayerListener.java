package org.cyanx86.listeners;

import org.bukkit.Material;
import org.bukkit.block.*;
import org.bukkit.block.data.type.Door;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.*;

import org.cyanx86.OverCrafted;
import org.cyanx86.classes.GameRound;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.cyanx86.config.GeneralSettings;
import org.cyanx86.config.Locale;
import org.cyanx86.utils.Functions;
import org.jetbrains.annotations.NotNull;

public class PlayerListener implements Listener {

    // -- [[ ATTRIBUTES ]] --

    // -- PUBLIC --

    // -- PRIVATE --
    private final OverCrafted master = OverCrafted.getInstance();
    private final Locale locale = GeneralSettings.getInstance().getLocale();

    // -- [[ METHODS ]] --

    // -- PUBLIC --
    @EventHandler
    public void onPlayerMoves(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        GameRound round = master.getGameRoundManager().getGameRound();

        if (this.isNotRoundPlayerRequisites(player))
            return;

        if (!round.getKitchenArea().isPointInsideBoundaries(player.getLocation()))
            round.spawnPlayer(player, true);
        else if (!round.isPlayerAbleToMove(player))
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
            master.getGameRoundManager().getGameRound().getKitchenArea().isPointInsideBoundaries(block.getLocation())
        ) {
            block.getWorld().dropItem(
                block.getLocation().add(0, 1, 0),
                new ItemStack(materialMap.get(block.getType()))
            );
        }

        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerBreakFrameOrPainting(HangingBreakByEntityEvent event) {
        Entity entity = event.getEntity();

        if (!(
            master.getGameRoundManager().getGameRound() != null &&
            (entity instanceof ItemFrame || entity instanceof Painting) &&
            Functions.entityBelongsKitchenArea(entity)
        ))
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
        Entity entity = event.getEntity();

        if (!(
            master.getGameRoundManager().getGameRound() != null &&
            (entity instanceof ItemFrame || entity instanceof Painting) &&
            Functions.entityBelongsKitchenArea(entity)
        ))
            return;

        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerInteracts(PlayerInteractEvent event) {
        if (this.isNotRoundPlayerRequisites(event.getPlayer()))
            return;

        this.onPlayerInteractsWithChest(event);
        this.onPlayerInsertItemInFurnace(event);
        this.onPlayerOpensDoor(event);
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

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        if (this.isNotRoundPlayerRequisites(event.getPlayer()))
            return;

        if (!(event.getItemDrop().getItemStack().getType().equals(Material.STONE_PICKAXE)))
            return;

        event.setCancelled(true);
    }
  
    @EventHandler
    public void onPlayerPickUpItem(EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player player) || this.isNotRoundPlayerRequisites(player))
            return;

        HashMap<Material, Integer> contents = getPlayerInventoryMap(player);
        ItemStack drop = event.getItem().getItemStack();

        if ((!contents.containsKey(drop.getType()) && contents.size() + 1 < 6) || (contents.containsKey(drop.getType()) && contents.get(drop.getType()) < 16))
            return;
        else {
            master.getGameRoundManager().getGameRound().sendActionBarMessageToPlayer(
                player,
                locale.getStr("player-listener.full-inventory"),
                1
            );
            event.setCancelled(true);
        }
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

    private void onPlayerOpensDoor(PlayerInteractEvent event) {
        Block block = event.getClickedBlock();

        if (
            block == null ||
            !((block instanceof Door) && event.getAction().equals(Action.RIGHT_CLICK_BLOCK))
        )
            return;

        event.setCancelled(true);
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
        if (dropping == null || dropping.equals(Material.AIR))
            return;

        chest.getWorld().dropItem(
            chest.getLocation().add(0, 1, 0),
            new ItemStack(dropping)
        );
        /*
        HashMap<Material, Integer> contents = getPlayerInventoryMap(event.getPlayer());

        if ((!contents.containsKey(drop.getType()) && contents.size() + 1 < 6) || (contents.containsKey(drop.getType()) && contents.get(drop.getType()) < 16))
            event.getPlayer().getInventory().addItem(drop);
         */
    }

    private void onPlayerDeliverRecipe(PlayerInteractEvent event, @NotNull Block chest) {
        boolean isGlowItemFrame = false;
        for (Entity entityItem : chest.getWorld().getNearbyEntities(chest.getLocation(), 2, 2, 2))
            if (
                entityItem instanceof GlowItemFrame &&
                entityItem.getLocation().getBlock()
                        .getRelative(((GlowItemFrame)entityItem).getAttachedFace()).equals(chest)
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

        boolean insertedItem = false;
        if (item.getType().isFuel()) {
            insertedItem = this.playerRefuelFurnace(
                furnace,
                item,
                event.getPlayer()
            );
        }
        else if (Functions.isSmeltable(item.getType(), furnace))
            insertedItem = this.playerInsertSmeltableToFurnace(
                furnace,
                item,
                event.getPlayer()
            );
        else {
            master.getGameRoundManager().getGameRound().sendActionBarMessageToPlayer(
                event.getPlayer(),
                locale.getStr("player-listener.not-smeltable"),
                1
            );
            return;
        }

        if (!insertedItem)
            return;

        if (item.getAmount() == 1)
            event.getPlayer().getInventory().setItem(EquipmentSlot.HAND, new ItemStack(Material.AIR));
        else
            item.setAmount(item.getAmount() - 1);
    }

    private boolean playerRefuelFurnace(Furnace furnace, ItemStack item, Player player) {
        GameRound round = master.getGameRoundManager().getGameRound();

        if (furnace.getBurnTime() > 0) {
            round.sendActionBarMessageToPlayer(
                player,
                locale.getStr("player-listener.furnace-burning"),
                2
            );
            return false;
        }
        else if (furnace.getInventory().getFuel() != null) {
            round.sendActionBarMessageToPlayer(
                player,
                locale.getStr("player-listener.furnace-fueled"),
                2
            );
            return false;
        }

        furnace.getInventory().setFuel(new ItemStack(Material.COAL, 1));
        round.sendActionBarMessageToPlayer(
            player,
            locale.getStr("player-listener.furnace-refueled"),
            2
        );
        return true;
    }

    private boolean playerInsertSmeltableToFurnace(Furnace furnace, ItemStack item, Player player) {
        GameRound round = master.getGameRoundManager().getGameRound();

        if (furnace.getInventory().getSmelting() != null) {
            round.sendActionBarMessageToPlayer(
                player,
                locale.getStr("player-listener.furnace-smelting-inside"),
                2
            );
            return false;
        }

        furnace.getInventory().setSmelting(new ItemStack(item.getType(), 1));
        round.sendActionBarMessageToPlayer(
            player,
            locale.getStr("player-listener.furnace-smeltable-inserted")
                    .replace("%item%", locale.getMatName(item.getType())),
            2
        );
        return true;
    }

    // -- Other utils
    private HashMap<Material, Integer> getPlayerInventoryMap(Player player) {
        HashMap<Material, Integer> contents = new HashMap<>();

        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null){
                if (!contents.containsKey(item.getType()))
                    contents.put(item.getType(), item.getAmount());
                else
                    contents.put(item.getType(), contents.get(item.getType()) + item.getAmount());
            }
        }
        return contents;
    }

}
