package org.cyanx86.managers;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.scheduler.BukkitTask;

import org.cyanx86.OverCrafted;
import org.cyanx86.classes.Order;
import org.cyanx86.utils.Functions;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.jetbrains.annotations.NotNull;

public class OrderManager {

    // -- [[ ATTRIBUTES ]] --

    // -- PUBLIC --

    // -- PRIVATE --
    private final OverCrafted master = OverCrafted.getInstance();
    private final ScoreManager scoreManager;

    private final List<Order> orderList = new ArrayList<>();
    private final List<Material> recipes;

    private int timeForNextOrder;
    private int orderTimeOut;

    private int orderStackLimit;

    private int time;
    private BukkitTask task;

    // -- [[ METHODS ]] --

    // -- PUBLIC --
    public OrderManager(@NotNull List<Material> recipes, ScoreManager scoreManager) {
        this.recipes = recipes;
        this.scoreManager = scoreManager;
        this.timeForNextOrder = 20;
        this.orderTimeOut = 80;
        this.orderStackLimit = 5;
    }

    public void setTimeForNextOrder(int timeseconds) {
        this.timeForNextOrder = timeseconds;
    }

    public void setOrderTimeOut(int timeseconds) {
        this.orderTimeOut = timeseconds;
    }

    public List<Order> getOrderList() {
        return new ArrayList<>(this.orderList);
    }

    public void removeOrder(@NotNull Order order, boolean lost) {
        this.orderList.remove(order);
        if (this.orderList.isEmpty())
            this.newOrder();

        if (lost)
            this.scoreManager.incrementLostOrder();
    }

    public boolean removeOrder(@NotNull Material recipe, boolean lost) {
        Optional<Order> query = this.orderList.stream()
                .filter(item -> item.getRecipe().equals(recipe))
                .findFirst();
        if (query.isEmpty())
            return false;

        Order order = query.get();
        order.dispose();

        boolean result = this.orderList.remove(query.get());
        if (this.orderList.isEmpty())
            this.newOrder();

        if (lost)
            this.scoreManager.incrementLostOrder();

        return result;
    }

    public void startGenerator() {
        this.time = 0;
        this.task = Bukkit.getScheduler().runTaskTimer(master, () -> {
            if (time == 0) {
                this.newOrder();
                this.time = this.timeForNextOrder;
            } else
                this.time--;
        }, 20L, 20L);
    }

    public void stopGenerator() {
        this.task.cancel();
        this.time = 0;
    }

    // -- PRIVATE --
    private void newOrder() {
        if (this.recipes.isEmpty())
            return;
        if (this.orderList.size() == 5)
            return;
        this.orderList.add(
            new Order(
                recipes.get(Functions.getRandomNumber(0, this.recipes.size() - 1)),
                this.orderTimeOut,
                this
            )
        );
    }

}
