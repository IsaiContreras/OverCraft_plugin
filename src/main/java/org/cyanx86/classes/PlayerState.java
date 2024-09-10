package org.cyanx86.classes;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import org.cyanx86.OverCrafted;
import org.cyanx86.utils.Messenger;

import org.jetbrains.annotations.NotNull;

public class PlayerState {

    // -- [[ ATTRIBUTES ]] --

    // -- Public
    public enum PLAYERSTATE {
        RUNNING,
        IMMOBILIZED
    }

    // -- Private
    private final Player player;
    private PLAYERSTATE currentState = PLAYERSTATE.RUNNING;

    private final Location previousLocation;

    private int time;
    private BukkitTask task;

    // -- [[ METHODS ]] --

    // -- Public
    public PlayerState(@NotNull Player player) {
        this.player = player;
        this.previousLocation = player.getLocation();
    }

    public PLAYERSTATE getCurrentState() {
        return this.currentState;
    }

    public boolean equal(@NotNull Player player) {
        return this.player.equals(player);
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

    public void immobilize(int timeseconds) {
        this.currentState = PLAYERSTATE.IMMOBILIZED;
        this.player.setWalkSpeed(0.0f);
        this.setImmobileTimer(timeseconds);
    }

    // -- Private
    private void setImmobileTimer(int time) {
        this.time = time;
        this.task = Bukkit.getScheduler().runTaskTimer(OverCrafted.getInstance(), () -> {
            if (this.time == 0) {
                this.task.cancel();
                this.currentState = PLAYERSTATE.RUNNING;
                this.player.setWalkSpeed(0.2f);
            }
            this.time--;
        }, 20, 20);
    }

}
