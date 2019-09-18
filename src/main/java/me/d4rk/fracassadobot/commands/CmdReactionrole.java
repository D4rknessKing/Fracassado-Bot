package me.d4rk.fracassadobot.commands;

import me.d4rk.fracassadobot.utils.Emoji;
import me.d4rk.fracassadobot.handlers.DataHandler;
import me.d4rk.fracassadobot.utils.ReactionRole;
import me.d4rk.fracassadobot.utils.EnumPerms;
import me.d4rk.fracassadobot.utils.Paste;
import me.d4rk.fracassadobot.utils.command.Command;
import me.d4rk.fracassadobot.utils.command.SubCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class CmdReactionrole {

    @Command(name="reactionrole", description = "With the reactionrole command, you can setup a reaction board that let users choose their roles.", category = "Guild", usage="", perms = {EnumPerms.BASE})
    public static void run(GuildMessageReceivedEvent event, String[] args) {

        HashMap<String, ReactionRole> reactionrole = DataHandler.loadGuildReactionrole(event.getGuild().getId());
        if(reactionrole.size() < 1) { event.getChannel().sendMessage("**Error: ** There's no available reactionroles in this guild!").queue(); return;}

        String cb = "";
        String cbEx = "";
        for (String name : reactionrole.keySet()) {
            if(cb.length() > 1800) {
                cbEx = cbEx+"ChannelID: "+reactionrole.get(name).getMessageId()+" NAME: "+name+"\n";
            }else{
                cb = cb+"ChannelID: "+reactionrole.get(name).getMessageId()+" NAME: "+name+"\n";
            }
        }

        MessageEmbed embed = new EmbedBuilder()
                .setAuthor("Available reactionroles for: "+event.getGuild().getName() ,null, event.getGuild().getIconUrl())
                .setDescription(cb)
                .setFooter("Requested by: " + event.getAuthor().getName() + "#" + event.getAuthor().getDiscriminator(), event.getAuthor().getAvatarUrl())
                .setColor(event.getMember().getColor())
                .build();
        event.getChannel().sendMessage(embed).queue();
    }

    @SubCommand(name="create", description = "Create a reaction role board in the desired channel.", usage = "(BoardName) (Channel)", perms = {EnumPerms.BASE, EnumPerms.GUILD})
    public static void create(GuildMessageReceivedEvent event, String[] args) {

        HashMap<String, ReactionRole> reactionroles = DataHandler.loadGuildReactionrole(event.getGuild().getId());

        if(reactionroles.keySet().contains(args[0])){
            event.getChannel().sendMessage("**Error: **There's already a reactionrole with this name.").queue(); return;
        }

        TextChannel chn = null;
        try{
            chn = event.getJDA().getTextChannelById(args[1]);
        }catch(Exception ignored){}
        List<TextChannel> ata = event.getJDA().getTextChannelsByName(args[1], false);
        if (ata.size() >= 1) {
            chn = ata.get(0);
        }
        if (event.getMessage().getMentionedChannels().size() >= 1) {
            chn = event.getMessage().getMentionedChannels().get(0);
        }
        if(chn == null){
            event.getChannel().sendMessage("**Error: **Couldn't find a channel that matches the arguments.").queue(); return;
        }
        Message msg = chn.sendMessage(new EmbedBuilder().setAuthor("Reaction role").setDescription("Please config this board using the editmsg, addrole or removerole subcommands").build()).complete();

        reactionroles.put(args[0], new ReactionRole(msg.getId(), chn.getId()));
        DataHandler.saveGuildReactionrole(event.getGuild().getId(), reactionroles);
        event.getChannel().sendMessage("**Successfully created reactionrole!**").queue();

    }

    @SubCommand(name="editMsg", description = "Use this command to edit the reaction role board message", usage = "(BoardName) (Title/TitleImg/Description/Image/Footer/Color) (NewValue)", perms = {EnumPerms.BASE, EnumPerms.GUILD})
    public static void editMsg(GuildMessageReceivedEvent event, String[] args) {

        HashMap<String, ReactionRole> reactionroles = DataHandler.loadGuildReactionrole(event.getGuild().getId());

        if(!reactionroles.keySet().contains(args[0])){
            event.getChannel().sendMessage("**Error: **There's no reactionrole with this name.").queue();
        }else{
            ReactionRole rr = reactionroles.get(args[0]);
            Message msg = event.getGuild().getTextChannelById(rr.getChannelId()).retrieveMessageById(rr.getMessageId()).complete();
            MessageEmbed embed = null;
            if(msg.getEmbeds().size() == 0) embed = new EmbedBuilder().setDescription("Please dont delete the embed thanks").build();
            else embed = msg.getEmbeds().get(0);
            //Porra caralho de null pointer exception filha da puta
            String title = null, titleimg = null, img = null, footer = null;
            try{title = embed.getAuthor().getName();}catch(Exception ignored){}
            try{titleimg = embed.getAuthor().getIconUrl();}catch(Exception ignored){}
            try{img = embed.getImage().getUrl();}catch(Exception ignored){}
            try{footer = embed.getFooter().getText();}catch(Exception ignored){}
            EmbedBuilder newEmbed = new EmbedBuilder()
                    .setAuthor(title, null, titleimg)
                    .setDescription(embed.getDescription())
                    .setImage(img)
                    .setFooter(footer)
                    .setColor(embed.getColorRaw());

            String s = event.getMessage().getContentRaw();
            String input = s.substring(s.indexOf(args[0])+(args[0]+args[1]).length()+2);
            try {
                switch (args[1].toLowerCase()) {
                    case "title":
                        newEmbed.setAuthor(input);
                        break;
                    case "titleimg":
                        newEmbed.setAuthor(title, null, input);
                        break;
                    case "description":
                        newEmbed.setDescription(input);
                        break;
                    case "image":
                        newEmbed.setImage(input);
                        break;
                    case "footer":
                        newEmbed.setFooter(input);
                        break;
                    case "color":
                        newEmbed.setColor(Integer.parseInt(input));
                        break;
                    default:
                        event.getChannel().sendMessage("**Error: **Unknown property.").queue();
                        return;
                }
            }catch(Exception e){
                e.printStackTrace();
                event.getChannel().sendMessage("**Error: **Wrong value type!").queue();
                return;
            }
            msg.editMessage(newEmbed.build()).queue();
            event.getChannel().sendMessage("**Successfully edited reactionrole!**").queue();
        }

    }

    @SubCommand(name="addRole", description = "Add a role to the board.", usage = "(Name) (Emoji) (Role)", perms = {EnumPerms.BASE, EnumPerms.GUILD})
    public static void addRole(GuildMessageReceivedEvent event, String[] args){

        HashMap<String, ReactionRole> reactionroles = DataHandler.loadGuildReactionrole(event.getGuild().getId());

        if(!reactionroles.keySet().contains(args[0])){
            event.getChannel().sendMessage("**Error: **There's no reactionrole with this name.").queue();
        }else{
            Emote emote = null;
            if((args[1].length() > 20)) {
                List<Emote> list = event.getGuild().getEmotesByName(args[1].substring(2, args[1].length()-20), true);
                if(list.size() > 0) emote = list.get(0);
            }
            if(!Emoji.getEmojiList().contains(args[1]) && emote == null){
                event.getChannel().sendMessage("**Error: **Unknown emoji.").queue();
                return;
            }

            String shit = String.join(" ", Arrays.copyOfRange(args, 2, args.length));
            Role role = null;
            try{role = event.getMessage().getMentionedRoles().get(0);}catch(Exception ignored){}
            try{role = event.getGuild().getRoleById(shit);}catch(Exception ignored){}
            List ata = event.getGuild().getRolesByName(shit, false);
            if (ata.size() >= 1) role = (Role) ata.get(0);
            if(role == null) {
                event.getChannel().sendMessage("**Error: **Couldn't find a role that matches the arguments.").queue();
                return;
            }

            reactionroles.get(args[0]).getEntries().put(args[1], role.getId());
            ReactionRole rr = reactionroles.get(args[0]);
            Message msg = event.getGuild().getTextChannelById(rr.getChannelId()).retrieveMessageById(rr.getMessageId()).complete();
            if(emote == null) {
                msg.addReaction(args[1]).complete();
            }else{
                msg.addReaction(emote).complete();
            }
            DataHandler.saveGuildReactionrole(event.getGuild().getId(), reactionroles);
            event.getChannel().sendMessage("**Successfully added role to reaction role board!**").queue();

        }



    }

    @SubCommand(name="removeRole", description = "Removes a role from the board.", usage = "(Name) (Emoji)", perms = {EnumPerms.BASE, EnumPerms.GUILD})
    public static void removeRole(GuildMessageReceivedEvent event, String[] args){

        HashMap<String, ReactionRole> reactionroles = DataHandler.loadGuildReactionrole(event.getGuild().getId());

        if(!reactionroles.keySet().contains(args[0])){
            event.getChannel().sendMessage("**Error: **There's no reactionrole with this name.").queue();
        }else{
            if(!reactionroles.get(args[0]).getEntries().keySet().contains(args[1])){
                event.getChannel().sendMessage("**Error: **There's no role assigned to this emoji.").queue();
            }else{
                reactionroles.get(args[0]).getEntries().remove(args[1]);
                DataHandler.saveGuildReactionrole(event.getGuild().getId(), reactionroles);
                event.getChannel().sendMessage("**Successfully removed role to reaction role board!**").queue();
            }
        }

    }

    @SubCommand(name="remove", description = "Remove the desired reaction role board.", usage = "(BoardName)", perms = {EnumPerms.BASE, EnumPerms.GUILD})
    public static void remove(GuildMessageReceivedEvent event, String[] args){

        HashMap<String, ReactionRole> reactionroles = DataHandler.loadGuildReactionrole(event.getGuild().getId());

        if(!reactionroles.keySet().contains(args[0])){
            event.getChannel().sendMessage("**Error: **There's no reactionrole with this name.").queue();
        }else{
            reactionroles.remove(args[0]);
            DataHandler.saveGuildReactionrole(event.getGuild().getId(), reactionroles);
            event.getChannel().sendMessage("**Successfully removed reactionrole!**").queue();
        }

    }

}
