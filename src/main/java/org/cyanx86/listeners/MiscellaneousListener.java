package org.cyanx86.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;

import org.cyanx86.eventhandlers.MiscellaneousEventsHandler;

public class MiscellaneousListener implements Listener {

    // -- [[ ATTRIBUTES ]] --

    // -- Public

    // -- Private
    private final MiscellaneousEventsHandler miscellaneousevents = new MiscellaneousEventsHandler();

    // -- [[ METHODS ]] --

    // -- Public
    @EventHandler
    public void onHangingBreakByEntity(HangingBreakByEntityEvent event) {
        this.miscellaneousevents.onEntityBreakGameAreaItemFrameOrPainting(event);
    }

    @EventHandler
    public void onEntityDamagedByEntity(EntityDamageByEntityEvent event) {
        this.miscellaneousevents.onEntityDamageGameAreaItemFrameOrPainting(event);
    }

    // -- Private

}
