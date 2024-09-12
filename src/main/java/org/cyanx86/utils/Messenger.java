package org.cyanx86.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import org.jetbrains.annotations.NotNull;

public class Messenger {

    public static void msgToConsole(@NotNull String message) {
        Bukkit.getConsoleSender().sendMessage(
            coloredText(message)
        );
    }

    public static void msgToSender(@NotNull CommandSender sender, @NotNull String message) {
        sender.sendMessage(
            coloredText(message)
        );
    }

    public static String coloredText(@NotNull String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static void msgToMultPlayers(@NotNull List<Player> players, @NotNull String message) {
        for (Player player : players)
            msgToSender(player, message);
    }

}
