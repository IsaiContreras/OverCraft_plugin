package org.cyanx86.managers;

import org.bukkit.entity.Player;

import org.cyanx86.OverCrafted;
import org.cyanx86.classes.GameAreaCornerAssistant;
import org.cyanx86.utils.Enums.ListResult;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GameAreaCornerAssistantManager {

    // -- [[ ATTRIBUTES ]] --

    // -- Public

    // -- Private
    private final OverCrafted master;

    private final Map<UUID, GameAreaCornerAssistant> assistants = new HashMap<>();
    private final Map<String, UUID> playerUUIDs = new HashMap<>();

    // -- [[ METHODS ]] --

    // -- Public
    public GameAreaCornerAssistantManager(OverCrafted master) {
        this.master = master;
    }

    public ListResult signInAssistant(Player player) {
        if (assistants.containsKey(player.getUniqueId()))
            return ListResult.ALREADY_IN;
        assistants.put(player.getUniqueId(), new GameAreaCornerAssistant());
        playerUUIDs.put(player.getName(), player.getUniqueId());
        return ListResult.SUCCESS;
    }

    public ListResult eraseAssistant(Player player) {
        if (!playerUUIDs.containsKey(player.getName()))
            return ListResult.NOT_FOUND;
        UUID playerUUID = playerUUIDs.get(player.getName());
        assistants.remove(playerUUID);
        playerUUIDs.remove(player.getName());
        return ListResult.SUCCESS;
    }

    public GameAreaCornerAssistant getAssistantByName(String name){
        UUID playerUUID = playerUUIDs.get(name);
        if (playerUUID == null)
            return null;
        return assistants.get(playerUUID);
    }

    public GameAreaCornerAssistant getAssistantByUUID(UUID uuid) {
        return assistants.get(uuid);
    }

    // -- Private

}
