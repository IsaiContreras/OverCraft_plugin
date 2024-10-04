package org.cyanx86.config;

import org.bukkit.configuration.file.FileConfiguration;

import org.cyanx86.utils.CustomConfigFile;
import org.cyanx86.utils.Defaults.RoundSettings.*;

public class RoundSettings extends CustomConfigFile {

    // -- [[ ATTRIBUTES ]] --

    // -- PUBLIC --

    // -- PRIVATE --
    private static RoundSettings instance;

    private int grStartCountdown;
    private int grRoundTime;
    private int grEndIntermission;

    private int smValuePerOrder;

    private int omTimeForNextOrder;
    private int omOrderTimeout;
    private int omOrderStackLimit;

    // -- [[ METHODS ]] --

    // -- PUBLIC --
    public static RoundSettings getInstance() {
        if (instance == null)
            instance = new RoundSettings();
        return instance;
    }

    public int getGRStartCountdown() { return this.grStartCountdown; }
    public int getGRTime() { return this.grRoundTime; }
    public int getGRIntermissionTime() { return this.grEndIntermission; }

    public int getSMValuePerOrder() { return this.smValuePerOrder; }

    public int getOMTimeForNextOrder() { return this.omTimeForNextOrder; }
    public int getOMOrderTimeout() { return this.omOrderTimeout; }
    public int getOMOrderStackLimit() { return this.omOrderStackLimit; }

    // -- PRIVATE --
    private RoundSettings() {
        super(
            "roundsettings.yml",
            "ocf_settings",
            true
        );
        this.loadDefault();
        if (this.registerConfig())
            this.load();
        else this.save();
    }

    @Override
    protected void load() {
        FileConfiguration config = this.getConfig();

        this.grStartCountdown = config.get("game_round.start_countdown") != null ?
                (int)config.get("game_round.start_countdown") : this.grStartCountdown;
        this.grRoundTime = config.get("game_round.round_time") != null ?
                (int)config.get("game_round.round_time") : this.grRoundTime;
        this.grEndIntermission = config.get("game_round.end_intermission") != null ?
                (int)config.get("game_round.end_intermission") : this.grEndIntermission;

        this.smValuePerOrder = config.get("score_manager.value_per_order") != null ?
                (int)config.get("score_manager.value_per_order") : this.smValuePerOrder;

        this.omTimeForNextOrder = config.get("order_manager.time_for_next_order") != null ?
                (int)config.get("order_manager.time_for_next_order") : this.omTimeForNextOrder;
        this.omOrderTimeout = config.get("order_manager.order_timeout") != null ?
                (int)config.get("order_manager.order_timeout") : this.omOrderTimeout;
        this.omOrderStackLimit = config.get("order_manager.order_stack_limit") != null ?
                (int)config.get("order_manager.order_stack_limit") : this.omOrderStackLimit;
    }

    @Override
    protected void reload() {
        this.reloadConfig();
        this.load();
    }

    @Override
    protected void save() {
        FileConfiguration config = this.getConfig();

        config.set("game_round.start_countdown", this.grStartCountdown);
        config.set("game_round.round_time", this.grRoundTime);
        config.set("game_round.end_intermission", this.grEndIntermission);

        config.set("score_manager.value_per_order", this.smValuePerOrder);

        config.set("order_manager.time_for_next_order", this.omTimeForNextOrder);
        config.set("order_manager.order_timeout", this.omOrderTimeout);
        config.set("order_manager.order_stack_limit", this.omOrderStackLimit);

        this.saveConfig();
    }

    private void loadDefault() {
        this.defaultGameRound();
        this.defaultScoreManager();
        this.defaultOrderManager();
    }

    private void defaultGameRound() {
        // Game Round Settings
        this.grStartCountdown = GameRound.startCountdown;
        this.grRoundTime = GameRound.roundTime;
        this.grEndIntermission = GameRound.endIntermission;
    }

    private void defaultScoreManager() {
        // Score Manager Settings
        this.smValuePerOrder = ScoreManager.valuePerOrder;
    }

    private void defaultOrderManager() {
        // Order Manager Settings
        this.omTimeForNextOrder = OrderManager.timeForNextOrder;
        this.omOrderTimeout = OrderManager.orderTimeOut;
        this.omOrderStackLimit = OrderManager.orderStackLimit;
    }

}
