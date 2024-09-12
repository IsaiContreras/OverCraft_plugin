package org.cyanx86.managers;

import org.bukkit.Material;

import java.util.HashMap;
import java.util.Map;

public class OreBlocksManager {

    // -- [[ ATTRIBUTES ]]

    // -- Public

    // -- Private
    private final Map<Material, Material> orelist = new HashMap<>();

    // -- [[ METHODS ]]

    // -- Public
    public OreBlocksManager() {
        this.orelist.put(Material.COAL_ORE, Material.COAL);
        this.orelist.put(Material.IRON_ORE, Material.RAW_IRON);
        this.orelist.put(Material.GOLD_ORE, Material.RAW_GOLD);
    }

    public Map<Material, Material> getOreMap() {
        return this.orelist;
    }

    // -- Private

}
