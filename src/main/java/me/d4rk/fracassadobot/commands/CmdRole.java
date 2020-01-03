package me.d4rk.fracassadobot.commands;

import me.d4rk.fracassadobot.core.permission.BotPerms;
import me.d4rk.fracassadobot.core.command.Command;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class CmdRole {

    @Command(name="role", description = "Gives some information about the given role.", category = "Info", usage = "(Role Name/ID)", perms = {BotPerms.BASE})
    public static void run(GuildMessageReceivedEvent event, String[] args) {

        String shit = String.join(" ",args);

        Role role = null;

        try{
            role = event.getGuild().getRoleById(shit);
        }catch(Exception ignored){}

        List ata = event.getGuild().getRolesByName(shit, false);
        if (ata.size() >= 1) {
            role = (Role) ata.get(0);
        }

        if (event.getMessage().getMentionedRoles().size() >= 1) {
            role = event.getMessage().getMentionedRoles().get(0);
        }

        if(role == null) { event.getChannel().sendMessage("**Error: **Couldn't find a role that matches the arguments.").queue(); return; }

        String members = "";
        int mem = 0;
        for (Member m : event.getGuild().getMembers()) {
            if(m.getRoles().contains(role)) {
                members = members + m.getAsMention() + " ";
                mem++;
            }
        }

        String permissions = "";
        for (Permission p : role.getPermissions()) {
            permissions = permissions + p.getName() + ", ";
        }
        if(role.getPermissions().size() >= 1) permissions = permissions.substring(0, permissions.length()-2);

        MessageEmbed embed = new EmbedBuilder()
                .setDescription("**Role information for: **"+role.getAsMention())
                .addField(role.getName()+":", "**ID: **"+role.getId(), true)
                .addField("Created at: ", role.getTimeCreated().format(DateTimeFormatter.ISO_DATE_TIME).replaceAll("[^0-9.:-]", " "), true)
                .addField("Members ("+mem+"): ", members, false)
                .addField("Permissions ("+role.getPermissions().size()+"): ", permissions, false)
                .setColor(role.getColor())
                .setFooter("Requested by: " + event.getAuthor().getName() + "#" + event.getAuthor().getDiscriminator(), event.getAuthor().getAvatarUrl())
                .build();

        event.getChannel().sendMessage(embed).queue();

    }

}
