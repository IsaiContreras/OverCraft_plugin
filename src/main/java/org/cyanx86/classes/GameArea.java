package org.cyanx86.classes;

import org.bukkit.Location;

import org.cyanx86.utils.Enums.ListResult;
import org.cyanx86.utils.Primitives.Cube;

import java.util.*;

public class GameArea {

    // -- [[ ATTRIBUTES ]] --

    // -- Public

    // -- Private

    private final String name;

    private final String world;

    private final int maxPlayers;

    private final Location[] corners = new Location[2];

    private final Cube cubearea;

    private List<SpawnPoint> spawnpoints = new ArrayList<>();

    // -- [[ METHODS ]] --

    // -- Public
    public GameArea(String name, Location corner1, Location corner2, int maxPlayers) {
        this.name = name;
        this.world = Objects.requireNonNull(corner1.getWorld()).getName();
        this.maxPlayers = maxPlayers;
        this.corners[0] = corner1;
        this.corners[1] = corner2;
        this.cubearea = new Cube(corner1, corner2);
    }
    public GameArea(String name, String world, Location corner1, Location corner2, int maxPlayers, List<SpawnPoint> spawn_points) {
        this.name = name;
        this.world = world;
        this.maxPlayers = maxPlayers;
        this.corners[0] = corner1;
        this.corners[1] = corner2;
        this.cubearea = new Cube(corner1, corner2);
        this.spawnpoints = spawn_points;
    }

    public String getName() {
        return this.name;
    }
    public String getWorld() {
        return this.world;
    }
    public Location getCorner(int index) {
        if (index < 0 || index > 1)
            return null;
        return this.corners[index];
    }

    public int getMaxPlayers() {
        return this.maxPlayers;
    }

    public ListResult addSpawnPoint(SpawnPoint spawnpoint) {
        if (spawnpoint == null)
            return ListResult.NULL;
        if (!this.isPointInsideBoundaries(spawnpoint.getSpawnLocation()))
            return ListResult.INVALID_ITEM;
        if (this.spawnpoints.size() == maxPlayers)
            return ListResult.FULL_LIST;

        int playerIndex = spawnpoints.size() + 1;
        spawnpoint.setPlayerIndex(playerIndex);

        this.spawnpoints.add(spawnpoint);
        return ListResult.SUCCESS;
    }

    public List<SpawnPoint> getSpawnPoints() {
        return this.spawnpoints;
    }

    public int getSpawnPointsCount() {
        return this.spawnpoints.size();
    }

    public void clearSpawnPointList() {
        this.spawnpoints.clear();
    }

    public boolean isPointInsideBoundaries(Location point) {
        return (
            !(point.getBlockX() < cubearea.left || point.getBlockX() > cubearea.right) &&
            !(point.getBlockY() < cubearea.bottom || point.getBlockY() > cubearea.top) &&
            !(point.getBlockZ() < cubearea.back || point.getBlockZ() > cubearea.front)
        );
    }

    public boolean isRegionOverlapping(GameArea other) {
        if (!this.world.equals(other.getWorld()))
            return false;

        return (
            (this.cubearea.left <= other.cubearea.right && this.cubearea.right >= other.cubearea.left) &&
            (this.cubearea.bottom <= other.cubearea.top && this.cubearea.top >= other.cubearea.bottom) &&
            (this.cubearea.back <= other.cubearea.front && this.cubearea.front >= other.cubearea.back)
        );
    }

    public boolean isValidSetUp() {
        return (spawnpoints.size() == maxPlayers);
    }

    public Map<String, Object> serialize() {
        Map<String, Object> data = new HashMap<>();
        List<Map<String, Object>> sppListMap = new ArrayList<>();

        for (SpawnPoint spawnpoint : this.spawnpoints) {
            sppListMap.add(spawnpoint.serialize());
        }

        data.put("name", this.name);
        data.put("world", this.world);
        data.put("corner1", this.corners[0].serialize());
        data.put("corner2", this.corners[1].serialize());
        data.put("maxplayers", this.maxPlayers);
        data.put("spawnpoints", sppListMap);

        return data;
    }

    public static GameArea deserialize(Map<String, Object> args) {
        List<SpawnPoint> spawnpointsList = new ArrayList<>();
        List<Map<String, Object>> sppMapList = (List<Map<String, Object>>)args.get("spawnpoints");

        for (Map<String, Object> sppMap : sppMapList) {
            spawnpointsList.add(SpawnPoint.deserialize(sppMap));
        }

        return new GameArea(
            (String)args.get("name"),
            (String)args.get("world"),
            Location.deserialize((Map<String, Object>) args.get("corner1")),
            Location.deserialize((Map<String, Object>) args.get("corner2")),
            (int)args.get("maxplayers"),
            spawnpointsList
        );
    }

    // -- Private

}
