package me.d4rk.fracassadobot.commands;

import me.d4rk.fracassadobot.utils.EnumPerms;
import me.d4rk.fracassadobot.utils.command.Command;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class CmdShutdown{

    @Command(name="shutdown", description = "Shutdowns the bot.", category = "Bot Owner", usage = "", perms = {EnumPerms.BASE, EnumPerms.ADMIN})
    public static void run(GuildMessageReceivedEvent event, String[] args) {

        event.getChannel().sendMessage("Bye! :wave:").complete();
        event.getJDA().shutdown();
        System.exit(0);

    }

}
