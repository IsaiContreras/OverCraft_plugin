package org.cyanx86.classes;

import org.bukkit.Location;
import org.bukkit.Material;

import org.cyanx86.utils.Enums.ListResult;
import org.cyanx86.utils.Primitives.Cube;

import java.util.*;
import org.jetbrains.annotations.NotNull;

public class GameArea {

    // -- [[ ATTRIBUTES ]] --

    // -- PUBLIC --

    // -- PRIVATE --

    private final String name;
    private final String world;
    private final int minPlayers;
    private final int maxPlayers;

    private final Location[] corners = new Location[2];
    private final Cube cubeArea;
    private List<SpawnPoint> spawnPoints = new ArrayList<>();
    private List<Material> recipes = new ArrayList<>();

    // -- [[ METHODS ]] --

    // -- PUBLIC --
    public GameArea(@NotNull String name, @NotNull Location corner1, @NotNull Location corner2, int minPlayers, int maxPlayers) {
        this.name = name;
        this.world = Objects.requireNonNull(corner1.getWorld()).getName();
        this.minPlayers = minPlayers;
        this.maxPlayers = maxPlayers;
        this.corners[0] = corner1;
        this.corners[1] = corner2;
        this.cubeArea = new Cube(corner1, corner2);
    }
    public GameArea(
            @NotNull String name,
            @NotNull String world,
            @NotNull Location corner1,
            @NotNull Location corner2,
            int minPlayers,
            int maxPlayers,
            @NotNull List<SpawnPoint> spawnPoints,
            @NotNull List<Material> recipes
    ) {
        this.name = name;
        this.world = world;
        this.minPlayers = minPlayers;
        this.maxPlayers = maxPlayers;
        this.corners[0] = corner1;
        this.corners[1] = corner2;
        this.cubeArea = new Cube(corner1, corner2);
        this.spawnPoints = spawnPoints;
        this.recipes = recipes;
    }

    public String getName() {
        return this.name;
    }
    public String getWorld() {
        return this.world;
    }
    public Location getCorner(int index) {
        return (index < 0 || index > 1 ? null : this.corners[index]);
    }

    public int getMinPlayers() { return this.minPlayers; }
    public int getMaxPlayers() {
        return this.maxPlayers;
    }

    // SpawnPoints
    public ListResult addSpawnPoint(@NotNull SpawnPoint spawnpoint) {
        if (!this.isPointInsideBoundaries(spawnpoint.getSpawnLocation()))
            return ListResult.INVALID_ITEM;
        if (this.spawnPoints.size() == maxPlayers)
            return ListResult.FULL_LIST;

        spawnpoint.setPlayerIndex(spawnPoints.size() + 1);

        this.spawnPoints.add(spawnpoint);
        return ListResult.SUCCESS;
    }

    public List<SpawnPoint> getSpawnPoints() {
        return new ArrayList<>(this.spawnPoints);
    }

    public int getSpawnPointsCount() {
        return this.spawnPoints.size();
    }

    public void clearSpawnPointList() {
        this.spawnPoints.clear();
    }

    // Recipes
    public ListResult addRecipe(@NotNull Material recipe) {
        if (this.recipes.contains(recipe))
            return ListResult.ALREADY_IN;

        this.recipes.add(recipe);
        return ListResult.SUCCESS;
    }

    public int getRecipesCount() {
        return this.recipes.size();
    }

    public List<Material> getRecipes() {
        return new ArrayList<>(this.recipes);
    }

    public void clearRecipes() {
        this.recipes.clear();
    }

    // Validators
    public boolean isPointInsideBoundaries(@NotNull Location point) {
        return (
            !(point.getBlockX() < this.cubeArea.left || point.getBlockX() > this.cubeArea.right) &&
            !(point.getBlockY() < this.cubeArea.bottom || point.getBlockY() > this.cubeArea.top) &&
            !(point.getBlockZ() < this.cubeArea.back || point.getBlockZ() > this.cubeArea.front)
        );
    }

    public boolean isRegionOverlapping(@NotNull GameArea other) {
        if (!this.world.equals(other.getWorld()))
            return false;

        return (
            (this.cubeArea.left <= other.cubeArea.right && this.cubeArea.right >= other.cubeArea.left) &&
            (this.cubeArea.bottom <= other.cubeArea.top && this.cubeArea.top >= other.cubeArea.bottom) &&
            (this.cubeArea.back <= other.cubeArea.front && this.cubeArea.front >= other.cubeArea.back)
        );
    }

    public boolean isValidSetUp() {
        return (spawnPoints.size() == this.maxPlayers && !this.recipes.isEmpty());
    }

    // Data management
    public Map<String, Object> serialize() {
        Map<String, Object> data = new HashMap<>();
        List<Map<String, Object>> sppListMap = new ArrayList<>();
        List<Map<String, Object>> idpListMap = new ArrayList<>();
        List<String> rcpListMap = new ArrayList<>();

        for (SpawnPoint spawnpoint : this.spawnPoints)
            sppListMap.add(spawnpoint.serialize());
        for (Material recipe : this.recipes)
            rcpListMap.add(recipe.name());

        data.put("name", this.name);
        data.put("world", this.world);
        data.put("corner_1", this.corners[0].serialize());
        data.put("corner_2", this.corners[1].serialize());
        data.put("min_players", this.minPlayers);
        data.put("max_players", this.maxPlayers);
        data.put("spawn_points", sppListMap);
        data.put("ingredient_dispensers", idpListMap);
        data.put("recipes", rcpListMap);

        return data;
    }

    public static GameArea deserialize(@NotNull Map<String, Object> args) {
        List<SpawnPoint> spawnpointsList = new ArrayList<>();
        List<Material> recipeList = new ArrayList<>();
        List<Map<String, Object>> sppMapList = (List<Map<String, Object>>)args.get("spawn_points");
        List<String> rcpList = ((List<String>)args.get("recipes"));

        for (Map<String, Object> sppMap : sppMapList)
            spawnpointsList.add(SpawnPoint.deserialize(sppMap));
        for (String rcpItem : rcpList)
            recipeList.add(Material.valueOf(rcpItem));

        return new GameArea(
            (String)args.get("name"),
            (String)args.get("world"),
            Location.deserialize((Map<String, Object>)args.get("corner_1")),
            Location.deserialize((Map<String, Object>)args.get("corner_2")),
            (int)args.get("min_players"),
            (int)args.get("max_players"),
            spawnpointsList,
            recipeList
        );
    }

    // -- PRIVATE --

}
