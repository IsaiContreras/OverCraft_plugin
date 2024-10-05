package org.cyanx86.managers;

import org.bukkit.entity.Player;

import org.cyanx86.OverCrafted;
import org.cyanx86.classes.PlayerState;

import java.util.*;

import org.cyanx86.utils.Enums.ListResult;
import org.jetbrains.annotations.NotNull;

public class GamePlayersManager {

    // -- [[ ATTRIBUTES ]] --

    // -- PUBLIC --

    // -- PRIVATE --
    private final List<PlayerState> players = new ArrayList<>();

    // -- [[ METHODS ]] --

    // -- PUBLIC --
    public GamePlayersManager(@NotNull List<Player> players) {
        Collections.shuffle(players);
        for (Player player : players)
            this.players.add(new PlayerState(player));
    }

    public List<PlayerState> getPlayerStates() {
        return new ArrayList<>(this.players);
    }

    public PlayerState getPlayerState(@NotNull Player player) {
        Optional<PlayerState> query = this.players.stream()
                .filter(item -> item.equal(player))
                .findFirst();
        return query.orElse(null);
    }

    public int getPlayerIndex(@NotNull PlayerState player) {
        return this.players.indexOf(player) + 1;
    }

    public ListResult removePlayer(@NotNull Player player) {
        PlayerState playerstate = this.getPlayerState(player);
        if (playerstate == null)
            return ListResult.NOT_FOUND;

        if (!this.players.remove(playerstate))
            return ListResult.ERROR;
        else return ListResult.SUCCESS;
    }

    public boolean anyPlayer(@NotNull Player player) {
        return this.players.stream().anyMatch(item -> item.equal(player));
    }

    public void sendMessageToPlayers(@NotNull String message) {
        for (PlayerState playerstate : this.players)
            playerstate.sendMessageToPlayer(OverCrafted.prefix + message);
    }

    public void sendTitleToPlayers(@NotNull String message1, @NotNull String message2, int fadeIn, int time, int fadeOut) {
        for (PlayerState playerState : this.players)
            playerState.sendTitleToPlayer(message1, message2, fadeIn, time, fadeOut);
    }

    public void sendActionBarToPlayers(@NotNull String message) {
        for (PlayerState playerState : this.players)
            playerState.sendActionBarToPlayer(message);
    }

    // -- PRIVATE --

}
