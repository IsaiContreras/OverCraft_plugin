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
    private final OverCrafted master;

    // -- [[ METHODS ]] --

    // -- Public
    public MainCommand(OverCrafted master) {
        this.master = master;
    }

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
            case "help":            // subcommand Help
                this.scmHelp(sender);
                break;
            default:
                this.scmHelp(sender);
                break;
        }
    }

    // Subcommands
    private void scmHelp(CommandSender sender) {

    }

}
