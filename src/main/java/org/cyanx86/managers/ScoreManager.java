package org.cyanx86.managers;

import org.cyanx86.config.GeneralSettings;

public class ScoreManager {

    // -- [[ ATTRIBUTES ]] --

    // -- PUBLIC --

    // -- PRIVATE --
    private final int valuePerOrder;

    private int deliveredOrders = 0;
    private int lostOrders = 0;
    private int score = 0;
    private int bonus = 0;

    // -- [[ METHODS ]] --

    // -- PUBLIC --
    public ScoreManager() {
        this.valuePerOrder = GeneralSettings.getInstance().getRoundSettings().getSMValuePerOrder();
    }

    public void incrementDeliveredOrder() {
        this.deliveredOrders++;
        this.updateScore();
    }

    public void incrementLostOrder() {
        this.lostOrders++;
        this.updateScore();
    }

    public void addBonus(int value) {
        this.bonus += value;
    }

    public int getDeliveredOrders() {
        return this.deliveredOrders;
    }

    public int getLostOrders() {
        return this.lostOrders;
    }

    public int getScore() { return this.score; }

    public int getBonus() { return this.bonus; }

    // -- PRIVATE --
    private void updateScore() {
        this.score = (this.deliveredOrders * this.valuePerOrder) - (this.lostOrders * this.valuePerOrder);
    }

}
