package org.cyanx86.managers;

import org.bukkit.Material;

import java.util.HashMap;
import java.util.Map;

public class OreBlocksManager {

    // -- [[ ATTRIBUTES ]]

    // -- PUBLIC --

    // -- PRIVATE --
    private final Map<Material, Material> oreList = new HashMap<>();

    // -- [[ METHODS ]]

    // -- PUBLIC --
    public OreBlocksManager() {
        this.oreList.put(Material.COAL_ORE, Material.COAL);
        this.oreList.put(Material.IRON_ORE, Material.RAW_IRON);
        this.oreList.put(Material.GOLD_ORE, Material.RAW_GOLD);
    }

    public Map<Material, Material> getOreMap() {
        return new HashMap<>(this.oreList);
    }

    // -- PRIVATE --

}
