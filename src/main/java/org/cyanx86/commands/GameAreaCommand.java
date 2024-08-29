package org.cyanx86.commands;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.cyanx86.OverCrafted;
import org.cyanx86.classes.GameArea;
import org.cyanx86.utils.Enums;
import org.cyanx86.utils.Messenger;

import java.util.Objects;
import java.util.Optional;

public class GameAreaCommand implements CommandExecutor {

    // -- [[ ATRIBUTES ]] --

    // -- Public

    // -- Private
    private final OverCrafted master;

    // -- [[ METHODS ]] --

    // -- Public
    public GameAreaCommand(OverCrafted master) {
        this.master = master;
    }

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
            case "setcorner":   // subcommand SetCorner
                this.scmSetCorner(sender, args);
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
                OverCrafted.prefix + "&cNo tienes permiso para usar este comando."
            );
        }
        // TODO: Language location of Help Page
        Messenger.msgToSender(sender, "&f&l------ OVERCRAFTED ------");
        Messenger.msgToSender(sender, "&7 [[ Comando /gamearea ]]");
        Messenger.msgToSender(sender, "&7- /gamearea help");
        Messenger.msgToSender(sender, "&7- /gamearea create <nombre>");
        Messenger.msgToSender(sender, "&7- /gamearea setcorner <1 o 2>");
        Messenger.msgToSender(sender, "&7- /gamearea setspawn");
        Messenger.msgToSender(sender, "&7- /gamearea resetspawns <nombre>");
        Messenger.msgToSender(sender, "&7- /gamearea list");
        Messenger.msgToSender(sender, "&7- /gamearea info <nombre>");
        Messenger.msgToSender(sender, "&7- /gamearea delete <nombre>");
    }

    private void scmCreate(CommandSender sender, String[] args) {
        if (args.length != 2) {
            Messenger.msgToSender(
                sender,
                OverCrafted.prefix + "&cArgumentos incompletos."    // TODO: Invalid arguments message.
            );
            return;
        }

        if (master.getCorner(1) == null || master.getCorner(2) == null) {
            Messenger.msgToSender(
                sender,
                OverCrafted.prefix + "&cFaltan esquinas por definir."   // TODO: Undefined GameArea corners message.
            );
            return;
        }

        String name = args[1].toLowerCase();

        switch (master.addGameArea(name)) {
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

        master.resetCorners();
        Messenger.msgToSender(
                sender,
                OverCrafted.prefix + "&aSe ha creado el área de juego &r&o" + name + "&r&a."
        );
    }

    private void scmSetCorner(CommandSender sender, String[] args) {
        if (args.length != 2) {
            Messenger.msgToSender(
                sender,
                OverCrafted.prefix + "&cArgumentos incompletos."    // TODO: Invalid arguments message.
            );
            return;
        }

        int index;
        try {
            index = Integer.parseInt(args[1]);
        } catch (NumberFormatException ignored) {
            Messenger.msgToSender(
                sender,
                OverCrafted.prefix + "&cSe requiere un número para éste parámetro."     // TODO: Number parameter format required message.
            );
            return;
        }

        master.setCornerIndex(index);

        Messenger.msgToSender(
            sender,
            OverCrafted.prefix + "&aListo para asignar esquina."    // TODO:
        );
    }

    private void scmSetSpawnPoint(CommandSender sender) {
        Location spawn_location = ((Player)sender).getLocation();
        GameArea gma = null;
        for (int i = 0; i < master.getGameAreas().size(); i++) {
            GameArea current = master.getGameAreas().get(i);
            if (
                current.isInsideBoundaries(spawn_location) &&
                current.getWorld().equals(Objects.requireNonNull(spawn_location.getWorld()).getName())
            ) {
                gma = current;
                break;
            }
        }

        if (gma == null) {
            Messenger.msgToSender(
                sender,
                OverCrafted.prefix + "&cEstas fuera de un GameArea."
            );
            return;
        }

        switch (gma.addSpawnPoint(spawn_location)) {
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
        }

        Messenger.msgToSender(
            sender,
            OverCrafted.prefix + "&aSpawnpoint añadido al GameArea &r&o" + gma.getName() + "&r&a."
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
        Optional<GameArea> query = master.getGameAreas().stream().filter(ga -> ga.getName().equals(name)).findFirst();
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

        for(int i = 0; i < master.getGameAreas().size(); i++) {
            Messenger.msgToSender(sender, "&7" + (i + 1) + ".- " + "&o" + master.getGameAreas().get(i).getName());
        }
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
        GameArea gma = this.master.getGameAreaByName(name);

        if (gma == null) {
            Messenger.msgToSender(
                sender,
                OverCrafted.prefix + "&cNo existe un GameArea con este nombre."    // TODO: GameArea not found message.
            );
            return;
        }

        Messenger.msgToSender(sender, "&f&l---- OverCrafted GameArea ----");
        Messenger.msgToSender(sender, "&b&o" + gma.getName() + " properties:");
        Messenger.msgToSender(sender, "&6&o  world: &r&e" + gma.getWorld());
        Messenger.msgToSender(sender, "&6&o  corner1: &r&e(" + "&r&c" + gma.getCorner1().getBlockX() + "&r&e, &r&a" + gma.getCorner1().getBlockY() + "&r&e, &r&9" + gma.getCorner1().getBlockZ() + "&r&e)");
        Messenger.msgToSender(sender, "&6&o  corner2: &r&e(" + "&r&c" + gma.getCorner2().getBlockX() + "&r&e, &r&a" + gma.getCorner2().getBlockY() + "&r&e, &r&9" + gma.getCorner2().getBlockZ() + "&r&e)");
        Messenger.msgToSender(sender, "&6&o  spawnpoints (&e" + gma.getSpawnPointsCount() + "&6):");
        for (int i = 0; i < gma.getSpawnPointsCount(); i++) {
            Location spawn = gma.getSpawnPoints().get(i);
            Messenger.msgToSender(sender, "&6&o    [" + (i + 1) + "]: &r&e(" + "&c" + String.format("%.2f", spawn.getX()) + "&e, &a" + String.format("%.2f", spawn.getY()) + "&e, &9" + String.format("%.2f", spawn.getZ()) + "&e, &b" + String.format("%.2f", spawn.getYaw()) + "&e, &d" + String.format("%.2f", spawn.getPitch()) + "&e)");
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

        switch(master.removeGameArea(name)) {
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
