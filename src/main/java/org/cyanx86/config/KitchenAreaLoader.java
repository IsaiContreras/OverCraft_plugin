package org.cyanx86.config;

import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;

import org.cyanx86.classes.KitchenArea;
import org.cyanx86.utils.CustomConfigFile;
import org.cyanx86.utils.Enums.ListResult;

import java.util.*;
import org.jetbrains.annotations.NotNull;

public class KitchenAreaLoader extends CustomConfigFile {

    // -- [[ ATTRIBUTES ]] --

    // -- PUBLIC --

    // -- PRIVATE --
    private final List<KitchenArea> kitchenAreas = new ArrayList<>();

    // -- [[ METHODS ]] --

    // -- PUBLIC --
    public KitchenAreaLoader() {
        super(
            "kitchen_areas.yml",
            null,
            true
        );
        if (this.registerConfig())
            this.load();
    }

    // Read/Write Methods
    @Override
    protected void load() {
        FileConfiguration config = this.getConfig();
        List<Map<?, ?>> ktcMapList = config.getMapList("kitchen-areas");

        for (Map<?, ?> ktcMap : ktcMapList)
            kitchenAreas.add(KitchenArea.deserialize((Map<String, Object>)ktcMap));
    }

    @Override
    public boolean reload() {
        if(!this.reloadConfig())
            return false;
        this.load();
        return true;
    }

    @Override
    public void save() {
        FileConfiguration config = this.getConfig();

        List<Map<String, Object>> ktcMapList = new ArrayList<>();

        for (KitchenArea ktcItem : this.kitchenAreas)
            ktcMapList.add(ktcItem.serialize());

        config.set("kitchen-areas", ktcMapList);
        this.saveConfig();
    }

    // List managing
    public ListResult addKitchenArea(
            @NotNull String name,
            @NotNull Location corner1,
            @NotNull Location corner2,
            int minPlayers,
            int maxPlayers
    ) {
        if (this.alreadyExists(name))
            return ListResult.ALREADY_IN;

        KitchenArea kitchenArea = new KitchenArea(
            name,
            corner1,
            corner2,
            minPlayers,
            maxPlayers
        );

        for (KitchenArea ktcItem : this.kitchenAreas)
            if (ktcItem.isRegionOverlapping(kitchenArea))
                return ListResult.INVALID_ITEM;

        this.kitchenAreas.add(kitchenArea);
        return ListResult.SUCCESS;
    }

    public ListResult removeKitchenArea(@NotNull String name) {
        if (this.isEmpty())
            return ListResult.EMPTY_LIST;

        KitchenArea kitchenArea = this.getByName(name);

        if (kitchenArea == null)
            return ListResult.NOT_FOUND;

        this.kitchenAreas.remove(kitchenArea);
        return ListResult.SUCCESS;
    }

    public KitchenArea getByName(@NotNull String name) {
        Optional<KitchenArea> queryKitchenArea = this.kitchenAreas.stream()
                .filter(item -> item.getName().equals(name))
                .findFirst();
        return queryKitchenArea.orElse(null);
    }

    public List<KitchenArea> getKitchenAreas() {
        return new ArrayList<>(this.kitchenAreas);
    }

    // Validators
    public boolean alreadyExists(@NotNull String name) {
        return this.kitchenAreas.stream().anyMatch(item -> item.getName().equals(name));
    }

    public boolean isEmpty() {
        return this.kitchenAreas.isEmpty();
    }

    // -- PRIVATE --

}
