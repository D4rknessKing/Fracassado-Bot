package me.d4rk.fracassadobot.listeners;

import me.d4rk.fracassadobot.Bot;
import me.d4rk.fracassadobot.core.command.CommandHandler;
import me.d4rk.fracassadobot.core.RankSystemHandler;
import me.d4rk.fracassadobot.core.postcommandrequest.PostCommandRequestHandler;
import me.d4rk.fracassadobot.utils.Config;
import me.d4rk.fracassadobot.utils.Stats;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class GuildMessageListener extends ListenerAdapter {

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        if(!event.getAuthor().isBot() && !event.getAuthor().isFake()){
            if(event.getChannel().getId().equals(Config.suggestionsChannel)){
                event.getMessage().addReaction("\uD83D\uDC4D").queue();
                event.getMessage().addReaction("\uD83D\uDC4E").queue();
            }
            if(RankSystemHandler.isSystemEnabled(event.getGuild().getId())) {
                RankSystemHandler.updateUser(event.getGuild().getId(), event.getAuthor().getId());
            }
            if(PostCommandRequestHandler.getActivePcr().get(event.getAuthor().getId()) != null && PostCommandRequestHandler.getActivePcr().get(event.getAuthor().getId()).getGuildId().equals(event.getGuild().getId())) {
                PostCommandRequestHandler.handle(event);
            }else{
                CommandHandler.handle(event);
            }
        }
        Stats.readMessages++;
        if(event.getAuthor() == Bot.jda.getSelfUser()) Stats.sendMessages++;
    }


}
