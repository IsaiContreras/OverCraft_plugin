package org.cyanx86.classes;

import org.bukkit.Location;

import java.util.Arrays;
import org.jetbrains.annotations.NotNull;

public class KitchenAreaCreatorAssistant {

    // -- [[ ATTRIBUTES ]] --

    // -- PUBLIC --

    // -- PRIVATE --
    private byte index = 0;
    private final Location[] corners = new Location[2];

    // -- [[ METHODS ]] --

    // -- PUBLIC --
    public KitchenAreaCreatorAssistant() {
        this.resetCorners();
    }

    public byte getCornerIndex() { return this.index; }
    public Location getCorner(int index) {
        return ((index < 0 || index > 1) ? null : this.corners[index]);
    }

    public void setCorner(@NotNull Location corner) {
        if (this.index == 0)
            this.corners[1] = null;

        this.corners[this.index] = corner;
        this.index++;

        if (this.index > 1) this.index = 0;
    }

    // Actions
    public void resetCorners() {
        Arrays.fill(this.corners, null);
    }

    // Validators
    public boolean isDefinedCorners() {
        for (Location corner : this.corners)
            if (corner == null) return false;
        return true;
    }

    // -- PRIVATE --

}
