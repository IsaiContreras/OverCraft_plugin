package org.cyanx86.classes;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import org.cyanx86.OverCrafted;
import org.cyanx86.utils.Messenger;

import java.util.Objects;
import org.jetbrains.annotations.NotNull;

public class PlayerState {

    // -- [[ ATTRIBUTES ]] --

    // -- PUBLIC --
    public enum PLAYERSTATE {
        RUNNING,
        IMMOBILIZED
    }

    // -- PRIVATE --
    private final Player player;
    private PLAYERSTATE currentState = PLAYERSTATE.RUNNING;

    private final Location previousLocation;
    private final GameMode prevGameMode;
    private final ItemStack[] prevInventory;

    private int time;
    private BukkitTask task;

    // -- [[ METHODS ]] --

    // -- PUBLIC --
    public PlayerState(@NotNull Player player) {
        this.player = player;
        this.previousLocation = player.getLocation();
        this.prevGameMode = player.getGameMode();
        this.prevInventory = player.getInventory().getContents();

        ItemStack stonePickaxe = new ItemStack(Material.STONE_PICKAXE);
        Objects.requireNonNull(stonePickaxe.getItemMeta()).setUnbreakable(true);

        this.immobilize();
        player.getInventory().clear();
        player.getInventory().addItem(stonePickaxe);
        player.setGameMode(GameMode.SURVIVAL);
    }

    public PLAYERSTATE getCurrentState() {
        return this.currentState;
    }

    public void moveToLocation(@NotNull Location location) {
        this.player.teleport(location);
    }

    public void moveToPreviousLocation() {
        this.player.teleport(this.previousLocation);
    }

    public void sendMessageToPlayer(@NotNull String message) {
        Messenger.msgToSender(
            this.player,
            message
        );
    }

    public void restoreInventory() {
        this.player.getInventory().clear();
        this.player.getInventory().setContents(prevInventory);
    }

    public void restoreGameMode() {
        this.player.setGameMode(this.prevGameMode);
    }

    public void immobilizeForTime(int timeseconds) {
        this.immobilize();
        this.setImmobileTimer(timeseconds);
    }

    public void immobilize() {
        this.currentState = PLAYERSTATE.IMMOBILIZED;
        this.player.setWalkSpeed(0.0f);
    }

    public void mobilize() {
        this.currentState = PLAYERSTATE.RUNNING;
        this.player.setWalkSpeed(0.2f);
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

            this.time--;
        }, 20, 20);
    }

}
