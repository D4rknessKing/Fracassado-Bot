package me.d4rk.fracassadobot.handlers.economy;

public enum EconomyEffect {

    BUFF_XP2(86400000),
    DEBUFF_MUTE5M(300000, EconomyCooldown.DEBUFF_MUTE),
    DEBUFF_MUTE10M(600000, EconomyCooldown.DEBUFF_MUTE),
    DEBUFF_XP50(21600000),
    DEBUFF_XP100(21600000),
    DEBUFF_VANISH(3600000),
    OWNROLE(86400000);

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

    public EconomyItem getItem() {
        return EconomyItem.valueOf(this.name());
    }

}
