package org.cyanx86.managers;

import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;

import org.cyanx86.OverCrafted;
import org.cyanx86.classes.GameArea;
import org.cyanx86.utils.Enums.ListResult;

import java.util.*;
import org.jetbrains.annotations.NotNull;

public class GameAreaManager {

    // -- [[ ATTRIBUTES ]] --

    // -- Public

    // -- Private
    private final OverCrafted master = OverCrafted.getInstance();
    private final CustomConfigFile configFile;

    private final String filename = "gameareas.yml";
    private final String folder = "ocf_settings";

    private final List<GameArea> gameAreas = new ArrayList<>();

    // -- [[ METHODS ]]

    // -- Public
    public GameAreaManager() {
        this.configFile = new CustomConfigFile(
            filename,
            folder,
            true
        );
        this.configFile.registerConfig();
        this.loadConfig();
    }

    // Read/Write Methods
    public void loadConfig() {
        FileConfiguration config = this.configFile.getConfig();
        List<Map<?, ?>> gmaMapList = config.getMapList("gameareas");

        for (Map<?, ?> gmaMap : gmaMapList) {
            gameAreas.add(GameArea.deserialize((Map<String, Object>)gmaMap));
        }
    }

    public void reloadConfig() {
        this.configFile.reloadConfig();
        this.loadConfig();
    }

    public void saveConfig() {
        FileConfiguration config = this.configFile.getConfig();

        List<Map<String, Object>> gmaMapList = new ArrayList<>();

        for (GameArea gma : this.gameAreas) {
            gmaMapList.add(gma.serialize());
        }

        config.set("gameareas", gmaMapList);
        this.configFile.saveConfig();
    }

    // List managing
    public ListResult addGameArea(@NotNull String name, @NotNull Location corner1, @NotNull Location corner2, int maxPlayers) {
        if (this.alreadyExists(name))
            return ListResult.ALREADY_IN;

        GameArea gamearea = new GameArea(
            name,
            corner1,
            corner2,
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
        Optional<GameArea> queryGameArea = gameAreas.stream().filter(item -> item.getName().equals(name)).findFirst();
        return queryGameArea.orElse(null);
    }

    public boolean alreadyExists(@NotNull String name) {
        return this.gameAreas.stream().anyMatch(item -> item.getName().equals(name));
    }

    public boolean isEmpty() {
        return this.gameAreas.isEmpty();
    }

    public List<GameArea> getGameAreas() { return this.gameAreas; }

    // -- Private

}
