package org.cyanx86.config;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.cyanx86.utils.CustomConfigFile;
import org.cyanx86.utils.DataFormatting;

import javax.xml.crypto.Data;
import java.util.ArrayList;
import java.util.List;

public class Locale extends CustomConfigFile {


    // -- [[ ATTRIBUTES ]] --

    // -- PUBLIC --

    // -- PRIVATE --
    private static Locale instance;

    // -- [[ METHODS ]] --

    // -- PUBLIC --
    public Locale(String language) {
        super(
            language + ".yml",
            "languages",
            false
        );
        this.registerConfig();
    }

    public String getStr(String path) {
        FileConfiguration config = this.getConfig();
        String message;

        try {
            message = (String)config.get(path);
        } catch(ClassCastException e) {
            message = GeneralSettings.getInstance().getInvalidMessagePath().replace("%path%", path);
        }

        if (message == null)
            message = GeneralSettings.getInstance().getInvalidMessagePath().replace("%path%", path);

        return message;
    }

    public List<String> getStrArray(String path) {
        FileConfiguration config = this.getConfig();
        List<String> array;

        try {
            array = (List<String>)config.get(path);
        } catch(ClassCastException ignored) {
            return null;
        }

        return array;
    }

    public String getMatName(Material material) {
        FileConfiguration config = this.getConfig();
        String name;

        try {
            name = (String)config.get("item-materials." + material.name());
        } catch (NullPointerException | ClassCastException e) {
            name = DataFormatting.formatMaterialToString(material.name());
        }

        return name;
    }

    // -- PROTECTED --
    @Override
    protected void load() {

    }

    @Override
    protected void reload() {
        this.reloadConfig();
        this.load();
    }

    @Override
    protected void save() {

    }

    // -- PRIVATE --

}
