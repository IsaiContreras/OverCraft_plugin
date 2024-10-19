package org.cyanx86.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import org.cyanx86.OverCrafted;
import org.cyanx86.config.GeneralSettings;
import org.cyanx86.config.Locale;
import org.cyanx86.utils.Messenger;

import org.jetbrains.annotations.NotNull;

public class ReloadSettingsCommand implements CommandExecutor {

    // -- [[ ATTRIBUTES ]] --

    // -- PUBLIC --

    // -- PRIVATE --
    private final OverCrafted master = OverCrafted.getInstance();
    private final Locale locale = GeneralSettings.getInstance().getLocale();

    // -- [[ METHODS ]] --

    // -- PUBLIC --
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        this.handleSubcommands(sender, args);
        return true;
    }

    // -- PRIVATE --
    private void handleSubcommands(CommandSender sender, String[] args) {
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
            case "help":        // subcommand Help
                this.scmHelp(sender);
                break;
            case "all":         // subcommand Create
                this.scmAll(sender);
                break;
            case "general":     // subCommand ResetSpawns
                this.scmGeneral(sender);
                break;
            case "kitchen":     // subCommand List
                this.scmKitchen(sender);
                break;
            case "recipes":     // subCommand Info
                this.scmRecipes(sender);
                break;
            case "oreblocks":   // subcommand Delete
                this.scmOreBlocks(sender);
                break;
            default:
                this.scmHelp(sender);
                break;
        }
    }

    // Subcommand
    private void scmHelp(CommandSender sender) {
        Messenger.msgToSender(sender, "&f&l------ OVERCRAFTED ------");
        Messenger.msgToSender(sender,
                "&7 [[ " + this.locale.getStr("common-messages.command") + " /reloadsettings ]]");
        Messenger.msgToSender(sender, "&7- /reloadsettings help");
        Messenger.msgToSender(sender, "&7- /reloadsettings all");
        Messenger.msgToSender(sender, "&7- /reloadsettings general");
        Messenger.msgToSender(sender, "&7- /reloadsettings kitchen");
        Messenger.msgToSender(sender, "&7- /reloadsettings recipes");
        Messenger.msgToSender(sender, "&7- /reloadsettings oreblocks");
    }

    private void scmAll(CommandSender sender) {
        this.reloadGeneral(sender);
        this.reloadKitchen(sender);
        this.reloadRecipes(sender);
        this.reloadOreBlocks(sender);
    }

    private void scmGeneral(CommandSender sender) {
        this.reloadGeneral(sender);
    }

    private void scmKitchen(CommandSender sender) {
        this.reloadKitchen(sender);
    }

    private void scmRecipes(CommandSender sender) {
        this.reloadRecipes(sender);
    }

    private void scmOreBlocks(CommandSender sender) {
        this.reloadOreBlocks(sender);
    }

    // Subprocedures
    private void reloadGeneral(@NotNull CommandSender sender) {
        if (!GeneralSettings.getInstance().reload())
            Messenger.msgToSender(
                sender,
                OverCrafted.prefix + this.locale.getStr("reload-settings-messages.not-reloaded-general")
            );
        else
            Messenger.msgToSender(
                sender,
                OverCrafted.prefix + this.locale.getStr("reload-settings-messages.reloaded-general")
            );
    }

    private void reloadKitchen(@NotNull CommandSender sender) {
        if (!master.getKitchenAreaLoader().reload())
            Messenger.msgToSender(
                sender,
                OverCrafted.prefix + this.locale.getStr("reload-settings-messages-not-reloaded-kitchen")
            );
        else
            Messenger.msgToSender(
                sender,
                OverCrafted.prefix + this.locale.getStr("reload-settings-messages-reloaded-kitchen")
            );
    }

    private void reloadRecipes(@NotNull CommandSender sender) {
        if (!master.getRecipesBonus().reload())
            Messenger.msgToSender(
                sender,
                OverCrafted.prefix + this.locale.getStr("reload-settings-messages.not-reloaded-recipes")
            );
        else
            Messenger.msgToSender(
                sender,
                OverCrafted.prefix + this.locale.getStr("reload-settings-messages.reloaded-recipes")
            );
    }

    private void reloadOreBlocks(@NotNull CommandSender sender) {
        if (!master.getOreBlocks().reload())
            Messenger.msgToSender(
                sender,
                OverCrafted.prefix + this.locale.getStr("reload-settings-messages.not-reloaded-oreblocks")
            );
        else
            Messenger.msgToSender(
                sender,
                OverCrafted.prefix + this.locale.getStr("reload-settings-messages.reloaded-oreblocks")
            );
    }

}
