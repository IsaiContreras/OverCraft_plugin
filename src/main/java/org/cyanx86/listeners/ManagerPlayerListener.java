package org.cyanx86.listeners;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import org.cyanx86.OverCrafted;
import org.cyanx86.classes.KitchenArea;
import org.cyanx86.classes.KitchenAreaCreatorAssistant;
import org.cyanx86.classes.GameRound;
import org.cyanx86.classes.SpawnPoint;
import org.cyanx86.config.GeneralSettings;
import org.cyanx86.config.Locale;
import org.cyanx86.utils.Enums;
import org.cyanx86.utils.Functions;
import org.cyanx86.utils.Messenger;

import java.util.Objects;

public class ManagerPlayerListener implements Listener {

    // -- [[ ATTRIBUTES ]] --

    // -- PUBLIC --

    // -- PRIVATE --
    private final OverCrafted master = OverCrafted.getInstance();
    private final Locale locale = GeneralSettings.getInstance().getLocale();

    // -- [[ METHODS ]] --

    // -- PUBLIC --
    @EventHandler
    public void onManagerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if(!player.hasPermission("overcrafted.manager"))
            return;

        // Register GameAreaCornerAssistant
        master.getKitchenAreaCreatorAssistantManager().signInAssistant(player);

        Messenger.msgToConsole(
            OverCrafted.prefix +
            this.locale.getStr("manager-player-listener.kitchen-assistant-registered")
                    .replace("%player%", player.getName())
        );
    }

    @EventHandler
    public void onManagerDisconnects(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (master.getKitchenAreaCreatorAssistantManager().eraseAssistant(player) == Enums.ListResult.NOT_FOUND)
            return;
        Messenger.msgToConsole(
            OverCrafted.prefix +
            this.locale.getStr("manager-player-listener.kitchen-assistant-dispose")
                    .replace("%player%", player.getName())
        );
    }

    @EventHandler
    public void onManagerInteracts(PlayerInteractEvent event) {
        if (!event.getPlayer().hasPermission("overcrafted.manager"))
            return;
        this.onManagerCreatesKitchenAreaCorner(event);
        this.onManagerCreatesKitchenAreaSpawnPoint(event);
    }

    @EventHandler
    public void onPlayerDisconnects(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        GameRound round = master.getGameRoundManager().getGameRound();

        if (round == null || !round.isPlayerInGame(player))
            return;

        round.removePlayer(player);
    }

    // -- PRIVATE --
    private void onManagerCreatesKitchenAreaCorner(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        KitchenAreaCreatorAssistant kacAssistant = master.getKitchenAreaCreatorAssistantManager()
                .getAssistantByName(player.getName());

        if (
            item == null ||
            !(
                event.getAction().equals(Action.RIGHT_CLICK_BLOCK) &&
                Objects.equals(event.getHand(), EquipmentSlot.HAND) &&
                item.getType().equals(Material.IRON_SHOVEL)
            )
        )
            return;

        if (kacAssistant == null) {
            Messenger.msgToSender(
                player,
                OverCrafted.prefix + this.locale.getStr("manager-player-listener.no-assistant-instance")
            );
            return;
        }

        Location blockLocat;
        int cornerIndex = kacAssistant.getCornerIndex();
        try {
            blockLocat = Objects.requireNonNull(event.getClickedBlock()).getLocation();
        } catch (Exception ignored) { return; }

        kacAssistant.setCorner(blockLocat);

        Messenger.msgToSender(
            player,
            OverCrafted.prefix +
                    this.locale.getStr("manager-player-listener.corner-set")
                            .replace("%index%", String.valueOf((cornerIndex + 1)))
                            .replace("%x%", String.valueOf(blockLocat.getBlockX()))
                            .replace("%y%", String.valueOf(blockLocat.getBlockY()))
                            .replace("%z%", String.valueOf(blockLocat.getBlockZ()))
        );
    }

    private void onManagerCreatesKitchenAreaSpawnPoint(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if (
            item == null ||
            !(
                event.getAction().equals(Action.RIGHT_CLICK_AIR) &&
                Objects.equals(event.getHand(), EquipmentSlot.HAND) &&
                item.getType().equals(Material.NETHER_STAR)
            )
        )
            return;

        Location location = player.getLocation();
        KitchenArea kitchenArea = Functions.getKitchenAreaFromLocation(location);
        if (kitchenArea == null) {
            Messenger.msgToSender(
                player,
                OverCrafted.prefix + locale.getStr("kitchen-messages.not-in-kitchen")
            );
            return;
        }

        switch (kitchenArea.addSpawnPoint(new SpawnPoint(location))) {
            case INVALID_ITEM -> {
                Messenger.msgToSender(
                    player,
                    OverCrafted.prefix + locale.getStr("kitchen-messages.spawnpoint-outside-boundaries")
                );
                return;
            }
            case FULL_LIST -> {
                Messenger.msgToSender(
                    player,
                    OverCrafted.prefix + locale.getStr("kitchen-messages.spawnpoint-full-list")
                );
                return;
            }
        }

        Messenger.msgToSender(
            player,
            OverCrafted.prefix +
                    this.locale.getStr("kitchen-messages.spawnpoint-added")
                            .replace("%x%", String.valueOf(location.getBlockX()))
                            .replace("%y%", String.valueOf(location.getBlockY()))
                            .replace("%z%", String.valueOf(location.getBlockZ()))
                            .replace("%kitchen%", kitchenArea.getName())
        );
    }

}
