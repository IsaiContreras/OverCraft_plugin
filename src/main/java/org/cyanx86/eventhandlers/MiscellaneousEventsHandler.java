package org.cyanx86.eventhandlers;

import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Painting;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.cyanx86.OverCrafted;
import org.cyanx86.utils.Functions;

public class MiscellaneousEventsHandler {

    // -- [[ ATTRIBUTES ]] --

    // -- Public

    // -- Private
    private final OverCrafted master = OverCrafted.getInstance();

    // -- [[ METHODS ]] --

    // -- Public
    public void onEntityBreakGameAreaItemFrameOrPainting(HangingBreakByEntityEvent event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof ItemFrame || entity instanceof Painting))
            return;

        if (event.getRemover() instanceof Player)
            return;

        if (!Functions.entityBelongsGameArea(entity))
            return;

        event.setCancelled(true);
    }

    public void onEntityDamageGameAreaItemFrameOrPainting(EntityDamageByEntityEvent event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof ItemFrame || entity instanceof Painting))
            return;

        if (event.getDamager() instanceof Player)
            return;

        if (!Functions.entityBelongsGameArea(entity))
            return;

        event.setCancelled(true);
    }

    // -- Private

}
