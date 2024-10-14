package org.cyanx86.managers;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.scheduler.BukkitTask;

import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.cyanx86.OverCrafted;
import org.cyanx86.classes.Order;
import org.cyanx86.classes.OrderDisplayer;
import org.cyanx86.config.GeneralSettings;
import org.cyanx86.utils.DataFormatting;
import org.cyanx86.config.RoundSettings;
import org.cyanx86.utils.Functions;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import org.cyanx86.utils.Messenger;
import org.jetbrains.annotations.NotNull;

public class OrderManager {

    // -- [[ ATTRIBUTES ]] --

    // -- PUBLIC --

    // -- PRIVATE --
    private final OverCrafted master = OverCrafted.getInstance();
    private final SoundEffectsManager soundEffectsManager;
    private final ScoreManager scoreManager;

    private final List<Order> orderList = new ArrayList<>();
    private final List<Material> recipes;

    private final OrderDisplayer displayer = new OrderDisplayer();

    private int timeForNextOrder;
    private int orderTimeOut;

    private final int orderStackLimit;
    private final float bonusProbability;

    private Objective ordersDisplayer;

    private int time;
    private BukkitTask task;
    private int taskId;

    // -- [[ METHODS ]] --

    // -- PUBLIC --
    public OrderManager(@NotNull List<Material> recipes, SoundEffectsManager soundEffectsManager, ScoreManager scoreManager) {
        this.recipes = recipes;
        this.soundEffectsManager = soundEffectsManager;
        this.scoreManager = scoreManager;

        RoundSettings settings = RoundSettings.getInstance();
        this.timeForNextOrder = settings.getOMTimeForNextOrder();
        this.orderTimeOut = settings.getOMOrderTimeout();
        this.orderStackLimit = settings.getOMOrderStackLimit();
        this.bonusProbability = settings.getOMBonusProbability();
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

        if (lost) {
            this.soundEffectsManager.playLostOrder();
            this.scoreManager.incrementLostOrder();
        }

        this.updateDisplayer();
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

        if (lost) {
            this.soundEffectsManager.playLostOrder();
            this.scoreManager.incrementLostOrder();
        }
        else if (Functions.getRandomFloatNumber() <= this.bonusProbability) {
            this.scoreManager.addBonus(
                OverCrafted.getInstance().getRecipesBonus().getBonusValue(
                    order.getRecipe()
                )
            );
        }

        this.updateDisplayer();

        return result;
    }

    public void startGenerator() {
        this.time = 0;
        this.task = Bukkit.getScheduler().runTaskTimer(master, () -> {
            if (this.time == 0) {
                this.newOrder();
                this.time = this.timeForNextOrder;
            } else
                this.time--;
        }, 20L, 20L);
        this.taskId = this.task.getTaskId();
    }

    public void stopGenerator() {
        Bukkit.getScheduler().cancelTask(this.taskId);
        this.clearOrders();
        this.displayer.clearScore();
        this.task.cancel();
        this.time = 0;
    }

    public Scoreboard getDisplayer() {
        return this.displayer.getScoreboard();
    }

    public void updateState(Order order, String state) {
        this.displayer.changeTeam(order, state);
    }

    // -- PRIVATE --
    private void newOrder() {
        Messenger.msgToConsole("Creating new order");
        this.soundEffectsManager.playOrderEntry();
        if (this.recipes.isEmpty())
            return;
        if (this.orderList.size() == this.orderStackLimit)
            return;
        this.orderList.add(
            new Order(
                this.recipes.get(Functions.getRandomIntNumber(0, this.recipes.size() - 1)),
                this.orderTimeOut,
                this
            )
        );
        this.updateDisplayer();
    }

    private void updateDisplayer() {
        this.displayer.clearScore();
        int orderNumber = 1;

        for(Order order : this.orderList) {
            String state = DataFormatting.repeate(orderNumber, "§r");
            String object = GeneralSettings.getInstance().getLocale().getMatName(order.getRecipe());

            String name = state + object;

            this.displayer.addLine(orderNumber, name, order);
            orderNumber += 1;
        }
    }

    private void clearOrders() {
        Iterator<Order> iterator = this.orderList.iterator();
        while (iterator.hasNext()) {
            Order order = iterator.next();
            iterator.remove();
            order.dispose();
        }

    }

}
