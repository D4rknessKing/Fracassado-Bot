package me.d4rk.fracassadobot.commands;

import me.d4rk.fracassadobot.Bot;
import me.d4rk.fracassadobot.utils.EnumPerms;
import me.d4rk.fracassadobot.utils.RandomUtils;
import me.d4rk.fracassadobot.utils.Stats;
import me.d4rk.fracassadobot.utils.command.Command;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;

public class CmdStats {

    //Adrian porrinha pra calcular cpu usage
    private static OperatingSystemMXBean os = ManagementFactory.getOperatingSystemMXBean();
    private static double lastProcessCpuTime = 0;
    private static long lastSystemTime = 0;

    @Command(name = "stats", description = "Shows some \"interesting\" statistics about the bot", category = "Utils", usage = "", perms = {EnumPerms.BASE})
    public static void run(GuildMessageReceivedEvent event, String[] args){

        //Adrian porrinha para calcular cpu usage
        lastSystemTime = System.nanoTime();
        lastProcessCpuTime = ((com.sun.management.OperatingSystemMXBean) os).getProcessCpuTime();

        EmbedBuilder embed = new EmbedBuilder();
        embed.setAuthor("Bot Stats", null, Bot.jda.getSelfUser().getAvatarUrl());
        embed.setDescription("**Online for "+ RandomUtils.getTime(System.currentTimeMillis()- Stats.loginTime) +
                "**\nRAM Usage: " + String.format("%s MB", ((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / (1024*1024))) + "/" + String.format("%s MB", (Runtime.getRuntime().totalMemory() / (1024*1024))) +
                "\nCPU Usage: "+String.valueOf(calculateCpuUsage()).substring(0, 3)+" %");

        embed.addField("Entities:", "Guilds: "+Bot.jda.getGuilds().size()+
                "\nUsers: "+Bot.jda.getUsers().size()+
                "\nText Channels: "+Bot.jda.getTextChannels().size()+
                "\nVoice Channels: "+Bot.jda.getVoiceChannels().size()+
                "\nCategories: "+Bot.jda.getCategories().size(),true);
        embed.addField("Usage:", "Messages sent: "+Stats.sendMessages+
                "\nMessages read: "+Stats.readMessages+
                "\nCommands Executed: "+Stats.executedCommands, true);

        embed.setColor(event.getMember().getColor());
        embed.setFooter("Requested by: " + event.getAuthor().getName() + "#" + event.getAuthor().getDiscriminator(), event.getAuthor().getAvatarUrl());
        event.getChannel().sendMessage(embed.build()).queue();
    }

    //Adrian porrinha pra calcular cpu usage
    private static double calculateCpuUsage() {
        long systemTime = System.nanoTime();
        double processCpuTime = ((com.sun.management.OperatingSystemMXBean) os).getProcessCpuTime();

        double cpuUsage = (processCpuTime - lastProcessCpuTime) / ((double) (systemTime - lastSystemTime));

        lastSystemTime = systemTime;
        lastProcessCpuTime = processCpuTime;

        return cpuUsage / Runtime.getRuntime().availableProcessors();
    }

}
