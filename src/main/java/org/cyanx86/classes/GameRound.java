package org.cyanx86.classes;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.cyanx86.OverCrafted;
import org.cyanx86.config.RoundSettings;
import org.cyanx86.managers.GamePlayersManager;
import org.cyanx86.managers.OrderManager;
import org.cyanx86.managers.ScoreManager;
import org.cyanx86.managers.SoundEffectsManager;
import org.cyanx86.utils.DataFormatting;
import org.cyanx86.utils.Enums.ListResult;
import org.cyanx86.utils.Messenger;

import java.util.*;
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
    private final SoundEffectsManager soundEffectsManager;

    private ROUNDSTATE currentState = ROUNDSTATE.COUNTDOWN;

    private final int startCountdownTime;
    private final int roundTime;
    private final int endIntermissionTime;

    private final int playerImmobilizationTime;

    private BukkitTask task;
    private int time;

    // -- [[ METHODS ]] --

    // -- PUBLIC --
    public GameRound(@NotNull GameArea gameArea, @NotNull List<Player> players) {
        this.gameArea = gameArea;
        this.playersManager = new GamePlayersManager(players);
        this.scoreManager = new ScoreManager();
        this.soundEffectsManager = new SoundEffectsManager(this.playersManager);
        this.orderManager = new OrderManager(this.gameArea.getRecipes(), this.soundEffectsManager, this.scoreManager);

        RoundSettings settings = RoundSettings.getInstance();
        this.startCountdownTime = settings.getGRStartCountdown();
        this.roundTime = settings.getGRTime();
        this.endIntermissionTime = settings.getGRIntermissionTime();

        this.playerImmobilizationTime = settings.getGRPlayerImmobilizationTime();

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
        results.put("bonus", this.scoreManager.getBonus());
        results.put("score", this.scoreManager.getScore());

        return results;
    }

    // Actions
    public boolean terminateRound(String reason) {
        if (this.currentState == ROUNDSTATE.ENDED) return false;

        String message = (reason != null ? reason : "&cLa ronda fue cancelada.");

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
            playerState.immobilizeForTime(this.playerImmobilizationTime);
    }

    public ListResult removePlayer(@NotNull Player player) {
        PlayerState playerState = this.playersManager.getPlayerState(player);
        if (playerState == null)
            return ListResult.NOT_FOUND;

        playerState.restorePlayer();
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
        this.soundEffectsManager.playDeliveredOrder();
        return this.orderManager.removeOrder(recipe, false);
    }

    // Validators
    public boolean isPlayerInGame(@NotNull Player player) {
        return this.playersManager.anyPlayer(player);
    }

    public boolean isPlayerAbleToMove(@NotNull Player player) {
        PlayerState playerState = this.playersManager.getPlayerState(player);
        if (playerState == null)
            return false;

        return playerState.isAbleToMove();
    }

    // -- PRIVATE --
    private SpawnPoint getPlayerSpawn(@NotNull PlayerState playerState) {
        Optional<SpawnPoint> query = this.gameArea.getSpawnPoints().stream()
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

            this.playersManager.sendTitleToPlayers(
                "&eComienza en",
                "&a" + this.time,
                0,
                20,
                0
            );
            this.soundEffectsManager.playCountDownNote();

            this.time--;
        }, 20L, 20L);
    }

    private void startRoundTimer() {
        this.playersManager.sendTitleToPlayers(
            "&a¡A CRAFTEAR!",
            "",
            0,
            20,
            0
        );
        this.soundEffectsManager.playStartRound();
        this.currentState = ROUNDSTATE.RUNNING;
        this.orderManager.startGenerator();

        for (PlayerState playerState : this.playersManager.getPlayerStates())
            playerState.mobilize();

        this.time = this.roundTime;
        this.task = Bukkit.getScheduler().runTaskTimer(OverCrafted.getInstance(), () -> {
            this.playersManager.sendActionBarToPlayers(
                "&" + (this.time <= 30 ? "c" : "f") +
                        DataFormatting.formatSecondsToTime(this.time)
            );
            if (this.time == 30)
                this.soundEffectsManager.playTimeRunningOut();

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
        this.playersManager.sendTitleToPlayers(
            "&a¡TIEMPO!",
            "",
            0,
            this.endIntermissionTime * 20,
            0
        );
        this.soundEffectsManager.playFinish();

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
        for (PlayerState playerState : this.playersManager.getPlayerStates())
            playerState.restorePlayer();
    }

}
