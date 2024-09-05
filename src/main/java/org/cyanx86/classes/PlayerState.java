package org.cyanx86.classes;

import org.bukkit.Location;

import java.util.UUID;

public class PlayerState {

    // -- [[ ATTRIBUTES ]] --

    // -- Public
    public enum PLAYERSTATE {
        RUNNING,
        INMOBILIZED
    }

    // -- Private
    private final UUID playerUUID;
    private PLAYERSTATE currentState = PLAYERSTATE.RUNNING;

    private final Location previousLocation;

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

    // -- Private

}
