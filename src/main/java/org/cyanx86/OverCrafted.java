package org.cyanx86;

import org.bukkit.plugin.java.JavaPlugin;

import org.cyanx86.commands.GameAreaCommand;
import org.cyanx86.commands.MainCommand;
import org.cyanx86.commands.PlayerListCommand;
import org.cyanx86.listeners.ManagerPlayerListener;
import org.cyanx86.listeners.MiscellaneousListener;
import org.cyanx86.listeners.NonPlayerListener;
import org.cyanx86.listeners.PlayerListener;
import org.cyanx86.managers.GameAreaPropertiesAssistantManager;
import org.cyanx86.managers.GameAreaManager;
import org.cyanx86.managers.GameRoundManager;
import org.cyanx86.managers.OreBlocksManager;
import org.cyanx86.utils.Messenger;

import java.util.Objects;

public class OverCrafted extends JavaPlugin {

    // -- [[ ATTRIBUTES ]] --

    // -- PUBLIC --
    public static String prefix = "&6[&l&eOverCrafted&6] ";

    // -- PRIVATE --
    private static OverCrafted instance;
    private final String version = getDescription().getVersion();

    private GameAreaManager gameAreaManager;
    private GameAreaPropertiesAssistantManager gameAreaPropertiesAssistantManager;
    private GameRoundManager gameRoundManager;

    private OreBlocksManager oreBlocks;

    // -- [[ METHODS ]] --

    // -- PUBLIC --

    // Events
    public void onEnable() {
        instance = this;

        gameAreaManager = new GameAreaManager();
        gameAreaPropertiesAssistantManager = new GameAreaPropertiesAssistantManager();
        gameRoundManager = new GameRoundManager();

        oreBlocks = new OreBlocksManager();

        this.setupCommands();
        this.setupEvents();

        Messenger.msgToConsole(
            prefix + "&ePlugin activo. &fVersion: " + version
        );
    }

    public void onDisable() {
        gameAreaManager.saveConfig();
        Messenger.msgToConsole(
            prefix + "ePlugin desactivado."
        );
    }

    // GameArea managing
    public GameAreaPropertiesAssistantManager getGameAreaPropertiesAssistantManager() {
        return this.gameAreaPropertiesAssistantManager;
    }

    public GameAreaManager getGameAreaManager() {
        return this.gameAreaManager;
    }

    public GameRoundManager getGameRoundManager() {
        return this.gameRoundManager;
    }

    public OreBlocksManager getOreBlocks() {
        return this.oreBlocks;
    }

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
