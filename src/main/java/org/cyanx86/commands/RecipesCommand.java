package org.cyanx86.commands;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.cyanx86.OverCrafted;
import org.cyanx86.classes.KitchenArea;
import org.cyanx86.config.GeneralSettings;
import org.cyanx86.config.Locale;
import org.cyanx86.utils.Enums;
import org.cyanx86.utils.Functions;
import org.cyanx86.utils.Messenger;
import org.jetbrains.annotations.NotNull;

public class RecipesCommand implements CommandExecutor {


    // -- [[ ATTRIBUTES ]] --

    // -- PUBLIC --

    // -- PRIVATE --
    private final Locale locale = GeneralSettings.getInstance().getLocale();

    // -- [[ METHODS ]] --

    // -- PUBLIC --
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
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
            case "help":                                // subCommand Help
                this.scmHelp(sender);
                break;
            case "add":                                 // subCommand Add
                this.scmAdd(sender, args);
                break;
            case "delete":                              // subCommand Delete
                this.scmDelete(sender, args);
                break;
            case "clear":                               // subCommand Clear
                this.scmClear(sender, args);
                break;
            case "of":                                  // subCommand Show
                this.scmShowRecipes(sender, args);
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
                "&7 [[" + this.locale.getStr("common-messages.command") + "/recipes ]]");
        Messenger.msgToSender(sender, "&7- /recipes help");
        Messenger.msgToSender(sender, "&7- /recipes add <kitchen | OPC> <material>");
        Messenger.msgToSender(sender, "&7- /recipes delete <kitchen | OPC> <material>");
        Messenger.msgToSender(sender, "&7- /recipes of <kitchen>");
    }

    private void scmAdd(@NotNull CommandSender sender, String[] args) {
        if (args.length == 3)
            this.addSpecificKitchen(sender, args);
        else if (args.length == 2)
            this.addAutoDetectKitchen(sender, args);
        else
            Messenger.msgToSender(
                sender,
                OverCrafted.prefix + locale.getStr("common-messages.invalid-arguments")
            );
    }

    private void scmDelete(@NotNull CommandSender sender, String[] args) {
        if (args.length == 3)
            this.deleteSpecificKitchen(sender, args);
        else if (args.length == 2)
            this.deleteAutoDetectKitchen(sender, args);
        else
            Messenger.msgToSender(
                sender,
                OverCrafted.prefix + locale.getStr("common-messages.invalid-arguments")
            );
    }

    private void scmClear(@NotNull CommandSender sender, String[] args) {
        if (args.length != 2) {
            Messenger.msgToSender(
                sender,
                OverCrafted.prefix + this.locale.getStr("common-messages.invalid-argument")
            );
            return;
        }

        KitchenArea kitchenArea = Functions.getKitchenAreaByName(args[1].toLowerCase());
        if (kitchenArea == null) {
            Messenger.msgToSender(
                sender,
                OverCrafted.prefix + this.locale.getStr("kitchen-messages.kitchen-not-found")
            );
            return;
        }

        if (kitchenArea.clearRecipes() == Enums.ListResult.EMPTY_LIST) {
            Messenger.msgToSender(
                sender,
                OverCrafted.prefix + locale.getStr("kitchen-messages.recipe-empty-list")
            );
            return;
        }

        Messenger.msgToSender(
            sender,
            OverCrafted.prefix +
                    this.locale.getStr("kitchen-messages.recipes-cleared")
                            .replace("%kitchen%", kitchenArea.getName())
        );
    }

    private void scmShowRecipes(@NotNull CommandSender sender, String[] args) {
        if (args.length != 2) {
            Messenger.msgToSender(
                sender,
                OverCrafted.prefix + locale.getStr("common-messages.invalid-arguments")
            );
            return;
        }

        KitchenArea kitchenArea = Functions.getKitchenAreaByName(args[1].toLowerCase());
        if (kitchenArea == null) {
            Messenger.msgToSender(
                sender,
                OverCrafted.prefix + locale.getStr("kitchen-messages.kitchen-not-found")
            );
            return;
        }

        Messenger.msgToSender(sender, "&f&l------ OVERCRAFTED ------\n");
        Messenger.msgToSender(
            sender,
            this.locale.getStr("kitchen-messages.recipe-show-list")
                    .replace("%kitchen%", kitchenArea.getName())
        );
        for (Material materialItem : kitchenArea.getRecipes()) {
            Messenger.msgToSender(
                sender,
                "&7  - " + locale.getMatName(materialItem) + "."
            );
        }
    }

    // Sub-procedures
    private void addSpecificKitchen(@NotNull CommandSender sender, String[] args) {
        KitchenArea kitchenArea = Functions.getKitchenAreaByName(args[1].toLowerCase());
        if (kitchenArea == null) {
            Messenger.msgToSender(
                sender,
                OverCrafted.prefix + this.locale.getStr("kitchen-messages.kitchen-not-found")
            );
            return;
        }

        Material recipe;
        try {
            recipe = Material.valueOf(args[2].toUpperCase());
        } catch (IllegalArgumentException e) {
            Messenger.msgToSender(
                sender,
                OverCrafted.prefix + this.locale.getStr("kitchen-messages.recipe-invalid-material")
            );
            return;
        }

        if (kitchenArea.addRecipe(recipe) == Enums.ListResult.ALREADY_IN) {
            Messenger.msgToSender(
                sender,
                OverCrafted.prefix + this.locale.getStr("kitchen-messages.recipe-already-listed")
            );
            return;
        }

        Messenger.msgToSender(
            sender,
            OverCrafted.prefix +
                    this.locale.getStr("kitchen-messages.recipe-added")
                            .replace("%recipe%", recipe.name())
                            .replace("%kitchen%", kitchenArea.getName())
        );
    }

    private void addAutoDetectKitchen(@NotNull CommandSender sender, String[] args) {
        KitchenArea kitchenArea = Functions.getKitchenAreaFromLocation(((Player)sender).getLocation());
        if (kitchenArea == null) {
            Messenger.msgToSender(
                sender,
                OverCrafted.prefix + this.locale.getStr("kitchen-messages.not-in-kitchen")
            );
            return;
        }

        Material recipe;
        try {
            recipe = Material.valueOf(args[1].toUpperCase());
        } catch (IllegalArgumentException e) {
            Messenger.msgToSender(
                sender,
                OverCrafted.prefix + this.locale.getStr("kitchen-messages.recipe-invalid-material")
            );
            return;
        }

        if (kitchenArea.addRecipe(recipe) == Enums.ListResult.ALREADY_IN) {
            Messenger.msgToSender(
                sender,
                OverCrafted.prefix + this.locale.getStr("kitchen-messages.recipe-already-listed")
            );
            return;
        }

        Messenger.msgToSender(
            sender,
            OverCrafted.prefix +
                    this.locale.getStr("kitchen-messages.recipe-added")
                            .replace("%recipe%", recipe.name())
                            .replace("%kitchen%", kitchenArea.getName())
        );
    }

    private void deleteSpecificKitchen(@NotNull CommandSender sender, String[] args) {
        KitchenArea kitchenArea = Functions.getKitchenAreaByName(args[1].toLowerCase());
        if (kitchenArea == null) {
            Messenger.msgToSender(
                sender,
                OverCrafted.prefix + this.locale.getStr("kitchen-messages.kitchen-not-found")
            );
            return;
        }

        String recipe = args[2].toUpperCase();
        switch(kitchenArea.deleteRecipe(recipe)) {
            case INVALID_ITEM -> {
                Messenger.msgToSender(
                    sender,
                    OverCrafted.prefix + this.locale.getStr("kitchen-messages.recipe-invalid-material")
                );
                return;
            }
            case NOT_FOUND -> {
                Messenger.msgToSender(
                    sender,
                    OverCrafted.prefix + this.locale.getStr("kitchen-messages.recipe-not-found")
                );
                return;
            }
        }

        Messenger.msgToSender(
            sender,
            OverCrafted.prefix +
                    this.locale.getStr("kitchen-messages.recipe-deleted")
                            .replace("%recipe%", recipe)
                            .replace("%kitchen%", kitchenArea.getName())
        );
    }

    private void deleteAutoDetectKitchen(@NotNull CommandSender sender, String[] args) {
        KitchenArea kitchenArea = Functions.getKitchenAreaFromLocation(((Player)sender).getLocation());
        if (kitchenArea == null) {
            Messenger.msgToSender(
                sender,
                OverCrafted.prefix + this.locale.getStr("kitchen-messages.not-in-kitchen")
            );
            return;
        }

        String recipe = args[1].toUpperCase();
        switch(kitchenArea.deleteRecipe(recipe)) {
            case INVALID_ITEM -> {
                Messenger.msgToSender(
                    sender,
                    OverCrafted.prefix + this.locale.getStr("kitchen-messages.recipe-invalid-material")
                );
                return;
            }
            case NOT_FOUND -> {
                Messenger.msgToSender(
                    sender,
                    OverCrafted.prefix + this.locale.getStr("kitchen-messages.recipe-not-found")
                );
                return;
            }
        }

        Messenger.msgToSender(
            sender,
            OverCrafted.prefix +
                    this.locale.getStr("kitchen-messages.recipe-deleted")
                            .replace("%recipe%", recipe)
                            .replace("%kitchen%", kitchenArea.getName())
        );
    }

}
