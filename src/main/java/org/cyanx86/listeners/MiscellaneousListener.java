package org.cyanx86.listeners;

import org.bukkit.Bukkit;
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
import org.cyanx86.OverCrafted;
import org.cyanx86.classes.GameRound;
import org.cyanx86.utils.Functions;
import org.cyanx86.utils.Messenger;

import java.util.ArrayList;
import java.util.List;

public class MiscellaneousListener implements Listener {

    // -- [[ ATTRIBUTES ]] --

    // -- PUBLIC --

    // -- PRIVATE --
    private final OverCrafted master = OverCrafted.getInstance();

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
        GameRound round = this.master.getGameRoundManager().getGameRound();
        if (
            !Functions.blockBelongsKitchenArea(event.getBlock()) ||
            !(round == null || round.getCurrentRoundState() != GameRound.ROUNDSTATE.ENDED)
        )
            return;

        Furnace furnace = (Furnace)event.getBlock().getState();
        ItemStack item = event.getResult();

        furnace.getWorld().dropItem(
            furnace.getLocation().add(0, 1, 0),
            new ItemStack(item.getType())
        );

        Bukkit.getScheduler().runTaskLater(OverCrafted.getInstance(), () -> {
            furnace.getInventory().setItem(2, new ItemStack(Material.AIR));
        }, 1);

        Messenger.msgToConsole("Furnace smelted and cleaned!");
    }

    // -- PRIVATE --

}
