package org.cyanx86.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.cyanx86.OverCrafted;
import org.cyanx86.classes.GameArea;
import org.cyanx86.utils.Enums;
import org.cyanx86.utils.Messenger;

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
            case "list":
                this.scmList(sender);
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
        Messenger.msgToSender(sender, "&7- /gamearea help" + "\t" + "\t" + "Ayuda del plugin.");
        Messenger.msgToSender(sender, "&7- /gamearea create <nombre>" + "\t" + "\t" + "Crea una nueva área de juego.");
        Messenger.msgToSender(sender, "&7- /gamearea setcorner <1 o 2>" + "\t" + "\t" + "Activa la selección de esquinas.");
        Messenger.msgToSender(sender, "&7- /gamearea list" + "\t" + "\t" + "Muestra la lista de áreas.");
        Messenger.msgToSender(sender, "&7- /gamearea delete <nombre>" + "\t" + "\t" + "Elimina un área de juego.");
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

        if (master.addGameArea(name) == Enums.ListResult.ALREADY_IN) {
            Messenger.msgToSender(
                sender,
                OverCrafted.prefix + "&cYa hay un elemento con este nombre."    // TODO: GameArea already in list message.
            );
            return;
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

    private void scmList(CommandSender sender) {
        Messenger.msgToSender(sender, "&f&l------ OVERCRAFTED ------\n");
        Messenger.msgToSender(sender, "&f&lÁreas de juego:");  // TODO: Language location.

        for(int i = 0; i < master.getGameAreas().size(); i++) {
            Messenger.msgToSender(sender, "&7" + (i + 1) + ".- " + "&o" + master.getGameAreas().get(i).getName());
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
