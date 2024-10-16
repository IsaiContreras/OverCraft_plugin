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
        if (
            player.hasPermission("overcrafted.manager") ||
            this.isRoundOff(player) ||
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
        if (
            player.hasPermission("overcrafted.manager") ||
            this.isRoundOff(player) ||
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
        if (
            remover == null ||
            remover instanceof Player && remover.hasPermission("overcrafted.manager") ||
            remover instanceof Player player && this.isRoundOff(player) ||
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
        if (
            player.hasPermission("overcrafted.manager") ||
            this.isRoundOff(player) ||
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
        if (
            !Functions.entityBelongsGameArea(event.getEntity()) ||
            damager instanceof Player player && this.isRoundOff(player) ||
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
    private boolean isRoundOff(Player player) {
        GameRound round = master.getGameRoundManager().getGameRound();
        return (round != null && round.isPlayerInGame(player));
    }

}
