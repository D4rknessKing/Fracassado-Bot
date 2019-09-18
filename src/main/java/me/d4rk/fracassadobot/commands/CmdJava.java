package me.d4rk.fracassadobot.commands;

import me.d4rk.fracassadobot.utils.EnumPerms;
import me.d4rk.fracassadobot.utils.JavaEval;
import me.d4rk.fracassadobot.utils.command.Command;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class CmdJava {

    @Command(name="java", description = "Java (Natan's Macumbas Certified) code evaluator.", category = "Bot Owner", usage = "(Code)", perms = {EnumPerms.BASE, EnumPerms.ADMIN, EnumPerms.EVAL})
    public static void run(GuildMessageReceivedEvent event, String[] args) {

        String input = String.join(" ", args);
        JavaEval.eval(event, input);

    }

}
