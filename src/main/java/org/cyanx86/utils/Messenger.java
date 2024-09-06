package org.cyanx86.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class Messenger {

    public static void msgToConsole(String message) {
        Bukkit.getConsoleSender().sendMessage(
            coloredText(message)
        );
    }

    public static void msgToSender(CommandSender sender, String message) {
        sender.sendMessage(
            coloredText(message)
        );
    }

    public static String coloredText(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static void msgToMultPlayers(List<Player> players, String message) {
        for (Player player : players) {
            msgToSender(player, message);
        }
    }

}
