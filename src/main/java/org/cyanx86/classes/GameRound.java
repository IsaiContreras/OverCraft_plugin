package org.cyanx86.classes;

import org.bukkit.entity.Player;
import org.cyanx86.OverCrafted;
import org.cyanx86.utils.Enums.ListResult;

import java.util.HashMap;
import java.util.Map;

public class GameRound {

    // -- [[ ATRIBUTES ]] --

    // -- Public

    // -- Private
    private final OverCrafted master;

    private final GameArea gamearea;
    private final Map<String, Player> players = new HashMap<>();        // TODO: Manejarlo con una clase que se llame GamePlayerManager

    // -- [[ METHODS ]] --

    // -- Public
    public GameRound(OverCrafted master, GameArea gamearea) {
        this.master = master;
        this.gamearea = gamearea;
    }

    // Player Map managing
    public ListResult addPlayer(Player player) {
        if (this.players.size() == 4)
            return ListResult.FULL_LIST;
        if (this.players.containsKey(player.getName()))
            return ListResult.ALREADY_IN;
        this.players.put(player.getName(), player);
        return ListResult.SUCCESS;
    }

    public ListResult removePlayer(String name) {
        if (this.players.isEmpty())
            return ListResult.EMPTY_LIST;
        if (!this.players.containsKey(name))
            return ListResult.NOT_FOUND;
        this.players.remove(name);
        return ListResult.SUCCESS;
    }

    public ListResult clearPlayers() {
        if (this.players.isEmpty())
            return ListResult.EMPTY_LIST;
        this.players.clear();;
        return ListResult.SUCCESS;
    }

    public Map<String, Player> getPlayers() { return this.players; }

    // -- Private

}
