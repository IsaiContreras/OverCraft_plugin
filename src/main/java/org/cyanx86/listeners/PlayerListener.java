package org.cyanx86.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.player.*;

import org.cyanx86.eventhandlers.PlayerEventsHandler;

public class PlayerListener implements Listener {

    // -- [[ ATTRIBUTES ]] --

    // -- Public

    // -- Private
    private final PlayerEventsHandler playerevents = new PlayerEventsHandler();

    // -- [[ METHODS ]] --

    // -- Public

    @EventHandler
    public void onPlayerJoins(PlayerJoinEvent event) {
        this.playerevents.onOverCraftedManagerJoin(event);
    }

    @EventHandler
    public void onPlayerDisconnects(PlayerQuitEvent event) {
        this.playerevents.onOverCraftedManagerDisconnects(event);
        this.playerevents.onOverCraftedPlayerDisconnects(event);
    }

    @EventHandler
    public void onPlayerInteracts(PlayerInteractEvent event) {
        this.playerevents.onOverCraftedManagerClicksBlockWithItem(event);
        this.playerevents.onOverCraftedPlayerInteractsWithChest(event);
    }

    @EventHandler
    public void onPlayerMoves(PlayerMoveEvent event) {
        this.playerevents.onOverCraftedPlayerMoves(event);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        this.playerevents.onOverCraftedPlayerPlacesBlock(event);
        this.playerevents.onOverCraftedNonPlayerPlacesBlock(event);
    }

    @EventHandler
    public void onBlockBreaks(BlockBreakEvent event) {
        this.playerevents.onOverCraftedNonPlayerBreaksBlock(event);
        this.playerevents.onOverCraftedPlayerBreaksBlock(event);
    }

    @EventHandler
    public void onHangingBreakByEntity(HangingBreakByEntityEvent event) {
        this.playerevents.onOverCraftedPlayerDamageFrameOrPainting(event);
        this.playerevents.onOverCraftedNonPlayerDamageFrameOrPainting(event);
    }

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        this.playerevents.onOverCraftedPlayerInteractItemFrame(event);
        this.playerevents.onOverCraftedNonPlayerInteractItemFrame(event);
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        this.playerevents.onOverCraftedPlayerRemoveItemFrameContent(event);
        this.playerevents.onOverCraftedNonPlayerRemoveItemFrameContent(event);
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        this.playerevents.onOverCraftedPlayerGetsDamage(event);
    }

    // -- Private

}
