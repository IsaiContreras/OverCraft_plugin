package org.cyanx86.utils;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Furnace;
import org.bukkit.entity.Entity;

import org.bukkit.entity.Item;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.Recipe;
import org.cyanx86.OverCrafted;
import org.cyanx86.classes.GameArea;

import java.util.*;

import org.jetbrains.annotations.NotNull;

public class Functions {

    static final private OverCrafted master = OverCrafted.getInstance();

    static public boolean blockBelongsGameArea(@NotNull Block block) {
        for (GameArea gmaItem : master.getGameAreaManager().getGameAreas()) {
            if (!Objects.equals(gmaItem.getWorld(), block.getWorld().getName()))
                continue;
            if (gmaItem.isPointInsideBoundaries(block.getLocation())) {
                return true;
            }
        }
        return false;
    }

    static public boolean entityBelongsGameArea(@NotNull Entity entity) {
        for (GameArea gmaItem : master.getGameAreaManager().getGameAreas()) {
            if (!Objects.equals(gmaItem.getWorld(), entity.getWorld().getName()))
                continue;
            if (gmaItem.isPointInsideBoundaries(entity.getLocation())) {
                return true;
            }
        }
        return false;
    }

    static public GameArea getGameAreaFromLocation(@NotNull Location location) {
        for (GameArea gmaItem : master.getGameAreaManager().getGameAreas())
            if (
                gmaItem.getWorld().equals(Objects.requireNonNull(location.getWorld()).getName()) &&
                gmaItem.isPointInsideBoundaries(location)
            )
                return gmaItem;
        return null;
    }

    static public GameArea getGameAreaByName(@NotNull String name) {
        Optional<GameArea> query = master.getGameAreaManager()
                .getGameAreas()
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

    static public boolean isSmeltable(@NotNull Material itemMaterial) {
        for (Iterator<Recipe> it = Bukkit.recipeIterator(); it.hasNext(); ) {
            if (it.next() instanceof FurnaceRecipe furnaceRecipe && furnaceRecipe.getInput().getType() == itemMaterial)
                return true;
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
