package org.cyanx86.managers;

import org.bukkit.entity.Player;
import org.cyanx86.OverCrafted;
import org.cyanx86.classes.GameArea;
import org.cyanx86.classes.GameRound;
import org.cyanx86.utils.Enums;

import java.util.ArrayList;
import java.util.List;

public class GameRoundManager {

    // -- [[ ATTRIBUTES ]] --

    // -- Public

    // -- Private
    private final OverCrafted master;

    private GameRound gameround;

    private GameArea gamearea;
    private final List<Player> gamePlayers = new ArrayList<>();

    // -- [[ METHODS ]] --

    // -- Public
    public GameRoundManager(OverCrafted master) {
        this.master = master;
    }

    // GameArea managing
    public void setGameArea(GameArea gamearea) {
        this.gamearea = gamearea;
    }

    public GameArea getGameArea() {
        return this.gamearea;
    }

    // Player managing
    public Enums.ListResult addPlayer(Player player) {
        if (this.gamePlayers.size() == 4)
            return Enums.ListResult.FULL_LIST;
        if (this.gamePlayers.contains(player))
            return Enums.ListResult.ALREADY_IN;
        this.gamePlayers.add(player);
        return Enums.ListResult.SUCCESS;
    }

    public Enums.ListResult removePlayer(Player player) {
        if (this.gamePlayers.isEmpty())
            return Enums.ListResult.EMPTY_LIST;
        if (!this.gamePlayers.remove(player))
            return Enums.ListResult.NOT_FOUND;
        else
            return Enums.ListResult.SUCCESS;
    }

    public Enums.ListResult clearPlayerList() {
        if (gamePlayers.isEmpty()) {
            return Enums.ListResult.EMPTY_LIST;
        }
        this.gamePlayers.clear();
        return Enums.ListResult.SUCCESS;
    }

    public List<Player> getGamePlayers() {
        return this.gamePlayers;
    }

    // -- Private

}
