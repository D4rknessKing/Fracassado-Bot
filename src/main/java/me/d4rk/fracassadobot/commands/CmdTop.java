package me.d4rk.fracassadobot.commands;

import me.d4rk.fracassadobot.core.RankSystemHandler;
import me.d4rk.fracassadobot.core.economy.EconomySystemHandler;
import me.d4rk.fracassadobot.core.economy.EconomyUser;
import me.d4rk.fracassadobot.core.permission.BotPerms;
import me.d4rk.fracassadobot.core.command.Command;
import me.d4rk.fracassadobot.core.command.SubCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.*;

import static java.util.stream.Collectors.toMap;

public class CmdTop {

    @Command(name = "top", description = "Shows some status about the most dedicated members of the guild", category = "Interaction", usage = "null", perms = {BotPerms.BASE})
    public static void top(GuildMessageReceivedEvent event, String[] args) {
    }

    @SubCommand(name = "rank", description = "Shows the highest ranked members of the guild", usage = "[page]", perms = {BotPerms.BASE})
    public static void rank(GuildMessageReceivedEvent event, String[] args){
        if(!RankSystemHandler.isSystemEnabled(event.getGuild().getId())) {
            event.getChannel().sendMessage("**Error: **The rank system is not enabled in this guild.").queue();
            return;
        }
        HashMap entries = RankSystemHandler.getEntries(event.getGuild().getId());
        if(entries.entrySet().size() == 0 ) {
            event.getChannel().sendMessage("**Error: **No one have been ranked yet.").queue();
            return;
        }
        int page = 1;
        if(args.length > 0){
            try {
                page = Integer.parseInt(args[0]);
            }catch(Exception e) {
                e.printStackTrace();
                event.getChannel().sendMessage("**Error: **Unknown page number.").queue();
                return;
            }
        }
        if((entries.entrySet().size() <= (page*10)-10) || page < 1) {
            event.getChannel().sendMessage("**Error: **There is no such page.").queue();
            return;
        }
        Message message = event.getChannel().sendMessage("Please wait while we rank the contestants.").complete();
        StringBuilder rank = new StringBuilder();
        HashMap<String, Long> contestants = new HashMap<>();
        for(Object userId : entries.keySet())
            contestants.put(userId.toString(), (Long) entries.get(userId.toString()));
        LinkedHashMap<String, Long> sorted = contestants
                .entrySet()
                .stream()
                .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                .collect(toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2, LinkedHashMap::new));
        for(int i = 0; i < 10; i++) {
            if(((page*10)-10+i) > entries.keySet().size()-1) break;
            Member member = event.getGuild().getMemberById((String) sorted.keySet().toArray()[(page*10)-10+i]);
            String name = (member != null) ? member.getEffectiveName() : "The user left the guild.";
            String pos = String.valueOf(((page*10)-10+i+1));
            switch(pos.length()) {
                case 1:
                    rank.append("["+pos+"º]    #"+name+"\n        ↳ Points: "+sorted.get(member.getId())+"\n");
                    break;
                case 2:
                    rank.append("["+pos+"º]   #"+name+"\n        ↳ Points: "+sorted.get(member.getId())+"\n");
                    break;
                case 3:
                    rank.append("["+pos+"º]  #"+name+"\n        ↳ Points: "+sorted.get(member.getId())+"\n");
                    break;
                default:
                    rank.append("["+pos+"º] #"+name+"\n        ↳ Points: "+sorted.get(member.getId())+"\n");
            }
        }
        message.editMessage(
                new EmbedBuilder()
                        .setAuthor("Highest ranked members of the guild:", null, event.getGuild().getIconUrl())
                        .setDescription(
                                "```pl\n"+rank.toString()+
                                        "\nPage: "+page+"/"+(int) Math.ceil(entries.keySet().size()/10f)+"```")
                        .setColor(event.getMember().getColor())
                        .setFooter("Requested by: " + event.getAuthor().getName() + "#" + event.getAuthor().getDiscriminator(), event.getAuthor().getAvatarUrl())
                        .build()
        ).append("Here are the results: ").queue();
    }


    @SubCommand(name = "money", description = "See the richest members of the guild", usage = "[page]", perms = {BotPerms.BASE})
    public static void money(GuildMessageReceivedEvent event, String[] args){
        List<EconomyUser> entries = EconomySystemHandler.getUsers(event.getGuild().getId());
        if(entries.size() == 0 ) {
            event.getChannel().sendMessage("**Error: **No one have been ranked yet.").queue();
            return;
        }
        int page = 1;
        if(args.length > 0){
            try {
                page = Integer.parseInt(args[0]);
            }catch(Exception e) {
                e.printStackTrace();
                event.getChannel().sendMessage("**Error: **Unknown page number.").queue();
                return;
            }
        }
        if((entries.size() <= (page*10)-10) || page < 1) {
            event.getChannel().sendMessage("**Error: **There is no such page.").queue();
            return;
        }
        Message message = event.getChannel().sendMessage("Please wait while we rank the contestants.").complete();
        StringBuilder rank = new StringBuilder();
        HashMap<String, Long> contestants = new HashMap<>();
        for(EconomyUser user : entries)
            contestants.put(user.getId(), user.getMoney());
        LinkedHashMap<String, Long> sorted = contestants
                .entrySet()
                .stream()
                .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                .collect(toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2, LinkedHashMap::new));
        for(int i = 0; i < 10; i++) {
            if(((page*10)-10+i) > entries.size()-1) break;
            Member member = event.getGuild().getMemberById((String) sorted.keySet().toArray()[(page*10)-10+i]);
            String name = (member != null) ? member.getEffectiveName() : "The user left the guild.";
            String pos = String.valueOf(((page*10)-10+i+1));
            switch(pos.length()) {
                case 1:
                    rank.append("["+pos+"º]    #"+name+"\n        ↳ FracassoCoins: "+sorted.get(member.getId())+"\n");
                    break;
                case 2:
                    rank.append("["+pos+"º]   #"+name+"\n        ↳ FracassoCoins: "+sorted.get(member.getId())+"\n");
                    break;
                case 3:
                    rank.append("["+pos+"º]  #"+name+"\n        ↳ FracassoCoins: "+sorted.get(member.getId())+"\n");
                    break;
                default:
                    rank.append("["+pos+"º] #"+name+"\n        ↳ FracassoCoins: "+sorted.get(member.getId())+"\n");
            }
        }
        message.editMessage(
                new EmbedBuilder()
                        .setAuthor("Highest ranked members of the guild:", null, event.getGuild().getIconUrl())
                        .setDescription(
                                "```pl\n"+rank.toString()+
                                        "\nPage: "+page+"/"+(int) Math.ceil(entries.size()/10f)+"```")
                        .setColor(event.getMember().getColor())
                        .setFooter("Requested by: " + event.getAuthor().getName() + "#" + event.getAuthor().getDiscriminator(), event.getAuthor().getAvatarUrl())
                        .build()
        ).append("Here are the results: ").queue();
    }
}
