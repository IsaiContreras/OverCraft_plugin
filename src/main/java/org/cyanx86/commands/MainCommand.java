package org.cyanx86.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import org.cyanx86.OverCrafted;
import org.cyanx86.classes.GameRound;
import org.cyanx86.config.GeneralSettings;
import org.cyanx86.config.Locale;
import org.cyanx86.utils.Messenger;

import java.util.List;
import java.util.Map;

import org.jetbrains.annotations.NotNull;

public class MainCommand implements CommandExecutor {

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
        if (args.length == 0) {
            this.scmHelp(sender);
            return;
        }

        switch (args[0].toLowerCase()) {
            case "help":            // subCommand Help
                this.scmHelp(sender);
                break;
            case "startround":      // subCommand StartRound
                this.scmStartRound(sender);
                break;
            case "endround":        // subcommand EndRound
                this.scmEndRound(sender);
                break;
            case "results":
                this.scmResults(sender);
                break;
            default:
                this.scmHelp(sender);
                break;
        }
    }

    // Subcommands
    private void scmHelp(@NotNull CommandSender sender) {
        Messenger.msgToSender(sender, "&f&l------ OVERCRAFTED ------");
        Messenger.msgToSender(sender, "&7 [[" + this.locale.getStr("common.command") + "/overcrafted ]]");
        Messenger.msgToSender(sender, "&7- /overcrafted help");
        Messenger.msgToSender(sender, "&7- /overcrafted recipelist");
        Messenger.msgToSender(sender, "&7- /overcrafted startround");
        Messenger.msgToSender(sender, "&7- /overcrafted endround");
        Messenger.msgToSender(sender, "&7- /overcrafted results");
    }

    private void scmStartRound(@NotNull CommandSender sender) {
        if (!sender.hasPermission("overcrafted.manager")) {
            Messenger.msgToSender(
                sender,
                OverCrafted.prefix + this.locale.getStr("common.no-permissions")
            );
            return;
        }
        if(!master.getGameRoundManager().startRound()) {
            Messenger.msgToSender(
                sender,
                OverCrafted.prefix + this.locale.getStr("main-command.round-requirements")
            );
        }
    }

    private void scmEndRound(@NotNull CommandSender sender) {
        if (!sender.hasPermission("overcrafted.manager")) {
            Messenger.msgToSender(
                sender,
                OverCrafted.prefix + this.locale.getStr("common.no-permissions")
            );
            return;
        }
        if (!master.getGameRoundManager().terminateRound(null)) {
            Messenger.msgToSender(
                sender,
                OverCrafted.prefix + this.locale.getStr("main-command.unable-cancel")
            );
            return;
        }

        Messenger.msgToSender(
            sender,
            OverCrafted.prefix + locale.getStr("main-command.round-cancelled")
        );
    }

    private void scmResults(@NotNull CommandSender sender) {
        GameRound round = master.getGameRoundManager().getGameRound();
        if (round == null || round.getCurrentRoundState() != GameRound.ROUNDSTATE.ENDED) {
            Messenger.msgToSender(
                sender,
                OverCrafted.prefix + this.locale.getStr("main-command.no-finished-round")
            );
            return;
        }

        Map<String, Object> results = round.getScores();

        Messenger.msgToSender(sender, "&f&l------ OVERCRAFTED ------\n");
        Messenger.msgToSender(sender, this.locale.getStr("main-command.round-results.title"));
        Messenger.msgToSender(sender,
                "  " + this.locale.getStr("main-command.round-results.delivered")
                        .replace("%delivered%", String.valueOf(results.get("delivered"))));
        Messenger.msgToSender(sender,
                "  " + this.locale.getStr("main-command.round-results.lost")
                        .replace("%lost%", String.valueOf(results.get("lost"))));
        Messenger.msgToSender(sender,
                "  " + this.locale.getStr("main-command.round-results.score")
                        .replace("%score%", String.valueOf(results.get("score"))));
        Messenger.msgToSender(sender,
                "  " + this.locale.getStr("main-command.round-results.bonus")
                        .replace("%bonus%", String.valueOf(results.get("bonus"))));
        Messenger.msgToSender(sender,
                "  " + this.locale.getStr("main-command.round-results.total")
                        .replace("%total%",
                            String.valueOf(((int)results.get("score") + (int)results.get("bonus")))
                        ));
    }

}
