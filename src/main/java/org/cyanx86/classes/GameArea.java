package org.cyanx86.classes;

import org.bukkit.Location;
import org.cyanx86.utils.Enums;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class GameArea {

    // -- [[ ATTRIBUTES ]] --

    // -- Public

    // -- Private
    private String name;

    private String world;

    private final Location corner1;
    private final Location corner2;

    private final List<Location> spawn_points = new ArrayList<>();

    // -- [[ METHODS ]] --

    // -- Public
    public GameArea(String name, Location corner1, Location corner2) {
        this.name = name;
        this.world = Objects.requireNonNull(corner1.getWorld()).getName();
        this.corner1 = corner1;
        this.corner2 = corner2;
    }

    public String getName() {
        return this.name;
    }
    public String getWorld() { return this.world; }

    public Enums.ListResult addSpawnPoint(Location location) {
        if (location == null)
            return Enums.ListResult.NULL;
        if (!Objects.requireNonNull(location.getWorld()).getName().equals(this.world))
            return Enums.ListResult.INVALID_ITEM;
        if (!this.isInsideBoundaries(location))
            return Enums.ListResult.INVALID_ITEM;

        this.spawn_points.add(location);
        return Enums.ListResult.SUCCESS;
    }

    public void clearSpawnPointList() {
        this.spawn_points.clear();
    }

    public boolean isInsideBoundaries(Location point) {
        // Checking X component
        boolean notinsideX =   (point.getBlockX() < Math.min(corner1.getBlockX(), corner2.getBlockX()) ||
                            point.getBlockX() > Math.max(corner1.getBlockX(), corner2.getBlockX()));
        // Checking Y component
        boolean notinsideY =   (point.getBlockY() < Math.min(corner1.getBlockY(), corner2.getBlockY()) ||
                            point.getBlockY() > Math.max(corner1.getBlockY(), corner2.getBlockY()));
        // Checking Z component
        boolean notinsideZ =   (point.getBlockZ() < Math.min(corner1.getBlockZ(), corner2.getBlockZ()) ||
                            point.getBlockZ() > Math.max(corner1.getBlockZ(), corner2.getBlockZ()));

        return (!notinsideX && !notinsideY && !notinsideZ);
    }

    // -- Private

}
