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
    private OverCrafted master;

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
        if (args.length == 0) {
            this.scmHelp(sender);
            return;
        }

        String mainArg = args[0].toLowerCase();

        switch (mainArg) {
            case "help":
                this.scmHelp(sender);
                break;
        }
    }

    // Subcommands
    private void scmHelp(CommandSender sender) {
        Messenger.sendToSender(sender, "&f&l------ OVERCRAFTED ------\n");
        Messenger.sendToSender(sender, "&7- /overcrafted help\tAyuda del plugin.");
    }

}
