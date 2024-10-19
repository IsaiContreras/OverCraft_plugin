package org.cyanx86;

import org.bukkit.plugin.java.JavaPlugin;

import org.cyanx86.commands.KitchenCommand;
import org.cyanx86.commands.RecipesCommand;
import org.cyanx86.commands.ReloadSettingsCommand;
import org.cyanx86.commands.RoundCommand;
import org.cyanx86.config.*;
import org.cyanx86.listeners.ManagerPlayerListener;
import org.cyanx86.listeners.MiscellaneousListener;
import org.cyanx86.listeners.NonPlayerListener;
import org.cyanx86.listeners.PlayerListener;
import org.cyanx86.managers.KitchenAreaCreatorAssistantManager;
import org.cyanx86.managers.GameRoundManager;
import org.cyanx86.utils.Messenger;

import java.util.Objects;

public class OverCrafted extends JavaPlugin {

    // -- [[ ATTRIBUTES ]] --

    // -- PUBLIC --
    public static String prefix = "&6[&l&eOverCrafted&6] ";

    // -- PRIVATE --
    private static OverCrafted instance;
    private final String version = getDescription().getVersion();

    private KitchenAreaLoader kitchenAreaLoader;
    private OreBlocksLoader oreBlocks;
    private RecipesBonus recipesBonus;

    private KitchenAreaCreatorAssistantManager kitchenAreaCreatorAssistantManager;
    private GameRoundManager gameRoundManager;

    // -- [[ METHODS ]] --

    // -- PUBLIC --

    // Events
    public void onEnable() {
        instance = this;

        this.kitchenAreaLoader = new KitchenAreaLoader();
        this.oreBlocks = new OreBlocksLoader();
        this.recipesBonus = new RecipesBonus();

        this.kitchenAreaCreatorAssistantManager = new KitchenAreaCreatorAssistantManager();
        this.gameRoundManager = new GameRoundManager();

        this.saveResource("languages/template.yml", false);

        GeneralSettings.getInstance();

        this.setupCommands();
        this.setupEvents();

        Messenger.msgToConsole(
            prefix + "&ePlugin activo. &fVersion: " + version
        );
    }

    public void onDisable() {
        this.kitchenAreaLoader.save();
        Messenger.msgToConsole(
            prefix + "ePlugin desactivado."
        );
    }

    // GameArea managing
    public KitchenAreaCreatorAssistantManager getKitchenAreaCreatorAssistantManager() {
        return this.kitchenAreaCreatorAssistantManager;
    }

    public KitchenAreaLoader getKitchenAreaLoader() {
        return this.kitchenAreaLoader;
    }

    public GameRoundManager getGameRoundManager() {
        return this.gameRoundManager;
    }

    public OreBlocksLoader getOreBlocks() {
        return this.oreBlocks;
    }

    public RecipesBonus getRecipesBonus() { return this.recipesBonus; }

    public static OverCrafted getInstance() {
        return instance;
    }

    // -- PRIVATE --
    private void setupCommands() {
        Objects.requireNonNull(this.getCommand("round")).setExecutor(new RoundCommand());
        Objects.requireNonNull(this.getCommand("kitchen")).setExecutor(new KitchenCommand());
        Objects.requireNonNull(this.getCommand("recipes")).setExecutor(new RecipesCommand());
        Objects.requireNonNull(this.getCommand("reloadsettings")).setExecutor(new ReloadSettingsCommand());
    }

    private void setupEvents() {
        getServer().getPluginManager().registerEvents(new PlayerListener(), this);
        getServer().getPluginManager().registerEvents(new NonPlayerListener(), this);
        getServer().getPluginManager().registerEvents(new ManagerPlayerListener(), this);
        getServer().getPluginManager().registerEvents(new MiscellaneousListener(), this);
    }

}
