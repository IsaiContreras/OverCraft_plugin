package org.cyanx86.commands;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import org.cyanx86.OverCrafted;
import org.cyanx86.classes.GameArea;
import org.cyanx86.classes.GameAreaCornerAssistant;
import org.cyanx86.classes.SpawnPoint;
import org.cyanx86.utils.Messenger;

import java.util.Objects;
import java.util.Optional;

public class GameAreaCommand implements CommandExecutor {

    // -- [[ ATRIBUTES ]] --

    // -- Public

    // -- Private
    private final OverCrafted master = OverCrafted.getInstance();

    // -- [[ METHODS ]] --

    // -- Public

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        this.handleSubcommands(sender, args);
        return false;
    }

    // -- Private
    private void handleSubcommands(CommandSender sender, String[] args) {
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

        String mainArg = args[0].toLowerCase();

        switch (mainArg) {
            case "help":        // subcommand Help
                this.scmHelp(sender);
                break;
            case "create":      // subcommand Create
                this.scmCreate(sender, args);
                break;
            case "setspawn":    // subCommand SetSpawn
                this.scmSetSpawnPoint(sender);
                break;
            case "resetspawns": // subCommand ResetSpawns
                this.scmResetSpawns(sender, args);
                break;
            case "list":        // subCommand List
                this.scmList(sender);
                break;
            case "select":      // subCommand Select
                this.scmSelect(sender, args);
                break;
            case "info":        // subCommand Info
                this.scmInfo(sender, args);
                break;
            case "delete":      // subcommand Delete
                this.scmDelete(sender, args);
                break;
            default:
                this.scmHelp(sender);
                break;
        }
    }

    // Subcommands
    private void scmHelp(CommandSender sender) {
        if (!sender.hasPermission("overcrafted.manager")) {
            Messenger.msgToSender(
                sender,
                OverCrafted.prefix + "&cNo tienes permiso para usar este comando."      // TODO: No permissions message.
            );
        }
        // TODO: Language location of Help Page
        Messenger.msgToSender(sender, "&f&l------ OVERCRAFTED ------");
        Messenger.msgToSender(sender, "&7 [[ Comando /gamearea ]]");
        Messenger.msgToSender(sender, "&7- /gamearea help");
        Messenger.msgToSender(sender, "&7- /gamearea create <nombre>");
        Messenger.msgToSender(sender, "&7- /gamearea setspawn");
        Messenger.msgToSender(sender, "&7- /gamearea resetspawns <nombre>");
        Messenger.msgToSender(sender, "&7- /gamearea list");
        Messenger.msgToSender(sender, "&7- /gamearea select <nombre>");
        Messenger.msgToSender(sender, "&7- /gamearea info <nombre>");
        Messenger.msgToSender(sender, "&7- /gamearea delete <nombre>");
    }

    private void scmCreate(CommandSender sender, String[] args) {
        Player player = (Player)sender;
        GameAreaCornerAssistant gacAssistant = master.getGacaManager().getAssistantByName(player.getName());
        if (gacAssistant == null) {
            Messenger.msgToSender(
                sender,
                OverCrafted.prefix + "&cNo hay asistente registrado."   // TODO: Invalid assistant message.
            );
            return;
        }

        if (args.length != 3) {
            Messenger.msgToSender(
                sender,
                OverCrafted.prefix + "&cArgumentos incompletos."    // TODO: Invalid arguments message.
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
        int maxPlayers = 0;
        try {
            maxPlayers = Integer.parseInt(args[2]);
        } catch(NumberFormatException e) {
            Messenger.msgToSender(
                sender,
                OverCrafted.prefix + "&cEste argumento solo acepta valores numéricos."
            );
            return;
        }

        switch (master.getGameAreaManager().addGameArea(name, gacAssistant.getCorner(0), gacAssistant.getCorner(1), maxPlayers)) {
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

    private void scmSetSpawnPoint(CommandSender sender) {
        Location spawnLocation = ((Player)sender).getLocation();
        GameArea gamearea = null;
        for (int i = 0; i < master.getGameAreaManager().getGameAreas().size(); i++) {
            GameArea current = master.getGameAreaManager().getGameAreas().get(i);
            if (
                current.isPointInsideBoundaries(spawnLocation) &&
                current.getWorld().equals(Objects.requireNonNull(spawnLocation.getWorld()).getName())
            ) {
                gamearea = current;
                break;
            }
        }

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

    private void scmResetSpawns(CommandSender sender, String[] args) {
        if (args.length != 2) {
            Messenger.msgToSender(
                sender,
                OverCrafted.prefix + "&cArgumentos incompletos."    // TODO: Invalid arguments message.
            );
            return;
        }

        String name = args[1].toLowerCase();
        Optional<GameArea> query = master.getGameAreaManager().getGameAreas().stream().filter(item -> item.getName().equals(name)).findFirst();
        if (query.isEmpty()) {
            Messenger.msgToSender(
                sender,
                OverCrafted.prefix + "&cNo se encontró un GameArea con este nombre."
            );
            return;
        }
        GameArea gamearea = query.get();

        gamearea.clearSpawnPointList();

        Messenger.msgToSender(
            sender,
            OverCrafted.prefix + "&aSe limpiaron los spawnpoints del GameArea &r&o" + gamearea.getName() + "&r&a."
        );
    }

    private void scmList(CommandSender sender) {
        Messenger.msgToSender(sender, "&f&l------ OVERCRAFTED ------\n");
        Messenger.msgToSender(sender, "&f&lÁreas de juego:");  // TODO: Language location.

        if (master.getGameAreaManager().getGameAreas().isEmpty()) {
            Messenger.msgToSender(
                sender,
                "&7&o** Vacío **"
            );
            return;
        }
        for(int i = 0; i < master.getGameAreaManager().getGameAreas().size(); i++) {
            Messenger.msgToSender(sender, "&7" + (i + 1) + ".- " + "&o" + master.getGameAreaManager().getGameAreas().get(i).getName());
        }
    }

    private void scmSelect(CommandSender sender, String[] args) {
        if (args.length != 2) {
            Messenger.msgToSender(
                sender,
                OverCrafted.prefix + "&cArgumentos incompletos."    // TODO: Invalid arguments message.
            );
            return;
        }

        String name = args[1].toLowerCase();
        GameArea gamearea = master.getGameAreaManager().getByName(name);
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
            OverCrafted.prefix + "&aEl GameArea " + gamearea.getName() + " fue seleccionado para el siguiente juego."
        );
    }

    private void scmInfo(CommandSender sender, String[] args) {
        if (args.length != 2) {
            Messenger.msgToSender(
                sender,
                OverCrafted.prefix + "&cArgumentos incompletos."    // TODO: Invalid arguments message.
            );
            return;
        }

        String name = args[1].toLowerCase();
        GameArea gamearea = this.master.getGameAreaManager().getByName(name);

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
    }

    private void scmDelete(CommandSender sender, String[] args) {
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
