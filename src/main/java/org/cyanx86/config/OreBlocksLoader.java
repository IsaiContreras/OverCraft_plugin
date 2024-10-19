package org.cyanx86.config;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.cyanx86.utils.CustomConfigFile;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class OreBlocksLoader extends CustomConfigFile {

    // -- [[ ATTRIBUTES ]] --

    // -- PUBLIC --

    // -- PRIVATE --
    private final Map<Material, Material> oreList = new HashMap<>();

    // -- [[ METHODS ]]

    // -- PUBLIC --
    public OreBlocksLoader() {
        super(
            "ore_blocks.yml",
            null,
            true
        );
        if (this.registerConfig())
            this.load();
    }

    // Read/Write Methods
    @Override
    protected void load() {
        FileConfiguration config = this.getConfig();
        Map<String, Object> read;
        try {
             read = Objects.requireNonNull(
                config.getConfigurationSection("ore_blocks")
            ).getValues(false);
        } catch (NullPointerException e) {
            return;
        }

        for (Map.Entry<String, Object> item : read.entrySet()) {
            Material key;
            Material value;

            try {
                key = Material.valueOf(item.getKey());
                value = Material.valueOf((String)item.getValue());
            } catch (IllegalArgumentException e) {
                continue;
            }

            this.oreList.put(key, value);
        }
    }

    @Override
    public boolean reload() {
        if(!this.reloadConfig())
            return false;
        this.load();
        return true;
    }

    @Override
    protected void save() { }

    // List managing
    public Map<Material, Material> getOreMap() {
        return new HashMap<>(this.oreList);
    }

    // -- PRIVATE --

}
