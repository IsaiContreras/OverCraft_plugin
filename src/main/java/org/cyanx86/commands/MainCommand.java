package org.cyanx86.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import org.cyanx86.OverCrafted;
import org.cyanx86.classes.GameRound;
import org.cyanx86.classes.Order;
import org.cyanx86.utils.DataFormatting;
import org.cyanx86.utils.Messenger;

import java.util.List;

public class MainCommand implements CommandExecutor {

    // -- [[ ATTRIBUTES ]] --

    // -- Public

    // -- Private
    private final OverCrafted master = OverCrafted.getInstance();

    // -- [[ METHODS ]] --

    // -- Public

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
            return;
        }
        if (args.length == 0) {
            this.scmHelp(sender);
            return;
        }

        switch (args[0].toLowerCase()) {
            case "help":            // subCommand Help
                this.scmHelp(sender);
                break;
            case "roundtime":       // subCommand SetRoundTime
                this.scmSetRoundTime(sender, args);
                break;
            case "recipelist":
                this.scmRecipeList(sender);
                break;
            case "startround":      // subCommand StartRound
                this.scmStartRound(sender);
                break;
            case "endround":        // subcommand EndRound
                this.scmEndRound(sender);
                break;
            default:
                this.scmHelp(sender);
                break;
        }
    }

    // Subcommands
    private void scmHelp(CommandSender sender) {
        Messenger.msgToSender(sender, "&f&l------ OVERCRAFTED ------");
        Messenger.msgToSender(sender, "&7 [[ Comando /overcrafted ]]");
        Messenger.msgToSender(sender, "&7- /overcrafted help");
        Messenger.msgToSender(sender, "&7- /overcrafted roundtime <tiempo>");
        Messenger.msgToSender(sender, "&7- /overcrafted recipelist");
        Messenger.msgToSender(sender, "&7- /overcrafted startround");
        Messenger.msgToSender(sender, "&7- /overcrafted endround");
    }

    private void scmSetRoundTime(CommandSender sender, String[] args) {
        if (args.length != 2) {
            Messenger.msgToSender(
                sender,
                OverCrafted.prefix + "&cArgumentos incompletos."    // TODO: Invalid arguments message.
            );
            return;
        }

        int time;
        try {
            time = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            Messenger.msgToSender(
                sender,
                OverCrafted.prefix + "&cEste argumento solo acepta valores numéricos."
            );
            return;
        }

        master.getGameRoundManager().setRoundTime(time);

        Messenger.msgToSender(
            sender,
            OverCrafted.prefix + "&aLa ronda durará &r&o" +
                    DataFormatting.formatSecondsToTime(time) + "&r&a minutos."
        );
    }

    private void scmRecipeList(CommandSender sender) {
        GameRound round = master.getGameRoundManager().getGameRound();
        if (round == null || round.getCurrentRoundState() == GameRound.ROUNDSTATE.ENDED) {
            Messenger.msgToSender(
                sender,
                OverCrafted.prefix + "&cAún no comienza la ronda o ha finalizado."
            );
            return;
        }

        Messenger.msgToSender(sender, "&f&l------ OVERCRAFTED ------\n");
        Messenger.msgToSender(sender, "&f&lRecetas actuales:");  // TODO: Language location.

        List<Order> orderList = round.getCurrentOrders();
        if (orderList.isEmpty()) {
            Messenger.msgToSender(
                sender,
                "&7&o** Vacío **"
            );
            return;
        }
        for (Order order : orderList) {
            Messenger.msgToSender(
                sender,
                "&7&o- &r&o" + order.getRecipe().name() + "&r&7&o."
            );
        }
    }

    private void scmStartRound(CommandSender sender) {
        if(!master.getGameRoundManager().startRound()) {
            Messenger.msgToSender(
                sender,
                OverCrafted.prefix + "&cDebe seleccionar un GameArea y los jugadores para la ronda."
            );
        }
    }

    private void scmEndRound(CommandSender sender) {
        if (!master.getGameRoundManager().terminateRound(null)) {
            Messenger.msgToSender(
                sender,
                OverCrafted.prefix + "&cNo se pudo cancelar la ronda. Aun no ha iniciado o ya ha acabado."
            );
            return;
        }

        Messenger.msgToSender(
            sender,
            OverCrafted.prefix + "&aRonda cancelada."
        );
    }

}
