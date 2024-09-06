package org.cyanx86.managers;

import org.bukkit.entity.Player;

import org.cyanx86.OverCrafted;
import org.cyanx86.classes.PlayerState;

import java.util.*;

public class GamePlayersManager {

    // -- [[ ATTRIBUTES ]] --

    // -- Public

    // -- Private
    private final OverCrafted master = OverCrafted.getInstance();

    private final List<Player> players = new ArrayList<>();
    private final Map<UUID, PlayerState> playerStates = new HashMap<>();

    // -- [[ METHODS ]] --

    // -- Public
    public GamePlayersManager(List<Player> players) {
        this.players.addAll(players);
        Collections.shuffle(this.players);

        for (Player player : this.players) {
            playerStates.put(
                player.getUniqueId(),
                new PlayerState(
                    player.getUniqueId(),
                    player.getLocation()
                )
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

    public void immobilizePlayer(Player player, int timeseconds) {
        this.playerStates.get(player.getUniqueId()).immobilize(timeseconds);
    }

    // -- Private

}
