package org.cyanx86.managers;

import org.bukkit.entity.Player;

import org.cyanx86.OverCrafted;
import org.cyanx86.classes.PlayerState;

import java.util.*;
import org.jetbrains.annotations.NotNull;

public class GamePlayersManager {

    // -- [[ ATTRIBUTES ]] --

    // -- Public

    // -- Private
    private final OverCrafted master = OverCrafted.getInstance();

    private final List<PlayerState> players = new ArrayList<>();

    // -- [[ METHODS ]] --

    // -- Public
    public GamePlayersManager(@NotNull List<Player> players) {
        Collections.shuffle(players);

        for (Player player : players)
            this.players.add(new PlayerState(player));
    }

    public List<PlayerState> getPlayerStates() { return this.players; }

    public PlayerState getPlayerState(@NotNull Player player) {
        Optional<PlayerState> query = this.players.stream().filter(item -> item.equal(player)).findFirst();
        return query.orElse(null);
    }

    public int getPlayerIndex(@NotNull PlayerState player) {
        return this.players.indexOf(player) + 1;
    }

    public boolean anyPlayer(@NotNull Player player) {
        return this.players.stream().anyMatch(item -> item.equal(player));
    }

    public void sendMessageToPlayers(@NotNull String message) {
        for (PlayerState playerstate : this.players)
            playerstate.sendMessageToPlayer(OverCrafted.prefix + message);
    }

    // -- Private

}
