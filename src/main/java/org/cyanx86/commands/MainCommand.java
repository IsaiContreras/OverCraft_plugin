package org.cyanx86.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import org.cyanx86.OverCrafted;
import org.cyanx86.utils.Messenger;

public class MainCommand implements CommandExecutor {

    // -- [[ ATTRIBUTES ]] --

    // -- Public

    // -- Private
    private final OverCrafted master = OverCrafted.getInstance();

    // -- [[ METHODS ]] --

    // -- Public

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        this.handleSubcommands(sender, args);
        return true;
    }

    // -- Private
    private void handleSubcommands(CommandSender sender, String[] args) {
        if (!sender.hasPermission("overcrafted.manager")) {
            Messenger.msgToSender(
                sender,
                OverCrafted.prefix + "&cNo tienes permiso para usar este comando." // TODO: No permission message.
            );
            return;
        }
        if (args.length == 0) {
            this.scmHelp(sender);
            return;
        }

        String mainArg = args[0].toLowerCase();

        switch (mainArg) {
            case "help":            // subCommand Help
                this.scmHelp(sender);
                break;
            case "roundtime":       // subCommand SetRoundTime
                this.scmSetRoundTime(sender, args);
                break;
            case "startround":      // subCommand StartRound
                this.scmStartRound(sender);
                break;
            case "endround":        // subcommand EndRound
                this.scmEndRound(sender);
                break;
            default:
                this.scmHelp(sender);
                break;
        }
    }

    // Subcommands
    private void scmHelp(CommandSender sender) {
        Messenger.msgToSender(sender, "&f&l------ OVERCRAFTED ------");
        Messenger.msgToSender(sender, "&7 [[ Comando /overcrafted ]]");
        Messenger.msgToSender(sender, "&7- /overcrafted help");
        Messenger.msgToSender(sender, "&7- /overcrafted roundtime <tiempo>");
        Messenger.msgToSender(sender, "&7- /overcrafted startround");
        Messenger.msgToSender(sender, "&7- /overcrafted endround");
    }

    private void scmSetRoundTime(CommandSender sender, String[] args) {
        if (args.length != 2) {
            return;
        }

        int time;
        try {
            time = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            return;
        }

        master.getGameRoundManager().setRoundTime(time);
    }

    private void scmStartRound(CommandSender sender) {
        master.getGameRoundManager().startRound();
    }

    private void scmEndRound(CommandSender sender) {
        if (!master.getGameRoundManager().terminateRound()) {
            Messenger.msgToSender(
                sender,
                OverCrafted.prefix + "&cNo se pudo cancelar la ronda. Aun no ha iniciado o ya ha acabado."
            );
            return;
        }

        Messenger.msgToSender(
            sender,
            OverCrafted.prefix + "&aRonda cancelada."
        );
    }

}
