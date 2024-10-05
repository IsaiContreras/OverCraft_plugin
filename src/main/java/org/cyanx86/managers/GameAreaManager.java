package org.cyanx86.managers;

import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;

import org.cyanx86.classes.GameArea;
import org.cyanx86.utils.CustomConfigFile;
import org.cyanx86.utils.Enums.ListResult;

import java.util.*;
import org.jetbrains.annotations.NotNull;

public class GameAreaManager extends CustomConfigFile {

    // -- [[ ATTRIBUTES ]] --

    // -- PUBLIC --

    // -- PRIVATE --
    private final List<GameArea> gameAreas = new ArrayList<>();

    // -- [[ METHODS ]] --

    // -- PUBLIC --
    public GameAreaManager() {
        super(
            "gameareas.yml",
            "ocf_settings",
            true
        );
        if (this.registerConfig())
            this.load();
    }

    // Read/Write Methods
    @Override
    protected void load() {
        FileConfiguration config = this.getConfig();
        List<Map<?, ?>> gmaMapList = config.getMapList("gameareas");

        for (Map<?, ?> gmaMap : gmaMapList)
            gameAreas.add(GameArea.deserialize((Map<String, Object>)gmaMap));
    }

    @Override
    public void reload() {
        this.reloadConfig();
        this.load();
    }

    @Override
    public void save() {
        FileConfiguration config = this.getConfig();

        List<Map<String, Object>> gmaMapList = new ArrayList<>();

        for (GameArea gma : this.gameAreas)
            gmaMapList.add(gma.serialize());

        config.set("gameareas", gmaMapList);
        this.saveConfig();
    }

    // List managing
    public ListResult addGameArea(@NotNull String name, @NotNull Location corner1, @NotNull Location corner2, int minPlayers, int maxPlayers) {
        if (this.alreadyExists(name))
            return ListResult.ALREADY_IN;

        GameArea gamearea = new GameArea(
            name,
            corner1,
            corner2,
            minPlayers,
            maxPlayers
        );

        for (GameArea gmaItem : this.gameAreas)
            if (gmaItem.isRegionOverlapping(gamearea))
                return ListResult.INVALID_ITEM;

        this.gameAreas.add(gamearea);
        return ListResult.SUCCESS;
    }

    public ListResult removeGameArea(@NotNull String name) {
        if (this.isEmpty())
            return ListResult.EMPTY_LIST;

        GameArea gamearea = this.getByName(name);

        if (gamearea == null)
            return ListResult.NOT_FOUND;

        this.gameAreas.remove(gamearea);
        return ListResult.SUCCESS;
    }

    public GameArea getByName(@NotNull String name) {
        Optional<GameArea> queryGameArea = this.gameAreas.stream()
                .filter(item -> item.getName().equals(name))
                .findFirst();
        return queryGameArea.orElse(null);
    }

    public List<GameArea> getGameAreas() {
        return new ArrayList<>(this.gameAreas);
    }

    // Validators
    public boolean alreadyExists(@NotNull String name) {
        return this.gameAreas.stream().anyMatch(item -> item.getName().equals(name));
    }

    public boolean isEmpty() {
        return this.gameAreas.isEmpty();
    }

    // -- PRIVATE --

}
