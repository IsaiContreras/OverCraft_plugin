package org.cyanx86.classes;

import org.bukkit.Bukkit;
import org.bukkit.Location;
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
    private int count;

    // -- [[ METHODS ]] --

    // -- Public
    public GameRound(GameArea gamearea, List<Player> players, int time) {
        this.gamearea = gamearea;
        this.playersManager = new GamePlayersManager(players);
        this.roundTime = Math.max(time, 30);

        this.movePlayersToGameArea();

        this.startCountdown();
    }

    public void terminateRound() {

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

    private void endRound() {
        Messenger.msgToMultPlayers(
            this.playersManager.getPlayers(),
            OverCrafted.prefix + "&a¡Buen juego! La ronda ha terminado."
        );
        this.currentState = ROUNDSTATE.ENDED;
        this.quitPlayersFromGameArea();
    }

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
        this.count = this.startCountdownTime;

        this.task = Bukkit.getScheduler().runTaskTimer(OverCrafted.getInstance(), () -> {
            Messenger.msgToMultPlayers(
                this.playersManager.getPlayers(),
                OverCrafted.prefix + "&a" + this.count
            );
            if (this.count == 0) {
                this.task.cancel();
                this.startRoundTimer();
            }
            this.count--;
        }, 20, 20);
    }

    private void startRoundTimer() {
        Messenger.msgToMultPlayers(
            this.playersManager.getPlayers(),
            OverCrafted.prefix + "&a¡A CRAFTEAR!"
        );
        this.currentState = ROUNDSTATE.RUNNING;
        this.count = this.roundTime;
        this.task = Bukkit.getScheduler().runTaskTimer(OverCrafted.getInstance(), () -> {
            if (this.count == 0) {
                this.task.cancel();
                this.intermissionTime();
            }
            this.count--;
        }, 20, 20);
    }

    private void intermissionTime() {
        Messenger.msgToMultPlayers(
            this.playersManager.getPlayers(),
            OverCrafted.prefix + "&a¡TIEMPO!"
        );
        this.currentState = ROUNDSTATE.FINISHED;
        this.count = this.endIntermissionTime;
        this.task = Bukkit.getScheduler().runTaskTimer(OverCrafted.getInstance(), () -> {
            if (this.count == 0) {
                this.task.cancel();
                this.endRound();
            }
            this.count--;
        }, 20, 20);
    }

}
