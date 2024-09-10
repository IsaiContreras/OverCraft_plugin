package org.cyanx86.managers;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import org.cyanx86.OverCrafted;

import java.io.File;
import java.io.IOException;
import org.jetbrains.annotations.NotNull;

public class CustomConfigFile {

    // -- [[ ATTRIBUTES ]] --

    // -- Public

    // -- Private
    private final OverCrafted master = OverCrafted.getInstance();
    private final String filename;
    private final String foldername;

    private FileConfiguration fileConfig;
    private File file;

    private boolean mustCreate;

    // -- [[ METHODS ]] --

    // -- Public
    public CustomConfigFile(@NotNull String filename, String foldername, boolean must_create) {
        this.filename = filename;
        this.foldername = foldername;
        this.mustCreate = must_create;
    }
    public String getPath() { return this.filename; }

    // -- Private
    public void registerConfig() {
        if (foldername != null) {
            file = new File(master.getDataFolder() + File.separator + foldername, filename);
        } else {
            file = new File(master.getDataFolder(), filename);
        }

        if (!file.exists()) {
            if (mustCreate) {
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                if (foldername != null) {
                    master.saveResource(foldername + File.separator + filename, false);
                } else {
                    master.saveResource(filename, false);
                }
            }
        }

        fileConfig = new YamlConfiguration();
        try {
            fileConfig.load(file);
        } catch(IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    public void saveConfig() {
        try {
            fileConfig.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public FileConfiguration getConfig() {
        if (fileConfig == null) {
            reloadConfig();
        }
        return fileConfig;
    }

    public void reloadConfig() {
        if (fileConfig == null) {
            if (foldername != null) {
                file = new File(master.getDataFolder() + File.separator + foldername, filename);
            } else {
                file = new File(master.getDataFolder(), filename);
            }
        }

        fileConfig = YamlConfiguration.loadConfiguration(file);
        if (file != null) {
            YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(file);
            fileConfig.setDefaults(defConfig);
        }
    }

}
