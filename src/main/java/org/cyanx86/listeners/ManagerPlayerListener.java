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
import org.cyanx86.classes.GameAreaPropertiesAssistant;
import org.cyanx86.classes.GameRound;
import org.cyanx86.utils.Enums;
import org.cyanx86.utils.Messenger;

import java.util.Objects;

public class ManagerPlayerListener implements Listener {

    // -- [[ ATTRIBUTES ]] --

    // -- PUBLIC --

    // -- PRIVATE --
    private final OverCrafted master = OverCrafted.getInstance();

    // -- [[ METHODS ]] --

    // -- PUBLIC --
    @EventHandler
    public void onManagerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if(!player.hasPermission("overcrafted.manager"))
            return;

        // Register GameAreaCornerAssistant
        master.getGameAreaPropertiesAssistantManager().signInAssistant(player);

        Messenger.msgToConsole(OverCrafted.prefix + player.getName() + " registro su asistente.");
    }

    @EventHandler
    public void onManagerDisconnects(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (master.getGameAreaPropertiesAssistantManager().eraseAssistant(player) == Enums.ListResult.NOT_FOUND)
            return;
        Messenger.msgToConsole(OverCrafted.prefix + player.getName() + " dispuso su asistente.");
    }

    @EventHandler
    public void onManagerInteracts(PlayerInteractEvent event) {
        if (!event.getPlayer().hasPermission("overcrafted.manager"))
            return;
        this.onManagerCreatesGameAreaCorner(event);
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
    private void onManagerCreatesGameAreaCorner(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        GameAreaPropertiesAssistant gapAssistant = master.getGameAreaPropertiesAssistantManager()
                .getAssistantByName(player.getName());

        if (
            item == null ||
            !(
                event.getAction().equals(Action.RIGHT_CLICK_BLOCK) &&
                Objects.equals(event.getHand(), EquipmentSlot.HAND) &&
                item.getType() == Material.IRON_SHOVEL
            )
        )
            return;

        if (gapAssistant == null) {
            Messenger.msgToSender(
                player,
                OverCrafted.prefix + "&cNo hay instancia de Asistente iniciada."
            );
            return;
        }

        Location blockLocat;
        int cornerIndex = gapAssistant.getCornerIndex();
        try {
            blockLocat = Objects.requireNonNull(event.getClickedBlock()).getLocation();
        } catch (Exception ignored) { return; }

        gapAssistant.setCorner(blockLocat);

        Messenger.msgToSender(
            player,
            OverCrafted.prefix + "&aEsquina " + (cornerIndex + 1) +
                    " asignada en x:" + blockLocat.getBlockX() +
                    ", y:" + blockLocat.getBlockY() +
                    ", z:" + blockLocat.getBlockZ()
        );
    }

}
