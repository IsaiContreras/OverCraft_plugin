package org.cyanx86.managers;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.cyanx86.OverCrafted;
import org.cyanx86.classes.GameArea;

import java.io.File;
import java.util.*;

public class GameAreaManager {

    // -- [[ ATTRIBUTES ]] --

    // -- Public

    // -- Private
    private final OverCrafted master;
    private final CustomConfigFile config_file;

    private final String filename = "gameareas.yml";
    private final String folder = "ocf_settings";

    private final List<GameArea> game_areas = new ArrayList<>();

    // -- [[ METHODS ]]

    // -- Public
    public GameAreaManager(OverCrafted master) {
        this.master = master;
        this.config_file = new CustomConfigFile(
            filename,
            folder,
            master,
            true
        );
        config_file.registerConfig();
        loadConfig();
    }

    public void loadConfig() {
        FileConfiguration config = config_file.getConfig();
        List<Map<?, ?>> gmaMapList = config.getMapList("gameareas");

        for (Map<?, ?> gmaMap : gmaMapList) {
            game_areas.add(GameArea.deserialize((Map<String, Object>) gmaMap));
        }
    }

    public void reloadConfig() {
        config_file.reloadConfig();
        loadConfig();
    }

    public void saveConfig() {
        FileConfiguration config = this.config_file.getConfig();

        List<Map<String, Object>> gmaMapList = new ArrayList<>();

        for (GameArea gma : this.game_areas) {
            gmaMapList.add(gma.serialize());
        }

        config.set("gameareas", gmaMapList);
        this.config_file.saveConfig();
    }

    public void addNewGameArea(GameArea gamearea) {
        this.game_areas.add(gamearea);
    }

    public void removeGameArea(GameArea gamearea) {
        this.game_areas.remove(gamearea);
    }

    public GameArea getByName(String name) {
        Optional<GameArea> query_gamearea = game_areas.stream().filter(item -> item.getName().equals(name)).findFirst();
        return query_gamearea.orElse(null);
    }

    public boolean alreadyExists(String name) {
        return this.game_areas.stream().anyMatch(item -> item.getName().equals(name));
    }

    public boolean isEmpty() {
        return this.game_areas.isEmpty();
    }

    public List<GameArea> getGameAreas() { return this.game_areas; }

    // -- Private

}
