package me.d4rk.fracassadobot.commands;

import me.d4rk.fracassadobot.Bot;
import me.d4rk.fracassadobot.utils.EnumPerms;
import me.d4rk.fracassadobot.utils.command.Command;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class CmdRestart{


    @Command(name="restart", description = "Restarts the bot.", category = "Bot Owner", usage = "", perms = {EnumPerms.BASE, EnumPerms.ADMIN})
    public static void run(GuildMessageReceivedEvent event, String[] args) {

        File currentJar = new File("pls work");

        try {
            currentJar = new File(Bot.class.getProtectionDomain().getCodeSource().getLocation().toURI());
        } catch (URISyntaxException e) {

        }
        if (!currentJar.getName().endsWith(".jar")) {
            event.getChannel().sendMessage("**Error: **Can't restart! .jar not found.").queue();
            return;
        }

        List<String> command = new ArrayList<String>();
        command.add("java");
        command.add("-jar");
        command.add(currentJar.getPath());

        event.getChannel().sendMessage("Restarting! :wave:").complete();

        ProcessBuilder builder = new ProcessBuilder(command);
        try {
            builder.start();
        } catch (IOException e) {

        }

        event.getJDA().shutdown();
        System.exit(0);

    }

}
