package org.cyanx86.classes;

import org.bukkit.Location;

import java.util.Arrays;

public class GameAreaCornerAssistant {

    // -- [[ ATTRIBUTES ]] --

    // -- Public

    // -- Private
    private byte index = 0;
    private final Location[] corners = new Location[2];

    // -- [[ METHODS ]] --

    // -- Public
    public GameAreaCornerAssistant() {
        resetCorners();
    }

    public byte getCornerIndex() { return this.index; }

    public Location getCorner(int index) {
        if (index < 0 || index > 1)
            return null;
        return this.corners[index];
    }

    public boolean setCorner(Location corner) {
        if (corner == null)
            return false;

        if (index == 0)
            this.corners[1] = null;

        this.corners[index] = corner;
        index++;

        if (index > 1) index = 0;
        return true;
    }

    public void resetCorners() {
        Arrays.fill(corners, null);
    }

    public boolean isDefinedCorners() {
        for (Location corner : corners) if (corner == null) return false;
        return true;
    }

    // -- Private

}
