package me.d4rk.fracassadobot.commands;

import me.d4rk.fracassadobot.core.permission.PermissionHandler;
import me.d4rk.fracassadobot.core.DataHandler;
import me.d4rk.fracassadobot.core.permission.BotPerms;
import me.d4rk.fracassadobot.core.command.Command;
import me.d4rk.fracassadobot.core.command.SubCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;


import java.util.*;

public class CmdPerm {

    @Command(name="perm", description = "Used to manipulate users perms.", category = "Bot Owner", usage = "(user)", perms = {BotPerms.BASE})
    public static void run(GuildMessageReceivedEvent event, String[] args) {

        User usr = stringToUser(String.join(" ",args), event);
        if(usr == null) {
            event.getChannel().sendMessage("**Error: **Couldn't find a user that matches the arguments.").queue();
            return;
        }

        String guild = "";
        HashMap<String, List<String>> guildHashMap = DataHandler.loadGuildPerms(event.getGuild().getId());

        if(guildHashMap.get(usr.getId()) == null) {
            guild = "Not yet generated.";
        }else {
            for (String lp : guildHashMap.get(usr.getId())) {
                guild = guild+lp+", ";
            }
            guild = guild.substring(0, guild.length()-2);
        }

        MessageEmbed embed = new EmbedBuilder()
                .setAuthor(usr.getName()+"'s Permissions", null, usr.getAvatarUrl())
                .setDescription("```"+guild+"```")
                .setColor(event.getMember().getColor())
                .setFooter("Requested by: " + event.getAuthor().getName() + "#" + event.getAuthor().getDiscriminator(), event.getAuthor().getAvatarUrl())
                .build();

        event.getChannel().sendMessage(embed).queue();



    }

    @SubCommand(name="add", description = "Add a perm to a given user", usage="(perm) (user)", perms={BotPerms.BASE, BotPerms.ADMIN})
    public static void add(GuildMessageReceivedEvent event, String[] args) {

        BotPerms perm;
        try {
            perm = BotPerms.valueOf(args[0]);
        } catch (Exception e) {
            event.getChannel().sendMessage("**Error: **Unknown perm!").queue();
            return;
        }

        User usr = stringToUser(String.join("", Arrays.copyOfRange(args, 1, args.length)), event);
        if (usr == null) {
            event.getChannel().sendMessage("**Error: **Couldn't find a user that matches the arguments.").queue();
            return;
        }

        if (PermissionHandler.hasGuildPerm(event.getAuthor().getId(), event.getGuild().getId(), perm)) {
            PermissionHandler.addGuildPerm(usr.getId(), event.getGuild().getId(), perm);
        } else {
            event.getChannel().sendMessage("**Error: **You don't have the permission you are trying to change.").queue();
        }

    }

    @SubCommand(name="remove", description = "Removes a perm from a given user", usage="(perm) (user)", perms={BotPerms.BASE, BotPerms.ADMIN})
    public static void remove(GuildMessageReceivedEvent event, String[] args){

        BotPerms perm;
        try{
            perm = BotPerms.valueOf(args[1]);
        }catch(Exception e){
            event.getChannel().sendMessage("**Error: **Unknown perm!").queue(); return;
        }

        User usr = stringToUser(String.join("", Arrays.copyOfRange(args, 2, args.length)), event);
        if(usr == null) {
            event.getChannel().sendMessage("**Error: **Couldn't find a user that matches the arguments.").queue(); return;
        }

        if (PermissionHandler.hasGuildPerm(event.getAuthor().getId(), event.getGuild().getId(), perm)) {
            PermissionHandler.removeGuildPerm(usr.getId(),event.getGuild().getId(), perm);
        } else {
            event.getChannel().sendMessage("**Error: **You don't have the permission you are trying to change.").queue();
        }
    }

    private static User stringToUser(String shit, GuildMessageReceivedEvent event) {
        User usr = null;

        try{
            usr = event.getJDA().getUserById(shit);
        }catch(Exception ignored){}

        List ata = event.getJDA().getUsersByName(shit, false);
        if (ata.size() >= 1) {
            usr = (User) ata.get(0);
        }
        List ata2 = event.getGuild().getMembersByNickname(shit, false);
        if (ata2.size() >= 1) {
            usr = ((Member) ata2.get(0)).getUser();
        }

        if (event.getMessage().getMentionedUsers().size() >= 1) {
            usr = event.getMessage().getMentionedUsers().get(0);
        }

        return usr;
    }
}
