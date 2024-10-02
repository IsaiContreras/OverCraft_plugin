package org.cyanx86.classes;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.cyanx86.OverCrafted;
import org.cyanx86.managers.GamePlayersManager;
import org.cyanx86.managers.OrderManager;
import org.cyanx86.managers.ScoreManager;
import org.cyanx86.utils.Enums.ListResult;

import java.util.*;

import org.cyanx86.utils.Messenger;
import org.jetbrains.annotations.NotNull;

public class GameRound {

    // -- [[ ATRIBUTES ]] --

    // -- PUBLIC --
    public enum ROUNDSTATE {
        COUNTDOWN,
        RUNNING,
        FINISHED,
        ENDED
    }

    // -- PRIVATE --
    private final GameArea gameArea;
    private final GamePlayersManager playersManager;
    private final OrderManager orderManager;
    private final ScoreManager scoreManager;

    private ROUNDSTATE currentState = ROUNDSTATE.COUNTDOWN;

    private final int startCountdownTime = 3;
    private final int roundTime;
    private final int endIntermissionTime = 3;

    private BukkitTask task;
    private int time;

    // -- [[ METHODS ]] --

    // -- PUBLIC --
    public GameRound(@NotNull GameArea gamearea, @NotNull List<Player> players, int time) {
        this.gameArea = gamearea;
        this.playersManager = new GamePlayersManager(players);
        this.scoreManager = new ScoreManager(30);
        this.orderManager = new OrderManager(this.gameArea.getRecipes(), this.scoreManager);
        this.roundTime = Math.max(time, 30);

        this.movePlayersToGameArea();

        this.startCountdown();
    }

    public ROUNDSTATE getCurrentRoundState() {
        return this.currentState;
    }
    public GameArea getGameArea() {
        return this.gameArea;
    }
    public Map<String, Object> getScores() {
        Map<String, Object> results = new HashMap<>();

        results.put("delivered", this.scoreManager.getDeliveredOrders());
        results.put("lost", this.scoreManager.getLostOrders());
        results.put("total", this.scoreManager.getTotalScore());

        return results;
    }

    /*
    public PLAYERSTATE getStateOfPlayer(@NotNull Player player) {
        PlayerState state = this.playersManager.getPlayerState(player);
        return (state == null) ? null : state.getCurrentState();
    }*/

    // Actions
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

    // Players
    public void spawnPlayer(@NotNull Player player, boolean immobilize) {
        PlayerState playerState = this.playersManager.getPlayerState(player);
        if (playerState == null) return;

        SpawnPoint spawnpoint = this.getPlayerSpawn(playerState);
        playerState.moveToLocation(spawnpoint.getSpawnLocation());

        if (immobilize)
            playerState.immobilizeForTime(3);
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

    // Orders
    public List<Order> getCurrentOrders() {
        return new ArrayList<>(this.orderManager.getOrderList());
    }

    public boolean dispatchOrder(@NotNull Material recipe) {
        this.scoreManager.incrementDeliveredOrder();
        return this.orderManager.removeOrder(recipe, false);
    }

    // Validators
    public boolean isPlayerInGame(@NotNull Player player) {
        return this.playersManager.anyPlayer(player);
    }

    // -- PRIVATE --
    private SpawnPoint getPlayerSpawn(@NotNull PlayerState playerState) {
        Optional<SpawnPoint> query = gameArea.getSpawnPoints().stream()
                .filter(item -> item.getPlayerIndex() == this.playersManager.getPlayerIndex(playerState))
                .findFirst();
        return query.orElse(null);
    }

    // Actions
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
        }, 20L, 20L);
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
        }, 20L, 20L);
    }

    private void endRound(String reason) {
        this.playersManager.sendMessageToPlayers(
                reason != null ? reason : "&a¡Buen juego! La ronda ha terminado."
        );

        this.currentState = ROUNDSTATE.ENDED;
        this.restorePlayerProperties();
    }

    // Players
    private void spawnPlayer(@NotNull PlayerState playerState) {
        SpawnPoint spawnpoint = this.getPlayerSpawn(playerState);
        playerState.moveToLocation(spawnpoint.getSpawnLocation());
    }

    private void movePlayersToGameArea() {
        Scoreboard sb = this.orderManager.getDisplayer();

        for (PlayerState playerState : this.playersManager.getPlayerStates()) {
            this.spawnPlayer(playerState);
            playerState.setDisplayer(sb);
        }
    }

    private void restorePlayerProperties() {
        for (PlayerState playerState : this.playersManager.getPlayerStates()) {
            playerState.moveToPreviousLocation();
            playerState.restoreGameMode();
            playerState.restoreInventory();
            playerState.mobilize();
        }
    }

}
