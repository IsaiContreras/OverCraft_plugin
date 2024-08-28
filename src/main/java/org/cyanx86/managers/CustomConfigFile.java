package org.cyanx86.managers;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.cyanx86.OverCrafted;

import java.io.File;
import java.io.IOException;

public class CustomConfigFile {

    // -- [[ ATTRIBUTES ]] --

    // -- Public

    // -- Private
    private final OverCrafted master;
    private final String filename;
    private final String foldername;

    private FileConfiguration file_config;
    private File file;

    private boolean must_create;

    // -- [[ METHODS ]] --

    // -- Public
    public CustomConfigFile(String filename, String foldername, OverCrafted master, boolean must_create) {
        this.filename = filename;
        this.foldername = foldername;
        this.master = master;
        this.must_create = must_create;
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
            if (must_create) {
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

        file_config = new YamlConfiguration();
        try {
            file_config.load(file);
        } catch(IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    public void saveConfig() {
        try {
            file_config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public FileConfiguration getConfig() {
        if (file_config == null) {
            reloadConfig();
        }
        return file_config;
    }

    public void reloadConfig() {
        if (file_config == null) {
            if (foldername != null) {
                file = new File(master.getDataFolder() + File.separator + foldername, filename);
            } else {
                file = new File(master.getDataFolder(), filename);
            }
        }

        file_config = YamlConfiguration.loadConfiguration(file);
        if (file != null) {
            YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(file);
            file_config.setDefaults(defConfig);
        }
    }

}
