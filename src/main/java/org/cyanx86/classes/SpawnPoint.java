package org.cyanx86.classes;

import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class SpawnPoint {

    // -- [[ ATTRIBUTES ]] --

    // -- PUBLIC --

    // -- PRIVATE --
    private final Location location;

    private int playerIndex = 0;

    // -- [[ METHODS ]] --

    // -- PUBLIC --
    public SpawnPoint(@NotNull Location location) {
        this.location = location;
    }

    public SpawnPoint(@NotNull Location location, int player_index) {
        this.location = location;
        this.playerIndex = player_index;
    }

    public Location getSpawnLocation() {
        return this.location;
    }

    public int getPlayerIndex() {
        return this.playerIndex;
    }

    public void setPlayerIndex(int index) {
        this.playerIndex = index;
    }

    // Data management
    public Map<String, Object> serialize() {
        Map<String, Object> data = new HashMap<>();

        data.put("location", location.serialize());
        data.put("player_index", playerIndex);

        return data;
    }

    public static SpawnPoint deserialize(@NotNull Map<String, Object> args) {
        Location location = Location.deserialize((Map<String, Object>)args.get("location"));

        return new SpawnPoint(
            location,
            (int)args.get("player_index")
        );
    }

    // -- PRIVATE --

}
