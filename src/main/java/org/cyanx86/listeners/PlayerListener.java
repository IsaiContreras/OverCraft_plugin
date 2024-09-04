package org.cyanx86.listeners;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import org.cyanx86.OverCrafted;
import org.cyanx86.classes.GameAreaCornerAssistant;
import org.cyanx86.classes.GameRound;
import org.cyanx86.classes.GameRound.ROUNDSTATE;
import org.cyanx86.classes.PlayerState;
import org.cyanx86.classes.PlayerState.PLAYERSTATE;
import org.cyanx86.utils.Messenger;

import java.util.Objects;

public class PlayerListener implements Listener {

    // -- [[ ATTRIBUTES ]] --

    // -- Public
    private final OverCrafted master;

    // -- Private

    // -- [[ METHODS ]] --

    // -- Public
    public PlayerListener (OverCrafted master) {
        this.master = master;
    }

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

        master.getGacaManager().eraseAssistant(player);

        Messenger.msgToConsole(OverCrafted.prefix + player.getName() + " dispuso su asistente.");
    }

    @EventHandler
    public void onOverCraftedManagerClicksBlock(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (!player.hasPermission("overcrafted.manager"))
            return;
        GameAreaCornerAssistant gacAssistant = master.getGacaManager().getAssistantByName(player.getName());
        if (gacAssistant == null)
            return;

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

            if (!gacAssistant.setCorner(blockCoords)) {
                Messenger.msgToSender(
                    player,
                    OverCrafted.prefix + "&cNo se pudo asignar coordenadas."
                );
            }

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
    public void onOverCraftPlayerMoves(PlayerMoveEvent event) {
        Player player = (Player)event.getPlayer();
        GameRound round = master.getGameRoundManager().getGameRound();

        // NA si la ronda no ha comenzado o si el jugador no está en el juego.
        if (
            round == null ||
            !round.isPlayerPlaying(player)
        )
            return;

        // No permite moverse si la Ronda no ha empezado
        if (round.getCurrentRoundState() != ROUNDSTATE.RUNNING) {
            event.setCancelled(true);
            return;
        }

        // No permite moverse si el estado del jugador está en INMOBILIZADO
        PlayerState playerState = round.getPlayersManager().getPlayerState(player);
        if (playerState.getCurrentState() != PLAYERSTATE.RUNNING) {
            event.setCancelled(true);
            return;
        }

        // Si sale del GameArea regresar
        if (!round.getGameArea().isPointInsideBoundaries(player.getLocation())) {
            round.respawnPlayer(player);
        }

    }

    // -- Private

}
