package org.cyanx86.eventhandlers;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.cyanx86.OverCrafted;
import org.cyanx86.classes.GameArea;
import org.cyanx86.classes.GameAreaPropertiesAssistant;
import org.cyanx86.classes.GameRound;
import org.cyanx86.classes.IngredientDispenser;
import org.cyanx86.utils.Enums;
import org.cyanx86.utils.Functions;
import org.cyanx86.utils.Messenger;

import java.util.Map;
import java.util.Objects;

public class PlayerEventsHandler {

    // -- [[ ATTRIBUTES ]] --

    // -- Public

    // -- Private
    private final OverCrafted master = OverCrafted.getInstance();

    // -- [[ METHODS ]] --

    // -- Public
    // ** MANAGER **
    public void onOverCraftedManagerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if(!player.hasPermission("overcrafted.manager"))
            return;

        // Register GameAreaCornerAssistant
        master.getGapaManager().signInAssistant(player);

        Messenger.msgToConsole(OverCrafted.prefix + player.getName() + " registro su asistente.");
    }

    public void onOverCraftedManagerDisconnects(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        if (master.getGapaManager().eraseAssistant(player) == Enums.ListResult.NOT_FOUND)
            return;

        Messenger.msgToConsole(OverCrafted.prefix + player.getName() + " dispuso su asistente.");
    }

    public void onOverCraftedManagerClicksBlockWithItem(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (!player.hasPermission("overcrafted.manager"))
            return;

        this.onOverCraftedManagerCreatesGameAreaCorner(event);
        this.onOverCraftedManagerCreatesIngredientDispenser(event);
    }

    public void onOverCraftedPlayerDisconnects(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        GameRound round = master.getGameRoundManager().getGameRound();

        // NA si la ronda no ha comenzado.
        if (round == null)
            return;

        if (!round.isPlayerInGame(player))
            return;

        round.removePlayer(player);
    }

    // ** PLAYERS **
    public void onOverCraftedPlayerMoves(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        GameRound round = master.getGameRoundManager().getGameRound();

        // NA si la ronda no ha comenzado o si el jugador no está en el juego o si la ronda ha terminado.
        if (round == null || !round.isPlayerInGame(player) || round.getCurrentRoundState() == GameRound.ROUNDSTATE.ENDED)
            return;

        // No permite moverse si la Ronda no ha empezado
        if (round.getCurrentRoundState() != GameRound.ROUNDSTATE.RUNNING) {
            event.setCancelled(true);
            return;
        }

        /* No permite moverse si el estado del jugador está en INMOBILIZADO
        if (round.getStateOfPlayer(player) != null && round.getStateOfPlayer(player) != PLAYERSTATE.RUNNING) {
            event.setCancelled(true);
            return;
        } */

        // Si sale del GameArea regresar jugador a su SpawnPoint
        if (!round.getGameArea().isPointInsideBoundaries(player.getLocation()))
            round.spawnPlayer(player, true);
    }

    public void onOverCraftedPlayerPlacesBlock(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        GameRound round = master.getGameRoundManager().getGameRound();

        // NA si la ronda no ha comenzado, si la ronda ha terminado o si el jugador no está jugando.
        if (round == null || round.getCurrentRoundState() == GameRound.ROUNDSTATE.ENDED || !round.isPlayerInGame(player))
            return;

        event.setCancelled(true);
    }

    public void onOverCraftedPlayerBreaksBlock(BlockBreakEvent event) {
        Player player = event.getPlayer();
        GameRound round = master.getGameRoundManager().getGameRound();

        // NA si la ronda no ha comenzado, si la ronda ha terminado o si el jugador no está jugando.
        if (round == null || round.getCurrentRoundState() == GameRound.ROUNDSTATE.ENDED || !round.isPlayerInGame(player))
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

    public void onOverCraftedPlayerDamageFrameOrPainting(HangingBreakByEntityEvent event) {
        Entity remover = event.getRemover();
        GameRound round = master.getGameRoundManager().getGameRound();

        // NA si la ronda no ha comenzado, si la ronda ha terminado o si el jugador no está jugando.
        if (
                remover instanceof Player &&
                        (
                                round == null ||
                                        round.getCurrentRoundState() == GameRound.ROUNDSTATE.ENDED ||
                                        !round.isPlayerInGame((Player)remover)
                        )
        )
            return;

        event.setCancelled(true);
    }

    public void onOverCraftedPlayerInteractItemFrame(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        GameRound round = master.getGameRoundManager().getGameRound();

        if (round == null || round.getCurrentRoundState() == GameRound.ROUNDSTATE.ENDED || !round.isPlayerInGame(player))
            return;

        if (!(event.getRightClicked() instanceof ItemFrame))
            return;

        event.setCancelled(true);
    }

    public void onOverCraftedPlayerRemoveItemFrameContent(EntityDamageByEntityEvent event) {
        Entity damager = event.getDamager();
        GameRound round = master.getGameRoundManager().getGameRound();

        // NA si la ronda no ha comenzado, si la ronda ha terminado o si el jugador no está jugando.
        if (
            damager instanceof Player &&
            (
                round == null ||
                round.getCurrentRoundState() == GameRound.ROUNDSTATE.ENDED ||
                !round.isPlayerInGame((Player)damager)
            )
        )
            return;

        event.setCancelled(true);
    }

    public void onOverCraftedPlayerInteractWithChest(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        GameRound round = master.getGameRoundManager().getGameRound();

        // NA si la ronda no ha comenzado, si la ronda ha terminado o si el jugador no está jugando.
        if (round == null || round.getCurrentRoundState() == GameRound.ROUNDSTATE.ENDED || !round.isPlayerInGame(player))
            return;

        Block block = event.getClickedBlock();
        if (block == null)
            return;

        if (!(
            block.getType().equals(Material.CHEST) &&
            event.getAction().equals(Action.RIGHT_CLICK_BLOCK)
        ))
            return;

        event.setCancelled(true);

        ItemStack drop = round.getGameArea()
                .getIngredientDispenserByLocation(block.getLocation())
                .dropItem();
        if (drop == null)
            return;

        player.getInventory().addItem(drop);
    }

    public void onOverCraftedPlayerGetsDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player))
            return;
        GameRound round = master.getGameRoundManager().getGameRound();

        // NA si la ronda no ha comenzado, si la ronda ha terminado o si el jugador no está jugando.
        if (round == null || round.getCurrentRoundState() == GameRound.ROUNDSTATE.ENDED || !round.isPlayerInGame(player))
            return;

        event.setCancelled(true);
    }

    // ** NON-PLAYERS **
    public void onOverCraftedNonPlayerMoves(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        GameRound round = master.getGameRoundManager().getGameRound();

        // NA para jugadores en ronda de juego.
        if (round != null && round.isPlayerInGame(player))
            return;

        boolean overlapped = false;
        for (GameArea gmaItem : master.getGameAreaManager().getGameAreas()) {
            if (!Objects.equals(gmaItem.getWorld(), Objects.requireNonNull(player.getLocation().getWorld()).getName()))
                continue;
            if (gmaItem.isPointInsideBoundaries(player.getLocation())) {
                overlapped = true;
                break;
            }
        }

        if (!overlapped) return;

        event.setCancelled(true);
    }

    public void onOverCraftedNonPlayerPlacesBlock(BlockPlaceEvent event) {
        GameRound round = master.getGameRoundManager().getGameRound();

        if (event.getPlayer().hasPermission("overcrafted.manager"))
            return;

        // NA para jugadores en ronda de juego.
        if (round != null && round.isPlayerInGame(event.getPlayer()))
            return;

        if (!Functions.blockBelongsGameArea(event.getBlock()))
            return;

        event.setCancelled(true);

        Messenger.msgToSender(
                event.getPlayer(),
                OverCrafted.prefix + "&cNo puedes poner bloques en un GameArea"
        );
    }

    public void onOverCraftedNonPlayerBreaksBlock(BlockBreakEvent event) {
        Player player = event.getPlayer();
        GameRound round = master.getGameRoundManager().getGameRound();

        if (player.hasPermission("overcrafted.manager"))
            return;
        // NA para jugadores en ronda de juego.
        if (round != null && round.isPlayerInGame(player))
            return;

        if (!Functions.blockBelongsGameArea(event.getBlock()))
            return;

        event.setCancelled(true);

        Messenger.msgToSender(
                event.getPlayer(),
                OverCrafted.prefix + "&cNo puedes romper bloques de un GameArea"
        );
    }

    public void onOverCraftedNonPlayerDamageFrameOrPainting(HangingBreakByEntityEvent event) {
        Entity remover = event.getRemover();
        GameRound round = master.getGameRoundManager().getGameRound();
        if (remover == null)
            return;

        if (remover instanceof Player && remover.hasPermission("overcrafted.manager"))
            return;
        // NA para jugadores en ronda de juego.
        if (remover instanceof Player && (round != null && round.isPlayerInGame((Player)remover)))
            return;

        if (!Functions.entityBelongsGameArea(event.getEntity()))
            return;

        event.setCancelled(true);

        Messenger.msgToSender(
                remover,
                OverCrafted.prefix + "&cNo puedes romper bloques de un GameArea"
        );
    }

    public void onOverCraftedNonPlayerInteractItemFrame(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        GameRound round = master.getGameRoundManager().getGameRound();

        if (player.hasPermission("overcrafted.manager"))
            return;
        if (round != null && round.isPlayerInGame(player))
            return;

        if (!Functions.entityBelongsGameArea(event.getRightClicked()))
            return;

        event.setCancelled(true);

        Messenger.msgToSender(
                player,
                OverCrafted.prefix + "&cNo puedes manipular bloques de un GameArea"
        );
    }

    public void onOverCraftedNonPlayerRemoveItemFrameContent(EntityDamageByEntityEvent event) {
        Entity damager = event.getDamager();
        GameRound round = master.getGameRoundManager().getGameRound();

        if (damager instanceof Player && damager.hasPermission("overcrafted.manager"))
            return;
        // NA para jugadores en ronda de juego.
        if (damager instanceof Player && (round != null && round.isPlayerInGame((Player)damager)))
            return;

        if (!Functions.entityBelongsGameArea(event.getEntity()))
            return;

        event.setCancelled(true);

        Messenger.msgToSender(
                damager,
                OverCrafted.prefix + "&cNo puedes romper bloques de un GameArea"
        );
    }

    // -- Private
    private void onOverCraftedManagerCreatesGameAreaCorner(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        if (item == null)
            return;

        if (!(
            event.getAction().equals(Action.RIGHT_CLICK_BLOCK) &&
            Objects.equals(event.getHand(), EquipmentSlot.HAND) &&
            item.getType() == Material.IRON_SHOVEL
        ))
            return;

        GameAreaPropertiesAssistant gapAssistant = master.getGapaManager().getAssistantByName(player.getName());
        if (gapAssistant == null) {
            Messenger.msgToSender(
                player,
                OverCrafted.prefix + "&cNo hay instancia de Asistente iniciada."
            );
            return;
        }

        Location blockCoords;
        int cornerIndex = gapAssistant.getCornerIndex();

        try {
            blockCoords = Objects.requireNonNull(event.getClickedBlock()).getLocation();
        } catch (Exception ignored) { return; }

        gapAssistant.setCorner(blockCoords);

        Messenger.msgToSender(
            player,
            OverCrafted.prefix + "&aEsquina " + (cornerIndex + 1) +
                    " asignada en x:" + blockCoords.getBlockX() +
                    ", y:" + blockCoords.getBlockY() +
                    ", z:" + blockCoords.getBlockZ()
        );
    }

    private void onOverCraftedManagerCreatesIngredientDispenser(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        if (item == null)
            return;

        if (!(
            event.getAction().equals(Action.RIGHT_CLICK_BLOCK) &&
            Objects.equals(event.getHand(), EquipmentSlot.HAND) &&
            item.getType() == Material.IRON_HOE
        ))
            return;

        GameAreaPropertiesAssistant gapAssistant = master.getGapaManager().getAssistantByName(player.getName());
        if (gapAssistant == null) {
            Messenger.msgToSender(
                player,
                OverCrafted.prefix + "&cNo hay instancia de Asistente iniciada."
            );
            return;
        }

        Block block = event.getClickedBlock();
        if (block == null)
            return;
        if (!Functions.blockBelongsGameArea(block)) {
            Messenger.msgToSender(
                player,
                OverCrafted.prefix + "&cEl cofre debe estar dentro de un GameArea."
            );
            return;
        }
        if (!block.getType().equals(Material.CHEST)) {
            Messenger.msgToSender(
                player,
                OverCrafted.prefix + "&cEl Dispensador debe ser un cofre."
            );
            return;
        }

        Location blockCoords = block.getLocation();
        Material dropping = null;
        for (Entity entity : block.getWorld().getNearbyEntities(blockCoords, 2, 2, 2))
            if (
                entity instanceof ItemFrame itemframe &&
                entity.getLocation().getBlock().getRelative(((ItemFrame)entity).getAttachedFace()).equals(block)
            ) {
                dropping = itemframe.getItem().getType();
                break;
            }

        if (dropping == null) {
            Messenger.msgToSender(
                player,
                OverCrafted.prefix + "&cColoque un ingrediente en un marco."
            );
            return;
        }

        GameArea gamearea = Functions.getGameAreaFromLocation(blockCoords);
        if (gamearea == null)
            return;

        switch (gamearea.addIngredientDispenser(
            new IngredientDispenser(
                blockCoords,
                dropping
            )
        )) {
            case INVALID_ITEM -> {
                Messenger.msgToSender(
                    player,
                    OverCrafted.prefix + "&cEl dispensador debe estar dentro de un GameArea."
                );
                return;
            }
            case ALREADY_IN -> {
                Messenger.msgToSender(
                    player,
                    OverCrafted.prefix + "&cEste dispensador ya fue definido."
                );
                return;
            }
        }

        Messenger.msgToSender(
                player,
            OverCrafted.prefix + "&aSe ha creado un Dispensador de Ingredientes en el GameArea &r&o" +
                    gamearea.getName() + "&r&a."
        );
        event.setCancelled(true);
    }

}
