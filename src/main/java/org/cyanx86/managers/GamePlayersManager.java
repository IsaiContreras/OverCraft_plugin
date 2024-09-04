package org.cyanx86.managers;

import org.bukkit.entity.Player;

import org.cyanx86.classes.PlayerState;

import java.util.*;

public class GamePlayersManager {

    // -- [[ ATTRIBUTES ]] --

    // -- Public

    // -- Private
    private List<Player> players;
    private Map<UUID, PlayerState> playerStates;

    // -- [[ METHODS ]] --

    // -- Public
    public GamePlayersManager(List<Player> players) {
        this.players = players;
        Collections.shuffle(this.players);
        playerStates = new HashMap<>();

        for (Player player : this.players) {
            playerStates.put(
                player.getUniqueId(),
                new PlayerState(player.getUniqueId())
            );
        }
    }

    public List<Player> getPlayers() {
        return this.players;
    }

    public PlayerState getPlayerState(Player player) {
        return playerStates.get(player.getUniqueId());
    }

    public int getPlayerIndex(Player player) {
        return (players.indexOf(player) + 1);
    }

    public boolean isPlayerInGame(Player player) {
        return this.players.contains(player);
    }

    // -- Private

}
