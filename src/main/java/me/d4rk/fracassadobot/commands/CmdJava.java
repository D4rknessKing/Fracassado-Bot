package me.d4rk.fracassadobot.commands;

import me.d4rk.fracassadobot.core.permission.BotPerms;
import me.d4rk.fracassadobot.utils.JavaEval;
import me.d4rk.fracassadobot.core.command.Command;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class CmdJava {

    @Command(name="java", description = "Java (Natan's Macumbas Certified) code evaluator.", category = "Bot Owner", usage = "(Code)", perms = {BotPerms.BASE, BotPerms.ADMIN, BotPerms.EVAL})
    public static void run(GuildMessageReceivedEvent event, String[] args) {

        String input = String.join(" ", args);
        JavaEval.eval(event, input);

    }

}
