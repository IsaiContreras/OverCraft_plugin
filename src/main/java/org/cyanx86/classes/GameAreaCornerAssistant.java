package org.cyanx86.classes;

import org.bukkit.Location;

import java.util.Arrays;
import org.jetbrains.annotations.NotNull;

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
        return ((index < 0 || index > 1) ? null : this.corners[index]);
    }

    public void setCorner(@NotNull Location corner) {
        if (index == 0)
            this.corners[1] = null;

        this.corners[index] = corner;
        index++;

        if (index > 1) index = 0;
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
