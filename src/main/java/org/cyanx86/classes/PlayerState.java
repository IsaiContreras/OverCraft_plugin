package org.cyanx86.classes;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitTask;
import org.cyanx86.OverCrafted;

import java.util.UUID;

public class PlayerState {

    // -- [[ ATTRIBUTES ]] --

    // -- Public
    public enum PLAYERSTATE {
        RUNNING,
        IMMOBILIZED
    }

    // -- Private
    private final UUID playerUUID;
    private PLAYERSTATE currentState = PLAYERSTATE.RUNNING;

    private final Location previousLocation;

    private int time;
    private BukkitTask task;

    // -- [[ METHODS ]] --

    // -- Public
    public PlayerState(UUID playerUUID, Location prevLocation) {
        this.playerUUID = playerUUID;
        this.previousLocation = prevLocation;
    }

    public PLAYERSTATE getCurrentState() {
        return this.currentState;
    }

    public Location getPrevLocation() {
        return this.previousLocation;
    }

    public void immobilize(int timeseconds) {
        this.currentState = PLAYERSTATE.IMMOBILIZED;
        this.setImmobileTimer(timeseconds);
    }

    // -- Private
    private void setImmobileTimer(int time) {
        this.time = time;
        this.task = Bukkit.getScheduler().runTaskTimer(OverCrafted.getInstance(), () -> {
            if (this.time == 0) {
                this.task.cancel();
                this.currentState = PLAYERSTATE.RUNNING;
            }
            this.time--;
        }, 20, 20);
    }

}
