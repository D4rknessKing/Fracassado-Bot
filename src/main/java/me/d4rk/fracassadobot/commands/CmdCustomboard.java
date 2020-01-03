package me.d4rk.fracassadobot.commands;

import me.d4rk.fracassadobot.core.customboard.Customboard;
import me.d4rk.fracassadobot.utils.Emoji;
import me.d4rk.fracassadobot.core.DataHandler;
import me.d4rk.fracassadobot.core.permission.BotPerms;
import me.d4rk.fracassadobot.core.command.Command;
import me.d4rk.fracassadobot.core.command.SubCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.HashMap;
import java.util.List;

public class CmdCustomboard {

    @Command(name="customboard", description = "With the customboard command, you can setup a reaction board of your choice using the emote that you want.", category = "Guild", usage="", perms = {BotPerms.BASE})
    public static void run(GuildMessageReceivedEvent event, String[] args) {

        HashMap<String, Customboard> customboards = DataHandler.loadGuildCustomboard(event.getGuild().getId());
        if(customboards.size() < 1) { event.getChannel().sendMessage("**Error: ** There's no available customboards in this guild!").queue(); return;}

        String cb = "";
        String cbEx = "";
        for (String name : customboards.keySet()) {
            if(cb.length() > 1800) {
                cbEx = cbEx+customboards.get(name).getEmote()+" - **"+name+"** - <#"+customboards.get(name).getChannelID()+">\n";
            }else{
                cb = cb+customboards.get(name).getEmote()+" - **"+name+"** - <#"+customboards.get(name).getChannelID()+">\n";
            }
        }

        MessageEmbed embed = new EmbedBuilder()
                .setAuthor("Available customboards for: "+event.getGuild().getName() ,null, event.getGuild().getIconUrl())
                .setDescription(cb)
                .setFooter("Requested by: " + event.getAuthor().getName() + "#" + event.getAuthor().getDiscriminator(), event.getAuthor().getAvatarUrl())
                .setColor(event.getMember().getColor())
                .build();
        event.getChannel().sendMessage(embed).queue();
    }

    @SubCommand(name="create", description = "", usage = "(name) (emote) (channel) [min]", perms = {BotPerms.BASE, BotPerms.GUILD})
    public static void create(GuildMessageReceivedEvent event, String[] args) {

        HashMap<String, Customboard> customboards = DataHandler.loadGuildCustomboard(event.getGuild().getId());

        if(customboards.keySet().contains(args[0])){
            event.getChannel().sendMessage("**Error: **There's already a customboard with this name.").queue(); return;
        }
        if(!Emoji.getEmojiList().contains(args[1]) && event.getGuild().getEmotesByName(args[1].substring(2, args[1].length()-20), true).size() == 0){
            event.getChannel().sendMessage("**Error: **Unknown emoji.").queue(); return;
        }

        TextChannel chn = null;
        try{
            chn = event.getJDA().getTextChannelById(args[2]);
        }catch(Exception ignored){}
        List<TextChannel> ata = event.getJDA().getTextChannelsByName(args[2], false);
        if (ata.size() >= 1) {
            chn = ata.get(0);
        }
        if (event.getMessage().getMentionedChannels().size() >= 1) {
                    chn = event.getMessage().getMentionedChannels().get(0);
        }
        if(chn == null){
            event.getChannel().sendMessage("**Error: **Couldn't find a channel that matches the arguments.").queue(); return;
        }
        if(args.length < 4){
            customboards.put(args[0], new Customboard(args[1], chn.getId(), 1));
        }else{
            int min;
            try{
                customboards.put(args[0], new Customboard(args[1], chn.getId(), Integer.valueOf(args[3])));
            }catch(Exception e){
                event.getChannel().sendMessage("**Error: **"+args[3]+" is not a valid number.").queue();
                e.printStackTrace();
                return;
            }
        }
        DataHandler.saveGuildCustomboard(event.getGuild().getId(), customboards);
        event.getChannel().sendMessage("**Successfully created customboard!**").queue();

    }

    @SubCommand(name="remove", description = "", usage = "(name)", perms = {BotPerms.BASE, BotPerms.GUILD})
    public static void remove(GuildMessageReceivedEvent event, String[] args){

        HashMap<String, Customboard> customboards = DataHandler.loadGuildCustomboard(event.getGuild().getId());

        if(!customboards.keySet().contains(args[0])){
            event.getChannel().sendMessage("**Error: **There's no customboard with this name.").queue();
        }else{
            customboards.remove(args[0]);
            DataHandler.saveGuildCustomboard(event.getGuild().getId(), customboards);
            event.getChannel().sendMessage("**Successfully removed customboard!**").queue();
        }

    }

}
