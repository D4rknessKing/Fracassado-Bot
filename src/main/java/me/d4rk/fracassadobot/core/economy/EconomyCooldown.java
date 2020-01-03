package me.d4rk.fracassadobot.core.economy;

public enum EconomyCooldown {

    DEBUFF_MUTE(7200000);

    private final long cooldown;

    EconomyCooldown(long cooldown) {
        this.cooldown = cooldown;
    }

    public long getCooldown() {
        return cooldown;
    }
    
    
}
