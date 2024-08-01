package org.cyanx86.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

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

}
