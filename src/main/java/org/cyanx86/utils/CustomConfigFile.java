package org.cyanx86.utils;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import org.cyanx86.OverCrafted;

import java.io.File;
import java.io.IOException;
import org.jetbrains.annotations.NotNull;

public abstract class CustomConfigFile {

    // -- [[ ATTRIBUTES ]] --

    // -- PUBLIC --

    // -- PRIVATE --
    private final OverCrafted master = OverCrafted.getInstance();
    private final String filename;
    private final String foldername;

    private FileConfiguration fileConfig;
    private File file;

    private final boolean mustCreate;

    // -- [[ METHODS ]] --

    // -- PUBLIC --
    protected CustomConfigFile(@NotNull String filename, String foldername, boolean must_create) {
        this.filename = filename;
        this.foldername = foldername;
        this.mustCreate = must_create;
    }
    protected String getPath() { return this.filename; }

    // -- PRIVATE --
    protected boolean registerConfig() {
        if (foldername != null)
            file = new File(master.getDataFolder() + File.separator + foldername, filename);
        else
            file = new File(master.getDataFolder(), filename);

        boolean fileCreated = false;
        if (!file.exists()) {
            if (mustCreate) {
                try {
                    fileCreated = file.createNewFile();
                } catch (IOException e) {
                    return false;
                }
            } else {
                if (foldername != null)
                    master.saveResource(foldername + File.separator + filename, false);
                else
                    master.saveResource(filename, false);
            }
        }

        fileConfig = new YamlConfiguration();
        try {
            fileConfig.load(file);
        } catch(IOException | InvalidConfigurationException e) {
            return false;
        }
        return !fileCreated;
    }

    protected void saveConfig() {
        try {
            fileConfig.save(file);
        } catch (IOException ignored) { }
    }

    protected FileConfiguration getConfig() {
        if (fileConfig == null)
            reloadConfig();
        return fileConfig;
    }

    protected void reloadConfig() {
        if (fileConfig == null) {
            if (foldername != null)
                file = new File(master.getDataFolder() + File.separator + foldername, filename);
            else
                file = new File(master.getDataFolder(), filename);
        }

        fileConfig = YamlConfiguration.loadConfiguration(file);
        if (file != null) {
            YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(file);
            fileConfig.setDefaults(defConfig);
        }
    }

    protected abstract void load();
    protected abstract void reload();
    protected abstract void save();

}
