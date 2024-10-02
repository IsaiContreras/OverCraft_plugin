package org.cyanx86.classes;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scoreboard.*;
import org.cyanx86.utils.Messenger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class OrderDisplayer {
    // -- [[ ATTRIBUTES ]] --

    // -- PUBLIC --

    // -- PRIVATE --

    private final HashMap<String, Order> lines = new HashMap<>();

    private Objective objective;
    private Scoreboard scoreboard;
    private Team green;
    private Team yellow;
    private Team red;

    // -- [[ METHODS ]] --

    // -- PUBLIC --

    public OrderDisplayer() {
        InitializeDisplayer();
    }

    public void addLine(int rowNumber, String line, Order order) {
        Messenger.msgToConsole("Adding order line");
        this.lines.put(line, order);
        Score score = this.objective.getScore(line);
        score.setScore(10-rowNumber);

        switch (order.getState()) {
            case "Green":
                Messenger.msgToConsole("Adding to green");
                this.green.addEntry(line);
                break;
            case "Yellow":
                Messenger.msgToConsole("Adding to yellow");
                this.yellow.addEntry(line);
                break;
            case "Red":
                Messenger.msgToConsole("Adding to red");
                this.red.addEntry(line);
                break;
        }
    }

    public void clearScore() {
        for(String line : this.lines.keySet()) {
            this.objective.getScore(line).setScore(0);
            this.scoreboard.resetScores(line);
        }
    }

    public void changeTeam(Order order, String team) {
        for(String line : this.lines.keySet()) {
            if (this.lines.get(line) == order){
                Team temp = scoreboard.getTeam(team);
                if(temp != null)
                    temp.addEntry(line);
            }
        }
    }
    // -- PRIVATE --

    private void InitializeDisplayer() {
        ScoreboardManager sbm = Bukkit.getScoreboardManager();

        if(sbm != null) {
            this.scoreboard = sbm.getNewScoreboard();
            this.objective = this.scoreboard.registerNewObjective("Orders", Criteria.DUMMY, ChatColor.BLUE + "ORDENES");
            this.objective.setDisplaySlot(DisplaySlot.SIDEBAR);

            this.green = scoreboard.registerNewTeam("Green");
            this.yellow = scoreboard.registerNewTeam("Yellow");
            this.red = scoreboard.registerNewTeam("Red");

            this.green.setColor(ChatColor.GREEN);
            this.yellow.setColor(ChatColor.YELLOW);
            this.red.setColor(ChatColor.RED);

        }
    }

    public Scoreboard getScoreboard() {
        return this.scoreboard;
    }
}
