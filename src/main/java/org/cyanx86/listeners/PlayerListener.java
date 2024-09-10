package org.cyanx86.listeners;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import org.cyanx86.OverCrafted;
import org.cyanx86.classes.GameArea;
import org.cyanx86.classes.GameAreaCornerAssistant;
import org.cyanx86.classes.GameRound;
import org.cyanx86.classes.GameRound.ROUNDSTATE;
import org.cyanx86.utils.Enums.ListResult;
import org.cyanx86.utils.Messenger;

import java.util.Map;
import java.util.Objects;

public class PlayerListener implements Listener {

    // -- [[ ATTRIBUTES ]] --

    // -- Public
    private final OverCrafted master = OverCrafted.getInstance();

    // -- Private

    // -- [[ METHODS ]] --

    // -- Public

    @EventHandler
    public void onOverCraftedManagerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if(!player.hasPermission("overcrafted.manager"))
            return;

        // Register GameAreaCornerAssistant
        master.getGacaManager().signInAssistant(player);

        Messenger.msgToConsole(OverCrafted.prefix + player.getName() + " registro su asistente.");
    }

    @EventHandler
    public void onOverCraftedManagerLeaves(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        if (master.getGacaManager().eraseAssistant(player) == ListResult.NOT_FOUND)
            return;

        Messenger.msgToConsole(OverCrafted.prefix + player.getName() + " dispuso su asistente.");
    }

    @EventHandler
    public void onOverCraftedManagerClicksBlock(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (!player.hasPermission("overcrafted.manager"))
            return;
        GameAreaCornerAssistant gacAssistant = master.getGacaManager().getAssistantByName(player.getName());
        if (gacAssistant == null) {
            Messenger.msgToSender(
                player,
                    OverCrafted.prefix + "&cNo hay instancia de Asistente iniciada."
            );
            return;
        }

        Action action = event.getAction();
        ItemStack item = event.getItem();

        if (item == null) return;

        if (
            action.equals(Action.RIGHT_CLICK_BLOCK) &&
            Objects.equals(event.getHand(), EquipmentSlot.HAND) &&
            item.getType() == Material.IRON_SHOVEL
        ) {
            Location blockCoords;
            int cornerIndex = gacAssistant.getCornerIndex();

            try {
                 blockCoords = Objects.requireNonNull(event.getClickedBlock()).getLocation();
            } catch (Exception ignored) {return;}

            gacAssistant.setCorner(blockCoords);

            Messenger.msgToSender(
                player,
                OverCrafted.prefix + "&aEsquina " + (cornerIndex + 1) +
                    " asignada en x:" + blockCoords.getBlockX() +
                    ", y:" + blockCoords.getBlockY() +
                    ", z:" + blockCoords.getBlockZ()
            );
        }
    }

    @EventHandler
    public void onOverCraftedPlayerDisconnect(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        GameRound round = master.getGameRoundManager().getGameRound();

        // NA si la ronda no ha comenzado.
        if (round == null) return;

        if (!round.isPlayerInGame(player))
            return;

        round.removePlayer(player);
    }

    /*
    @EventHandler
    public void onOverCraftedNonPlayerMoves(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        GameRound round = master.getGameRoundManager().getGameRound();

        // NA para jugadores en ronda de juego.
        if (round != null && round.isPlayerInGame(player))
            return;

        boolean overlapped = false;
        for (GameArea gmaItem : master.getGameAreaManager().getGameAreas()) {
            if (!Objects.equals(gmaItem.getWorld(), Objects.requireNonNull(player.getLocation().getWorld()).getName())) continue;
            if (gmaItem.isPointInsideBoundaries(player.getLocation())) {
                overlapped = true;
                break;
            }
        }

        if (!overlapped) return;

        event.setCancelled(true);
    }
    */

    @EventHandler
    public void onOverCraftedPlayerMoves(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        GameRound round = master.getGameRoundManager().getGameRound();

        // NA si la ronda no ha comenzado o si el jugador no está en el juego o si la ronda ha terminado.
        if (round == null || !round.isPlayerInGame(player) || round.getCurrentRoundState() == ROUNDSTATE.ENDED)
            return;

        // No permite moverse si la Ronda no ha empezado
        if (round.getCurrentRoundState() != ROUNDSTATE.RUNNING) {
            event.setCancelled(true);
            return;
        }

        /* No permite moverse si el estado del jugador está en INMOBILIZADO
        if (round.getStateOfPlayer(player) != null && round.getStateOfPlayer(player) != PLAYERSTATE.RUNNING) {
            event.setCancelled(true);
            return;
        } */

        // Si sale del GameArea regresar jugador a su SpawnPoint
        if (!round.getGameArea().isPointInsideBoundaries(player.getLocation())) {
            round.spawnPlayer(player, true);
        }
    }

    @EventHandler
    public void onOverCraftedNonPlayerBreaksBlock(BlockBreakEvent event) {
        Block block = event.getBlock();
        GameRound round = master.getGameRoundManager().getGameRound();

        if (event.getPlayer().hasPermission("overcrafted.manager"))
            return;

        // NA para jugadores en ronda de juego.
        if (round != null && round.isPlayerInGame(event.getPlayer()))
            return;

        boolean isGameAreaBlock = false;
        for (GameArea gmaItem : master.getGameAreaManager().getGameAreas()) {
            if (!Objects.equals(gmaItem.getWorld(), block.getWorld().getName())) continue;
            if (gmaItem.isPointInsideBoundaries(block.getLocation())) {
                isGameAreaBlock = true;
                break;
            }
        }

        if (!isGameAreaBlock) return;

        event.setCancelled(true);

        Messenger.msgToSender(
            event.getPlayer(),
            OverCrafted.prefix + "&cNo puedes romper bloques de un GameArea"
        );
    }

    @EventHandler
    public void onOverCraftedPlayerBreaksBlock(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        GameRound round = master.getGameRoundManager().getGameRound();
        Map<Material, Material> materialMap = master.getOreBlocks().getOreMap();

        if (round == null || round.getCurrentRoundState() == ROUNDSTATE.ENDED || !round.isPlayerInGame(player)) return;
        if (!round.getGameArea().isPointInsideBoundaries(block.getLocation())) return;

        if (!materialMap.containsKey(block.getType())) return;

        ItemStack deliver = new ItemStack(
            materialMap.get(block.getType())
        );
        player.getInventory().addItem(deliver);

        event.setCancelled(true);
    }

    // -- Private

}
