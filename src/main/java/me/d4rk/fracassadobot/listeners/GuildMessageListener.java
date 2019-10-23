package me.d4rk.fracassadobot.listeners;

import me.d4rk.fracassadobot.Bot;
import me.d4rk.fracassadobot.handlers.CommandHandler;
import me.d4rk.fracassadobot.handlers.RankSystemHandler;
import me.d4rk.fracassadobot.utils.Config;
import me.d4rk.fracassadobot.utils.Stats;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class GuildMessageListener extends ListenerAdapter {

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        if(RankSystemHandler.isSystemEnabled(event.getGuild().getId()) && !event.getAuthor().isBot() && !event.getAuthor().isFake()) {
            long time = System.currentTimeMillis();
            RankSystemHandler.updateUser(event.getGuild().getId(), event.getAuthor().getId());
            if(event.getChannel().getId().equals(Config.suggestionsChannel)){
                event.getMessage().addReaction("\uD83D\uDC4D").queue();
                event.getMessage().addReaction("\uD83D\uDC4E").queue();
            }
            if(System.currentTimeMillis()-time > 0) System.out.println("Message by "+event.getAuthor().getName()+" took "+(System.currentTimeMillis()-time)+"ms to process.");
        }
        CommandHandler.handle(event);
        Stats.readMessages++;
        if(event.getAuthor() == Bot.jda.getSelfUser()) Stats.sendMessages++;
    }


}
