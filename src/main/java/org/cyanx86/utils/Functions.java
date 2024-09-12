package org.cyanx86.utils;

import org.bukkit.block.Block;
import org.bukkit.entity.Entity;

import org.cyanx86.OverCrafted;
import org.cyanx86.classes.GameArea;

import java.util.Objects;

public class Functions {

    static private OverCrafted master = OverCrafted.getInstance();

    static public boolean blockBelongsGameArea(Block block) {
        for (GameArea gmaItem: master.getGameAreaManager().getGameAreas()) {
            if (!Objects.equals(gmaItem.getWorld(), block.getWorld().getName()))
                continue;
            if (gmaItem.isPointInsideBoundaries(block.getLocation())) {
                return true;
            }
        }
        return false;
    }

    static public boolean entityBelongsGameArea(Entity entity) {
        for (GameArea gmaItem: master.getGameAreaManager().getGameAreas()) {
            if (!Objects.equals(gmaItem.getWorld(), entity.getWorld().getName()))
                continue;
            if (gmaItem.isPointInsideBoundaries(entity.getLocation())) {
                return true;
            }
        }
        return false;
    }

}
