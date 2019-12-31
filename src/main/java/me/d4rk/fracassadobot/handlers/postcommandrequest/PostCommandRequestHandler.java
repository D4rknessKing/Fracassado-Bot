package me.d4rk.fracassadobot.handlers.postcommandrequest;

import javafx.util.Pair;
import me.d4rk.fracassadobot.handlers.economy.EconomyItem;
import me.d4rk.fracassadobot.handlers.economy.EconomySystemHandler;
import me.d4rk.fracassadobot.handlers.economy.EconomyUser;
import me.d4rk.fracassadobot.utils.RandomUtils;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.HashMap;

public class PostCommandRequestHandler {

    private static HashMap<String, PostCommandRequest> activePcr = new HashMap<>();

    public static HashMap<String, PostCommandRequest> getActivePcr() {
        return activePcr;
    }

    public static void addPcr(String userId, PostCommandRequest pcr) {
        activePcr.put(userId, pcr);
    }

    public static void handle(GuildMessageReceivedEvent event) {
        PostCommandRequest pcr = PostCommandRequestHandler.activePcr.get(event.getAuthor().getId());
        boolean success = false;

        switch (pcr.getId()) {
            case "USE_DEBUFF":
                Member member = RandomUtils.getMember(event);
                if(member != null) {
                    EconomyUser user = EconomySystemHandler.getUser(event.getGuild().getId(), member.getId());
                    boolean containsEffect = false;
                    System.out.println(user.getEffects());
                    for(Pair<String, Long> effect : user.getEffects()) {
                        if(EconomyItem.valueOf(pcr.getInfo()).getEffect().name().equals(effect.getKey())) containsEffect = true;
                        System.out.println(EconomyItem.valueOf(pcr.getInfo()).getEffect().name()+" - "+effect.getKey());
                    }
                    if(containsEffect) {
                        event.getChannel().sendMessage("**O usuario já possui um debuff desse tipo ativo!**").queue();
                    }else{
                        boolean containsCooldown = false;
                        long cooldownLeft = 0;
                        for(Pair<String, Long> cooldown : user.getCooldown()) {
                            if(EconomyItem.valueOf(pcr.getInfo()).getEffect().getCooldown() != null && EconomyItem.valueOf(pcr.getInfo()).getEffect().getCooldown().name().equals(cooldown.getKey())) {
                                containsCooldown = true;
                                cooldownLeft = cooldown.getValue()-System.currentTimeMillis();
                            }
                        }
                        if(containsCooldown) {
                            event.getChannel().sendMessage("**O usuario foi afetado por um debuff desse tipo a pouco tempo! Aguarde mais "+(cooldownLeft/60000)+" minutos antes de usar um debuff desse tipo novamente! **").queue();
                        }
                        EconomySystemHandler.useItem(event.getGuild().getId(), event.getAuthor().getId(), member.getId(), EconomyItem.valueOf(pcr.getInfo()));
                        event.getChannel().sendMessage("**O item foi utilizado com sucesso!**").queue();
                        success = true;
                    }
                }
                break;
            case "USE_TRASHDAY":
                break;
            default:
                break;
        }

        if(!success) event.getChannel().sendMessage(pcr.getErrorMessage()).queue();
        activePcr.remove(event.getAuthor().getId());
    }

}
