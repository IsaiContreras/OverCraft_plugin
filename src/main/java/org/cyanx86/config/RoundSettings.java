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
    private int grPlayerImmobilizationTime;

    private int smValuePerOrder;

    private int omTimeForNextOrder;
    private int omOrderTimeout;
    private int omOrderStackLimit;
    private float omBonusProbability;

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
    public int getGRPlayerImmobilizationTime() { return this.grPlayerImmobilizationTime; }

    public int getSMValuePerOrder() { return this.smValuePerOrder; }

    public int getOMTimeForNextOrder() { return this.omTimeForNextOrder; }
    public int getOMOrderTimeout() { return this.omOrderTimeout; }
    public int getOMOrderStackLimit() { return this.omOrderStackLimit; }
    public float getOMBonusProbability() { return this.omBonusProbability; }

    // -- PROTECTED --
    @Override
    protected void load() {
        FileConfiguration config = this.getConfig();

        try { if (config.get("game_round.start_countdown") != null)
            this.grStartCountdown = (int)config.get("game_round.start_countdown");
        } catch (NullPointerException | ClassCastException ignored) { }
        try { if (config.get("game_round.round_time") != null)
            this.grRoundTime = (int)config.get("game_round.round_time");
        } catch (NullPointerException | ClassCastException ignored) { }
        try { if (config.get("game_round.end_intermission") != null)
            this.grEndIntermission = (int)config.get("game_round.end_intermission");
        } catch (NullPointerException | ClassCastException ignored) { }
        try { if (config.get("game_round.player_immobilization") != null)
            this.grPlayerImmobilizationTime = (int)config.get("game_round.player_immobilization");
        } catch (NullPointerException | ClassCastException ignored) { }

        try { if (config.get("score_manager.value_per_order") != null)
            this.smValuePerOrder = (int)config.get("score_manager.value_per_order");
        } catch (NullPointerException | ClassCastException ignored) { }

        try { if (config.get("order_manager.time_for_next_order") != null)
            this.omTimeForNextOrder = (int)config.get("order_manager.time_for_next_order");
        } catch (NullPointerException | ClassCastException ignored) { }
        try { if (config.get("order_manager.order_timeout") != null)
            this.omOrderTimeout = (int)config.get("order_manager.order_timeout");
        } catch (NullPointerException | ClassCastException ignored) { }
        try { if (config.get("order_manager.order_stack_limit") != null)
            this.omOrderStackLimit = (int)config.get("order_manager.order_stack_limit");
        } catch (NullPointerException | ClassCastException ignored) { }
        try { if (config.get("order_manager.bonus_probability") != null)
            this.omBonusProbability = (float)((double)config.get("order_manager.bonus_probability"));
        } catch (NullPointerException | ClassCastException ignored) { }
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
        config.set("game_round.player_immobilization", this.grPlayerImmobilizationTime);

        config.set("score_manager.value_per_order", this.smValuePerOrder);

        config.set("order_manager.time_for_next_order", this.omTimeForNextOrder);
        config.set("order_manager.order_timeout", this.omOrderTimeout);
        config.set("order_manager.order_stack_limit", this.omOrderStackLimit);
        config.set("order_manager.bonus_probability", this.omBonusProbability);

        this.saveConfig();
    }

    // -- PRIVATE --
    private RoundSettings() {
        super(
            "round_settings.yml",
            null,
            true
        );
        this.loadDefault();
        if (this.registerConfig())
            this.load();
        else this.save();
    }

    private void loadDefault() {
        // Game Round Settings
        this.grStartCountdown = GameRound.startCountdown;
        this.grRoundTime = GameRound.roundTime;
        this.grEndIntermission = GameRound.endIntermission;
        this.grPlayerImmobilizationTime = GameRound.playerImmobilization;
        // Score Manager Settings
        this.smValuePerOrder = ScoreManager.valuePerOrder;
        // Order Manager Settings
        this.omTimeForNextOrder = OrderManager.timeForNextOrder;
        this.omOrderTimeout = OrderManager.orderTimeOut;
        this.omOrderStackLimit = OrderManager.orderStackLimit;
        this.omBonusProbability = OrderManager.bonusProbability;
    }

}
