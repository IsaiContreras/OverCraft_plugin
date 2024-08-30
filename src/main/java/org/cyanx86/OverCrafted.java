package org.cyanx86;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import org.cyanx86.classes.GameArea;
import org.cyanx86.classes.GameAreaCornerAssistant;
import org.cyanx86.commands.GameAreaCommand;
import org.cyanx86.commands.MainCommand;
import org.cyanx86.commands.PlayerListCommand;
import org.cyanx86.listeners.PlayerListener;
import org.cyanx86.managers.GameAreaCornerAssistantManager;
import org.cyanx86.managers.GameAreaManager;
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

    private final List<Player> gamePlayers = new ArrayList<>();

    // -- [[ METHODS ]] --

    // -- Public

    // Events
    public void onEnable() {
        this.setupCommands();
        this.setupEvents();
        gameAreaManager = new GameAreaManager(this);
        gacaManager = new GameAreaCornerAssistantManager(this);

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

    // Player managing
    public ListResult addPlayer(Player player) {
        if (this.gamePlayers.size() == 4)
            return ListResult.FULL_LIST;
        if (this.gamePlayers.contains(player))
            return ListResult.ALREADY_IN;
        this.gamePlayers.add(player);
        return ListResult.SUCCESS;
    }

    public ListResult removePlayer(Player player) {
        if (this.gamePlayers.isEmpty())
            return ListResult.EMPTY_LIST;
        if (!this.gamePlayers.remove(player))
            return ListResult.NOT_FOUND;
        else
            return ListResult.SUCCESS;
    }

    public ListResult clearPlayerList() {
        if (gamePlayers.isEmpty()) {
            return ListResult.EMPTY_LIST;
        }
        this.gamePlayers.clear();
        return ListResult.SUCCESS;
    }

    public List<Player> getGamePlayers() {
        return this.gamePlayers;
    }

    // GameArea managing
    public ListResult signPlayerAssistant(Player player) {
        return this.gacaManager.signInAssistant(player);
    }

    public ListResult logoutPlayerAssistant(Player player) {
        return this.gacaManager.eraseAssistant(player);
    }

    public GameAreaCornerAssistant getAssistantByName(String name) {
        return this.gacaManager.getAssistantByName(name);
    }

    public ListResult addGameArea(String name, Location gaCorner1, Location gaCorner2) {
        return this.gameAreaManager.addNewGameArea(
            name,
            gaCorner1,
            gaCorner2
        );
    }

    public ListResult removeGameArea(String name) {
        return this.gameAreaManager.removeGameArea(name);
    }

    public GameArea getGameAreaByName(String name) {
        return this.gameAreaManager.getByName(name);
    }

    public List<GameArea> getGameAreas() {
        return this.gameAreaManager.getGameAreas();
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
