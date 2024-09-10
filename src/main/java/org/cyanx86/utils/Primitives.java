package org.cyanx86.utils;

import org.bukkit.Location;

import org.jetbrains.annotations.NotNull;

public class Primitives {

    public static class Cube {

        public double left;
        public double right;
        public double top;
        public double bottom;
        public double front;
        public double back;

        public Cube (@NotNull Location corner1, @NotNull Location corner2) {
            left = Math.min(corner1.getBlockX(), corner2.getBlockX());
            right = Math.max(corner1.getBlockX(), corner2.getBlockX());
            bottom = Math.min(corner1.getBlockY(), corner1.getBlockY());
            top = Math.max(corner1.getBlockY(), corner2.getBlockY());
            back = Math.min(corner1.getBlockZ(), corner2.getBlockZ());
            front = Math.max(corner1.getBlockZ(), corner2.getBlockZ());
        }

    }

}
