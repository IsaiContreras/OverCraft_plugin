package org.cyanx86;

import org.bukkit.plugin.java.JavaPlugin;
import org.cyanx86.commands.MainCommand;
import org.cyanx86.utils.Messenger;

public class OverCrafted extends JavaPlugin {

    // -- [[ ATTRIBUTES ]] --

    // -- Public
    public static String prefix = "&8[&c&lOverCrafted&8] ";

    // -- Private
    private String version = getDescription().getVersion();

    // -- [[ METHODS ]] --

    // -- Public
    public void onEnable() {
        this.setupCommands();

        Messenger.sendConsoleMessage(
            prefix + "&ePlugin activo. &fVersion: " + version
        );
    }

    public void onDisable() {
        Messenger.sendConsoleMessage(
            prefix + "ePlugin desactivado."
        );
    }

    // -- Private
    private void setupCommands() {
        this.getCommand("overcrafted").setExecutor(new MainCommand(this));
    }

    private void setupEvents() {

    }

}
