package org.cyanx86.managers;

import org.bukkit.entity.Player;

import org.cyanx86.OverCrafted;
import org.cyanx86.classes.GameArea;
import org.cyanx86.classes.GameRound;
import org.cyanx86.classes.OrderDisplayer;
import org.cyanx86.utils.Enums.ListResult;

import java.util.ArrayList;
import java.util.List;
import org.jetbrains.annotations.NotNull;

public class GameRoundManager {

    // -- [[ ATTRIBUTES ]] --

    // -- PUBLIC --

    // -- PRIVATE --
    private final OverCrafted master = OverCrafted.getInstance();

    private GameRound gameround;

    private GameArea gamearea;
    private final List<Player> gamePlayers = new ArrayList<>();

    // -- [[ METHODS ]] --

    // -- PUBLIC --

    // Round
    public boolean startRound() {
        if (this.gamearea == null)
            return false;
        if (this.gamePlayers.isEmpty())
            return false;

        this.gameround = new GameRound(
                this.gamearea,
                this.gamePlayers
        );
        this.gamearea = null;
        this.gamePlayers.clear();

        return true;
    }

    public boolean terminateRound(String reason) {
        return this.gameround.terminateRound(reason);
    }

    public GameRound getGameRound() {
        return this.gameround;
    }

    // GameArea managing
    public void setGameArea(@NotNull GameArea gamearea) {
        this.gamearea = gamearea;
    }

    public GameArea getGameArea() {
        return this.gamearea;
    }

    // Player managing
    public ListResult addPlayer(@NotNull Player player) {
        if (this.gamearea == null)
            return ListResult.ERROR;
        if (this.gamePlayers.size() == this.gamearea.getMaxPlayers())
            return ListResult.FULL_LIST;
        if (this.gamePlayers.contains(player))
            return ListResult.ALREADY_IN;

        this.gamePlayers.add(player);
        return ListResult.SUCCESS;
    }

    public ListResult removePlayer(@NotNull Player player) {
        if (this.gamePlayers.isEmpty())
            return ListResult.EMPTY_LIST;
        if (!this.gamePlayers.remove(player))
            return ListResult.NOT_FOUND;
        else
            return ListResult.SUCCESS;
    }

    public ListResult clearPlayerList() {
        if (this.gamePlayers.isEmpty())
            return ListResult.EMPTY_LIST;

        this.gamePlayers.clear();
        return ListResult.SUCCESS;
    }

    public List<Player> getGamePlayers() {
        return new ArrayList<>(this.gamePlayers);
    }

    // -- PRIVATE --

}
