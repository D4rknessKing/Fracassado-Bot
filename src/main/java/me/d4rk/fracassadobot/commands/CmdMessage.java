package me.d4rk.fracassadobot.commands;

import me.d4rk.fracassadobot.core.permission.BotPerms;
import me.d4rk.fracassadobot.core.command.Command;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.awt.*;
import java.time.format.DateTimeFormatter;

public class CmdMessage {

    @Command(name="message", description = "Gives some information about the given message.", category = "Info", usage = "(ID)", perms = {BotPerms.BASE})
    public static void run(GuildMessageReceivedEvent event, String[] args) {

        String shit = String.join(" ",args);


        Message mem;
        try{
            mem = event.getChannel().retrieveMessageById(shit).complete();
        }catch(Exception e){
            event.getChannel().sendMessage("**Error: **Couldn't find a message that matches the arguments in this channel.").queue();
            return;
        }

        String edited = "";
        if(mem.getTimeEdited() != null) {
            edited = mem.getTimeEdited().format(DateTimeFormatter.ISO_DATE_TIME).replaceAll("[^0-9.:-]", " ");
        }else{
            edited = "Not edited.";
        }

        Color color;
        if(event.getGuild().getMember(mem.getAuthor()) != null) {
            color = event.getGuild().getMember(mem.getAuthor()).getColor();
        }else{
            color = event.getMember().getColor();
        }


        MessageEmbed embed = new EmbedBuilder()
                .addField("Message information: ", "**ID: **"+mem.getId(), true)
                .addField("Created by: ", mem.getAuthor().getAsMention(), true)
                .addField("Created at: ", mem.getTimeCreated().format(DateTimeFormatter.ISO_DATE_TIME).replaceAll("[^0-9.:-]", " "), true)
                .addField("Edited at: ", edited, true)
                .addField("Content: ", mem.getContentRaw(), false)
                .setThumbnail(mem.getAuthor().getAvatarUrl())
                .setFooter("Requested by: " + event.getAuthor().getName() + "#" + event.getAuthor().getDiscriminator(), event.getAuthor().getAvatarUrl())
                .setColor(color)
                .build();

        event.getChannel().sendMessage(embed).queue();


    }

}
