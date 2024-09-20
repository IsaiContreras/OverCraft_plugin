package org.cyanx86.classes;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import org.cyanx86.OverCrafted;
import org.cyanx86.classes.PlayerState.*;
import org.cyanx86.managers.GamePlayersManager;
import org.cyanx86.managers.OrderManager;
import org.cyanx86.utils.Enums.ListResult;

import java.util.List;
import java.util.Optional;

import org.cyanx86.utils.Messenger;
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
    private final OrderManager orderManager;

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
        this.orderManager = new OrderManager(this.gamearea.getRecipes());
        this.roundTime = Math.max(time, 30);

        this.movePlayersToGameArea();

        this.startCountdown();
    }

    public boolean terminateRound(String reason) {
        if (this.currentState == ROUNDSTATE.ENDED) return false;

        String message = reason != null ? reason : "&cLa ronda fue cancelada.";

        this.task.cancel();
        this.endRound(message);

        Messenger.msgToConsole(
            OverCrafted.prefix + "Ronda terminada por: " + message
        );

        return true;
    }

    public ROUNDSTATE getCurrentRoundState() {
        return this.currentState;
    }

    /*
    public PLAYERSTATE getStateOfPlayer(@NotNull Player player) {
        PlayerState state = this.playersManager.getPlayerState(player);
        return (state == null) ? null : state.getCurrentState();
    }*/

    public boolean isPlayerInGame(@NotNull Player player) {
        return this.playersManager.anyPlayer(player);
    }

    public ListResult removePlayer(@NotNull Player player) {
        PlayerState playerState = this.playersManager.getPlayerState(player);
        if (playerState == null)
            return ListResult.NOT_FOUND;

        playerState.moveToPreviousLocation();
        playerState.restoreGameMode();
        playerState.restoreInventory();
        ListResult result = this.playersManager.removePlayer(player);

        if (this.playersManager.getPlayerStates().isEmpty())
            this.terminateRound("&cTodos los jugadores se eliminaron o desconectaron.");

        return result;
    }

    public GameArea getGameArea() {
        return this.gamearea;
    }

    public void spawnPlayer(@NotNull Player player, boolean immobilize) {
        PlayerState playerState = this.playersManager.getPlayerState(player);
        if (playerState == null) return;

        SpawnPoint spawnpoint = this.getPlayerSpawn(playerState);
        playerState.moveToLocation(spawnpoint.getSpawnLocation());

        if (immobilize)
            playerState.immobilizeForTime(3);
    }

    public List<Order> getCurrentOrders() {
        return this.orderManager.getOrderList();
    }

    public boolean dispatchOrder(@NotNull Material recipe) {
        return this.orderManager.removeOrder(recipe);
    }

    // -- Private

    private void endRound(String reason) {
        this.playersManager.sendMessageToPlayers(
            reason != null ? reason : "&a¡Buen juego! La ronda ha terminado."
        );

        this.currentState = ROUNDSTATE.ENDED;
        this.restorePlayerProperties();
    }

    private SpawnPoint getPlayerSpawn(@NotNull PlayerState playerState) {
        Optional<SpawnPoint> query = gamearea.getSpawnPoints().stream()
                .filter(item -> item.getPlayerIndex() == this.playersManager.getPlayerIndex(playerState))
                .findFirst();
        return query.orElse(null);
    }

    private void spawnPlayer(@NotNull PlayerState playerState) {
        SpawnPoint spawnpoint = this.getPlayerSpawn(playerState);
        playerState.moveToLocation(spawnpoint.getSpawnLocation());
    }

    private void movePlayersToGameArea() {
        for (PlayerState playerState : this.playersManager.getPlayerStates())
            this.spawnPlayer(playerState);
    }

    private void restorePlayerProperties() {
        for (PlayerState playerState : this.playersManager.getPlayerStates()) {
            playerState.moveToPreviousLocation();
            playerState.restoreGameMode();
            playerState.restoreInventory();
            playerState.mobilize();
        }
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
        }, 20L, 20L);
    }

    private void startRoundTimer() {
        this.playersManager.sendMessageToPlayers("&a¡A CRAFTEAR!");
        this.currentState = ROUNDSTATE.RUNNING;
        this.orderManager.startGenerator();
        for (PlayerState playerState : this.playersManager.getPlayerStates())
            playerState.mobilize();

        this.time = this.roundTime;
        this.task = Bukkit.getScheduler().runTaskTimer(OverCrafted.getInstance(), () -> {
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
        this.orderManager.stopGenerator();
        for (PlayerState playerState : this.playersManager.getPlayerStates())
            playerState.immobilize();

        this.time = this.endIntermissionTime;
        this.task = Bukkit.getScheduler().runTaskTimer(OverCrafted.getInstance(), () -> {
            if (this.time == 0) {
                this.task.cancel();
                this.endRound(null);
                return;
            }
            this.time--;
        }, 20, 20);
    }

}
