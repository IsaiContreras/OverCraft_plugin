package org.cyanx86.classes;

import org.bukkit.Location;

public class GameArea {

    // -- [[ ATTRIBUTES ]] --

    // -- Public

    // -- Private
    private String name;

    private String world;

    private final Location corner1;
    private final Location corner2;

    // -- [[ METHODS ]] --

    // -- Public
    public GameArea(String name, Location corner1, Location corner2) {
        this.name = name;
        this.corner1 = corner1;
        this.corner2 = corner2;
    }

    public String getName() {
        return this.name;
    }

    // -- Private

}
