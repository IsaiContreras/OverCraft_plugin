package org.cyanx86.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import org.cyanx86.OverCrafted;
import org.cyanx86.config.GeneralSettings;
import org.cyanx86.config.Locale;
import org.cyanx86.utils.Messenger;

import org.jetbrains.annotations.NotNull;

public class PlayerListCommand implements CommandExecutor {

    // -- [[ ATRIBUTES ]] --

    // -- PUBLIC --

    // -- PRIVATE --
    private final OverCrafted master = OverCrafted.getInstance();
    private final Locale locale = GeneralSettings.getInstance().getLocale();

    // -- [[ METHODS ]] --

    // -- PUBLIC --

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, String[] args) {
        this.handleSubcommand(sender, args);
        return true;
    }

    // -- PRIVATE --
    private void handleSubcommand(@NotNull CommandSender sender, @NotNull String[] args) {
        if(!sender.hasPermission("overcrafted.manager")) {
            Messenger.msgToSender(
                sender,
                OverCrafted.prefix + this.locale.getStr("common.no-permissions")
            );
            return;
        }
        if (args.length == 0) {
            this.scmHelp(sender);
            return;
        }

        switch (args[0].toLowerCase()) {
            case "help":        // subcommand Help.
                this.scmHelp(sender);
                break;
            case "add":         // subcommand AddPlayer.
                this.scmAdd(sender, args);
                break;
            case "rm":          // subcommand RemovePlayer.
                this.scmRemove(sender, args);
                break;
            case "show":        // subcommand ShowList.
                this.scmShow(sender);
                break;
            case "clear":       // subcommand ClearList.
                this.scmClear(sender);
                break;
        }
    }

    // -- Subcommands
    private void scmHelp(@NotNull CommandSender sender) {
        // TODO: Language location of Help Page
        Messenger.msgToSender(sender, "&f&l------ OVERCRAFTED ------\n");
        Messenger.msgToSender(sender, "&7 [[ " + this.locale.getStr("common.command") + " /playerlist ]]");
        Messenger.msgToSender(sender, "&7- /playerlist help");
        Messenger.msgToSender(sender, "&7- /playerlist add <jugador>");
        Messenger.msgToSender(sender, "&7- /playerlist rm <jugador>");
        Messenger.msgToSender(sender, "&7- /playerlist show");
        Messenger.msgToSender(sender, "&7- /playerlist clear");
    }

    private void scmAdd(@NotNull CommandSender sender, @NotNull String[] args) {
        if (args.length != 2) {
            Messenger.msgToSender(
                sender,
                OverCrafted.prefix + this.locale.getStr("common.invalid-arguments")
            );
            return;
        }

        Player player = Bukkit.getPlayer(args[1]);
        if (player == null) {
            Messenger.msgToSender(
                sender,
                OverCrafted.prefix +
                        this.locale.getStr("playerlist-command.player-not-found")
                                .replace("%player%", args[1])
            );
            return;
        }

        switch (master.getGameRoundManager().addPlayer(player)) {
            case ERROR -> {
                Messenger.msgToSender(
                    sender,
                    OverCrafted.prefix + this.locale.getStr("playerlist-command.gamearea-not-selected")
                );
                return;
            }
            case FULL_LIST -> {
                Messenger.msgToSender(
                    sender,
                    OverCrafted.prefix + this.locale.getStr("playerlist-command.full-list")
                );
                return;
            }
            case ALREADY_IN -> {
                Messenger.msgToSender(
                    sender,
                    OverCrafted.prefix + this.locale.getStr("playerlist-command.player-already-listed")
                );
                return;
            }
        }

        Messenger.msgToSender(
            sender,
            OverCrafted.prefix +
                    this.locale.getStr("playerlist-command.player-added.executor")
                            .replace("%player%", player.getName())
        );
        if (sender != player)
            Messenger.msgToSender(
                player,
                OverCrafted.prefix + this.locale.getStr("playerlist-command.player-added.player")
            );
    }

    private void scmRemove(@NotNull CommandSender sender, @NotNull String[] args) {
        if (args.length != 2) {
            Messenger.msgToSender(
                sender,
                OverCrafted.prefix + this.locale.getStr("common.invalid-arguments")
            );
            return;
        }

        Player player = Bukkit.getPlayer(args[1]);
        if (player == null) {
            Messenger.msgToSender(
                sender,
                OverCrafted.prefix + this.locale.getStr("playerlist-command.player-not-found")
            );
            return;
        }

        switch (master.getGameRoundManager().removePlayer(player)) {
            case EMPTY_LIST -> {
                Messenger.msgToSender(
                    sender,
                    OverCrafted.prefix + this.locale.getStr("playerlist-command.empty-list")
                );
                return;
            }
            case NOT_FOUND -> {
                Messenger.msgToSender(
                    sender,
                    OverCrafted.prefix +
                            this.locale.getStr("playerlist-command.player-not-found-list")
                                    .replace("%player%", player.getName())
                );
                return;
            }
        }

        Messenger.msgToSender(
            sender,
            OverCrafted.prefix +
                    this.locale.getStr("playerlist-command.player-removed.executor")
                            .replace("%player%", player.getName())
        );
        if (sender != player)
            Messenger.msgToSender(
                player,
                OverCrafted.prefix + this.locale.getStr("playerlist-command.player-removed.player")
            );
    }

    private void scmShow(@NotNull CommandSender sender) {
        Messenger.msgToSender(sender, "&f&l------ OVERCRAFTED ------\n");
        Messenger.msgToSender(sender, this.locale.getStr("playerlist-command.show-player-list.title"));

        for(int i = 0; i < 4; i++) {
            Player current = null;

            try {
                current = master.getGameRoundManager().getGamePlayers().get(i);
            } catch (Exception ignored) { }

            if (current == null) {
                Messenger.msgToSender(
                        sender,
                        this.locale.getStr("playerlist-command.show-player-list.empty-slot")
                                .replace("%index%", String.valueOf(i + 1))
                );
            } else {
                Messenger.msgToSender(sender, "&7" + (i + 1) + ".- " + "&o" + current.getName());
            }
        }
    }

    private void scmClear(@NotNull CommandSender sender) {
        switch(master.getGameRoundManager().clearPlayerList()) {
            case EMPTY_LIST -> {
                Messenger.msgToSender(
                    sender,
                    OverCrafted.prefix +
                            this.locale.getStr("playerlist-command.empty-list")
                );
                return;
            }
            case SUCCESS -> {
                Messenger.msgToSender(
                    sender,
                    OverCrafted.prefix + this.locale.getStr("playerlist-command.player-list-cleared")
                );
                return;
            }
        }

        for (Player gamePlayer : master.getGameRoundManager().getGamePlayers()) {
            Player current;
            try {
                current = gamePlayer;
            } catch (Exception ignored) {
                continue;
            }

            if (sender != current) {
                Messenger.msgToSender(
                    current,
                    OverCrafted.prefix + this.locale.getStr("playerlist-command.player-removed.player")
                );
            }
        }
    }

}
