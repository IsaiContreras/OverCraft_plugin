package org.cyanx86.classes;

import org.bukkit.Location;
import org.cyanx86.utils.Enums;

import java.util.*;

public class GameArea {

    // -- [[ ATTRIBUTES ]] --

    // -- Public

    // -- Private
    private String name;

    private String world;

    private final Location corner1;
    private final Location corner2;

    private List<Location> spawn_points = new ArrayList<>();

    // -- [[ METHODS ]] --

    // -- Public
    public GameArea(String name, Location corner1, Location corner2) {
        this.name = name;
        this.world = Objects.requireNonNull(corner1.getWorld()).getName();
        this.corner1 = corner1;
        this.corner2 = corner2;
    }
    public GameArea(String name, String world, Location corner1, Location corner2, List<Location> spawn_points) {
        this.name = name;
        this.world = world;
        this.corner1 = corner1;
        this.corner2 = corner2;
        this.spawn_points = spawn_points;
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

    public int spawnPointCount() {
        return this.spawn_points.size();
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

    public Map<String, Object> serialize() {
        Map<String, Object> data = new HashMap<>();
        List<Map<String, Object>> sppListMap = new ArrayList<>();

        for (Location spawnpoint : this.spawn_points) {
            sppListMap.add(spawnpoint.serialize());
        }

        data.put("name", this.name);
        data.put("world", this.world);
        data.put("corner1", this.corner1.serialize());
        data.put("corner2", this.corner2.serialize());
        data.put("spawnpoints", sppListMap);

        return data;
    }

    public static GameArea deserialize(Map<String, Object> args) {
        List<Location> spawnpoints_list = new ArrayList<>();
        List<Map<String, Object>> sppMapList = (List<Map<String, Object>>) args.get("spawnpoints");

        for (Map<String, Object> sppMap : sppMapList) {
            spawnpoints_list.add(Location.deserialize(sppMap));
        }

        return new GameArea(
            (String)args.get("name"),
            (String)args.get("world"),
            Location.deserialize((Map<String, Object>) args.get("corner1")),
            Location.deserialize((Map<String, Object>) args.get("corner2")),
            spawnpoints_list
        );
    }

    // -- Private

}
