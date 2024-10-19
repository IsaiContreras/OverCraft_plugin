package org.cyanx86.config;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import org.cyanx86.utils.CustomConfigFile;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class RecipesBonus extends CustomConfigFile {

    // -- [[ ATTRIBUTES ]] --

    // -- PUBLIC --

    // -- PRIVATE --
    private final Map<Material, Integer> bonusList = new HashMap<>();

    // -- [[ METHODS ]] --

    // -- PUBLIC --
    public RecipesBonus() {
        super(
            "recipes_bonus.yml",
            null,
            true
        );
        if (this.registerConfig())
            this.load();
    }

    @Override
    protected void load() {
        FileConfiguration config = this.getConfig();
        Map<String, Object> read;

        try {
            read = Objects.requireNonNull(
                config.getConfigurationSection("recipes_bonus")
            ).getValues(false);
        } catch (NullPointerException e) {
            return;
        }

        for (Map.Entry<String, Object> item : read.entrySet()) {
            Material key;
            try {
                key = Material.valueOf(item.getKey());
            } catch (IllegalArgumentException e) {
                continue;
            }
            int value = (int)item.getValue();

            this.bonusList.put(key, value);
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

    public int getBonusValue(Material material) {
        return this.bonusList.get(material) != null ? this.bonusList.get(material) : 0;
    }

    // -- PRIVATE --

}
