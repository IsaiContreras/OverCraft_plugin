package org.cyanx86.commands;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import org.cyanx86.OverCrafted;
import org.cyanx86.classes.KitchenArea;
import org.cyanx86.classes.KitchenAreaCreatorAssistant;
import org.cyanx86.classes.SpawnPoint;
import org.cyanx86.config.GeneralSettings;
import org.cyanx86.config.Locale;
import org.cyanx86.utils.Enums;
import org.cyanx86.utils.Functions;
import org.cyanx86.utils.Messenger;

import java.util.List;
import org.jetbrains.annotations.NotNull;

public class KitchenCommand implements CommandExecutor {

    // -- [[ ATRIBUTES ]] --

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
            case "help":            // subcommand Help
                this.scmHelp(sender);
                break;
            case "create":          // subcommand Create
                this.scmCreate(sender, args);
                break;
            case "resetspawns":     // subCommand ResetSpawns
                this.scmResetSpawns(sender, args);
                break;
            case "list":            // subCommand List
                this.scmList(sender);
                break;
            case "info":            // subCommand Info
                this.scmInfo(sender, args);
                break;
            case "delete":          // subcommand Delete
                this.scmDelete(sender, args);
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
                "&7 [[ " + this.locale.getStr("common-messages.command") + " /kitchen ]]");
        Messenger.msgToSender(sender, "&7- /kitchen help");
        Messenger.msgToSender(sender, "&7- /kitchen create <name> <minPlayers> <maxPlayers>");
        Messenger.msgToSender(sender, "&7- /kitchen delete <name>");
        Messenger.msgToSender(sender, "&7- /kitchen resetspawns <name>");
        Messenger.msgToSender(sender, "&7- /kitchen list");
        Messenger.msgToSender(sender, "&7- /kitchen info <name>");
    }

    private void scmCreate(@NotNull CommandSender sender, @NotNull String[] args) {
        if (args.length != 4) {
            Messenger.msgToSender(
                sender,
                OverCrafted.prefix + this.locale.getStr("common-messages.invalid-arguments")
            );
            return;
        }

        KitchenAreaCreatorAssistant kacAssistant = master.getKitchenAreaCreatorAssistantManager()
                .getAssistantByName(sender.getName());
        if (kacAssistant == null) {
            Messenger.msgToSender(
                sender,
                OverCrafted.prefix + this.locale.getStr("kitchen-messages.no-assistant")
            );
            return;
        }

        if (!kacAssistant.isDefinedCorners()) {
            Messenger.msgToSender(
                sender,
                OverCrafted.prefix + this.locale.getStr("kitchen-messages.undefined-corner")
            );
            return;
        }

        String name = args[1].toLowerCase();
        int minPlayers;
        int maxPlayers;
        try {
            minPlayers = Integer.parseInt(args[2]);
            maxPlayers = Integer.parseInt(args[3]);
        } catch(NumberFormatException e) {
            Messenger.msgToSender(
                sender,
                OverCrafted.prefix + this.locale.getStr("common-messages.numeric-argument")
            );
            return;
        }

        switch (
            master.getKitchenAreaLoader().addKitchenArea(
                name,
                kacAssistant.getCorner(0),
                kacAssistant.getCorner(1),
                Math.min(minPlayers, maxPlayers),
                Math.max(minPlayers, maxPlayers)
            )
        ) {
            case ALREADY_IN -> {
                Messenger.msgToSender(
                    sender,
                    OverCrafted.prefix + this.locale.getStr("kitchen-messages.name-used")
                );
                return;
            }
            case INVALID_ITEM -> {
                Messenger.msgToSender(
                    sender,
                    OverCrafted.prefix + this.locale.getStr("kitchen-messages.kitchen-overlapped")
                );
                return;
            }
        }

        kacAssistant.resetCorners();

        Messenger.msgToSender(
            sender,
            OverCrafted.prefix +
                    this.locale.getStr("kitchen-messages.kitchen-created")
                            .replace("%kitchen%", name)
        );
    }

    private void scmResetSpawns(@NotNull CommandSender sender, @NotNull String[] args) {
        if (args.length != 2) {
            Messenger.msgToSender(
                sender,
                OverCrafted.prefix + this.locale.getStr("common-messages.invalid-arguments")
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

        if (kitchenArea.clearSpawnPointList() == Enums.ListResult.EMPTY_LIST) {
            Messenger.msgToSender(
                sender,
                OverCrafted.prefix + this.locale.getStr("kitchen-messages.spawnpoint-empty-list")
            );
            return;
        }

        Messenger.msgToSender(
            sender,
            OverCrafted.prefix +
                    this.locale.getStr("kitchen-messages.spawnpoint-cleared")
                            .replace("%kitchen%", kitchenArea.getName())
        );
    }

    private void scmList(@NotNull CommandSender sender) {
        Messenger.msgToSender(sender, "&f&l------ OVERCRAFTED ------\n");
        Messenger.msgToSender(sender, this.locale.getStr("kitchen-messages.show-kitchen-list.title"));

        List<KitchenArea> kitchenAreaList = master.getKitchenAreaLoader().getKitchenAreas();

        if (kitchenAreaList.isEmpty()) {
            Messenger.msgToSender(
                sender,
                this.locale.getStr("kitchen-messages.show-kitchen-list.empty-list")
            );
            return;
        }
        for(int i = 0; i < kitchenAreaList.size(); i++) {
            Messenger.msgToSender(
                sender,
                "&7" + (i + 1) + ".- " +
                        "&o" + master.getKitchenAreaLoader().getKitchenAreas().get(i).getName()
            );
        }
    }

    private void scmInfo(@NotNull CommandSender sender, @NotNull String[] args) {
        if (args.length != 2) {
            Messenger.msgToSender(
                sender,
                OverCrafted.prefix + this.locale.getStr("common-messages.invalid-arguments")
            );
            return;
        }

        KitchenArea kitchenArea = this.master.getKitchenAreaLoader().getByName(args[1].toLowerCase());
        if (kitchenArea == null) {
            Messenger.msgToSender(
                sender,
                OverCrafted.prefix + this.locale.getStr("kitchen-messages.kitchen-not-found")
            );
            return;
        }

        Messenger.msgToSender(sender, "&f&l---- OverCrafted Kitchen ----");
        Messenger.msgToSender(sender, "&b&o" + kitchenArea.getName() + " properties:");
        Messenger.msgToSender(sender, "&6&o  world: &r&e" + kitchenArea.getWorld());
        Messenger.msgToSender(sender,
            "&6&o  for &r&e" +
                    (
                        kitchenArea.getMinPlayers() == kitchenArea.getMaxPlayers() ?
                                kitchenArea.getMinPlayers() :
                                kitchenArea.getMinPlayers() + "&6&o to &r&e" + kitchenArea.getMaxPlayers()
                    ) +
                    "&6&o " +
                    (kitchenArea.getMaxPlayers() == 1 ? "player" : "players") +
                    "."
        );
        Messenger.msgToSender(sender,
            "&6&o  corner1: &r&e(" + "&r&c" + kitchenArea.getCorner(0).getBlockX() +
                    "&r&e, &r&a" + kitchenArea.getCorner(0).getBlockY() +
                    "&r&e, &r&9" + kitchenArea.getCorner(0).getBlockZ() + "&r&e)"
        );
        Messenger.msgToSender(sender,
            "&6&o  corner2: &r&e(" + "&r&c" + kitchenArea.getCorner(1).getBlockX()+
                    "&r&e, &r&a" + kitchenArea.getCorner(1).getBlockY() +
                    "&r&e, &r&9" + kitchenArea.getCorner(1).getBlockZ() + "&r&e)"
        );

        Messenger.msgToSender(sender, "&6&o  spawnpoints (&e" + kitchenArea.getSpawnPointsCount() + "&6):");
        for (int i = 0; i < kitchenArea.getSpawnPointsCount(); i++) {
            SpawnPoint spawn = kitchenArea.getSpawnPoints().get(i);
            Messenger.msgToSender(sender,
                "&6&o    [" + (i + 1) + "]: &r&6pi: &e"+ spawn.getPlayerIndex() +
                        "&6 loc: &e(" + "&c" + String.format("%.2f", spawn.getSpawnLocation().getX()) +
                        "&e, &a" + String.format("%.2f", spawn.getSpawnLocation().getY()) +
                        "&e, &9" + String.format("%.2f", spawn.getSpawnLocation().getZ()) +
                        "&e, &b" + String.format("%.2f", spawn.getSpawnLocation().getYaw()) +
                        "&e, &d" + String.format("%.2f", spawn.getSpawnLocation().getPitch()) + "&e)"
            );
        }

        Messenger.msgToSender(sender, "&6&o  recipes (&e" + kitchenArea.getRecipesCount() + "&6):");
        for (int i = 0; i < kitchenArea.getRecipes().size(); i++) {
            Material recipe = kitchenArea.getRecipes().get(i);
            Messenger.msgToSender(sender,
                    "&6&o    [" + (i + 1) + "]:" +
                            "&6 name: &e&c" + recipe.name() + "&e."
            );
        }
    }

    private void scmDelete(@NotNull CommandSender sender, @NotNull String[] args) {
        if (args.length != 2) {
            Messenger.msgToSender(
                sender,
                OverCrafted.prefix + this.locale.getStr("common-messages.invalid-arguments")
            );
            return;
        }

        String name = args[1].toLowerCase();
        switch(master.getKitchenAreaLoader().removeKitchenArea(name)) {
            case NOT_FOUND -> {
                Messenger.msgToSender(
                    sender,
                    OverCrafted.prefix + this.locale.getStr("kitchen-messages.kitchen-not-found")
                );
                return;
            }
            case EMPTY_LIST -> {
                Messenger.msgToSender(
                    sender,
                    OverCrafted.prefix + this.locale.getStr("kitchen-messages.kitchen-empty-list")
                );
                return;
            }
        }

        Messenger.msgToSender(
            sender,
            OverCrafted.prefix +
                    this.locale.getStr("kitchen-messages.kitchen-deleted")
                            .replace("%kitchen%", name)
        );
    }

}
