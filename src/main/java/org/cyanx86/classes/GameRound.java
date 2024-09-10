package org.cyanx86.classes;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import org.cyanx86.OverCrafted;
import org.cyanx86.classes.PlayerState.*;
import org.cyanx86.managers.GamePlayersManager;

import java.util.List;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;

public class GameRound {

    // -- [[ ATRIBUTES ]] --

    // -- Public
    public enum ROUNDSTATE {
        COUNTDOWN,
        RUNNING,
        FINISHED,
        ENDED
    }

    // -- Private
    private final GameArea gamearea;
    private final GamePlayersManager playersManager;

    private ROUNDSTATE currentState = ROUNDSTATE.COUNTDOWN;

    private final int startCountdownTime = 3;
    private final int roundTime;
    private final int endIntermissionTime = 3;

    private BukkitTask task;
    private int time;

    // -- [[ METHODS ]] --

    // -- Public
    public GameRound(@NotNull GameArea gamearea, @NotNull List<Player> players, int time) {
        this.gamearea = gamearea;
        this.playersManager = new GamePlayersManager(players);
        this.roundTime = Math.max(time, 30);

        this.movePlayersToGameArea();

        this.startCountdown();
    }

    public boolean terminateRound() {
        if (currentState != ROUNDSTATE.RUNNING) return false;
        this.currentState = ROUNDSTATE.ENDED;
        return true;
    }

    public ROUNDSTATE getCurrentRoundState() {
        return this.currentState;
    }

    public PLAYERSTATE getStateOfPlayer(@NotNull Player player) {
        PlayerState state = this.playersManager.getPlayerState(player);
        return (state == null) ? null : state.getCurrentState();
    }

    public boolean isPlayerInGame(@NotNull Player player) {
        return this.playersManager.anyPlayer(player);
    }

    public GameArea getGameArea() {
        return this.gamearea;
    }

    public void spawnPlayer(@NotNull Player player, boolean immobilize) {
        PlayerState playerstate = this.playersManager.getPlayerState(player);
        if (playerstate == null) return;

        SpawnPoint spawnpoint = this.getPlayerSpawn(playerstate);

        playerstate.moveToLocation(spawnpoint.getSpawnLocation());
        if (immobilize) {
            playerstate.immobilize(3);
        }
    }

    // -- Private

    private void endRound(boolean terminated) {
        String message = terminated ? "&aEl juego ha sido cancelado." : "&a¡Buen juego! La ronda ha terminado.";

        this.playersManager.sendMessageToPlayers(message);

        this.currentState = ROUNDSTATE.ENDED;
        this.quitPlayersFromGameArea();
    }

    private SpawnPoint getPlayerSpawn(@NotNull PlayerState playerstate) {
        Optional<SpawnPoint> query = gamearea.getSpawnPoints().stream()
                .filter(item -> item.getPlayerIndex() == this.playersManager.getPlayerIndex(playerstate))
                .findFirst();
        return query.orElse(null);
    }

    private void spawnPlayer(@NotNull PlayerState playerstate) {
        SpawnPoint spawnpoint = this.getPlayerSpawn(playerstate);
        playerstate.moveToLocation(spawnpoint.getSpawnLocation());
    }

    private void movePlayersToGameArea() {
        for (PlayerState playerstate : this.playersManager.getPlayerStates())
            this.spawnPlayer(playerstate);
    }

    private void quitPlayersFromGameArea() {
        for (PlayerState playerstate : playersManager.getPlayerStates())
            playerstate.moveToPreviousLocation();
    }

    private void startCountdown() {
        this.playersManager.sendMessageToPlayers("&aLa ronda ha comenzado.");
        this.time = this.startCountdownTime;

        this.task = Bukkit.getScheduler().runTaskTimer(OverCrafted.getInstance(), () -> {
            if (this.time == 0) {
                this.task.cancel();
                this.startRoundTimer();
                return;
            }

            this.playersManager.sendMessageToPlayers("&a" + this.time);

            this.time--;
        }, 20, 20);
    }

    private void startRoundTimer() {
        this.playersManager.sendMessageToPlayers("&a¡A CRAFTEAR!");
        this.currentState = ROUNDSTATE.RUNNING;

        this.time = this.roundTime;
        this.task = Bukkit.getScheduler().runTaskTimer(OverCrafted.getInstance(), () -> {
            if (this.currentState == ROUNDSTATE.ENDED) {
                this.task.cancel();
                this.endRound(true);
                return;
            }
            if (this.time == 0) {
                this.task.cancel();
                this.intermissionTime();
                return;
            }
            this.time--;
            // Display timer
        }, 20, 20);
    }

    private void intermissionTime() {
        this.playersManager.sendMessageToPlayers("&a¡TIEMPO!");
        this.currentState = ROUNDSTATE.FINISHED;

        this.time = this.endIntermissionTime;
        this.task = Bukkit.getScheduler().runTaskTimer(OverCrafted.getInstance(), () -> {
            if (this.time == 0) {
                this.task.cancel();
                this.endRound(false);
                return;
            }
            this.time--;
        }, 20, 20);
    }

}
