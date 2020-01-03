package me.d4rk.fracassadobot.handlers.economy;

public enum EconomyItem {

    UNKNOWN("Item Desconhecido", "Placehold item for displaying errors in the item system", "⁉", 0, false, false),
    BUFF_XP2("2XP Buff", "Ao ser utilizado, duplica o ganho de XP nas proximas 24 horas", "<:buff_2xp:661225911479304202>", 1200, true, true, EconomyEffect.BUFF_XP2),
    DEBUFF_MUTE5M("5min Mute", "Quando consumido, permite ao usuário mutar um membro da guild por 5 minutos", "🔇", 2500, true, true, EconomyEffect.DEBUFF_MUTE5M),
    DEBUFF_MUTE10M("10min Mute", "Quando consumido, permite ao usuário mutar um membro da guild por 10 minutos", "<:mute10m:662567415246487570>", 5200, true, true, EconomyEffect.DEBUFF_MUTE10M),
    DEBUFF_XP50("50%XP Debuff", "Ao ser usado em um membro da guild, diminui o ganho de XP do mesmo em 50% por 6 horas", "<:debuff_50_xp:661321497524305971>", 2000, true, true, EconomyEffect.DEBUFF_XP50),
    DEBUFF_XP100("100%XP Debuff", "Ao ser usado em um membro da guild, diminui o ganho de XP do mesmo em 100% por 6 horas", "<:debuff_100_xp:661682177909588008>", 5200, true, true, EconomyEffect.DEBUFF_XP100),
    DEBUFF_VANISH("Vanish Power", "Ao ser usado em um membro da guild, retira a cor de seu cargo por 1 hora", "<:debuff_vanish:661683704803885076>", 2000, true, true, EconomyEffect.DEBUFF_VANISH),
    REVERSE("Reverse Card", "Caso o usuario possua esse item no inventario, quando atacado por um debuff, o debuff automaticamente será enviado de volta (Pode ser afetado por outros reverse cards)", "<:reverse:661686210561048588>", 6000, false, true),
    LOTTERY("Loteria", "Quando utilizado, o usuario tem 1 chance em 10.000 de ganhar 20.000 FracassoCoins", "🧾", 1000, true, true),
    TRASHDAY("Bomba de Tinta", "Ao ser ativado a Bomba de Tinta faz com que todos os membros da guild fiquem por 24hrs com apenas uma cor a ser escolhida pelo comprador", "<:colorificator:661232286280187905>", 30000, true, true),
    OWNROLE("Role Proprio", "O usuário que utilizar essa perk terá o direito de criar seu próprio cargo escolhendo qualquer nome e cor para ele. O cargo ficará disponível exclusivamente por 1 semana.", "⚙", 15000, true, true, EconomyEffect.OWNROLE),
    AUTODAILY("Auto Daily (1 Dia)", "Ao ter um Auto Daily no seu inventario o bonus daily é automaticamente coletado! (O streak não conta caso o bonus tenha sido coletado por um Auto Daily)", "🤖", 150, false, true),
    AUTODAILY_ON("Auto Daily (On)", "Ao ter um Auto Daily no seu inventario o bonus daily é automaticamente coletado! (O streak não conta caso o bonus tenha sido coletado por um Auto Daily)", "<:auto_daily_on:662566765636616193>", 15000, true, true),
    AUTODAILY_OFF("Auto Daily (Off)", "Ao ter um Auto Daily no seu inventario o bonus daily é automaticamente coletado! (O streak não conta caso o bonus tenha sido coletado por um Auto Daily)", "<:auto_daily_off:662627546067894273>", 15000, true, false),
    NUDEDASTAFF("Nude da Staff", "Ao utilizar esse precioso item, você receberá automaticamente na sua inbox um nude exclusivo da equipe do Bonde do Fracasso, fotografado durante nosso \"encontro\" anual", "🔞", 99999999999L, false, true);

    private String name, description, emote;
    private long price;
    private boolean usable, sellable;
    EconomyEffect effect;

    EconomyItem(String name, String description, String emote, long price, boolean usable, boolean sellable) {
        this.name = name;
        this.description = description;
        this.emote = emote;
        this.price = price;
        this.usable = usable;
        this.sellable = sellable;
    }

    EconomyItem(String name, String description, String emote, long price, boolean usable, boolean sellable, EconomyEffect effect) {
        this.name = name;
        this.description = description;
        this.emote = emote;
        this.price = price;
        this.usable = usable;
        this.sellable = sellable;
        this.effect = effect;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getEmote() {
        return emote;
    }

    public long getPrice() {
        return price;
    }

    public String getId() {
        return this.name();
    }

    public EconomyEffect getEffect() {
        return effect;
    }

    public boolean isUsable() {
        return usable;
    }

    public boolean isSellable() {
        return sellable;
    }
}
