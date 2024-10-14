package org.cyanx86.classes;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.cyanx86.OverCrafted;
import org.cyanx86.config.GeneralSettings;
import org.cyanx86.config.Locale;
import org.cyanx86.utils.Messenger;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;

public class PlayerState {

    // -- [[ ATTRIBUTES ]] --

    // -- PUBLIC --

    // -- PRIVATE --
    private final Player player;
    private final Locale locale = GeneralSettings.getInstance().getLocale();

    private final Location prevLocation;
    private final GameMode prevGameMode;
    private final ItemStack[] prevInventory;
    private final int prevFoodLevel;

    private boolean ableToMove = true;

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
        this.player.getInventory().setMaxStackSize(4);
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

    public void sendSoundToPlayer(@NotNull Sound sound, float volume, float pitch) {
        this.player.playSound(
            this.player.getLocation(),
            sound,
            volume,
            pitch
        );
    }

    public void sendNoteToPlayer(@NotNull Instrument instrument, @NotNull Note note) {
        this.player.playNote(
            this.player.getLocation(),
            instrument,
            note
        );
    }

    public void restorePlayer() {
        if (this.task != null)
            this.task.cancel();
        this.player.teleport(this.prevLocation);
        this.player.getInventory().setMaxStackSize(64);
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
        this.ableToMove = false;
    }

    public void mobilize() {
        this.ableToMove = true;
    }

    public boolean isAbleToMove() {
        return this.ableToMove;
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
            if (this.time <= 0) {
                this.task.cancel();
                this.mobilize();
            }

            List<String> texts = this.locale.getStrArray("round-titles.player-immobilized");

            Messenger.titleToPlayer(
                this.player,
                texts.get(0),
                texts.get(1).replace("%time%", String.valueOf(this.time)),
                0,
                20,
                0
            );

            this.time--;
        }, 20, 20);
    }

}
