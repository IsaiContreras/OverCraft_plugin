package org.cyanx86;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import org.cyanx86.classes.GameRound;
import org.cyanx86.commands.GameAreaCommand;
import org.cyanx86.commands.MainCommand;
import org.cyanx86.commands.PlayerListCommand;
import org.cyanx86.listeners.PlayerListener;
import org.cyanx86.managers.GameAreaCornerAssistantManager;
import org.cyanx86.managers.GameAreaManager;
import org.cyanx86.managers.GameRoundManager;
import org.cyanx86.utils.Messenger;
import org.cyanx86.utils.Enums.ListResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class OverCrafted extends JavaPlugin {

    // -- [[ ATTRIBUTES ]] --

    // -- Public
    public static String prefix = "&8[&c&lOverCrafted&8] ";

    // -- Private
    private final String version = getDescription().getVersion();

    private GameAreaManager gameAreaManager;
    private GameAreaCornerAssistantManager gacaManager;
    private GameRoundManager gameRoundManager;

    // -- [[ METHODS ]] --

    // -- Public

    // Events
    public void onEnable() {
        this.setupCommands();
        this.setupEvents();
        gameAreaManager = new GameAreaManager(this);
        gacaManager = new GameAreaCornerAssistantManager(this);
        gameRoundManager = new GameRoundManager(this);

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
    public GameAreaCornerAssistantManager getGacaManager() {
        return this.gacaManager;
    }

    public GameAreaManager getGameAreaManager() {
        return this.gameAreaManager;
    }

    public GameRoundManager getGameRoundManager() {
        return this.gameRoundManager;
    }

    // -- Private
    private void setupCommands() {
        Objects.requireNonNull(this.getCommand("overcrafted")).setExecutor(new MainCommand(this));
        Objects.requireNonNull(this.getCommand("playerlist")).setExecutor(new PlayerListCommand(this));
        Objects.requireNonNull(this.getCommand("gamearea")).setExecutor(new GameAreaCommand(this));
    }

    private void setupEvents() {
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
    }

}
