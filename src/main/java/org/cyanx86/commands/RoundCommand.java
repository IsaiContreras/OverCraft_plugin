package org.cyanx86.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import org.bukkit.entity.Player;
import org.cyanx86.OverCrafted;
import org.cyanx86.classes.GameRound;
import org.cyanx86.classes.KitchenArea;
import org.cyanx86.config.GeneralSettings;
import org.cyanx86.config.Locale;
import org.cyanx86.utils.Messenger;

import java.util.Map;

import org.jetbrains.annotations.NotNull;

public class RoundCommand implements CommandExecutor {

    // -- [[ ATTRIBUTES ]] --

    // -- PUBLIC --

    // -- PRIVATE --
    private final OverCrafted master = OverCrafted.getInstance();
    private final Locale locale = GeneralSettings.getInstance().getLocale();

    // -- [[ METHODS ]] --

    // -- PUBLIC --
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, String[] args) {
        this.handleSubcommands(sender, args);
        return true;
    }

    // -- PRIVATE --
    private void handleSubcommands(@NotNull CommandSender sender, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            Messenger.msgToSender(
                sender,
                OverCrafted.prefix + this.locale.getStr("common-messages.console-command")
            );
            return;
        }
        if (!sender.hasPermission("overcrafted.manager")) {
            Messenger.msgToSender(
                sender,
                OverCrafted.prefix + this.locale.getStr("common-messages.no-permissions")
            );
            return;
        }
        if (args.length == 0) {
            this.scmHelp(sender);
            return;
        }

        switch (args[0].toLowerCase()) {
            case "help":                // subCommand Help
                this.scmHelp(sender);
                break;
            case "start":               // subCommand StartRound
                this.scmStartRound(sender);
                break;
            case "terminate":           // subCommand EndRound
                this.scmEndRound(sender);
                break;
            case "results":             // subCommand RoundResults
                this.scmResults(sender);
                break;
            case "kitchen":             // subCommand SelectKitchen
                this.scmKitchenSelect(sender, args);
                break;
            case "addplayer":           // subCommand AddPlayer
                this.scmAddPlayer(sender, args);
                break;
            case "remplayer":           // subCommand RemovePlayer
                this.scmRemPlayer(sender, args);
                break;
            case "players":             // subCommand ShowPlayers
                this.scmShowPlayers(sender);
                break;
            case "resetplayers":        // subCommand ResetPlayers
                this.scmResetPlayers(sender);
                break;
            default:
                this.scmHelp(sender);
                break;
        }
    }

    // Subcommands
    private void scmHelp(@NotNull CommandSender sender) {
        Messenger.msgToSender(sender, "&f&l------ OVERCRAFTED ------");
        Messenger.msgToSender(sender,
                "&7 [[" + this.locale.getStr("common-messages.command") + "/round ]]");
        Messenger.msgToSender(sender, "&7- /round help");
        Messenger.msgToSender(sender, "&7- /round start");
        Messenger.msgToSender(sender, "&7- /round terminate");
        Messenger.msgToSender(sender, "&7- /round results");
        Messenger.msgToSender(sender, "&7- /round kitchen <name>");
        Messenger.msgToSender(sender, "&7- /round addplayer <name>");
        Messenger.msgToSender(sender, "&7- /round remplayer <name>");
        Messenger.msgToSender(sender, "&7- /round players");
        Messenger.msgToSender(sender, "&7- /round resetplayers");
    }

    private void scmStartRound(@NotNull CommandSender sender) {
        if (!sender.hasPermission("overcrafted.manager")) {
            Messenger.msgToSender(
                sender,
                OverCrafted.prefix + this.locale.getStr("common-messages.no-permissions")
            );
            return;
        }
        if(!master.getGameRoundManager().startRound()) {
            Messenger.msgToSender(
                sender,
                OverCrafted.prefix + this.locale.getStr("round-messages.requirements-needed")
            );
        }
    }

    private void scmEndRound(@NotNull CommandSender sender) {
        if (!sender.hasPermission("overcrafted.manager")) {
            Messenger.msgToSender(
                sender,
                OverCrafted.prefix + this.locale.getStr("common-messages.no-permissions")
            );
            return;
        }
        if (!master.getGameRoundManager().terminateRound(null)) {
            Messenger.msgToSender(
                sender,
                OverCrafted.prefix + this.locale.getStr("round-messages.unable-cancel")
            );
            return;
        }

        Messenger.msgToSender(
            sender,
            OverCrafted.prefix + locale.getStr("round-messages.cancelled")
        );
    }

    private void scmResults(@NotNull CommandSender sender) {
        GameRound round = master.getGameRoundManager().getGameRound();
        if (round == null || round.getCurrentRoundState() != GameRound.ROUNDSTATE.ENDED) {
            Messenger.msgToSender(
                sender,
                OverCrafted.prefix + this.locale.getStr("round-messages.no-finished-round")
            );
            return;
        }

        Map<String, Object> results = round.getScores();
        if (results == null) {
            Messenger.msgToSender(
                sender,
                OverCrafted.prefix + this.locale.getStr("round-messages.no-finished-round")
            );
            return;
        }

        Messenger.msgToSender(sender, "&f&l------ OVERCRAFTED ------\n");
        Messenger.msgToSender(sender, this.locale.getStr("round-messages.results.title"));
        Messenger.msgToSender(sender,
            "  " + this.locale.getStr("round-messages.results.delivered")
                    .replace("%delivered%", String.valueOf(results.get("delivered"))));
        Messenger.msgToSender(sender,
            "  " + this.locale.getStr("round-messages.results.lost")
                    .replace("%lost%", String.valueOf(results.get("lost"))));
        Messenger.msgToSender(sender,
            "  " + this.locale.getStr("round-messages.results.score")
                    .replace("%score%", String.valueOf(results.get("score"))));
        Messenger.msgToSender(sender,
            "  " + this.locale.getStr("round-messages.results.bonus")
                    .replace("%bonus%", String.valueOf(results.get("bonus"))));
        Messenger.msgToSender(sender,
            "  " + this.locale.getStr("round-messages.results.total")
                    .replace("%total%",
                        String.valueOf(((int)results.get("score") + (int)results.get("bonus")))
                    ));
    }

    private void scmKitchenSelect(@NotNull CommandSender sender, @NotNull String[] args) {
        if (args.length != 2) {
            Messenger.msgToSender(
                sender,
                OverCrafted.prefix + this.locale.getStr("common-messages.invalid-arguments")
            );
            return;
        }

        KitchenArea kitchenArea = master.getKitchenAreaLoader().getByName(args[1].toLowerCase());
        if (kitchenArea == null) {
            Messenger.msgToSender(
                sender,
                OverCrafted.prefix + this.locale.getStr("kitchen-messages.kitchen-not-found")
            );
            return;
        }
        if (!kitchenArea.isValidSetUp()) {
            Messenger.msgToSender(
                sender,
                OverCrafted.prefix + this.locale.getStr("kitchen-messages.requirements-needed")
            );
            return;
        }

        master.getGameRoundManager().setKitchenArea(kitchenArea);

        Messenger.msgToSender(
            sender,
            OverCrafted.prefix +
                    this.locale.getStr("kitchen-messages.kitchen-selected")
                            .replace("%kitchen%", kitchenArea.getName())
        );
    }

    private void scmAddPlayer(@NotNull CommandSender sender, @NotNull String[] args) {
        if (args.length != 2) {
            Messenger.msgToSender(
                sender,
                OverCrafted.prefix + this.locale.getStr("common-messages.invalid-arguments")
            );
            return;
        }

        Player player = Bukkit.getPlayer(args[1]);
        if (player == null) {
            Messenger.msgToSender(
                sender,
                OverCrafted.prefix +
                        this.locale.getStr("playerlist-messages.player-not-found")
                                .replace("%player%", args[1])
            );
            return;
        }

        switch (master.getGameRoundManager().addPlayer(player)) {
            case ERROR -> {
                Messenger.msgToSender(
                    sender,
                    OverCrafted.prefix + this.locale.getStr("kitchen-messages.kitchen-not-selected")
                );
                return;
            }
            case FULL_LIST -> {
                Messenger.msgToSender(
                    sender,
                    OverCrafted.prefix + this.locale.getStr("playerlist-messages.full-list")
                );
                return;
            }
            case ALREADY_IN -> {
                Messenger.msgToSender(
                    sender,
                    OverCrafted.prefix + this.locale.getStr("playerlist-messages.already-listed")
                );
                return;
            }
        }

        Messenger.msgToSender(
            sender,
            OverCrafted.prefix +
                    this.locale.getStr("playerlist-messages.player-added.executor")
                            .replace("%player%", player.getName())
        );
        if (sender != player)
            Messenger.msgToSender(
                player,
                OverCrafted.prefix + this.locale.getStr("playerlist-messages.player-added.player")
            );
    }

    private void scmRemPlayer(@NotNull CommandSender sender, @NotNull String[] args) {
        if (args.length != 2) {
            Messenger.msgToSender(
                sender,
                OverCrafted.prefix + this.locale.getStr("common-messages.invalid-arguments")
            );
            return;
        }

        Player player = Bukkit.getPlayer(args[1]);
        if (player == null) {
            Messenger.msgToSender(
                sender,
                OverCrafted.prefix +
                        this.locale.getStr("playerlist-messages.player-not-found")
                                .replace("%player%", args[1])
            );
            return;
        }

        switch (master.getGameRoundManager().removePlayer(player)) {
            case EMPTY_LIST -> {
                Messenger.msgToSender(
                    sender,
                    OverCrafted.prefix + locale.getStr("playerlist-messages.emtpy-list")
                );
                return;
            }
            case NOT_FOUND -> {
                Messenger.msgToSender(
                        sender,
                        OverCrafted.prefix + locale.getStr("playerlist-messages.player-not-found-list")
                );
                return;
            }
        }

        Messenger.msgToSender(
            sender,
            OverCrafted.prefix +
                    this.locale.getStr("playerlist-messages.player-removed.executor")
                            .replace("%player%", player.getName())
        );
        if (sender != player)
            Messenger.msgToSender(
                player,
                OverCrafted.prefix + this.locale.getStr("playerlist-messages.player-removed.player")
            );
    }

    private void scmShowPlayers(@NotNull CommandSender sender) {
        Messenger.msgToSender(sender, "&f&l------ OVERCRAFTED ------\n");
        Messenger.msgToSender(sender, this.locale.getStr("playerlist-messages.show-player-list.title"));

        for(int i = 0; i < 4; i++) {
            Player current = null;

            try {
                current = master.getGameRoundManager().getGamePlayers().get(i);
            } catch (Exception ignored) { }

            if (current == null) {
                Messenger.msgToSender(
                    sender,
                    this.locale.getStr("playerlist-messages.show-player-list.empty-slot")
                            .replace("%index%", String.valueOf(i + 1))
                );
            } else {
                Messenger.msgToSender(sender, "&7" + (i + 1) + ".- " + "&o" + current.getName());
            }
        }
    }

    private void scmResetPlayers(@NotNull CommandSender sender) {
        switch(master.getGameRoundManager().clearPlayerList()) {
            case EMPTY_LIST -> {
                Messenger.msgToSender(
                    sender,
                    OverCrafted.prefix +
                            this.locale.getStr("playerlist-messages.empty-list")
                );
                return;
            }
            case SUCCESS -> {
                Messenger.msgToSender(
                    sender,
                    OverCrafted.prefix + this.locale.getStr("playerlist-messages.player-list-cleared")
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
                    OverCrafted.prefix + this.locale.getStr("playerlist-messages.player-removed.player")
                );
            }
        }
    }

}
