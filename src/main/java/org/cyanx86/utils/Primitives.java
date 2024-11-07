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
            this.left = Math.min(corner1.getBlockX(), corner2.getBlockX());
            this.right = Math.max(corner1.getBlockX(), corner2.getBlockX());
            this.bottom = Math.min(corner1.getBlockY(), corner1.getBlockY());
            this.top = Math.max(corner1.getBlockY(), corner2.getBlockY());
            this.back = Math.min(corner1.getBlockZ(), corner2.getBlockZ());
            this.front = Math.max(corner1.getBlockZ(), corner2.getBlockZ());
        }

        public double getWidth() {
            return (this.right - this.left);
        }
        public double getHeight() {
            return (this.top - this.bottom);
        }
        public double getDepth() {
            return (this.front - this.back);
        }

    }

}
