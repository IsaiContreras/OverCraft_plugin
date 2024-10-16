package org.cyanx86.listeners;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Furnace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Painting;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;

import org.bukkit.event.inventory.FurnaceSmeltEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
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

    @EventHandler
    public void onFurnaceSmelt(FurnaceSmeltEvent event) {
        Furnace furnace = (Furnace)event.getBlock().getState();
        if (!Functions.blockBelongsGameArea(event.getBlock()))
            return;

        ItemStack item = event.getResult();

        furnace.getWorld().dropItem(
            furnace.getLocation().add(new Vector(0, 1, 0)),
            item
        );

        furnace.getInventory().setResult(new ItemStack(Material.AIR));
    }

    // -- PRIVATE --

}
