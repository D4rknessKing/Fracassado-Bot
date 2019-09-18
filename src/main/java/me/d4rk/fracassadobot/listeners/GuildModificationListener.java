package me.d4rk.fracassadobot.listeners;

import net.dv8tion.jda.api.events.channel.text.TextChannelDeleteEvent;
import net.dv8tion.jda.api.events.emote.EmoteRemovedEvent;
import net.dv8tion.jda.api.events.emote.update.EmoteUpdateNameEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageDeleteEvent;
import net.dv8tion.jda.api.events.role.RoleDeleteEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;

public class GuildModificationListener extends ListenerAdapter {

    @Override
    public void onGuildMessageDelete(GuildMessageDeleteEvent event) {
    }

    @Override
    public void onEmoteRemoved(EmoteRemovedEvent event) {
    }

    @Override
    public void onEmoteUpdateName(EmoteUpdateNameEvent event) {
    }

    @Override
    public void onTextChannelDelete(TextChannelDeleteEvent event) {
    }

    @Override
    public void onRoleDelete(RoleDeleteEvent event) {

    }
}
