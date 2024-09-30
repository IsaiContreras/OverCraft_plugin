package org.cyanx86.listeners;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.GlowItemFrame;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import org.cyanx86.OverCrafted;
import org.cyanx86.classes.GameRound;

import java.util.Map;
import java.util.Objects;

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

        // NA si la ronda no ha comenzado o si el jugador no está en el juego o si la ronda ha terminado.
        if (this.isNotRoundPlayerRequisites(round, player))
            return;

        // Si sale del GameArea regresar jugador a su SpawnPoint
        if (!round.getGameArea().isPointInsideBoundaries(player.getLocation()))
            round.spawnPlayer(player, true);
    }

    @EventHandler
    public void onPlayerPlacesBlock(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        GameRound round = master.getGameRoundManager().getGameRound();

        // NA si la ronda no ha comenzado, si la ronda ha terminado o si el jugador no está jugando.
        if (this.isNotRoundPlayerRequisites(round, player))
            return;

        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerBreaksBlock(BlockBreakEvent event) {
        Player player = event.getPlayer();
        GameRound round = master.getGameRoundManager().getGameRound();

        // NA si la ronda no ha comenzado, si la ronda ha terminado o si el jugador no está jugando.
        if (this.isNotRoundPlayerRequisites(round, player))
            return;

        Block block = event.getBlock();
        Map<Material, Material> materialMap = master.getOreBlocks().getOreMap();

        if (
            materialMap.containsKey(block.getType()) &&
            round.getGameArea().isPointInsideBoundaries(block.getLocation())
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
        Entity remover = event.getRemover();
        GameRound round = master.getGameRoundManager().getGameRound();

        // NA si la ronda no ha comenzado, si la ronda ha terminado o si el jugador no está jugando.
        if (
            remover instanceof Player player &&
            this.isNotRoundPlayerRequisites(round, player)
        )
            return;

        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerInteractItemFrame(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        GameRound round = master.getGameRoundManager().getGameRound();

        if (
            this.isNotRoundPlayerRequisites(round, player) ||
            !(event.getRightClicked() instanceof ItemFrame)
        )
            return;

        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerRemoveItemFrameContent(EntityDamageByEntityEvent event) {
        Entity damager = event.getDamager();
        GameRound round = master.getGameRoundManager().getGameRound();

        // NA si la ronda no ha comenzado, si la ronda ha terminado o si el jugador no está jugando.
        if (
            damager instanceof Player player &&
            this.isNotRoundPlayerRequisites(round, player)
        )
            return;

        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerInteractsWithChest(PlayerInteractEvent event) {
        GameRound round = master.getGameRoundManager().getGameRound();
        Block block = event.getClickedBlock();

        // NA si la ronda no ha comenzado, si la ronda ha terminado o si el jugador no está jugando.
        if (
            this.isNotRoundPlayerRequisites(round, event.getPlayer()) ||
            block == null ||
            !(
                block.getType().equals(Material.CHEST) &&
                event.getAction().equals(Action.RIGHT_CLICK_BLOCK)
            )
        )
            return;

        event.setCancelled(true);

        this.onPlayerDispenserIngredient(event);
        this.onPlayerDeliverRecipe(event);
    }

    @EventHandler
    public void onPlayerGetsDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player))
            return;
        GameRound round = master.getGameRoundManager().getGameRound();

        // NA si la ronda no ha comenzado, si la ronda ha terminado o si el jugador no está jugando.
        if (this.isNotRoundPlayerRequisites(round, player))
            return;

        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerFoodLevelChange(FoodLevelChangeEvent event) {
        if (!(event.getEntity() instanceof Player player))
            return;
        GameRound round = master.getGameRoundManager().getGameRound();

        if (this.isNotRoundPlayerRequisites(round, player))
            return;

        event.setCancelled(true);
    }

    // -- PRIVATE --
    private boolean isNotRoundPlayerRequisites(GameRound round, Player player) {
        return (
            round == null ||
            round.getCurrentRoundState() == GameRound.ROUNDSTATE.ENDED ||
            !round.isPlayerInGame(player)
        );
    }

    private void onPlayerDispenserIngredient(PlayerInteractEvent event) {
        Block block = event.getClickedBlock();
        if (block == null)
            return;

        Material dropping = null;
        for (Entity entity : block.getWorld().getNearbyEntities(block.getLocation(), 2, 2, 2))
            if (
                entity instanceof ItemFrame itemframe &&
                entity.getLocation().getBlock().getRelative(((ItemFrame)entity).getAttachedFace()).equals(block)
            ) {
                dropping = itemframe.getItem().getType();
                break;
            }
        if (dropping == null)
            return;
        Player player = event.getPlayer();
        ItemStack drop = new ItemStack(dropping);

        player.getInventory().addItem(drop);
    }

    private void onPlayerDeliverRecipe(PlayerInteractEvent event) {
        Block block = event.getClickedBlock();
        if (block == null)
            return;

        boolean isGlowItemFrame = false;
        for (Entity entity : block.getWorld().getNearbyEntities(block.getLocation(), 2, 2, 2))
            if (
                entity instanceof GlowItemFrame &&
                entity.getLocation().getBlock().getRelative(((GlowItemFrame)entity).getAttachedFace()).equals(block)
            ) {
                isGlowItemFrame = true;
                break;
            }
        if (!isGlowItemFrame)
            return;

        Player player = event.getPlayer();
        GameRound round = master.getGameRoundManager().getGameRound();
        ItemStack item = player.getInventory().getItem(EquipmentSlot.HAND);
        if (item == null)
            return;

        if (round.dispatchOrder(item.getType())) {
            if (Objects.requireNonNull(player.getInventory().getItem(EquipmentSlot.HAND)).getAmount() == 1)
                player.getInventory().setItem(EquipmentSlot.HAND, new ItemStack(Material.AIR));
            else
                Objects.requireNonNull(player.getInventory().getItem(EquipmentSlot.HAND)).setAmount(
                    Objects.requireNonNull(player.getInventory().getItem(EquipmentSlot.HAND)).getAmount() - 1
                );
        }
    }

    /* -- FURNACE INTERACTION MODIFICATION --
    public void onPlayerInteractsWithFurnace(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        GameRound round = master.getGameRoundManager().getGameRound();

        // NA si la ronda no ha comenzado, si la ronda ha terminado o si el jugador no está jugando.
        if (round == null || round.getCurrentRoundState() == GameRound.ROUNDSTATE.ENDED || !round.isPlayerInGame(player))
            return;

        Block block = event.getClickedBlock();
        if (block == null)
            return;

        if (!(
            block.getType().equals(Material.FURNACE) &&
            event.getAction().equals(Action.RIGHT_CLICK_BLOCK)
        ))
            return;

        event.setCancelled(true);
    } */

}
