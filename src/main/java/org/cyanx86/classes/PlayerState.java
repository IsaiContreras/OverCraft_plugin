package org.cyanx86.classes;

import java.util.UUID;

public class PlayerState {

    // -- [[ ATTRIBUTES ]] --

    // -- Public
    public static enum PLAYERSTATE {
        RUNNING,
        INMOBILIZED
    }

    // -- Private
    private final UUID playerUUID;
    private PLAYERSTATE currentState = PLAYERSTATE.RUNNING;

    // -- [[ METHODS ]] --

    // -- Public
    public PlayerState(UUID playerUUID) {
        this.playerUUID = playerUUID;
    }

    public PLAYERSTATE getCurrentState() {
        return this.currentState;
    }

    // -- Private

}
