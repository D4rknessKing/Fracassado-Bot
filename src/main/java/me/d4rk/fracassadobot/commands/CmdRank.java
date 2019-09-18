package me.d4rk.fracassadobot.commands;

import javafx.util.Pair;
import me.d4rk.fracassadobot.handlers.RankSystemHandler;
import me.d4rk.fracassadobot.utils.EnumPerms;
import me.d4rk.fracassadobot.utils.command.Command;
import me.d4rk.fracassadobot.utils.command.SubCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.*;

import static java.util.stream.Collectors.toMap;


public class CmdRank {

    @Command(name="rank", description = "Used to see your rank.", category = "Guild", usage = "[user]", perms = {EnumPerms.BASE})
    public static void rank(GuildMessageReceivedEvent event, String[] args) {
        if(!RankSystemHandler.isSystemEnabled(event.getGuild().getId())){
            event.getChannel().sendMessage("**Error: **The rank system is not enabled in this guild.").queue();
        }else{
            Member mem = event.getMember();
            if(args.length > 0) {
                String shit = String.join(" ", args);
                List<Member> ata2 = event.getGuild().getMembersByNickname(shit, false);
                if (ata2.size() >= 1) mem = ata2.get(0);
                if (event.getMessage().getMentionedUsers().size() >= 1)
                    mem = event.getMessage().getMentionedMembers().get(0);
                if (mem == null)
                    event.getChannel().sendMessage("**Error: **Couldn't find a user that matches the arguments.").queue();
            }
            HashMap entries = RankSystemHandler.getEntries(event.getGuild().getId());
            if(entries.entrySet().size() == 0 ) {
                event.getChannel().sendMessage("**Error: **No one have been ranked yet.").queue();
                return;
            }
            if(!entries.containsKey(mem.getId())) {
                event.getChannel().sendMessage("**Error: **This member hasn't been ranked yet.").queue();
                return;
            }
            Message message = event.getChannel().sendMessage("Please wait while we rank "+mem.getEffectiveName()+".").complete();
            StringBuilder rank = new StringBuilder();
            HashMap<String, Long> contestants = new HashMap<>();
            for(Object userId : entries.keySet())
                contestants.put(userId.toString(), (Long) entries.get(userId.toString()));
            LinkedHashMap<String, Long> sorted = contestants
                    .entrySet()
                    .stream()
                    .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                    .collect(toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2, LinkedHashMap::new));
            long userPoints = (long) entries.get(mem.getId());
            StringBuilder string = new StringBuilder();
            string.append("**User Points:** "+userPoints+"\n");
            long userPosition = (Arrays.asList(sorted.keySet().toArray()).indexOf(mem.getId())+1);
            string.append("**Position:** "+userPosition+"/"+sorted.keySet().size()+"\n");
            if(userPosition > 1) {
                String aheadId = sorted.keySet().toArray()[(int) userPosition-2].toString();
                string.append("("+(sorted.get(aheadId)-userPoints)+" points behind <@"+aheadId+">)\n");
            }
            HashMap<String, Long> roles = RankSystemHandler.getRoles(event.getGuild().getId());
            LinkedHashMap<String, Long> sortedRoles = roles
                    .entrySet()
                    .stream()
                    .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                    .collect(toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2, LinkedHashMap::new));
            String role = "None", lastName = null;
            long lastPoints = 0;
            for(String name : sortedRoles.keySet()) {
                if(userPoints > sortedRoles.get(name)) {
                    role = "<@&"+name+">";
                    break;
                }
                lastName = name;
                lastPoints = sortedRoles.get(name);
            }
            string.append("**Role:** "+role+"\n");
            if(lastName != null) {
                string.append("("+(lastPoints-userPoints)+" points left for <@&"+lastName+">)\n");
            }
            message.editMessage(
                    new EmbedBuilder()
                            .setAuthor("Point system:", null, event.getGuild().getIconUrl())
                            .setThumbnail(mem.getUser().getAvatarUrl())
                            .setDescription(string.toString())
                            .setColor(mem.getColor())
                            .setFooter("Requested by: " + event.getAuthor().getName() + "#" + event.getAuthor().getDiscriminator(), event.getAuthor().getAvatarUrl())
                            .build()
            ).append("Here is your classification:").queue();
        }

    }

    @SubCommand(name="top", description = "See the highest ranked members of the guild", usage="[page]", perms={EnumPerms.BASE})
    public static void top(GuildMessageReceivedEvent event, String[] args) {
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

    @SubCommand(name="enable", description = "Enable the rank system", usage="", perms={EnumPerms.BASE, EnumPerms.GUILD})
    public static void enable(GuildMessageReceivedEvent event, String[] args) {
        if(RankSystemHandler.isSystemEnabled(event.getGuild().getId())) {
            event.getChannel().sendMessage("**Error: **The rank system is already enabled").queue();
        }else{
            RankSystemHandler.enableSystem(event.getGuild().getId());
            event.getChannel().sendMessage("**Successfully enabled rank system on "+event.getGuild().getName()+"**").queue();
        }
    }

    @SubCommand(name="disable", description = "Disable the rank system", usage="", perms={EnumPerms.BASE, EnumPerms.GUILD})
    public static void disable(GuildMessageReceivedEvent event, String[] args) {
        if(!RankSystemHandler.isSystemEnabled(event.getGuild().getId())) {
            event.getChannel().sendMessage("**Error: **The rank system is already disabled").queue();
        }else{
            RankSystemHandler.disableSystem(event.getGuild().getId());
            event.getChannel().sendMessage("**Successfully disabled rank system on "+event.getGuild().getName()+"**").queue();
        }
    }

    @SubCommand(name="listRoles", description = "List roles configured in the guild rank system", usage = "", perms={EnumPerms.BASE})
    public static void listRoles(GuildMessageReceivedEvent event, String[] args) {
        HashMap<String, Long> roles = RankSystemHandler.getRoles(event.getGuild().getId());
        if(roles.keySet().size() == 0) {
            event.getChannel().sendMessage("**Error: **There are no roles configured in this guild's rank system.").queue();
            return;
        }
        LinkedHashMap<String, Long> sortedRoles = roles
                .entrySet()
                .stream()
                .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                .collect(toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2, LinkedHashMap::new));
        StringBuilder roleString = new StringBuilder();
        for(String r: sortedRoles.keySet()) roleString.append("<@&"+r+"> at "+sortedRoles.get(r)+" points.\n");
        event.getChannel().sendMessage(
                new EmbedBuilder()
                        .setAuthor("Rank system roles for "+event.getGuild().getName()+":", null, event.getGuild().getIconUrl())
                        .setDescription(roleString.toString())
                        .setFooter("Requested by: " + event.getAuthor().getName() + "#" + event.getAuthor().getDiscriminator(), event.getAuthor().getAvatarUrl())
                        .setColor(event.getMember().getColor())
                        .build()
        ).queue();
    }

    @SubCommand(name="configRoles", description = "Used to configure the rank system", usage="(add/remove) [points] [role]", perms={EnumPerms.BASE, EnumPerms.GUILD})
    public static void configRoles(GuildMessageReceivedEvent event, String[] args) {
        if(!RankSystemHandler.isSystemEnabled(event.getGuild().getId())) {
            event.getChannel().sendMessage("**Error: **The rank system is not enabled in this guild.").queue();
            return;
        }
        HashMap<String, Long> roles = RankSystemHandler.getRoles(event.getGuild().getId());
        switch (args[0]) {
            case "add": {
                if(args.length < 3) event.getChannel().sendMessage("**Error: **Missing arguments").queue();
                else{
                    long requiredPoints;
                    try{requiredPoints = Long.parseLong(args[1]);}catch(Exception ignored){
                        event.getChannel().sendMessage("**Error: **Unknown number.").queue();
                        return;
                    }
                    String shit = String.join(" ", Arrays.copyOfRange(args, 2, args.length));
                    Role role = null;
                    try {role = event.getGuild().getRoleById(shit);}catch(Exception ignored){}
                    List ata = event.getGuild().getRolesByName(shit, false);
                    if (ata.size() >= 1) role = (Role) ata.get(0);
                    if (event.getMessage().getMentionedRoles().size() >= 1) role = event.getMessage().getMentionedRoles().get(0);
                    if(role == null) event.getChannel().sendMessage("**Error: **Couldn't find a role that matches the arguments.").queue();
                    else {
                        if(roles.keySet().contains(role.getId())) event.getChannel().sendMessage("**Error: **This role is already in the rank system.").queue();
                        else {
                            RankSystemHandler.addRole(event.getGuild().getId(), role.getId(), requiredPoints);
                            event.getChannel().sendMessage("**Successfully added role to guild rank system.**").queue();
                        }
                    }
                }
                break;
            }
            case "remove": {
                if(args.length < 2) event.getChannel().sendMessage("**Error: **Missing arguments").queue();
                else{
                    String shit = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
                    Role role = null;
                    try {role = event.getGuild().getRoleById(shit);}catch(Exception ignored){}
                    List ata = event.getGuild().getRolesByName(shit, false);
                    if (ata.size() >= 1) role = (Role) ata.get(0);
                    if (event.getMessage().getMentionedRoles().size() >= 1) role = event.getMessage().getMentionedRoles().get(0);
                    if(role == null) event.getChannel().sendMessage("**Error: **Couldn't find a role that matches the arguments.").queue();
                    else {
                        if(!roles.keySet().contains(role.getId())) event.getChannel().sendMessage("**Error: **This role is not in the rank system.").queue();
                        else {
                            RankSystemHandler.removeRole(event.getGuild().getId(), role.getId());
                            event.getChannel().sendMessage("**Successfully removed role to guild rank system.**").queue();
                        }
                    }
                }
                break;
            }
            default: {
                event.getChannel().sendMessage("**Error: **Incorrect option (valid ones are: add and remove)").queue();
            }
        }
    }
}
