package me.d4rk.fracassadobot.core.postcommandrequest;

import javafx.util.Pair;
import me.d4rk.fracassadobot.core.economy.EconomyItem;
import me.d4rk.fracassadobot.core.economy.EconomySystemHandler;
import me.d4rk.fracassadobot.core.economy.EconomyUser;
import me.d4rk.fracassadobot.utils.RandomUtils;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.awt.*;
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
        activePcr.remove(event.getAuthor().getId());

        switch (pcr.getId()) {
            case "USE_DEBUFF":
                Member member = RandomUtils.getMember(event);
                if(member != null) {
                    EconomyUser user = EconomySystemHandler.getUser(event.getGuild().getId(), member.getId());
                    boolean containsEffect = false;
                    for(Pair<String, Long> effect : user.getEffectsPair()) {
                        if(EconomyItem.valueOf(pcr.getInfo()).getEffect().name().equals(effect.getKey())) containsEffect = true;
                    }
                    if(containsEffect) {
                        event.getChannel().sendMessage("**O usuario já possui um debuff desse tipo ativo!**").queue();
                    }else{
                        boolean containsCooldown = false;
                        long cooldownLeft = 0;
                        for(Pair<String, Long> cooldown : user.getCooldownPair()) {
                            if(EconomyItem.valueOf(pcr.getInfo()).getEffect().getCooldown() != null && EconomyItem.valueOf(pcr.getInfo()).getEffect().getCooldown().name().equals(cooldown.getKey())) {
                                containsCooldown = true;
                                cooldownLeft = cooldown.getValue()-System.currentTimeMillis();
                            }
                        }
                        if(containsCooldown) {
                            event.getChannel().sendMessage("**O usuario foi afetado por um debuff desse tipo a pouco tempo! Aguarde mais :clock4: _"+ RandomUtils.getTime(cooldownLeft) +"_ :clock4:_ antes de usar um debuff desse tipo novamente! **").queue();
                        }else{
                            EconomySystemHandler.useItem(event.getGuild().getId(), event.getAuthor().getId(), member.getId(), EconomyItem.valueOf(pcr.getInfo()));
                            event.getChannel().sendMessage("**O item foi utilizado com sucesso!**").queue();
                        }
                    }
                }
                break;
            case "USE_OWNROLE1":
                PostCommandRequestHandler.addPcr(event.getAuthor().getId(), new PostCommandRequest(event, PostCommandRequest.ENUM.USE_OWNROLE2.getPcr(), event.getMessage().getContentRaw()));
                break;
            case "USE_OWNROLE2":
                Color color = null;
                String[] args = event.getMessage().getContentRaw().split("\\s+");
                try {
                    if (args.length == 1 && args[0].length() == 6) {
                        color = Color.decode("#"+args[0].toUpperCase());
                    } else if (args.length == 3) {
                        color = new Color(
                                Integer.valueOf(args[0]),
                                Integer.valueOf(args[1]),
                                Integer.valueOf(args[2])
                        );
                    }
                }catch (Exception ignored){}
                if(color == null) {
                    event.getChannel().sendMessage("**Error: ** Não foi possível identificar essa cor!").queue();
                }else{
                    EconomyUser user = EconomySystemHandler.getUser(event.getGuild().getId(), event.getAuthor().getId());
                    boolean containsEffect = false;
                    for(Pair<String, Long> effect : user.getEffectsPair())
                        if(EconomyItem.valueOf("OWNROLE").getEffect().name().equals(effect.getKey())) containsEffect = true;
                    if(containsEffect) {
                        event.getChannel().sendMessage("**O usuario já possui um buff desse tipo ativo!**").queue();
                    }else{
                        if(pcr.getInfo().length() > 100)
                            event.getChannel().sendMessage("**Error: **Esse nome é muito grande para um cargo!").queue();
                        else {
                            Role role = event.getGuild().createRole().setName(pcr.getInfo()).setColor(color).complete(),
                                 mute = RandomUtils.getMuteRole(event.getGuild());
                            event.getGuild().modifyRolePositions().selectPosition(mute.getPosition()-1).swapPosition(role).queue();
                            event.getGuild().addRoleToMember(event.getMember(), role).queue();
                            EconomySystemHandler.useOwnrole(event.getGuild().getId(), event.getAuthor().getId(), role.getId());
                            event.getChannel().sendMessage("**O item foi utilizado com sucesso!**").queue();
                        }

                    }
                }
                break;
            default:
                break;
        }

    }

}
