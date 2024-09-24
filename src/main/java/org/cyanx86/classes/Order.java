package org.cyanx86.classes;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.scheduler.BukkitTask;
import org.cyanx86.OverCrafted;
import org.cyanx86.managers.OrderManager;

public class Order {

    // -- [[ ATTRIBUTES ]] --

    // -- PUBLIC --

    // -- PRIVATE --
    private final Material recipe;
    private final OrderManager father;

    private final int timeout;

    private BukkitTask task;

    // -- [[ METHODS ]] --

    // -- PUBLIC --
    public Order(Material recipe, int timeout, OrderManager father) {
        this.recipe = recipe;
        this.timeout = timeout;
        this.father = father;
        this.startTimer();
    }

    public Material getRecipe() {
        return this.recipe;
    }

    public void dispose() {
        this.task.cancel();
    }

    // -- PRIVATE --
    private void startTimer() {
        this.task = Bukkit.getScheduler().runTaskLater(OverCrafted.getInstance(), () -> {
            this.dispose();
            this.father.removeOrder(this, true);
        }, (this.timeout * 20L));
    }

}
