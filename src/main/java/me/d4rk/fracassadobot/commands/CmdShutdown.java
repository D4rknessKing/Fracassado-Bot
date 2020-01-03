package me.d4rk.fracassadobot.commands;

import me.d4rk.fracassadobot.core.permission.BotPerms;
import me.d4rk.fracassadobot.core.command.Command;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class CmdShutdown{

    @Command(name="shutdown", description = "Shutdowns the bot.", category = "Bot Owner", usage = "", perms = {BotPerms.BASE, BotPerms.ADMIN})
    public static void run(GuildMessageReceivedEvent event, String[] args) {

        event.getChannel().sendMessage("Bye! :wave:").complete();
        event.getJDA().shutdown();
        System.exit(0);

    }

}
