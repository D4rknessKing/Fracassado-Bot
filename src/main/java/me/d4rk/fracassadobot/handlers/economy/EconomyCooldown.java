package me.d4rk.fracassadobot.handlers.economy;

public enum EconomyCooldown {

    DEBUFF_MUTE(21600000);

    private final long cooldown;

    EconomyCooldown(long cooldown) {
        this.cooldown = cooldown;
    }

    public long getCooldown() {
        return cooldown;
    }
    
    
}
