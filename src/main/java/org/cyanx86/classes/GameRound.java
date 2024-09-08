package org.cyanx86.classes;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import org.bukkit.scheduler.BukkitTask;
import org.cyanx86.OverCrafted;
import org.cyanx86.managers.GamePlayersManager;
import org.cyanx86.utils.Messenger;

import java.util.List;
import java.util.Optional;

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
    public GameRound(GameArea gamearea, List<Player> players, int time) {
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

    public boolean isPlayerPlaying(Player player) {
        return this.playersManager.isPlayerInGame(player);
    }

    public GamePlayersManager getPlayersManager() {
        return this.playersManager;
    }

    public GameArea getGameArea() {
        return this.gamearea;
    }

    public void movePlayerToSpawn(Player player, boolean immobilize) {
        Optional<SpawnPoint> query = gamearea.getSpawnPoints().stream().filter(item -> item.getPlayerIndex() == playersManager.getPlayerIndex(player)).findFirst();
        if (query.isEmpty())
            return;
        player.teleport(query.get().getSpawnLocation());
        if (immobilize) {
            this.playersManager.immobilizePlayer(player, 3);
        }
    }

    // -- Private
    private void movePlayersToGameArea() {
        for (Player player : playersManager.getPlayers()) {
            this.movePlayerToSpawn(player, false);
        }
    }

    private void quitPlayersFromGameArea() {
        for (Player player : playersManager.getPlayers()) {
            player.teleport(playersManager.getPlayerState(player).getPrevLocation());
        }
    }

    private void startCountdown() {
        Messenger.msgToMultPlayers(
            this.playersManager.getPlayers(),
            OverCrafted.prefix + "&aLa ronda ha comenzado."
        );
        this.time = this.startCountdownTime;

        this.task = Bukkit.getScheduler().runTaskTimer(OverCrafted.getInstance(), () -> {
            if (this.time == 0) {
                this.task.cancel();
                this.startRoundTimer();
                return;
            }

            Messenger.msgToMultPlayers(
                    this.playersManager.getPlayers(),
                    OverCrafted.prefix + "&a" + this.time
            );

            this.time--;
        }, 20, 20);
    }

    private void startRoundTimer() {
        Messenger.msgToMultPlayers(
            this.playersManager.getPlayers(),
            OverCrafted.prefix + "&a¡A CRAFTEAR!"
        );
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
        Messenger.msgToMultPlayers(
            this.playersManager.getPlayers(),
            OverCrafted.prefix + "&a¡TIEMPO!"
        );
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

    private void endRound(boolean terminated) {
        String message;
        if (terminated) message = "&aEl juego ha sido cancelado.";
        else message = "&a¡Buen juego! La ronda ha terminado.";

        Messenger.msgToMultPlayers(
            this.playersManager.getPlayers(),
            OverCrafted.prefix + message
        );
        this.currentState = ROUNDSTATE.ENDED;
        this.quitPlayersFromGameArea();
    }

}
