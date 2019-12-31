package me.d4rk.fracassadobot.handlers.economy;

public enum EconomyEffect {

    BUFF_XP2(8640000),
    DEBUFF_MUTE5M(300000, EconomyCooldown.DEBUFF_MUTE),
    DEBUFF_MUTE10M(600000, EconomyCooldown.DEBUFF_MUTE),
    DEBUFF_XP50(2160000),
    DEBUFF_XP100(2160000),
    DEBUFF_VANISH(360000),
    OWNROLE(8640000);

    private final long time;
    private EconomyCooldown cooldown = null;

    EconomyEffect(long time) {
        this.time = time;
    }

    EconomyEffect(long time, EconomyCooldown cooldown) {
        this.time = time;
        this.cooldown = cooldown;
    }

    public long getTime() {
        return time;
    }

    public EconomyCooldown getCooldown() {
        return cooldown;
    }

}
