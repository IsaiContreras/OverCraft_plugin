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
    private String filename;
    private String foldername;

    private FileConfiguration fileConfig;
    private File file;

    private final boolean mustCreate;

    // -- [[ METHODS ]] --

    // -- PUBLIC --

    // -- PROTECTED --
    protected CustomConfigFile(@NotNull String filename, String foldername, boolean must_create) {
        this.filename = filename;
        this.foldername = foldername;
        this.mustCreate = must_create;
    }

    protected void changeDir(@NotNull String filename, String foldername) {
        this.filename = filename;
        this.foldername = foldername;
    }

    // -- PRIVATE --
    protected boolean registerConfig() {
        if (this.foldername != null)
            this.file = new File(this.master.getDataFolder() + File.separator + this.foldername, this.filename);
        else
            this.file = new File(this.master.getDataFolder(), this.filename);

        boolean fileCreated = false;
        if (!this.file.exists()) {
            if (this.mustCreate) {
                try {
                    fileCreated = this.file.createNewFile();
                } catch (IOException e) {
                    return false;
                }
            } else {
                if (this.foldername != null)
                    this.master.saveResource(this.foldername + File.separator + this.filename, false);
                else
                    this.master.saveResource(this.filename, false);
            }
        }

        this.fileConfig = new YamlConfiguration();
        try {
            this.fileConfig.load(this.file);
        } catch(IOException | InvalidConfigurationException e) {
            return false;
        }
        return !fileCreated;
    }

    protected void saveConfig() {
        try {
            this.fileConfig.save(this.file);
        } catch (IOException ignored) { }
    }

    protected FileConfiguration getConfig() {
        if (this.fileConfig == null)
            reloadConfig();
        return this.fileConfig;
    }

    protected boolean reloadConfig() {
        if (this.fileConfig == null) {
            if (this.foldername != null)
                this.file = new File(this.master.getDataFolder() + File.separator + this.foldername, this.filename);
            else
                this.file = new File(this.master.getDataFolder(), this.filename);
        }

        this.fileConfig = YamlConfiguration.loadConfiguration(this.file);
        if (this.file != null) {
            YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(this.file);
            this.fileConfig.setDefaults(defConfig);
            return true;
        }
        else
            return false;
    }

    protected abstract void load();
    protected abstract boolean reload();
    protected abstract void save();

}
