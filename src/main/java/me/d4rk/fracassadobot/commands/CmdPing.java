package me.d4rk.fracassadobot.commands;

import me.d4rk.fracassadobot.utils.EnumPerms;
import me.d4rk.fracassadobot.utils.command.Command;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class CmdPing {

    @Command(name="ping", description = "Shows the ping between the Discord API gateway and the bot's server.", category = "Utils", usage = "", perms = {EnumPerms.BASE})
    public static void run(GuildMessageReceivedEvent event, String[] args) {
        long time = System.currentTimeMillis();
        event.getChannel().sendTyping().queue(success -> {
            long ping = System.currentTimeMillis() - time;
            event.getChannel().sendMessage("**Ping: **" + ping + "*ms*").queue();
        });
    }

}
