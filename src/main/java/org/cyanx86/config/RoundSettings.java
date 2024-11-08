package org.cyanx86.config;

import org.bukkit.configuration.file.FileConfiguration;

import org.cyanx86.utils.Defaults.RoundSettings.*;

public class RoundSettings {

    // -- [[ ATTRIBUTES ]] --

    // -- PUBLIC --

    // -- PRIVATE --
    private int grStartCountdown;
    private int grRoundTime;
    private int grEndIntermission;
    private int grPlayerImmobilizationTime;
    private boolean grChestDrop;
    private double grDispensingDistance;

    private int smValuePerOrder;

    private int omTimeForNextOrder;
    private int omOrderTimeout;
    private int omOrderStackLimit;
    private float omBonusProbability;

    // -- [[ METHODS ]] --

    // -- PUBLIC --
    public RoundSettings() {
        this.loadDefault();
    }

    public int getGRStartCountdown() { return this.grStartCountdown; }
    public int getGRTime() { return this.grRoundTime; }
    public int getGRIntermissionTime() { return this.grEndIntermission; }
    public int getGRPlayerImmobilizationTime() { return this.grPlayerImmobilizationTime; }
    public boolean getGRChestDrop() { return this.grChestDrop; }
    public double getGrDispensingDistance() { return this.grDispensingDistance; }

    public int getSMValuePerOrder() { return this.smValuePerOrder; }

    public int getOMTimeForNextOrder() { return this.omTimeForNextOrder; }
    public int getOMOrderTimeout() { return this.omOrderTimeout; }
    public int getOMOrderStackLimit() { return this.omOrderStackLimit; }
    public float getOMBonusProbability() { return this.omBonusProbability; }

    // -- PROTECTED --
    public void load(FileConfiguration config) {
        try { if (config.get("game-round.start-countdown") != null)
            this.grStartCountdown = (int)config.get("game-round.start-countdown");
        } catch (NullPointerException | ClassCastException ignored) { }
        try { if (config.get("game-round.round-time") != null)
            this.grRoundTime = (int)config.get("game-round.round-time");
        } catch (NullPointerException | ClassCastException ignored) { }
        try { if (config.get("game-round.end-intermission") != null)
            this.grEndIntermission = (int)config.get("game-round.end-intermission");
        } catch (NullPointerException | ClassCastException ignored) { }
        try { if (config.get("game-round.player-immobilization") != null)
            this.grPlayerImmobilizationTime = (int)config.get("game-round.player-immobilization");
        } catch (NullPointerException | ClassCastException ignored) { }
        try { if (config.get("game-round.chest-drop") != null)
            this.grChestDrop = (boolean)config.get("game-round.chest-drop");
        } catch (NullPointerException | ClassCastException ignored) { }
        try { if (config.get("game-round.dispencing-distance") != null)
            this.grDispensingDistance = (double) config.get("game-round.dispencing-distance");
        } catch (NullPointerException | ClassCastException ignored) { }

        try { if (config.get("score-manager.value-per-order") != null)
            this.smValuePerOrder = (int)config.get("score-manager.value-per-order");
        } catch (NullPointerException | ClassCastException ignored) { }

        try { if (config.get("order-manager.time-for-next-order") != null)
            this.omTimeForNextOrder = (int)config.get("order-manager.time-for-next-order");
        } catch (NullPointerException | ClassCastException ignored) { }
        try { if (config.get("order-manager.order-timeout") != null)
            this.omOrderTimeout = (int)config.get("order-manager.order-timeout");
        } catch (NullPointerException | ClassCastException ignored) { }
        try { if (config.get("order-manager.order-stack-limit") != null)
            this.omOrderStackLimit = (int)config.get("order-manager.order-stack-limit");
        } catch (NullPointerException | ClassCastException ignored) { }
        try { if (config.get("order-manager.bonus-probability") != null)
            this.omBonusProbability = (float)((double)config.get("order-manager.bonus-probability"));
        } catch (NullPointerException | ClassCastException ignored) { }
    }

    public void save(FileConfiguration config) {
        config.set("game-round.start-countdown", this.grStartCountdown);
        config.set("game-round.round-time", this.grRoundTime);
        config.set("game-round.end-intermission", this.grEndIntermission);
        config.set("game-round.player-immobilization", this.grPlayerImmobilizationTime);
        config.set("game-round.chest-drop", this.grChestDrop);
        config.set("game-round.dispencing-distance", this.grDispensingDistance);

        config.set("score-manager.value-per-order", this.smValuePerOrder);

        config.set("order-manager.time-for-next-order", this.omTimeForNextOrder);
        config.set("order-manager.order-timeout", this.omOrderTimeout);
        config.set("order-manager.order-stack-limit", this.omOrderStackLimit);
        config.set("order-manager.bonus-probability", this.omBonusProbability);
    }

    // -- PRIVATE --

    private void loadDefault() {
        // Game Round Settings
        this.grStartCountdown = GameRound.startCountdown;
        this.grRoundTime = GameRound.roundTime;
        this.grEndIntermission = GameRound.endIntermission;
        this.grPlayerImmobilizationTime = GameRound.playerImmobilization;
        this.grChestDrop = GameRound.chestDrop;
        this.grDispensingDistance = GameRound.dispencingDistance;
        // Score Manager Settings
        this.smValuePerOrder = ScoreManager.valuePerOrder;
        // Order Manager Settings
        this.omTimeForNextOrder = OrderManager.timeForNextOrder;
        this.omOrderTimeout = OrderManager.orderTimeOut;
        this.omOrderStackLimit = OrderManager.orderStackLimit;
        this.omBonusProbability = OrderManager.bonusProbability;
    }

}
