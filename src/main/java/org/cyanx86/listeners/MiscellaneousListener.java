package org.cyanx86.listeners;

import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Painting;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;

import org.cyanx86.utils.Functions;

public class MiscellaneousListener implements Listener {

    // -- [[ ATTRIBUTES ]] --

    // -- PUBLIC --

    // -- PRIVATE --

    // -- [[ METHODS ]] --

    // -- PUBLIC --
    @EventHandler
    public void onEntityBreakGameAreaItemFrameOrPainting(HangingBreakByEntityEvent event) {
        Entity entity = event.getEntity();
        if (
            !(entity instanceof ItemFrame || entity instanceof Painting) ||
            event.getRemover() instanceof Player ||
            !Functions.entityBelongsGameArea(entity)
        )
            return;

        event.setCancelled(true);
    }

    @EventHandler
    public void onEntityDamageGameAreaItemFrameOrPainting(EntityDamageByEntityEvent event) {
        Entity entity = event.getEntity();
        if (
            !(entity instanceof ItemFrame || entity instanceof Painting) ||
            event.getDamager() instanceof Player ||
            !Functions.entityBelongsGameArea(entity)
        )
            return;

        event.setCancelled(true);
    }

    // -- PRIVATE --

}
