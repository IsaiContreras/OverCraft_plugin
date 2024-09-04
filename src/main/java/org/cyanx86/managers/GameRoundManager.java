package org.cyanx86.managers;

import org.bukkit.entity.Player;

import org.cyanx86.OverCrafted;
import org.cyanx86.classes.GameArea;
import org.cyanx86.classes.GameRound;
import org.cyanx86.utils.Enums.ListResult;

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

    // Round
    public void startRound() {
        this.gameround = new GameRound(
            this.master,
            this.gamearea,
            this.gamePlayers
        );
    }

    public GameRound getGameRound() {
        return gameround;
    }

    // GameArea managing
    public void setGameArea(GameArea gamearea) {
        this.gamearea = gamearea;
    }

    public GameArea getGameArea() {
        return this.gamearea;
    }

    // Player managing
    public ListResult addPlayer(Player player) {
        if (gamearea == null)
            return ListResult.ERROR;
        if (this.gamePlayers.size() == gamearea.getMaxPlayers())
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

    // -- Private

}
