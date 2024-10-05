package org.cyanx86.managers;

import org.bukkit.entity.Player;

import org.cyanx86.OverCrafted;
import org.cyanx86.classes.GameAreaPropertiesAssistant;
import org.cyanx86.utils.Enums.ListResult;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.jetbrains.annotations.NotNull;

public class GameAreaPropertiesAssistantManager {

    // -- [[ ATTRIBUTES ]] --

    // -- PUBLIC --

    // -- PRIVATE --
    private final OverCrafted master = OverCrafted.getInstance();

    private final Map<UUID, GameAreaPropertiesAssistant> assistants = new HashMap<>();
    private final Map<String, UUID> playerUUIDs = new HashMap<>();

    // -- [[ METHODS ]] --

    // -- PUBLIC --

    public ListResult signInAssistant(@NotNull Player player) {
        if (this.assistants.containsKey(player.getUniqueId()))
            return ListResult.ALREADY_IN;

        this.assistants.put(player.getUniqueId(), new GameAreaPropertiesAssistant());
        this.playerUUIDs.put(player.getName(), player.getUniqueId());

        return ListResult.SUCCESS;
    }

    public ListResult eraseAssistant(@NotNull Player player) {
        if (!this.playerUUIDs.containsKey(player.getName()))
            return ListResult.NOT_FOUND;

        UUID playerUUID = this.playerUUIDs.get(player.getName());
        this.assistants.remove(playerUUID);
        this.playerUUIDs.remove(player.getName());

        return ListResult.SUCCESS;
    }

    public GameAreaPropertiesAssistant getAssistantByName(@NotNull String name) {
        UUID playerUUID = this.playerUUIDs.get(name);
        if (playerUUID == null)
            return null;
        return this.assistants.get(playerUUID);
    }

    // -- PRIVATE --

}
