package org.cyanx86.config;

import org.bukkit.configuration.file.FileConfiguration;
import org.cyanx86.utils.CustomConfigFile;
import org.cyanx86.utils.Defaults;

public class GeneralSettings extends CustomConfigFile {

    // -- [[ ATTRIBUTES ]] --

    // -- PUBLIC --

    // -- PRIVATE --
    private static GeneralSettings instance;

    private Locale locale;

    private String language;
    private String invalid_message_path;



    // -- [[ METHODS ]] --

    // -- PUBLIC --
    public static GeneralSettings getInstance() {
        if (instance == null)
            instance = new GeneralSettings();
        return instance;
    }

    public Locale getLocale() {
        return this.locale;
    }

    public String getLanguage() {
        return this.language;
    }

    public String getInvalidMessagePath() {
        return this. invalid_message_path;
    }

    // -- PROTECTED --
    @Override
    protected void load() {
        FileConfiguration config = this.getConfig();

        try { if (config.get("language") != null)
            this.language = (String)config.get("language");
        } catch (NullPointerException | ClassCastException ignored) { }
        try { if (config.get("invalid-message-path") != null)
            this.invalid_message_path = (String)config.get("invalid-message-path");
        } catch (NullPointerException | ClassCastException ignored) { }
    }

    @Override
    protected void reload() {
        this.reloadConfig();
        this.load();
    }

    @Override
    protected void save() {
        FileConfiguration config = this.getConfig();

        config.set("language", this.language);
        config.set("invalid-message-path", this.invalid_message_path);

        this.saveConfig();
    }

    // -- PRIVATE --
    private GeneralSettings() {
        super(
            "general_settings.yml",
            null,
            true
        );
        this.loadDefault();
        if (this.registerConfig())
            this.load();
        else this.save();
        this.locale = new Locale(this.getLanguage());
    }

    private void loadDefault() {
        this.language = Defaults.GeneralSettings.language;
        this.invalid_message_path = Defaults.GeneralSettings.invalid_message_path;
    }

}
