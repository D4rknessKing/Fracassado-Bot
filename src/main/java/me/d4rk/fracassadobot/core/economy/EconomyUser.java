package me.d4rk.fracassadobot.core.economy;

import javafx.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class EconomyUser {

    private final String id;
    private final long money, lastDaily, lastStreak;
    private final int streak;
    private final List<String> inventory;
    private final List<HashMap<String, Object>> effects, cooldown;

    public EconomyUser(String id, long money, long lastDaily, int streak, long lastStreak, List<String> inventory, List<HashMap<String, Object>> effects, List<HashMap<String, Object>> cooldown) {
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

    public List<HashMap<String, Object>> getEffects() {
        return effects;
    }

    public List<HashMap<String, Object>> getCooldown() {
        return cooldown;
    }

    public List<Pair<String, Long>> getEffectsPair() {
        List<Pair<String, Long>> userEffects = new ArrayList<>();
        for(HashMap<String, Object> hMap : effects) {
            userEffects.add(new Pair<>((String) hMap.get("key"), (Long) hMap.get("value")));
        }
        return userEffects;
    }

    public List<Pair<String, Long>> getCooldownPair() {
        List<Pair<String, Long>> userCooldown = new ArrayList<>();
        for(HashMap<String, Object> hMap : cooldown) {
            userCooldown.add(new Pair<>((String) hMap.get("key"), (Long) hMap.get("value")));
        }
        return userCooldown;
    }
}
