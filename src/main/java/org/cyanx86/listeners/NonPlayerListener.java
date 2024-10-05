package org.cyanx86.listeners;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;

import org.cyanx86.OverCrafted;
import org.cyanx86.classes.GameRound;
import org.cyanx86.utils.Functions;
import org.cyanx86.utils.Messenger;

public class NonPlayerListener implements Listener {

    // -- [[ ATTRIBUTES ]] --

    // -- PUBLIC --

    // -- PRIVATE --
    private final OverCrafted master = OverCrafted.getInstance();

    // -- [[ METHODS ]] --

    // -- PUBLIC --
    @EventHandler
    public void onNonPlayerPlacesBlock(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        GameRound round = master.getGameRoundManager().getGameRound();

        if (
            player.hasPermission("overcrafted.manager") ||
            this.isRoundOff(round, player) ||
            !Functions.blockBelongsGameArea(event.getBlock())
        )
            return;

        event.setCancelled(true);

        Messenger.msgToSender(
            event.getPlayer(),
            OverCrafted.prefix + "&cNo puedes poner bloques en un GameArea"
        );
    }

    @EventHandler
    public void onNonPlayerBreaksBlock(BlockBreakEvent event) {
        Player player = event.getPlayer();
        GameRound round = master.getGameRoundManager().getGameRound();

        if (
            player.hasPermission("overcrafted.manager") ||
            this.isRoundOff(round, player) ||
            !Functions.blockBelongsGameArea(event.getBlock())
        )
            return;

        event.setCancelled(true);

        Messenger.msgToSender(
            event.getPlayer(),
            OverCrafted.prefix + "&cNo puedes romper bloques de un GameArea"
        );
    }

    @EventHandler
    public void onNonPlayerDamageFrameOrPainting(HangingBreakByEntityEvent event) {
        Entity remover = event.getRemover();
        GameRound round = master.getGameRoundManager().getGameRound();
        if (
            remover == null ||
            remover instanceof Player && remover.hasPermission("overcrafted.manager") ||
            remover instanceof Player player && this.isRoundOff(round, player) ||
            !Functions.entityBelongsGameArea(event.getEntity())
        )
            return;

        event.setCancelled(true);

        Messenger.msgToSender(
            remover,
            OverCrafted.prefix + "&cNo puedes romper bloques de un GameArea"
        );
    }

    @EventHandler
    public void onNonPlayerInteractItemFrame(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        GameRound round = master.getGameRoundManager().getGameRound();

        if (
            player.hasPermission("overcrafted.manager") ||
            this.isRoundOff(round, player) ||
            !Functions.entityBelongsGameArea(event.getRightClicked())
        )
            return;

        event.setCancelled(true);

        Messenger.msgToSender(
            player,
            OverCrafted.prefix + "&cNo puedes manipular bloques de un GameArea"
        );
    }

    @EventHandler
    public void onNonPlayerRemoveItemFrameContent(EntityDamageByEntityEvent event) {
        Entity damager = event.getDamager();
        GameRound round = master.getGameRoundManager().getGameRound();

        if (
            !Functions.entityBelongsGameArea(event.getEntity()) ||
            damager instanceof Player player && this.isRoundOff(round, player) ||
            damager instanceof Player && damager.hasPermission("overcrafted.manager")
        )
            return;

        event.setCancelled(true);

        Messenger.msgToSender(
            damager,
            OverCrafted.prefix + "&cNo puedes romper bloques de un GameArea"
        );
    }

    // -- PRIVATE --
    private boolean isRoundOff(GameRound round, Player player) {
        return (round != null && round.isPlayerInGame(player));
    }

    /*
    public void onNonPlayerMoves(PlayerMoveEvent event) {
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
    }*/

}
