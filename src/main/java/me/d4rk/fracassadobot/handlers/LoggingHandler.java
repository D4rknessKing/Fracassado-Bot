package me.d4rk.fracassadobot.handlers;

import me.d4rk.fracassadobot.Bot;
import me.d4rk.fracassadobot.utils.Config;
import me.d4rk.fracassadobot.utils.Paste;
import me.d4rk.fracassadobot.utils.RandomUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionRemoveEvent;
import org.apache.commons.logging.impl.SimpleLog;

public class LoggingHandler {

    //SimpleLog log = new SimpleLog("Fracassado Bot");

    public static void logGenericUserError() {

    }

    public static void logCommand(GuildMessageReceivedEvent event) {
        /*
        Bot.jda.getTextChannelById(Config.loggingChannel).sendMessage(
                String.format(
                        "**[Fracassado BOT]** %s executed `%s` command in %s.",
                        event.getAuthor().getName(),
                        event.getMessage().getContentRaw(),
                        event.getChannel().getAsMention()
                )
        ).queue();
        */
    }

    public static void logCommandError(GuildMessageReceivedEvent event, Exception e) {
        /*
        String stacktrace = RandomUtils.getFullStacktrace(e);
        Paste.toHastebinAsync(stacktrace).whenComplete((response, error) -> {
            String link;
            if(error == null) {
                link = "https://hastebin.com/"+response.getBody().getObject().getString("key");
            }else{
                error.printStackTrace();
                link = "Unknown error while uploading paste to hastebin, please check logs.";
            }
            Bot.jda.getTextChannelById(Config.loggingChannel).sendMessage(
                    new EmbedBuilder()
                            .setTitle("Unkwnown error while executing command:")
                            .setDescription("**Stacktrace:**\n```"+stacktrace.substring(0, 1900)+"```\n**Full Stacktrace:** "+link)
                            .setColor(-65536)
                            .setFooter(String.format("Command executed by %s in #%s", event.getAuthor().getName(), event.getChannel().getName()))
                            .build()
            ).append("<@>").queue();
        });
        */
    }

    public static void logReactionRoleAdd(GuildMessageReactionAddEvent event, Role r) {
        Bot.jda.getTextChannelById(Config.loggingChannel).sendMessage(
                String.format(
                        "**[Fracassado BOT]** %s was assigned to `%s` by a reaction role board.",
                        r.getAsMention(),
                        event.getMember().getEffectiveName()
                )
        ).queue();
    }

    public static void logReactionRoleRemove(GuildMessageReactionRemoveEvent event, Role r) {
        Bot.jda.getTextChannelById(Config.loggingChannel).sendMessage(
                String.format(
                        "**[Fracassado BOT]** %s was removed from `%s` by a reaction role board.",
                        r.getAsMention(),
                        event.getMember().getEffectiveName()
                )
        ).queue();
    }

}
