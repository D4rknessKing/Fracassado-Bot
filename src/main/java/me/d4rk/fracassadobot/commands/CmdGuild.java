package me.d4rk.fracassadobot.commands;

import me.d4rk.fracassadobot.core.permission.BotPerms;
import me.d4rk.fracassadobot.core.command.Command;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.time.format.DateTimeFormatter;

public class CmdGuild {

    @Command(name="guild", description = "Gives some information about the current guild.", category = "Info", usage = "", perms = {BotPerms.BASE})
    public static void run(GuildMessageReceivedEvent event, String[] args) {

        Guild g = event.getGuild();

        int online = 0;
        for (Member m : g.getMembers()){
            if(m.getOnlineStatus().equals(OnlineStatus.ONLINE)) {
                online++;
            }
        }

        String emojis = "";
        for (Emote em : g.getEmotes()) {
            emojis = emojis + em.getAsMention() + " ";
        }

        String roles = "";
        for (Role ro : g.getRoles()) {
            roles = roles + ro.getAsMention() + " ";
        }

        MessageEmbed embed = new EmbedBuilder()
                .setAuthor("Guild information:", null, g.getIconUrl())
                .setThumbnail(g.getIconUrl())
                .addField(g.getName(), "**ID: **"+g.getId(), false)
                .addField("Online/Unique Members: ", String.valueOf(online) + g.getMembers().size(), true)
                .addField("Voice/Text Channels: ", String.valueOf(g.getVoiceChannels().size()) + "/" + String.valueOf(g.getTextChannels().size()), true)
                .addField("Created at: ", g.getTimeCreated().format(DateTimeFormatter.ISO_DATE_TIME).replaceAll("[^0-9.:-]", " "), true)
                .addField("Owner: ", g.getOwner().getUser().getName() + "#" + g.getOwner().getUser().getDiscriminator(), true)
                .addField("Region: ", g.getRegion().getName(), true)
                .addField("Roles ("+String.valueOf(g.getRoles().size())+"): ", roles , false)
                .addField("Emojis ("+g.getEmotes().size()+"): ", emojis, false)
                .setFooter("Requested by: " + event.getAuthor().getName() + "#" + event.getAuthor().getDiscriminator(), event.getAuthor().getAvatarUrl())
                .setColor(event.getMember().getColor())
                .build();

        event.getChannel().sendMessage(embed).queue();


    }

}
