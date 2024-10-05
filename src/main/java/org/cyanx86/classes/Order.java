package org.cyanx86.classes;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.scheduler.BukkitTask;

import org.cyanx86.OverCrafted;
import org.cyanx86.managers.OrderManager;
import org.cyanx86.utils.Messenger;

import org.jetbrains.annotations.NotNull;

public class Order {

    // -- [[ ATTRIBUTES ]] --

    // -- PUBLIC --

    // -- PRIVATE --
    private final Material recipe;
    private final OrderManager father;

    private final int timeout;

    private int timeLeft;

    private String color;

    private BukkitTask task;
    private BukkitTask state;

    // -- [[ METHODS ]] --

    // -- PUBLIC --
    public Order(@NotNull Material recipe, int timeout, @NotNull OrderManager father) {
        this.recipe = recipe;
        this.timeout = timeout;
        this.father = father;
        this.timeLeft = this.timeout;
        this.color = "Green";
        this.startTimer();
        this.stateUpdater();
    }

    public Material getRecipe() {
        return this.recipe;
    }

    public void dispose() {
        this.state.cancel();
        this.task.cancel();
    }

    public String getState() {
        return this.color;
    }

    // -- PRIVATE --
    private void startTimer() {
        this.task = Bukkit.getScheduler().runTaskLater(OverCrafted.getInstance(), () -> {
            this.dispose();
            this.father.removeOrder(this, true);
        }, (this.timeout * 20L));
    }

    private void stateUpdater() {
        this.state = Bukkit.getScheduler().runTaskTimer(OverCrafted.getInstance(), () -> {
            if (this.timeLeft > ((this.timeout/3)*2) && !this.color.equalsIgnoreCase("Green")) {
                Messenger.msgToConsole("Updating order to green");
                this.color = "Green";
                this.father.updateState(this,"Green");
            }
            else if (this.timeLeft <= ((this.timeout/3)*2) && this.timeLeft > (this.timeout/3) && !this.color.equalsIgnoreCase("Yellow")) {
                Messenger.msgToConsole("Updating order to yellow");
                this.color = "Yellow";
                this.father.updateState(this,"Yellow");
            }
            else if (this.timeLeft < (this.timeout/3) && !this.color.equalsIgnoreCase("Red")) {
                Messenger.msgToConsole("Updating order to red");
                this.color = "Red";
                this.father.updateState(this,"Red");
            }

            this.timeLeft -= 1;
        }, 20, 20);
    }
}
