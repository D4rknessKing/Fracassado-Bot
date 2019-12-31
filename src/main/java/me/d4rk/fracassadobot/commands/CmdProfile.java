package me.d4rk.fracassadobot.commands;

import me.d4rk.fracassadobot.handlers.economy.EconomyItem;
import me.d4rk.fracassadobot.handlers.economy.EconomySystemHandler;
import me.d4rk.fracassadobot.handlers.economy.EconomyUser;
import me.d4rk.fracassadobot.handlers.RankSystemHandler;
import me.d4rk.fracassadobot.utils.EnumPerms;
import me.d4rk.fracassadobot.utils.command.Command;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.*;

import static java.util.stream.Collectors.toMap;

public class CmdProfile {

    @Command(name="profile", description = "Used to see your profile.", category = "Interaction", usage = "[user]", perms = {EnumPerms.BASE})
    public static void profile(GuildMessageReceivedEvent event, String[] args) {
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

        Message message = event.getChannel().sendMessage("Please wait while we profile "+mem.getEffectiveName()+".").complete();
        StringBuilder string = new StringBuilder();
        EconomyUser economyUser = EconomySystemHandler.getUser(event.getGuild().getId(), event.getAuthor().getId());

        if(!RankSystemHandler.isSystemEnabled(event.getGuild().getId())){
            event.getChannel().sendMessage("**Error: **The rank system is not enabled in this guild.").queue();
        }else{
            HashMap entries = RankSystemHandler.getEntries(event.getGuild().getId());
            if(!(entries.entrySet().size() == 0 || !entries.containsKey(mem.getId()))) {
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
            }
        }

        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setAuthor(event.getMember().getEffectiveName()+"'s Profile:", null, event.getGuild().getIconUrl())
                .setThumbnail(mem.getUser().getAvatarUrl())
                .addField("Money:", economyUser.getMoney()+" FracassoCoins", true)
                .setColor(mem.getColor())
                .setFooter("Requested by: " + event.getAuthor().getName() + "#" + event.getAuthor().getDiscriminator(), event.getAuthor().getAvatarUrl());

        if (string.length() > 0) embedBuilder.addField("Point system:", string.toString(), false);
        if (economyUser.getInventory().size() > 0) {
            StringBuilder inventory = new StringBuilder();
            for (String id : economyUser.getInventory()) {
                EconomyItem item = null;
                try{item = EconomyItem.valueOf(id);}catch (Exception ignored){}
                if(inventory.length() > 0) inventory.append(",   ");
                if(item == null) inventory.append("**:interrobang: Item Desconhecido**");
                else inventory.append("**").append(item.getEmote()).append(" ").append(item.getName()).append("**");
            }
            embedBuilder.addField("Inventory ("+economyUser.getInventory().size()+"/20):", inventory.toString(), false);
        }else{
            embedBuilder.addField("Inventory (0/20):", "Empty", false);
        }

        message.editMessage(embedBuilder.build()).append("Here is your profile:").queue();
    }
}
