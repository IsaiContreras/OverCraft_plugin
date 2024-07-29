package org.cyanx86.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
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
            case "help":        // subcomand Help
                this.scmHelp(sender);
                break;
            case "addplayer":   // subcomand AddPlayer
                this.scmAddPlayer(sender, args);
                break;
            case "outplayer":   // subcomand RemovePlayer
                this.scmRemovePlayer(sender, args);
                break;
            case "seelist":
                this.scmSeeList(sender);
                break;
        }
    }

    // Subcommands
    private void scmHelp(CommandSender sender) {
        if (!sender.hasPermission("overcrafted.manager")) {
            Messenger.sendToSender(
                sender,
                master.prefix + "&cNo tienes permiso para usar este comando."
            );
        }
        Messenger.sendToSender(sender, "&f&l------ OVERCRAFTED ------\n");
        Messenger.sendToSender(sender, "&7- /overcrafted help" + "\t" + "Ayuda del plugin.");
        Messenger.sendToSender(sender, "&7- /overcrafted addplayer" + "\t" + "Añadir jugador a la lista.");
        Messenger.sendToSender(sender, "&7- /overcrafted outplayer" + "\t" + "Quitar jugador de la lista.");
    }

    private void scmAddPlayer(CommandSender sender, String[] args) {
        if (!sender.hasPermission("overcrafted.manager")) {
            Messenger.sendToSender(
                sender,
                master.prefix + "&cNo tienes permiso para usar este comando."
            );
            return;
        }
        if (args.length != 2) {
            Messenger.sendToSender(
                sender,
                master.prefix + "&cArgumentos incompletos."
            );
            return;
        }

        Player player = Bukkit.getPlayer(args[1]);
        if (player == null) {
            Messenger.sendToSender(
                sender,
                master.prefix + "&cEl jugador &r&o" + args[1] + "&r&c no se encontró."
            );
            return;
        }

        if (master.getGamePlayers().size() == 4) {
            Messenger.sendToSender(
                sender,
                master.prefix + "&cLa lista de jugadores está llena."
            );
            return;
        }
        if (master.getGamePlayers().contains(player)) {
            Messenger.sendToSender(
                sender,
                master.prefix + "&cEl jugador &r&o" + player.getName() + "&r&c ya está en la lista."
            );
            return;
        }

        if (!master.addPlayer(player)) {
            Messenger.sendToSender(
                sender,
                master.prefix + "&cNo se pudo añadir jugador."
            );
        } else {
            Messenger.sendToSender(
                sender,
                master.prefix + "&aSe añadió al jugador &r&o" + player.getName() + "&r&a."
            );
            if (sender != player)
                Messenger.sendToSender(
                    player,
                    master.prefix + "&aFuiste añadido a una lista de jugadores."
                );
        }

    }

    private void scmRemovePlayer(CommandSender sender, String[] args) {
        if (!sender.hasPermission("overcrafted.manager")) {
            Messenger.sendToSender(
                sender,
                master.prefix + "&cNo tienes permiso para usar este comando."
            );
            return;
        }
        if (args.length != 2) {
            Messenger.sendToSender(
                sender,
                master.prefix + "&cArgumentos incompletos."
            );
            return;
        }

        Player player = Bukkit.getPlayer(args[1]);
        if (player == null) {
            Messenger.sendToSender(
                sender,
                master.prefix + "&cEl jugador &r&o" + args[1] + "&r&c no se encontró."
            );
            return;
        }

        if (master.getGamePlayers().isEmpty()) {
            Messenger.sendToSender(
                sender,
                master.prefix + "&cLa lista está vacía."
            );
            return;
        }

        if (!master.removePlayer(player)) {
            Messenger.sendToSender(
                sender,
                master.prefix + "&cNo se pudo eliminar o no se encontró al jugador."
            );
        } else {
            Messenger.sendToSender(
                sender,
                master.prefix + "&aSe removió al jugador &r&o" + player.getName() + "&r&a."
            );
            if (sender != player)
                Messenger.sendToSender(
                    player,
                    master.prefix + "&aFuiste retirado de la lista de jugadores."
                );
        }
    }

    private void scmSeeList(CommandSender sender) {
        if (!sender.hasPermission("overcraft.manager")) {
            Messenger.sendToSender(
                sender,
                master.prefix + "&cNo tienes permiso para usar este comando."
            );
            return;
        }

        Messenger.sendToSender(sender, "&f&l------ OVERCRAFTED ------\n");
        Messenger.sendToSender(sender, "&f&lJugadores actuales:");

        for(int i = 0; i < 4; i++) {
            Player current = null;
            try {
                 current = master.getGamePlayers().get(i);
            } catch (Exception e) {
                current = null;
            }
            if (current == null) {
                Messenger.sendToSender(sender, "&7" + (i + 1) + ".- " + "&o*** Vacío ***");
            } else {
                Messenger.sendToSender(sender, "&7" + (i + 1) + ".- " + "&o" + current.getName());
            }
        }
    }

}
