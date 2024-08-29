package org.cyanx86.classes;

import org.bukkit.Location;
import org.cyanx86.utils.Enums.ListResult;
import org.cyanx86.utils.Primitives.Cube;

import java.util.*;

public class GameArea {

    // -- [[ ATTRIBUTES ]] --

    // -- Public

    // -- Private
    private String name;

    private String world;

    private final Location corner1;
    private final Location corner2;

    private final Cube cubeArea;

    private List<Location> spawnPoints = new ArrayList<>();

    // -- [[ METHODS ]] --

    // -- Public
    public GameArea(String name, Location corner1, Location corner2) {
        this.name = name;
        this.world = Objects.requireNonNull(corner1.getWorld()).getName();
        this.corner1 = corner1;
        this.corner2 = corner2;
        this.cubeArea = new Cube(corner1, corner2);
    }
    public GameArea(String name, String world, Location corner1, Location corner2, List<Location> spawn_points) {
        this.name = name;
        this.world = world;
        this.corner1 = corner1;
        this.corner2 = corner2;
        this.cubeArea = new Cube(corner1, corner2);
        this.spawnPoints = spawn_points;
    }

    public String getName() {
        return this.name;
    }
    public String getWorld() { return this.world; }
    public Location getCorner1() { return this.corner1; }
    public Location getCorner2() { return this.corner2; }

    public ListResult addSpawnPoint(Location location) {
        if (location == null)
            return ListResult.NULL;
        if (!Objects.requireNonNull(location.getWorld()).getName().equals(this.world))
            return ListResult.INVALID_ITEM;
        if (!this.isInsideBoundaries(location))
            return ListResult.INVALID_ITEM;

        this.spawnPoints.add(location);
        return ListResult.SUCCESS;
    }

    public List<Location> getSpawnPoints() {
        return this.spawnPoints;
    }

    public int getSpawnPointsCount() {
        return this.spawnPoints.size();
    }

    public void clearSpawnPointList() {
        this.spawnPoints.clear();
    }

    public boolean isInsideBoundaries(Location point) {
        return (
            !(point.getBlockX() < cubeArea.left || point.getBlockX() > cubeArea.right) &&
            !(point.getBlockY() < cubeArea.bottom || point.getBlockY() > cubeArea.top) &&
            !(point.getBlockZ() < cubeArea.back || point.getBlockZ() > cubeArea.front)
        );
    }

    public boolean isRegionOverlapping(GameArea other) {
        if (!this.world.equals(other.getWorld()))
            return false;

        return (
            (this.cubeArea.left <= other.cubeArea.right && this.cubeArea.right >= other.cubeArea.left) &&
            (this.cubeArea.bottom <= other.cubeArea.top && this.cubeArea.top >= other.cubeArea.bottom) &&
            (this.cubeArea.back <= other.cubeArea.front && this.cubeArea.front >= other.cubeArea.back)
        );
    }

    public Map<String, Object> serialize() {
        Map<String, Object> data = new HashMap<>();
        List<Map<String, Object>> sppListMap = new ArrayList<>();

        for (Location spawnpoint : this.spawnPoints) {
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
