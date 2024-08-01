package org.cyanx86.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.cyanx86.OverCrafted;
import org.cyanx86.utils.Messenger;

import java.util.List;

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
            case "addplayer":       // subcommand AddPlayer
                this.scmAddPlayer(sender, args);
                break;
            case "rmplayer":        // subcommand RemovePlayer
                this.scmRemovePlayer(sender, args);
                break;
            case "clearlist":       // subcommand ClearList
                this.scmClearList(sender);
                break;
            case "playerlist":      // subcommand PlayerList
                this.scmPlayerList(sender);
                break;
            default:
                this.scmHelp(sender);
                break;
        }
    }

    // Subcommands
    private void scmHelp(CommandSender sender) {
        // TODO: Language location of Help Page
        Messenger.msgToSender(sender, "&f&l------ OVERCRAFTED ------\n");
        Messenger.msgToSender(sender, "&7 [[ Comando /overcrafted ]]");
        Messenger.msgToSender(sender, "&7- /overcrafted help" + "\t" + "\t" + "Ayuda del plugin.");
        Messenger.msgToSender(sender, "&7- /overcrafted addplayer <jugador>" + "\t" + "\t" + "Añadir jugador a la lista.");
        Messenger.msgToSender(sender, "&7- /overcrafted rmplayer <jugador>" + "\t" + "\t" + "Quitar jugador de la lista.");
        Messenger.msgToSender(sender, "&7- /overcrafted playerlist" + "\t" + "\t" + "Ver lista de jugadores.");
        Messenger.msgToSender(sender, "&7- /overcrafted clearlist" + "\t" + "\t" + "Limpia la lista de jugadores.");
    }

    private void scmAddPlayer(CommandSender sender, String[] args) {
        if (args.length != 2) {
            Messenger.msgToSender(
                sender,
                OverCrafted.prefix + "&cArgumentos incompletos."    // TODO: Invalid arguments message.
            );
            return;
        }

        Player player = Bukkit.getPlayer(args[1]);
        if (player == null) {
            Messenger.msgToSender(
                sender,
                OverCrafted.prefix + "&cEl jugador &r&o" + args[1] + "&r&c no se encontró."     // TODO: Player not found message.
            );
            return;
        }

        switch (master.addPlayer(player)) {
            case FULL_LIST -> {
                Messenger.msgToSender(
                    sender,
                    OverCrafted.prefix + "&cLa lista está llena."   // TODO: Full list message.
                );
                return;
            }
            case ALREADY_IN -> {
                Messenger.msgToSender(
                    sender,
                    OverCrafted.prefix + "&cEl jugador ya está en la lista."    // TODO: Already in message.
                );
                return;
            }
        }

        Messenger.msgToSender(
            sender,
            OverCrafted.prefix + "&aSe añadió al jugador &r&o" + player.getName() + "&r&a."     // TODO: Player successfully added message.
        );
        if (sender != player)
            Messenger.msgToSender(
                player,
                OverCrafted.prefix + "&aFuiste añadido a una lista de jugadores."   // TODO: Player added to list advice message.
            );
    }

    private void scmRemovePlayer(CommandSender sender, String[] args) {
        if (args.length != 2) {
            Messenger.msgToSender(
                sender,
                OverCrafted.prefix + "&cArgumentos incompletos."    // TODO: Invalid arguments message.
            );
            return;
        }

        Player player = Bukkit.getPlayer(args[1]);
        if (player == null) {
            Messenger.msgToSender(
                sender,
                OverCrafted.prefix + "&cEl jugador &r&o" + args[1] + "&r&c no se encontró."     // TODO: Player not found message.
            );
            return;
        }

        switch (master.removePlayer(player)) {
            case EMPTY_LIST -> {
                Messenger.msgToSender(
                    sender,
                    OverCrafted.prefix + "&cLa lista está vacía."   // TODO: Empty list message;
                );
                return;
            }
            case NOT_FOUND -> {
                Messenger.msgToSender(
                    sender,
                    OverCrafted.prefix + "&cNo encontró al jugador &r&o" + player.getName() + "&r&a."
                );
                return;
            }
        }

        Messenger.msgToSender(
            sender,
            OverCrafted.prefix + "&aSe removió al jugador &r&o" + player.getName() + "&r&a."    // TODO: Player successfully removed message.
        );
        if (sender != player)
            Messenger.msgToSender(
                player,
                OverCrafted.prefix + "&cFuiste retirado de la lista de jugadores."  // TODO: Player removed from list advide message.
            );
    }

    private void scmPlayerList(CommandSender sender) {
        Messenger.msgToSender(sender, "&f&l------ OVERCRAFTED ------\n");
        Messenger.msgToSender(sender, "&f&lJugadores actuales:");  // TODO: Language location.

        for(int i = 0; i < 4; i++) {
            Player current = null;

            try {
                 current = master.getGamePlayers().get(i);
            } catch (Exception ignored) {
            }

            if (current == null) {
                Messenger.msgToSender(sender, "&7" + (i + 1) + ".- " + "&o*** Vacío ***");
            } else {
                Messenger.msgToSender(sender, "&7" + (i + 1) + ".- " + "&o" + current.getName());
            }
        }
    }

    private void scmClearList(CommandSender sender) {
        List<Player> game_players = master.getGamePlayers();

        switch(master.clearPlayerList()) {
            case EMPTY_LIST -> {
                Messenger.msgToSender(
                    sender,
                    OverCrafted.prefix + "&cLa lista está vacía."
                );
                return;
            }
            case SUCCESS -> {
                Messenger.msgToSender(
                    sender,
                    OverCrafted.prefix + "&aLa lista fue vaciada."  // TODO: PlayerList successfully cleared message.
                );
                return;
            }
        }

        for (Player gamePlayer : game_players) {
            Player current;
            try {
                current = gamePlayer;
            } catch (Exception ignored) {
                continue;
            }

            if (sender != current) {
                Messenger.msgToSender(
                        current,
                        OverCrafted.prefix + "&cFuiste retirado de la lista de jugadores."  // TODO: Player removed from list advide message.
                );
            }
        }
    }

}
