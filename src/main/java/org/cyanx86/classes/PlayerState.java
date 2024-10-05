package org.cyanx86.classes;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.cyanx86.OverCrafted;
import org.cyanx86.utils.Messenger;

import java.util.Objects;
import org.jetbrains.annotations.NotNull;

public class PlayerState {

    // -- [[ ATTRIBUTES ]] --

    // -- PUBLIC --

    // -- PRIVATE --
    private final Player player;

    private final Location prevLocation;
    private final GameMode prevGameMode;
    private final ItemStack[] prevInventory;
    private final int prevFoodLevel;

    private int time;
    private BukkitTask task;

    // -- [[ METHODS ]] --

    // -- PUBLIC --
    public PlayerState(@NotNull Player player) {
        this.player = player;
        this.prevLocation = player.getLocation();
        this.prevGameMode = player.getGameMode();
        this.prevInventory = player.getInventory().getContents();
        this.prevFoodLevel = player.getFoodLevel();

        ItemStack stonePickaxe = new ItemStack(Material.STONE_PICKAXE);
        Objects.requireNonNull(stonePickaxe.getItemMeta()).setUnbreakable(true);
        this.player.setFoodLevel(20);

        this.immobilize();
        this.player.getInventory().clear();
        this.player.getInventory().addItem(stonePickaxe);
        this.player.setGameMode(GameMode.SURVIVAL);
    }

    public void moveToLocation(@NotNull Location location) {
        this.player.teleport(location);
    }

    public void sendMessageToPlayer(@NotNull String message) {
        Messenger.msgToSender(
            this.player,
            message
        );
    }

    public void sendTitleToPlayer(@NotNull String message1, @NotNull String message2, int fadeIn, int time, int fadeOut) {
        Messenger.titleToPlayer(this.player, message1, message2, fadeIn, time, fadeOut);
    }

    public void sendActionBarToPlayer(@NotNull String message) {
        Messenger.actionBarToPlayer(
            this.player,
            message
        );
    }

    public void restorePlayer() {
        if (this.task != null)
            this.task.cancel();
        this.player.teleport(this.prevLocation);
        this.player.getInventory().clear();
        this.player.getInventory().setContents(prevInventory);
        this.player.setGameMode(this.prevGameMode);
        this.player.setFoodLevel(this.prevFoodLevel);
        this.mobilize();
    }

    public void immobilizeForTime(int timeseconds) {
        this.immobilize();
        this.setImmobileTimer(timeseconds);
    }

    public void immobilize() {
        this.player.setWalkSpeed(0.0f);
    }

    public void mobilize() {
        this.player.setWalkSpeed(0.2f);
    }

    public void setDisplayer(Scoreboard scoreboard) {
        this.player.setScoreboard(scoreboard);
    }

    public boolean equal(@NotNull Player player) {
        return this.player.equals(player);
    }

    // -- PRIVATE --
    private void setImmobileTimer(int time) {
        this.time = time;
        this.task = Bukkit.getScheduler().runTaskTimer(OverCrafted.getInstance(), () -> {
            if (this.time == 0) {
                this.task.cancel();
                this.mobilize();
            }

            Messenger.titleToPlayer(
                this.player,
                "&c¡Te saliste del área!",
                "&eTe recuperarás en: &a" + this.time,
                0,
                20,
                0
            );

            this.time--;
        }, 20, 20);
    }

}
