package org.cyanx86;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.cyanx86.commands.MainCommand;
import org.cyanx86.utils.Messenger;

import java.util.ArrayList;
import java.util.List;

public class OverCrafted extends JavaPlugin {

    // -- [[ ATTRIBUTES ]] --

    // -- Public
    public static String prefix = "&8[&c&lOverCrafted&8] ";

    // -- Private
    private final String version = getDescription().getVersion();
    private final List<Player> game_players = new ArrayList<>();

    // -- [[ METHODS ]] --

    // -- Public
    public void onEnable() {
        this.setupCommands();

        Messenger.sendConsoleMessage(
            prefix + "&ePlugin activo. &fVersion: " + version
        );
    }

    public void onDisable() {
        Messenger.sendConsoleMessage(
            prefix + "ePlugin desactivado."
        );
    }

    public boolean addPlayer(Player player) {
        return this.game_players.add(player);
    }

    public boolean removePlayer(Player player) {
        return this.game_players.remove(player);
    }

    public List<Player> getGamePlayers() {
        return this.game_players;
    }

    // -- Private
    private void setupCommands() {
        this.getCommand("overcrafted").setExecutor(new MainCommand(this));
    }

    private void setupEvents() {

    }

}
