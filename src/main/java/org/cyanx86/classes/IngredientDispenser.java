package org.cyanx86.classes;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class IngredientDispenser {

    // -- [[ ATTRIBUTES ]] --

    // -- Public

    // -- Private
    private final Location location;
    private final Material dropping;

    // -- [[ METHODS ]] --

    // -- Public
    public IngredientDispenser(@NotNull Location location, @NotNull Material material) {
        this.location = location;
        this.dropping = material;
    }

    public Location getLocation() {
        return this.location;
    }

    public boolean isLocationOfDispenser(Location location) {
        return(
            this.location.getBlockX() == location.getBlockX() &&
            this.location.getBlockY() == location.getBlockY() &&
            this.location.getBlockZ() == location.getBlockZ()
        );
    }

    public ItemStack dropItem() {
        return new ItemStack(dropping);
    }

    public String getDropItemName() {
        return this.dropping.toString();
    }

    public Map<String, Object> serialize() {
        Map<String, Object> data = new HashMap<>();

        data.put("location", this.location.serialize());
        data.put("dropping", this.dropping.toString());

        return data;
    }

    public static IngredientDispenser deserialize(@NotNull Map<String, Object> args) {
        return new IngredientDispenser(
            Location.deserialize((Map<String, Object>)args.get("location")),
            Material.valueOf((String)args.get("dropping"))
        );
    }

    // -- Private

}
