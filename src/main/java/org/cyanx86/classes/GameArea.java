package org.cyanx86.classes;

import org.bukkit.Location;

import org.cyanx86.utils.Enums.ListResult;
import org.cyanx86.utils.Primitives.Cube;

import java.util.*;
import org.jetbrains.annotations.NotNull;

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
    private List<IngredientDispenser> dispensers = new ArrayList<>();

    // -- [[ METHODS ]] --

    // -- Public
    public GameArea(@NotNull String name, @NotNull Location corner1, @NotNull Location corner2, int maxPlayers) {
        this.name = name;
        this.world = Objects.requireNonNull(corner1.getWorld()).getName();
        this.maxPlayers = maxPlayers;
        this.corners[0] = corner1;
        this.corners[1] = corner2;
        this.cubearea = new Cube(corner1, corner2);
    }
    public GameArea(
            @NotNull String name,
            @NotNull String world,
            @NotNull Location corner_1,
            @NotNull Location corner_2,
            int max_players,
            @NotNull List<SpawnPoint> spawn_points,
            @NotNull List<IngredientDispenser> ingredient_dispensers
    ) {
        this.name = name;
        this.world = world;
        this.maxPlayers = max_players;
        this.corners[0] = corner_1;
        this.corners[1] = corner_2;
        this.cubearea = new Cube(corner_1, corner_2);
        this.spawnpoints = spawn_points;
        this.dispensers = ingredient_dispensers;
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

    public ListResult addSpawnPoint(@NotNull SpawnPoint spawnpoint) {
        if (!this.isPointInsideBoundaries(spawnpoint.getSpawnLocation()))
            return ListResult.INVALID_ITEM;
        if (this.spawnpoints.size() == maxPlayers)
            return ListResult.FULL_LIST;

        spawnpoint.setPlayerIndex(spawnpoints.size() + 1);

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

    public ListResult addIngredientDispenser(@NotNull IngredientDispenser dispenser) {
        if (!this.isPointInsideBoundaries(dispenser.getLocation()))
            return ListResult.INVALID_ITEM;
        if (this.getIngredientDispenserByLocation(dispenser.getLocation()) != null)
            return ListResult.ALREADY_IN;

        this.dispensers.add(dispenser);
        return ListResult.SUCCESS;
    }

    public List<IngredientDispenser> getIngredientDispensers() {
        return this.dispensers;
    }

    public IngredientDispenser getIngredientDispenserByLocation(@NotNull Location location) {
        Optional<IngredientDispenser> query = this.dispensers.stream()
                .filter(item -> item.isLocationOfDispenser(location))
                .findFirst();

        return query.orElse(null);
    }

    public int getIngredientDispensersCount() {
        return this.dispensers.size();
    }

    public void clearIngredientDispensers() {
        this.dispensers.clear();
    }

    public boolean isPointInsideBoundaries(@NotNull Location point) {
        return (
            !(point.getBlockX() < cubearea.left || point.getBlockX() > cubearea.right) &&
            !(point.getBlockY() < cubearea.bottom || point.getBlockY() > cubearea.top) &&
            !(point.getBlockZ() < cubearea.back || point.getBlockZ() > cubearea.front)
        );
    }

    public boolean isRegionOverlapping(@NotNull GameArea other) {
        if (!this.world.equals(other.getWorld()))
            return false;

        return (
            (this.cubearea.left <= other.cubearea.right && this.cubearea.right >= other.cubearea.left) &&
            (this.cubearea.bottom <= other.cubearea.top && this.cubearea.top >= other.cubearea.bottom) &&
            (this.cubearea.back <= other.cubearea.front && this.cubearea.front >= other.cubearea.back)
        );
    }

    public boolean isValidSetUp() {
        return (spawnpoints.size() == maxPlayers && !dispensers.isEmpty());
    }

    public Map<String, Object> serialize() {
        Map<String, Object> data = new HashMap<>();
        List<Map<String, Object>> sppListMap = new ArrayList<>();
        List<Map<String, Object>> idpListMap = new ArrayList<>();

        for (SpawnPoint spawnpoint : this.spawnpoints)
            sppListMap.add(spawnpoint.serialize());
        for (IngredientDispenser dispenser: this.dispensers)
            idpListMap.add(dispenser.serialize());

        data.put("name", this.name);
        data.put("world", this.world);
        data.put("corner_1", this.corners[0].serialize());
        data.put("corner_2", this.corners[1].serialize());
        data.put("max_players", this.maxPlayers);
        data.put("spawn_points", sppListMap);
        data.put("ingredient_dispensers", idpListMap);

        return data;
    }

    public static GameArea deserialize(@NotNull Map<String, Object> args) {
        List<SpawnPoint> spawnpointsList = new ArrayList<>();
        List<IngredientDispenser> dispenserList = new ArrayList<>();
        List<Map<String, Object>> sppMapList = (List<Map<String, Object>>)args.get("spawn_points");
        List<Map<String, Object>> idpMapList = (List<Map<String, Object>>)args.get("ingredient_dispensers");

        for (Map<String, Object> sppMap : sppMapList)
            spawnpointsList.add(SpawnPoint.deserialize(sppMap));
        for (Map<String, Object> idpMap : idpMapList)
            dispenserList.add(IngredientDispenser.deserialize(idpMap));

        return new GameArea(
            (String)args.get("name"),
            (String)args.get("world"),
            Location.deserialize((Map<String, Object>)args.get("corner_1")),
            Location.deserialize((Map<String, Object>)args.get("corner_2")),
            (int)args.get("max_players"),
            spawnpointsList,
            dispenserList
        );
    }

    // -- Private

}
