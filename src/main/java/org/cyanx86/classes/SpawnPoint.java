package org.cyanx86.classes;

import org.bukkit.Location;

import java.util.HashMap;
import java.util.Map;

public class SpawnPoint {

    // -- [[ ATTRIBUTES ]] --

    // -- Public

    // -- Private
    private final Location location;

    private int playerIndex = 0;

    // -- [[ METHODS ]] --

    // -- Public
    public SpawnPoint(Location location) {
        this.location = location;
    }

    public SpawnPoint(Location location, int playerIndex) {
        this.location = location;
        this.playerIndex = playerIndex;
    }

    public Location getSpawnLocation() {
        return this.location;
    }

    public void setPlayerIndex(int index) {
        this.playerIndex = index;
    }

    public int getPlayerIndex() {
        return this.playerIndex;
    }

    public Map<String, Object> serialize() {
        Map<String, Object> data = new HashMap<>();

        data.put("location", location.serialize());
        data.put("playerindex", playerIndex);

        return data;
    }

    public static SpawnPoint deserialize(Map<String, Object> args) {
        Location location = Location.deserialize((Map<String, Object>)args.get("location"));

        return new SpawnPoint(
            location,
            (int)args.get("playerindex")
        );
    }

    // -- Private

}
