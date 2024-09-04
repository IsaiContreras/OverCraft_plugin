package org.cyanx86.classes;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import org.cyanx86.OverCrafted;
import org.cyanx86.managers.GamePlayersManager;

import java.util.List;
import java.util.Optional;

public class GameRound {

    // -- [[ ATRIBUTES ]] --

    // -- Public
    public static enum ROUNDSTATE {
        COUNTDOWN,
        RUNNING,
        ENDED
    }

    // -- Private
    private final OverCrafted master;

    private final GameArea gamearea;
    private final GamePlayersManager playersManager;

    private ROUNDSTATE currentState = ROUNDSTATE.RUNNING;

    // -- [[ METHODS ]] --

    // -- Public
    public GameRound(OverCrafted master, GameArea gamearea, List<Player> players) {
        this.master = master;
        this.gamearea = gamearea;
        this.playersManager = new GamePlayersManager(players);

        this.movePlayersToGameArea();
    }

    public ROUNDSTATE getCurrentRoundState() {
        return this.currentState;
    }

    public boolean isPlayerPlaying(Player player) {
        return this.playersManager.isPlayerInGame(player);
    }

    public GamePlayersManager getPlayersManager() {
        return this.playersManager;
    }

    public GameArea getGameArea() {
        return this.gamearea;
    }

    public void respawnPlayer(Player player) {
        Optional<SpawnPoint> query = gamearea.getSpawnPoints().stream().filter(item -> item.getPlayerIndex() == playersManager.getPlayerIndex(player)).findFirst();
        if (query.isEmpty())
            return;
        Location spawnLocation = query.get().getSpawnLocation();

        player.teleport(spawnLocation);
    }

    // -- Private

    private void movePlayersToGameArea() {
        for (Player player : playersManager.getPlayers()) {
            this.respawnPlayer(player);
        }
    }

}
