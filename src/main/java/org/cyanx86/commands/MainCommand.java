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
import java.util.Map;

import org.jetbrains.annotations.NotNull;

public class MainCommand implements CommandExecutor {

    // -- [[ ATTRIBUTES ]] --

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
    private void handleSubcommands(CommandSender sender, String[] args) {
        if (args.length == 0) {
            this.scmHelp(sender);
            return;
        }

        switch (args[0].toLowerCase()) {
            case "help":            // subCommand Help
                this.scmHelp(sender);
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
            case "results":
                this.scmResults(sender);
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
        Messenger.msgToSender(sender, "&7- /overcrafted recipelist");
        Messenger.msgToSender(sender, "&7- /overcrafted startround");
        Messenger.msgToSender(sender, "&7- /overcrafted endround");
        Messenger.msgToSender(sender, "&7- /overcrafted results");
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
        if (!sender.hasPermission("overcrafted.manager")) {
            Messenger.msgToSender(
                sender,
                OverCrafted.prefix + "&cNo tienes permiso para usar este comando." // TODO: No permission message.
            );
            return;
        }
        if(!master.getGameRoundManager().startRound()) {
            Messenger.msgToSender(
                sender,
                OverCrafted.prefix + "&cDebe seleccionar un GameArea y los jugadores para la ronda."
            );
        }
    }

    private void scmEndRound(CommandSender sender) {
        if (!sender.hasPermission("overcrafted.manager")) {
            Messenger.msgToSender(
                sender,
                OverCrafted.prefix + "&cNo tienes permiso para usar este comando." // TODO: No permission message.
            );
            return;
        }
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

    private void scmResults(CommandSender sender) {
        GameRound round = master.getGameRoundManager().getGameRound();
        if (round == null || round.getCurrentRoundState() != GameRound.ROUNDSTATE.ENDED) {
            Messenger.msgToSender(
                sender,
                OverCrafted.prefix + "&cAún no hay ronda finalizada."
            );
            return;
        }

        Map<String, Object> results = round.getScores();

        Messenger.msgToSender(sender, "&f&l------ OVERCRAFTED ------\n");
        Messenger.msgToSender(sender, "&f&lResultados:");
        Messenger.msgToSender(sender, "  &7Ordenes entregadas: &o" +
                results.get("delivered"));
        Messenger.msgToSender(sender, "  &7Ordenes perdidas: &o" +
                results.get("lost"));
        Messenger.msgToSender(sender, "  &7Puntos totales: &o" +
                results.get("total"));
    }

}
