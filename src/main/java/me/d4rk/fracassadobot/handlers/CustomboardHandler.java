package me.d4rk.fracassadobot.handlers;

import me.d4rk.fracassadobot.utils.Customboard;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionRemoveEvent;

import java.awt.*;
import java.util.HashMap;

public class CustomboardHandler {

    public static void onAdd(GuildMessageReactionAddEvent event, String name , Customboard cb) {
        HashMap<String, Customboard> finalHashMap = DataHandler.loadGuildCustomboard(event.getGuild().getId());

        HashMap<String, String> hashMap = cb.getEntries();
        if(hashMap.get(event.getMessageId()) != null) {
            Message msg = event.getGuild().getTextChannelById(cb.getChannelID()).retrieveMessageById(hashMap.get(event.getMessageId())).complete();
            MessageEmbed embed = msg.getEmbeds().get(0);
            MessageEmbed newEmbed = new EmbedBuilder(embed).setTitle(cb.getEmote()+" | "+getQuantity(event, cb.getEmote())).build();
            msg.editMessage(newEmbed).queue();
        }else{
            if(getQuantity(event, cb.getEmote()) >= cb.getMinimum()) {
                Message msg = event.getChannel().retrieveMessageById(event.getMessageId()).complete();
                EmbedBuilder embed = new EmbedBuilder()
                        .setAuthor(msg.getAuthor().getName()+"#"+msg.getAuthor().getDiscriminator(), null, msg.getAuthor().getAvatarUrl())
                        .setTitle(cb.getEmote()+" | "+getQuantity(event, cb.getEmote()))
                        .setDescription(msg.getContentRaw())
                        .setFooter("Message sent on #"+msg.getChannel().getName(), null)
                        .setTimestamp(msg.getTimeCreated())
                        .setColor(new Color(255, 172, 51));
                if(msg.getAttachments().size() > 0 && msg.getAttachments().get(0).isImage()) embed.setImage(msg.getAttachments().get(0).getUrl());
                event.getGuild().getTextChannelById(cb.getChannelID()).sendMessage(embed.build()).queue(e -> {
                    hashMap.put(event.getMessageId(), e.getId());
                    cb.setEntries(hashMap);

                    finalHashMap.remove(name);
                    finalHashMap.put(name, cb);
                    DataHandler.saveGuildCustomboard(event.getGuild().getId(), finalHashMap);
                });
            }
        }
    }

    public static void onRemove(GuildMessageReactionRemoveEvent event, String name, Customboard cb) {
        HashMap<String, Customboard> finalHashMap = DataHandler.loadGuildCustomboard(event.getGuild().getId());

        HashMap<String, String> hashMap = cb.getEntries();
        if(hashMap.get(event.getMessageId()) == null) {
            return;
        }
        if (getQuantity(event, cb.getEmote()) < cb.getMinimum()) {
            Message msg = event.getGuild().getTextChannelById(cb.getChannelID()).retrieveMessageById(hashMap.get(event.getMessageId())).complete();
            msg.delete().queue(e -> {
                hashMap.remove(event.getMessageId());
                cb.setEntries(hashMap);

                finalHashMap.remove(name);
                finalHashMap.put(name, cb);
                DataHandler.saveGuildCustomboard(event.getGuild().getId(), finalHashMap);
            });
        }else{
            Message msg = event.getGuild().getTextChannelById(cb.getChannelID()).retrieveMessageById(hashMap.get(event.getMessageId())).complete();
            MessageEmbed embed = msg.getEmbeds().get(0);
            MessageEmbed newEmbed = new EmbedBuilder(embed).setTitle(cb.getEmote()+" | "+getQuantity(event, cb.getEmote())).build();
            msg.editMessage(newEmbed).queue();
        }
    }

    private static int getQuantity(GuildMessageReactionAddEvent event, String emote){
        Message msg = event.getChannel().retrieveMessageById(event.getMessageId()).complete();
        for (MessageReaction r : msg.getReactions()) {
            if (r.getReactionEmote().getName().equals(emote) || (r.getReactionEmote().isEmote() && r.getReactionEmote().getEmote().getAsMention().equals(emote))) {
                if (r.retrieveUsers().complete().contains(msg.getAuthor())) {
                    return r.getCount()-1;
                }else{
                    return r.getCount();
                }
            }
        }
        return 0;
    }

    private static int getQuantity(GuildMessageReactionRemoveEvent event, String emote){
        Message msg = event.getChannel().retrieveMessageById(event.getMessageId()).complete();
        for (MessageReaction r : msg.getReactions()) {
            if (r.getReactionEmote().getName().equals(emote) || (r.getReactionEmote().isEmote() && r.getReactionEmote().getEmote().getAsMention().equals(emote))) {
                if (r.retrieveUsers().complete().contains(msg.getAuthor())) {
                    return r.getCount()-1;
                }else{
                    return r.getCount();
                }
            }
        }
        return 0;
    }
}
