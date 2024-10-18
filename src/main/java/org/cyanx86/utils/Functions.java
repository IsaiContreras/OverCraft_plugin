package org.cyanx86.utils;

import org.bukkit.*;
import org.bukkit.block.BlastFurnace;
import org.bukkit.block.Block;
import org.bukkit.block.Furnace;
import org.bukkit.block.Smoker;
import org.bukkit.entity.Entity;

import org.bukkit.inventory.BlastingRecipe;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.SmokingRecipe;
import org.cyanx86.OverCrafted;
import org.cyanx86.classes.KitchenArea;

import java.util.*;

import org.jetbrains.annotations.NotNull;

public class Functions {

    static final private OverCrafted master = OverCrafted.getInstance();

    static public boolean blockBelongsKitchenArea(@NotNull Block block) {
        for (KitchenArea ktcItem : master.getKitchenAreaLoader().getKitchenAreas()) {
            if (!Objects.equals(ktcItem.getWorld(), block.getWorld().getName()))
                continue;
            if (ktcItem.isPointInsideBoundaries(block.getLocation())) {
                return true;
            }
        }
        return false;
    }

    static public boolean entityBelongsKitchenArea(@NotNull Entity entity) {
        for (KitchenArea ktcItem : master.getKitchenAreaLoader().getKitchenAreas()) {
            if (!Objects.equals(ktcItem.getWorld(), entity.getWorld().getName()))
                continue;
            if (ktcItem.isPointInsideBoundaries(entity.getLocation())) {
                return true;
            }
        }
        return false;
    }

    static public KitchenArea getKitchenAreaFromLocation(@NotNull Location location) {
        for (KitchenArea ktcItem : master.getKitchenAreaLoader().getKitchenAreas())
            if (
                ktcItem.getWorld().equals(Objects.requireNonNull(location.getWorld()).getName()) &&
                ktcItem.isPointInsideBoundaries(location)
            )
                return ktcItem;
        return null;
    }

    static public KitchenArea getKitchenAreaByName(@NotNull String name) {
        Optional<KitchenArea> query = master.getKitchenAreaLoader()
                .getKitchenAreas()
                .stream()
                .filter(item -> item.getName().equals(name))
                .findFirst();
        return query.orElse(null);
    }

    static public int getRandomIntNumber(int min, int max) {
        return new Random().nextInt((max - min) + 1) + min;
    }

    static public float getRandomFloatNumber() {
        return new Random().nextFloat();
    }

    static public boolean isSmeltable(@NotNull Material itemMaterial, @NotNull Furnace furnace) {
        for (Iterator<Recipe> it = Bukkit.recipeIterator(); it.hasNext(); ) {
            if (furnace instanceof BlastFurnace) {
                if (
                    it.next() instanceof BlastingRecipe blastingRecipe &&
                    blastingRecipe.getInput().getType().equals(itemMaterial)
                )
                    return true;
            }
            else if (furnace instanceof Smoker) {
                if (
                    it.next() instanceof SmokingRecipe smokingRecipe &&
                    smokingRecipe.getInput().getType().equals(itemMaterial)
                )
                    return true;
            }
            else {
                if (
                    it.next() instanceof FurnaceRecipe furnaceRecipe &&
                    furnaceRecipe.getInput().getType().equals(itemMaterial)
                )
                    return true;
            }
        }

        return false;
    }

    static public Map<String, Object> serializeNote(@NotNull Note note) {
        Map<String, Object> data = new HashMap<>();

        data.put("octave", note.getOctave());
        data.put("tone", note.getTone().name());
        data.put("sharped", note.isSharped());

        return data;
    }

    static public Note deserializeNote(@NotNull Map<String, Object> args) {
        Note.Tone tone;
        try {
             tone = Note.Tone.valueOf((String)args.get("tone"));
        } catch (NullPointerException | ClassCastException e){
            return null;
        }

        return new Note(
            (int)args.get("octave"),
            tone,
            (boolean)args.get("sharped")
        );
    }

}
