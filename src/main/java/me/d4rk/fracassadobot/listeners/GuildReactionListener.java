package me.d4rk.fracassadobot.listeners;

import me.d4rk.fracassadobot.core.LoggingHandler;
import me.d4rk.fracassadobot.core.customboard.Customboard;
import me.d4rk.fracassadobot.core.customboard.CustomboardHandler;
import me.d4rk.fracassadobot.core.DataHandler;
import me.d4rk.fracassadobot.utils.ReactionRole;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.HashMap;

public class GuildReactionListener extends ListenerAdapter {

    public void onGuildMessageReactionAdd(GuildMessageReactionAddEvent event) {
        HashMap<String, Customboard> customBoards = DataHandler.loadGuildCustomboard(event.getGuild().getId());
        for (String name : customBoards.keySet()) {
            if (customBoards.get(name).getEmote().equals(event.getReaction().getReactionEmote().getName()) || (event.getReaction().getReactionEmote().isEmote() && customBoards.get(name).getEmote().equals(event.getReaction().getReactionEmote().getEmote().getAsMention()))) {
                CustomboardHandler.onAdd(event, name, customBoards.get(name));
            }
        }
        HashMap<String, ReactionRole> reactionRoles = DataHandler.loadGuildReactionrole(event.getGuild().getId());
        for (String name : reactionRoles.keySet()) {
            if(reactionRoles.get(name).getMessageId().equals(event.getMessageId())) {
                for (String emote : reactionRoles.get(name).getEntries().keySet()) {
                    if (emote.equals(event.getReaction().getReactionEmote().getName()) || (event.getReaction().getReactionEmote().isEmote() && emote.equals(event.getReaction().getReactionEmote().getEmote().getAsMention()))) {
                        Member mem = event.getGuild().getMember(event.getUser());
                        if(mem.getUser().isBot() || mem.getUser().isFake()) return;
                        Role rol = event.getGuild().getRoleById(reactionRoles.get(name).getEntries().get(emote));
                        event.getGuild().addRoleToMember(mem, rol).queue(
                                success -> LoggingHandler.logReactionRoleAdd(event, rol)
                        );
                    }
                }
            }
        }
    }

    public void onGuildMessageReactionRemove(GuildMessageReactionRemoveEvent event) {
        HashMap<String, Customboard> customBoards = DataHandler.loadGuildCustomboard(event.getGuild().getId());
        for (String name : customBoards.keySet()) {
            if (customBoards.get(name).getEmote().equals(event.getReaction().getReactionEmote().getName()) || (event.getReaction().getReactionEmote().isEmote() && customBoards.get(name).getEmote().equals(event.getReaction().getReactionEmote().getEmote().getAsMention()))) {
                CustomboardHandler.onRemove(event, name, customBoards.get(name));
            }
        }
        HashMap<String, ReactionRole> reactionRoles = DataHandler.loadGuildReactionrole(event.getGuild().getId());
        for (String name : reactionRoles.keySet()) {
            if(reactionRoles.get(name).getMessageId().equals(event.getMessageId())) {
                for (String emote : reactionRoles.get(name).getEntries().keySet()) {
                    if (emote.equals(event.getReaction().getReactionEmote().getName()) || (event.getReaction().getReactionEmote().isEmote() && emote.equals(event.getReaction().getReactionEmote().getEmote().getAsMention()))) {
                        Member mem = event.getGuild().getMember(event.getUser());
                        if(mem.getUser().isBot() || mem.getUser().isFake()) return;
                        Role rol = event.getGuild().getRoleById(reactionRoles.get(name).getEntries().get(emote));
                        event.getGuild().removeRoleFromMember(mem, rol).queue(
                                success -> LoggingHandler.logReactionRoleRemove(event, rol)
                        );
                    }
                }
            }
        }
    }

}
