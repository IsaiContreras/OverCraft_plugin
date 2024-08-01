package org.cyanx86.listeners;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.cyanx86.OverCrafted;
import org.cyanx86.utils.Messenger;

import java.util.Objects;

public class PlayerListener implements Listener {

    // -- [[ ATTRIBUTES ]] --

    // -- Public
    private final OverCrafted master;

    // -- Private

    // -- [[ METHODS ]] --

    // -- Public
    public PlayerListener (OverCrafted master) {
        this.master = master;
    }

    @EventHandler
    public void onPlayerClickBlock(PlayerInteractEvent event) {
        int cornerIndex = master.getCornerIndex();
        if (cornerIndex == 0) {
            return;
        }

        Player player = event.getPlayer();
        Action action = event.getAction();
        ItemStack item = event.getItem();

        if (item == null) return;

        if (
            action.equals(Action.RIGHT_CLICK_BLOCK) &&
            Objects.equals(event.getHand(), EquipmentSlot.HAND) &&
            item.getType() == Material.IRON_SHOVEL
        ) {
            Location blockCoords;

            try {
                 blockCoords = Objects.requireNonNull(event.getClickedBlock()).getLocation();
            } catch (Exception ignored) {return;}

            if (!master.setCorner(cornerIndex, blockCoords)) {
                Messenger.msgToSender(
                    player,
                    OverCrafted.prefix + "&cNo se pudo asignar coordenadas."
                );
            }

            master.setCornerIndex(0);
            Messenger.msgToSender(
                player,
                OverCrafted.prefix + "&aEsquina " + cornerIndex +
                    " asignada en x:" + blockCoords.getBlockX() +
                    ", y:" + blockCoords.getBlockY() +
                    ", z:" + blockCoords.getBlockZ()
            );
        }
    }

    // -- Private

}
