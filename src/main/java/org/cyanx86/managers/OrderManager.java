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

    // -- Public

    // -- Private
    private final OverCrafted master = OverCrafted.getInstance();

    private final List<Order> orderList = new ArrayList<>();
    private final List<Material> recipes;

    private int timeForNextOrder;
    private int orderTimeOut;

    private int orderStackLimit;

    private int time;
    private BukkitTask task;

    // -- [[ METHODS ]] --

    // -- Public
    public OrderManager(@NotNull List<Material> recipes) {
        this.recipes = recipes;
        this.timeForNextOrder = 20;
        this.orderTimeOut = 40;
        this.orderStackLimit = 5;
    }

    public void changeTimeForNextOrder(int timeseconds) {
        this.timeForNextOrder = timeseconds;
    }

    public void changeOrderTimeOut(int timeseconds) {
        this.orderTimeOut = timeseconds;
    }

    public List<Order> getOrderList() {
        return this.orderList;
    }

    public void removeOrder(@NotNull Order order) {
        this.orderList.remove(order);
        if (this.orderList.isEmpty())
            this.newOrder();
    }

    public boolean removeOrder(@NotNull Material recipe) {
        Optional<Order> query = this.orderList.stream()
                .filter(item -> item.getRecipe().equals(recipe))
                .findFirst();
        if (query.isEmpty())
            return false;

        boolean result = this.orderList.remove(query.get());
        if (this.orderList.isEmpty())
            this.newOrder();

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

    // -- Private
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
