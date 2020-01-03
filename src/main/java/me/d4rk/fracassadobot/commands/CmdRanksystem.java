package me.d4rk.fracassadobot.commands;

import me.d4rk.fracassadobot.core.RankSystemHandler;
import me.d4rk.fracassadobot.core.permission.BotPerms;
import me.d4rk.fracassadobot.core.command.Command;
import me.d4rk.fracassadobot.core.command.SubCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.*;

import static java.util.stream.Collectors.toMap;


public class CmdRanksystem {

    @Command(name="ranksystem", description = "", category = "Guild", usage = "null", perms = {BotPerms.GUILD})
    public static void ranksystem(GuildMessageReceivedEvent event, String[] args) {
    }

    @SubCommand(name="enable", description = "Enable the rank system", usage="", perms={BotPerms.BASE, BotPerms.GUILD})
    public static void enable(GuildMessageReceivedEvent event, String[] args) {
        if(RankSystemHandler.isSystemEnabled(event.getGuild().getId())) {
            event.getChannel().sendMessage("**Error: **The rank system is already enabled").queue();
        }else{
            RankSystemHandler.enableSystem(event.getGuild().getId());
            event.getChannel().sendMessage("**Successfully enabled rank system on "+event.getGuild().getName()+"**").queue();
        }
    }

    @SubCommand(name="disable", description = "Disable the rank system", usage="", perms={BotPerms.BASE, BotPerms.GUILD})
    public static void disable(GuildMessageReceivedEvent event, String[] args) {
        if(!RankSystemHandler.isSystemEnabled(event.getGuild().getId())) {
            event.getChannel().sendMessage("**Error: **The rank system is already disabled").queue();
        }else{
            RankSystemHandler.disableSystem(event.getGuild().getId());
            event.getChannel().sendMessage("**Successfully disabled rank system on "+event.getGuild().getName()+"**").queue();
        }
    }

    @SubCommand(name="listRoles", description = "List roles configured in the guild rank system", usage = "", perms={BotPerms.BASE})
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

    @SubCommand(name="configRoles", description = "Used to configure the rank system", usage="(add/remove) [points] [role]", perms={BotPerms.BASE, BotPerms.GUILD})
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
