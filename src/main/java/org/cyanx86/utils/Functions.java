package org.cyanx86.utils;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;

import org.cyanx86.OverCrafted;
import org.cyanx86.classes.GameArea;

import java.util.Objects;
import java.util.Optional;

import org.jetbrains.annotations.NotNull;

public class Functions {

    static private OverCrafted master = OverCrafted.getInstance();

    static public boolean blockBelongsGameArea(@NotNull Block block) {
        for (GameArea gmaItem: master.getGameAreaManager().getGameAreas()) {
            if (!Objects.equals(gmaItem.getWorld(), block.getWorld().getName()))
                continue;
            if (gmaItem.isPointInsideBoundaries(block.getLocation())) {
                return true;
            }
        }
        return false;
    }

    static public boolean entityBelongsGameArea(@NotNull Entity entity) {
        for (GameArea gmaItem: master.getGameAreaManager().getGameAreas()) {
            if (!Objects.equals(gmaItem.getWorld(), entity.getWorld().getName()))
                continue;
            if (gmaItem.isPointInsideBoundaries(entity.getLocation())) {
                return true;
            }
        }
        return false;
    }

    static public GameArea getGameAreaFromLocation(@NotNull Location location) {
        for (GameArea gmaItem : master.getGameAreaManager().getGameAreas())
            if (
                gmaItem.getWorld().equals(Objects.requireNonNull(location.getWorld()).getName()) &&
                gmaItem.isPointInsideBoundaries(location)
            )
                return gmaItem;
        return null;
    }

    static public GameArea getGameAreaByName(@NotNull String name) {
        Optional<GameArea> query = master.getGameAreaManager()
                .getGameAreas()
                .stream()
                .filter(item -> item.getName().equals(name))
                .findFirst();
        return query.orElse(null);
    }

    static public int getRandomNumber(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }

}
