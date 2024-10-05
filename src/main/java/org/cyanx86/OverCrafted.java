package org.cyanx86;

import org.bukkit.plugin.java.JavaPlugin;

import org.cyanx86.commands.GameAreaCommand;
import org.cyanx86.commands.MainCommand;
import org.cyanx86.commands.PlayerListCommand;
import org.cyanx86.config.RecipesBonus;
import org.cyanx86.config.RoundSettings;
import org.cyanx86.listeners.ManagerPlayerListener;
import org.cyanx86.listeners.MiscellaneousListener;
import org.cyanx86.listeners.NonPlayerListener;
import org.cyanx86.listeners.PlayerListener;
import org.cyanx86.managers.GameAreaPropertiesAssistantManager;
import org.cyanx86.config.GameAreaLoader;
import org.cyanx86.managers.GameRoundManager;
import org.cyanx86.config.OreBlocksLoader;
import org.cyanx86.utils.Messenger;

import java.util.Objects;

public class OverCrafted extends JavaPlugin {

    // -- [[ ATTRIBUTES ]] --

    // -- PUBLIC --
    public static String prefix = "&6[&l&eOverCrafted&6] ";

    // -- PRIVATE --
    private static OverCrafted instance;
    private final String version = getDescription().getVersion();

    private GameAreaLoader gameAreaManager;
    private OreBlocksLoader oreBlocks;
    private RecipesBonus recipesBonus;

    private GameAreaPropertiesAssistantManager gameAreaPropertiesAssistantManager;
    private GameRoundManager gameRoundManager;

    // -- [[ METHODS ]] --

    // -- PUBLIC --

    // Events
    public void onEnable() {
        instance = this;

        gameAreaManager = new GameAreaLoader();
        oreBlocks = new OreBlocksLoader();
        recipesBonus = new RecipesBonus();

        gameAreaPropertiesAssistantManager = new GameAreaPropertiesAssistantManager();
        gameRoundManager = new GameRoundManager();

        RoundSettings.getInstance();

        this.setupCommands();
        this.setupEvents();

        Messenger.msgToConsole(
            prefix + "&ePlugin activo. &fVersion: " + version
        );
    }

    public void onDisable() {
        gameAreaManager.save();
        Messenger.msgToConsole(
            prefix + "ePlugin desactivado."
        );
    }

    // GameArea managing
    public GameAreaPropertiesAssistantManager getGameAreaPropertiesAssistantManager() {
        return this.gameAreaPropertiesAssistantManager;
    }

    public GameAreaLoader getGameAreaManager() {
        return this.gameAreaManager;
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
        Objects.requireNonNull(this.getCommand("overcrafted")).setExecutor(new MainCommand());
        Objects.requireNonNull(this.getCommand("playerlist")).setExecutor(new PlayerListCommand());
        Objects.requireNonNull(this.getCommand("gamearea")).setExecutor(new GameAreaCommand());
    }

    private void setupEvents() {
        getServer().getPluginManager().registerEvents(new PlayerListener(), this);
        getServer().getPluginManager().registerEvents(new NonPlayerListener(), this);
        getServer().getPluginManager().registerEvents(new ManagerPlayerListener(), this);
        getServer().getPluginManager().registerEvents(new MiscellaneousListener(), this);
    }

}
