package me.d4rk.fracassadobot.commands;

import me.d4rk.fracassadobot.core.permission.BotPerms;
import me.d4rk.fracassadobot.core.command.Command;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class CmdUser {

    @Command(name="user", description = "Gives some information about the mentioned user.", category = "Info", usage = "(User ID/Name/Nickname/Mention)", perms = {BotPerms.BASE})
    public static void run(GuildMessageReceivedEvent event, String[] args) {

        String shit = String.join(" ",args);

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

        if(usr == null) { event.getChannel().sendMessage("**Error: **Couldn't find a user that matches the arguments.").queue(); return; }

        String mu = "";
        for (Guild g : usr.getMutualGuilds()) {
            mu = mu + g.getName() + ", ";
        }
        if(usr.getMutualGuilds().size() >= 1) mu = mu.substring(0, mu.length()-2);

        boolean inguild = event.getGuild().getMember(usr) != null;
        MessageEmbed embed;

        if(inguild){
            Member mem = event.getGuild().getMember(usr);

            String roles = "";
            for (Role ro : mem.getRoles()) {
                roles = roles + ro.getAsMention() + " ";
            }

            String game = "WIP";
            /*if(mem.getGame() != null){
                game = mem.getGame().getName();
            }else{
                game = "Nothing";
            }*/

            String nickname;
            if(mem.getNickname() != null){
                nickname = mem.getNickname();
            }else{
                nickname = "None";
            }

            embed = new EmbedBuilder()
                    .setAuthor("User information: ", null, usr.getAvatarUrl())
                    .addField("Name: "+usr.getName(), "**ID: **" + usr.getId(), true)
                    .addField("Created at: ", usr.getTimeCreated().format(DateTimeFormatter.ISO_DATE_TIME).replaceAll("[^0-9.:-]", " "), true)
                    .addField("Discriminator: ", usr.getDiscriminator(), true)
                    .addField("As mention: ", usr.getAsMention(), true)
                    .addField("Is bot? ", String.valueOf(usr.isBot()).toUpperCase(), true)
                    .addField("In guild? ", "TRUE", true)
                    .addField("Mutual guilds (" + usr.getMutualGuilds().size() + "): ", mu, false)
                    .addField("Joined at: ", mem.getTimeJoined().format(DateTimeFormatter.ISO_DATE_TIME).replaceAll("[^0-9.:-]", " "), true)
                    .addField("Status: ", mem.getOnlineStatus().name(), true)
                    .addField("Is playing: ", game, true)
                    .addField("Nickname: ", nickname, true)
                    .addField("Roles ("+mem.getRoles().size()+"):", roles, false)
                    .setFooter("Requested by: " + event.getAuthor().getName() + "#" + event.getAuthor().getDiscriminator(), event.getAuthor().getAvatarUrl())
                    .setColor(mem.getColor())
                    .setThumbnail(usr.getAvatarUrl())
                    .build();
        }else {
            embed = new EmbedBuilder()
                    .setAuthor("User information: ", null, usr.getAvatarUrl())
                    .addField(usr.getName(), "**ID: **" + usr.getId(), true)
                    .addField("Created at: ", usr.getTimeCreated().format(DateTimeFormatter.ISO_DATE_TIME).replaceAll("[^0-9.:-]", " "), true)
                    .addField("Discriminator: ", usr.getDiscriminator(), true)
                    .addField("As mention: ", usr.getAsMention(), true)
                    .addField("Is bot? ", String.valueOf(usr.isBot()).toUpperCase(), true)
                    .addField("In guild? ", "FALSE", true)
                    .addField("Mutual guilds (" + usr.getMutualGuilds().size() + "): ", mu, false)
                    .setFooter("Requested by: " + event.getAuthor().getName() + "#" + event.getAuthor().getDiscriminator(), event.getAuthor().getAvatarUrl())
                    .setColor(event.getMember().getColor())
                    .setThumbnail(usr.getAvatarUrl())
                    .build();
        }

        event.getChannel().sendMessage(embed).queue();

    }

}
