package me.d4rk.fracassadobot.commands;

import me.d4rk.fracassadobot.utils.EnumPerms;
import me.d4rk.fracassadobot.utils.command.Command;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;

import java.awt.*;

public class CmdQuote {

    @Command(name="quote", description = "Quotes the given message.", category = "Info", usage = "(ID)", perms = {EnumPerms.BASE})
    public static void run(GuildMessageReceivedEvent event, String[] args) {

        String shit = String.join(" ",args);


        Message mem;
        try{
            mem = event.getChannel().retrieveMessageById(shit).complete();
        }catch(ErrorResponseException e){
            event.getChannel().sendMessage("**Error: **Couldn't find a message that matches the arguments in this channel.").queue();
            return;
        }

        Color color;
        if(event.getGuild().getMember(mem.getAuthor()) != null) {
            color = event.getGuild().getMember(mem.getAuthor()).getColor();
        }else{
            color = event.getMember().getColor();
        }


        MessageEmbed embed = new EmbedBuilder()
                .setAuthor("Message by: "+mem.getAuthor().getName(), null, mem.getAuthor().getAvatarUrl())
                .setDescription(mem.getContentRaw())
                .setFooter("Message sent on " + "#" + mem.getChannel().getName(), null)
                .setTimestamp(mem.getTimeCreated())
                .setColor(color)
                .build();

        event.getChannel().sendMessage(embed).queue();

    }

}
