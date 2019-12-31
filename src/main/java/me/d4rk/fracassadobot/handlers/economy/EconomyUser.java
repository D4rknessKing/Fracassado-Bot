package me.d4rk.fracassadobot.handlers.economy;

import javafx.util.Pair;

import java.util.List;

public class EconomyUser {

    private final String id;
    private final long money, lastDaily, lastStreak;
    private final int streak;
    private final List<String> inventory;
    private final List<Pair<String, Long>> effects, cooldown;

    public EconomyUser(String id, long money, long lastDaily, int streak, long lastStreak, List<String> inventory, List<Pair<String, Long>> effects, List<Pair<String, Long>> cooldown) {
        this.id = id;
        this.money = money;
        this.lastDaily = lastDaily;
        this.streak = streak;
        this.lastStreak = lastStreak;
        this.inventory = inventory;
        this.effects = effects;
        this.cooldown = cooldown;
    }

    public String getId() {
        return id;
    }

    public long getMoney() {
        return money;
    }

    public long getLastDaily() {
        return lastDaily;
    }

    public int getStreak() {
        return streak;
    }

    public long getLastStreak() {
        return lastStreak;
    }

    public List<String> getInventory() {
        return inventory;
    }

    public List<Pair<String, Long>> getEffects() {
        return effects;
    }

    public List<Pair<String, Long>> getCooldown() {
        return cooldown;
    }
}
