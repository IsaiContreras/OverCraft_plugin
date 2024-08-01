package org.cyanx86;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.cyanx86.classes.GameArea;
import org.cyanx86.commands.GameAreaCommand;
import org.cyanx86.commands.MainCommand;
import org.cyanx86.listeners.PlayerListener;
import org.cyanx86.utils.Messenger;
import org.cyanx86.utils.Enums;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class OverCrafted extends JavaPlugin {

    // -- [[ ATTRIBUTES ]] --

    // -- Public
    public static String prefix = "&8[&c&lOverCrafted&8] ";

    // -- Private
    private final String version = getDescription().getVersion();
    private final List<Player> game_players = new ArrayList<>();
    private final List<GameArea> game_areas = new ArrayList<>();

    private int cornerIndex = 0;
    private Location gaCorner1 = null;
    private Location gaCorner2 = null;

    // -- [[ METHODS ]] --

    // -- Public
    public void onEnable() {
        this.setupCommands();
        this.setupEvents();

        Messenger.msgToConsole(
                prefix + "&ePlugin activo. &fVersion: " + version
        );
    }

    public void onDisable() {
        Messenger.msgToConsole(
                prefix + "ePlugin desactivado."
        );
    }

    public Enums.ListResult addPlayer(Player player) {
        if (this.game_players.size() == 4)
            return Enums.ListResult.FULL_LIST;
        if (this.game_players.contains(player)) {
            return Enums.ListResult.ALREADY_IN;
        }
        this.game_players.add(player);
        return Enums.ListResult.SUCCESS;
    }

    public Enums.ListResult removePlayer(Player player) {
        if (this.game_players.isEmpty()) {
            return Enums.ListResult.EMPTY_LIST;
        }
        if (!this.game_players.remove(player))
            return Enums.ListResult.NOT_FOUND;
        else
            return Enums.ListResult.SUCCESS;
    }

    public Enums.ListResult clearPlayerList() {
        if (game_players.isEmpty()) {
            return Enums.ListResult.EMPTY_LIST;
        }
        this.game_players.clear();
        return Enums.ListResult.SUCCESS;
    }

    public List<Player> getGamePlayers() {
        return this.game_players;
    }

    public Enums.ListResult addGameArea(String name) {
        if (game_areas.stream().anyMatch(item -> item.getName().equals(name))) {
            return Enums.ListResult.ALREADY_IN;
        }
        GameArea gamearea = new GameArea(
            name,
            gaCorner1,
            gaCorner2
        );
        this.game_areas.add(gamearea);
        return Enums.ListResult.SUCCESS;
    }

    public Enums.ListResult removeGameArea(String name) {
        if (game_areas.isEmpty()) {
            return Enums.ListResult.EMPTY_LIST;
        }

        GameArea gamearea;
        Optional<GameArea> query_gamearea = game_areas.stream().filter(item -> item.getName().equals(name)).findFirst();
        if (query_gamearea.isEmpty()) {
            return Enums.ListResult.NOT_FOUND;
        }

        gamearea = query_gamearea.get();

        this.game_areas.remove(gamearea);
        return Enums.ListResult.SUCCESS;
    }

    public List<GameArea> getGameAreas() {
        return this.game_areas;
    }

    public void setCornerIndex(int index) {
        this.cornerIndex = index;
    }

    public int getCornerIndex() { return this.cornerIndex; }

    public boolean setCorner(int index, Location corner) {
        switch (index) {
            case 1:
                this.gaCorner1 = corner;
                break;
            case 2:
                this.gaCorner2 = corner;
                break;
            default:
                return false;
        }
        return true;
    }

    public Location getCorner(int index) {
        return switch (index) {
            case 1 -> this.gaCorner1;
            case 2 -> this.gaCorner2;
            default -> null;
        };
    }

    public void resetCorners() {
        this.gaCorner1 = null;
        this.gaCorner2 = null;
    }

    // -- Private
    private void setupCommands() {
        Objects.requireNonNull(this.getCommand("overcrafted")).setExecutor(new MainCommand(this));
        Objects.requireNonNull(this.getCommand("gamearea")).setExecutor(new GameAreaCommand(this));
    }

    private void setupEvents() {
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
    }

}
