package org.cyanx86.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Text;

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

    public static void msgToPlayer(@NotNull Player player, @NotNull String message) {
        player.sendMessage(
            coloredText(message)
        );
    }

    public static String coloredText(@NotNull String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static void titleToPlayer(@NotNull Player player, @NotNull String message1, @NotNull String message2, int fadeIn, int time, int fadeOut) {
        player.sendTitle(
            coloredText(message1),
            coloredText(message2),
            fadeIn,
            time,
            fadeOut
        );
    }

    public static void actionBarToPlayer(@NotNull Player player, @NotNull String message) {
        player.spigot().sendMessage(
            ChatMessageType.ACTION_BAR,
            TextComponent.fromLegacyText(coloredText(message))
        );
    }

}
