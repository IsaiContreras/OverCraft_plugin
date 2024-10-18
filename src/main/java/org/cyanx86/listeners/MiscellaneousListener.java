package org.cyanx86.listeners;

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
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;

import org.bukkit.event.inventory.FurnaceSmeltEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.cyanx86.utils.Functions;

import java.util.ArrayList;
import java.util.List;

public class MiscellaneousListener implements Listener {

    // -- [[ ATTRIBUTES ]] --

    // -- PUBLIC --

    // -- PRIVATE --

    // -- [[ METHODS ]] --

    // -- PUBLIC --
    @EventHandler
    public void onEntityBreakGameAreaItemFrameOrPainting(HangingBreakByEntityEvent event) {
        Entity entity = event.getEntity();
        if (!(
            (entity instanceof ItemFrame || entity instanceof Painting) &&
            Functions.entityBelongsKitchenArea(entity) &&
            !(event.getRemover() instanceof Player)
        ))
            return;

        event.setCancelled(true);
    }

    @EventHandler
    public void onEntityDamageGameAreaItemFrameOrPainting(EntityDamageByEntityEvent event) {
        Entity entity = event.getEntity();
        if (!(
            (entity instanceof ItemFrame || entity instanceof Painting) &&
            Functions.entityBelongsKitchenArea(entity) &&
            !(event.getDamager() instanceof Player)
        ))
            return;

        event.setCancelled(true);
    }

    @EventHandler
    public void onExplosionBreaksBlocks(EntityExplodeEvent event) {
        if (Functions.getKitchenAreaFromLocation(event.getLocation()) == null)
            return;

        List<Block> eventBlocks = event.blockList();
        List<Block> ktcBlocks = new ArrayList<>();

        for (Block blockItem : eventBlocks) {
            if (Functions.blockBelongsKitchenArea(blockItem))
                ktcBlocks.add(blockItem);
        }

        for (Block blockItem : ktcBlocks)
            eventBlocks.remove(blockItem);
    }

    @EventHandler
    public void onFurnaceSmelt(FurnaceSmeltEvent event) {
        Furnace furnace = (Furnace)event.getBlock().getState();
        if (!Functions.blockBelongsKitchenArea(event.getBlock()))
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
