package org.cyanx86.commands;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import org.cyanx86.OverCrafted;
import org.cyanx86.classes.GameArea;
import org.cyanx86.classes.GameAreaPropertiesAssistant;
import org.cyanx86.classes.SpawnPoint;
import org.cyanx86.utils.Enums;
import org.cyanx86.utils.Functions;
import org.cyanx86.utils.Messenger;

import java.util.List;
import org.jetbrains.annotations.NotNull;

public class GameAreaCommand implements CommandExecutor {

    // -- [[ ATRIBUTES ]] --

    // -- PUBLIC --

    // -- PRIVATE --
    private final OverCrafted master = OverCrafted.getInstance();

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
                OverCrafted.prefix + "&cEste comando no puede ejecutarse desde consola."    // TODO: No console command message.
            );
            return;
        }
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

        switch (args[0].toLowerCase()) {
            case "help":            // subcommand Help
                this.scmHelp(sender);
                break;
            case "create":          // subcommand Create
                this.scmCreate(sender, args);
                break;
            case "setspawn":        // subCommand SetSpawn
                this.scmSetSpawnPoint(sender);
                break;
            case "resetspawns":     // subCommand ResetSpawns
                this.scmResetSpawns(sender, args);
                break;
            case "addrecipe":       // subCommand AddRecipe
                this.scmAddRecipe(sender, args);
                break;
            case "resetrecipes":    // subCommand ResetRecipes
                this.scmResetRecipes(sender, args);
                break;
            case "list":            // subCommand List
                this.scmList(sender);
                break;
            case "select":          // subCommand Select
                this.scmSelect(sender, args);
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
        // TODO: Language location of Help Page
        Messenger.msgToSender(sender, "&f&l------ OVERCRAFTED ------");
        Messenger.msgToSender(sender, "&7 [[ Comando /gamearea ]]");
        Messenger.msgToSender(sender, "&7- /gamearea help");
        Messenger.msgToSender(sender, "&7- /gamearea create <nombre> <minPlayers> <maxPlayers>");
        Messenger.msgToSender(sender, "&7- /gamearea setspawn");
        Messenger.msgToSender(sender, "&7- /gamearea resetspawns <nombre>");
        Messenger.msgToSender(sender, "&7- /gamearea addrecipe <nombre> <material>");
        Messenger.msgToSender(sender, "&7- /gamearea resetrecipes <nombre>");
        Messenger.msgToSender(sender, "&7- /gamearea list");
        Messenger.msgToSender(sender, "&7- /gamearea select <nombre>");
        Messenger.msgToSender(sender, "&7- /gamearea info <nombre>");
        Messenger.msgToSender(sender, "&7- /gamearea delete <nombre>");
    }

    private void scmCreate(@NotNull CommandSender sender, @NotNull String[] args) {
        if (args.length != 4) {
            Messenger.msgToSender(
                sender,
                OverCrafted.prefix + "&cArgumentos incompletos."    // TODO: Invalid arguments message.
            );
            return;
        }

        GameAreaPropertiesAssistant gacAssistant = master.getGameAreaPropertiesAssistantManager()
                .getAssistantByName(sender.getName());
        if (gacAssistant == null) {
            Messenger.msgToSender(
                sender,
                OverCrafted.prefix + "&cNo hay asistente registrado."   // TODO: Invalid assistant message.
            );
            return;
        }

        if (!gacAssistant.isDefinedCorners()) {
            Messenger.msgToSender(
                sender,
                OverCrafted.prefix + "&cFaltan esquinas por definir."   // TODO: Undefined GameArea corners message.
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
                OverCrafted.prefix + "&cEste argumento solo acepta valores numéricos."
            );
            return;
        }

        switch (
            master.getGameAreaManager().addGameArea(
                name,
                gacAssistant.getCorner(0),
                gacAssistant.getCorner(1),
                Math.min(minPlayers, maxPlayers),
                Math.max(minPlayers, maxPlayers)
            )
        ) {
            case ALREADY_IN -> {
                Messenger.msgToSender(
                    sender,
                    OverCrafted.prefix + "&cYa hay un elemento con este nombre."    // TODO: GameArea already in list message.
                );
                return;
            }
            case INVALID_ITEM -> {
                Messenger.msgToSender(
                    sender,
                    OverCrafted.prefix + "&cHay un GameArea ocupando parte de este espacio."    // TODO: GameArea overlapped.
                );
                return;
            }
        }

        gacAssistant.resetCorners();

        Messenger.msgToSender(
            sender,
            OverCrafted.prefix + "&aSe ha creado el área de juego &r&o" + name + "&r&a."
        );
    }

    private void scmSetSpawnPoint(@NotNull CommandSender sender) {
        Location spawnLocation = ((Player)sender).getLocation();
        GameArea gamearea = Functions.getGameAreaFromLocation(spawnLocation);
        if (gamearea == null) {
            Messenger.msgToSender(
                sender,
                OverCrafted.prefix + "&cEstas fuera de un GameArea."
            );
            return;
        }

        switch (
            gamearea.addSpawnPoint(new SpawnPoint(
                spawnLocation
            ))
        ) {
            case NULL -> {
                Messenger.msgToSender(
                    sender,
                    OverCrafted.prefix + "&cSe intentó añadir un objeto vacío."
                );
                return;
            }
            case INVALID_ITEM -> {
                Messenger.msgToSender(
                    sender,
                    OverCrafted.prefix + "&cEl punto no se encuentra dentro de los límites del GameArea."
                );
                return;
            }
            case FULL_LIST -> {
                Messenger.msgToSender(
                    sender,
                    OverCrafted.prefix + "&cEste GameArea ya tiene suficientes SpawnPoints."
                );
                return;
            }
        }

        Messenger.msgToSender(
            sender,
            OverCrafted.prefix + "&aSpawnPoint añadido al GameArea &r&o" + gamearea.getName() + "&r&a."
        );
    }

    private void scmResetSpawns(@NotNull CommandSender sender, @NotNull String[] args) {
        if (args.length != 2) {
            Messenger.msgToSender(
                sender,
                OverCrafted.prefix + "&cArgumentos incompletos."    // TODO: Invalid arguments message.
            );
            return;
        }

        GameArea gamearea = Functions.getGameAreaByName(args[1].toLowerCase());
        if (gamearea == null) {
            Messenger.msgToSender(
                sender,
                OverCrafted.prefix + "&cNo se encontró un GameArea con este nombre."
            );
            return;
        }

        gamearea.clearSpawnPointList();

        Messenger.msgToSender(
            sender,
            OverCrafted.prefix + "&aSe limpiaron los spawnpoints del GameArea &r&o" + gamearea.getName() + "&r&a."
        );
    }

    private void scmAddRecipe(@NotNull CommandSender sender, @NotNull String[] args) {
        if (args.length != 3) {
            Messenger.msgToSender(
                sender,
                OverCrafted.prefix + "&cArgumentos incompletos."    // TODO: Invalid arguments message.
            );
            return;
        }

        GameArea gamearea = Functions.getGameAreaByName(args[1].toLowerCase());
        if (gamearea == null) {
            Messenger.msgToSender(
                sender,
                OverCrafted.prefix + "&cNo se encontró un GameArea con este nombre."
            );
            return;
        }

        Material recipe;
        try {
            recipe = Material.valueOf(args[2].toUpperCase());
        } catch (IllegalArgumentException e) {
            Messenger.msgToSender(
                sender,
                OverCrafted.prefix + "&cNo se encontró este Material."
            );
            return;
        }

        if (gamearea.addRecipe(recipe) == Enums.ListResult.ALREADY_IN) {
            Messenger.msgToSender(
                sender,
                OverCrafted.prefix + "&cYa hay una receta en la lista del GameArea."
            );
            return;
        }

        Messenger.msgToSender(
            sender,
            OverCrafted.prefix + "&aSe añadió &r&o" + recipe.name() +
                    " &r&acomo receta del GameArea &r&o" + gamearea.getName() + "&r&a."
        );
    }

    private void scmResetRecipes(@NotNull CommandSender sender, @NotNull String[] args) {
        if (args.length != 2) {
            Messenger.msgToSender(
                sender,
                OverCrafted.prefix + "&cArgumentos incompletos."    // TODO: Invalid arguments message.
            );
            return;
        }

        GameArea gamearea = Functions.getGameAreaByName(args[1].toLowerCase());
        if (gamearea == null) {
            Messenger.msgToSender(
                sender,
                OverCrafted.prefix + "&cNo se encontró un GameArea con este nombre."
            );
            return;
        }

        gamearea.clearRecipes();

        Messenger.msgToSender(
            sender,
            OverCrafted.prefix + "&aSe limpiaron las recetas del GameArea &r&o" + gamearea.getName() + "&r&a."
        );
    }

    private void scmList(@NotNull CommandSender sender) {
        Messenger.msgToSender(sender, "&f&l------ OVERCRAFTED ------\n");
        Messenger.msgToSender(sender, "&f&lÁreas de juego:");  // TODO: Language location.

        List<GameArea> gameAreaList = master.getGameAreaManager().getGameAreas();

        if (gameAreaList.isEmpty()) {
            Messenger.msgToSender(
                sender,
                "&7&o** Vacío **"
            );
            return;
        }
        for(int i = 0; i < gameAreaList.size(); i++) {
            Messenger.msgToSender(
                sender,
                "&7" + (i + 1) + ".- " + "&o" + master.getGameAreaManager().getGameAreas().get(i).getName()
            );
        }
    }

    private void scmSelect(@NotNull CommandSender sender, @NotNull String[] args) {
        if (args.length != 2) {
            Messenger.msgToSender(
                sender,
                OverCrafted.prefix + "&cArgumentos incompletos."    // TODO: Invalid arguments message.
            );
            return;
        }

        GameArea gamearea = master.getGameAreaManager().getByName(args[1].toLowerCase());
        if (gamearea == null) {
            Messenger.msgToSender(
                sender,
                OverCrafted.prefix + "&cNo se encontró el elemento con este nombre."    // TODO: Not found GameArea message.
            );
            return;
        }
        if (!gamearea.isValidSetUp()) {
            Messenger.msgToSender(
                sender,
                OverCrafted.prefix + "&cFaltan definir SpawnPoints para este GameArea."    // TODO: Not found GameArea message.
            );
            return;
        }

        master.getGameRoundManager().setGameArea(gamearea);

        Messenger.msgToSender(
            sender,
            OverCrafted.prefix +
            "&aEl GameArea &r&o" + gamearea.getName() + "&r&a fue seleccionado para el siguiente juego."
        );
    }

    private void scmInfo(@NotNull CommandSender sender, @NotNull String[] args) {
        if (args.length != 2) {
            Messenger.msgToSender(
                sender,
                OverCrafted.prefix + "&cArgumentos incompletos."    // TODO: Invalid arguments message.
            );
            return;
        }

        GameArea gamearea = this.master.getGameAreaManager().getByName(args[1].toLowerCase());
        if (gamearea == null) {
            Messenger.msgToSender(
                sender,
                OverCrafted.prefix + "&cNo existe un GameArea con este nombre."    // TODO: GameArea not found message.
            );
            return;
        }

        Messenger.msgToSender(sender, "&f&l---- OverCrafted GameArea ----");
        Messenger.msgToSender(sender, "&b&o" + gamearea.getName() + " properties:");
        Messenger.msgToSender(sender, "&6&o  world: &r&e" + gamearea.getWorld());
        Messenger.msgToSender(sender,
            "&6&o  for &r&e" +
                    (
                        gamearea.getMinPlayers() == gamearea.getMaxPlayers() ?
                                gamearea.getMinPlayers() :
                                gamearea.getMinPlayers() + "&6&o to &r&e" + gamearea.getMaxPlayers()
                    ) +
                    "&6&o players."
        );
        Messenger.msgToSender(sender,
            "&6&o  corner1: &r&e(" + "&r&c" + gamearea.getCorner(0).getBlockX() +
                    "&r&e, &r&a" + gamearea.getCorner(0).getBlockY() +
                    "&r&e, &r&9" + gamearea.getCorner(0).getBlockZ() + "&r&e)"
        );
        Messenger.msgToSender(sender,
            "&6&o  corner2: &r&e(" + "&r&c" + gamearea.getCorner(1).getBlockX()+
                    "&r&e, &r&a" + gamearea.getCorner(1).getBlockY() +
                    "&r&e, &r&9" + gamearea.getCorner(1).getBlockZ() + "&r&e)"
        );

        Messenger.msgToSender(sender, "&6&o  spawnpoints (&e" + gamearea.getSpawnPointsCount() + "&6):");
        for (int i = 0; i < gamearea.getSpawnPointsCount(); i++) {
            SpawnPoint spawn = gamearea.getSpawnPoints().get(i);
            Messenger.msgToSender(sender,
                "&6&o    [" + (i + 1) + "]: &r&6pi: &e"+ spawn.getPlayerIndex() +
                        "&6 loc: &e(" + "&c" + String.format("%.2f", spawn.getSpawnLocation().getX()) +
                        "&e, &a" + String.format("%.2f", spawn.getSpawnLocation().getY()) +
                        "&e, &9" + String.format("%.2f", spawn.getSpawnLocation().getZ()) +
                        "&e, &b" + String.format("%.2f", spawn.getSpawnLocation().getYaw()) +
                        "&e, &d" + String.format("%.2f", spawn.getSpawnLocation().getPitch()) + "&e)"
            );
        }

        Messenger.msgToSender(sender, "&6&o  recipes (&e" + gamearea.getRecipesCount() + "&6):");
        for (int i = 0; i < gamearea.getRecipes().size(); i++) {
            Material recipe = gamearea.getRecipes().get(i);
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
                OverCrafted.prefix + "&cArgumentos incompletos."    // TODO: Invalid arguments message.
            );
            return;
        }

        String name = args[1].toLowerCase();
        switch(master.getGameAreaManager().removeGameArea(name)) {
            case NOT_FOUND -> {
                Messenger.msgToSender(
                    sender,
                    OverCrafted.prefix + "&cNo se encontró el elemento con este nombre."    // TODO: Not found GameArea message.
                );
                return;
            }
            case EMPTY_LIST -> {
                Messenger.msgToSender(
                    sender,
                    OverCrafted.prefix + "&cLa lista está vacía"    // TODO: Emtpy list message.
                );
                return;
            }
        }

        Messenger.msgToSender(
            sender,
            OverCrafted.prefix + "&aSe ha eliminado el área de juego &r&o" + name + "&r&a."
        );
    }

}
