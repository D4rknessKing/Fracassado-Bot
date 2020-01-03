package me.d4rk.fracassadobot.utils;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.List;

public class RandomUtils {

    public static Role getMuteRole(Guild guild) {
        if(guild.getRolesByName("Mute", true).size() > 0) {
            return guild.getRolesByName("Mute", true).get(0);
        }else{
            return null;
        }
    }

    public static Member getMember(GuildMessageReceivedEvent event) {
        Member mem = null;
        String string = event.getMessage().getContentRaw();
        if (event.getMessage().getMentionedUsers().size() >= 1) mem = event.getMessage().getMentionedMembers().get(0);
        List<Member> ata2 = event.getGuild().getMembersByNickname(string, false);
        if (ata2.size() >= 1) mem = ata2.get(0);
        return mem;
    }


    public static String getFullStacktrace(Exception e) {
        StringBuilder str = new StringBuilder();
        str.append(e.getClass().getName());
        if(e.getMessage() != null) str.append(": ").append(e.getMessage());
        str.append("\n");
        for(StackTraceElement ste : e.getStackTrace()) {
            str.append("    at ").append(ste.toString()).append("\n");
        }
        if(e.getCause() != null) {
            str.append("Caused by: ").append(e.getCause().getClass().getName());
            if(e.getCause().getMessage() != null) str.append(": ").append(e.getCause().getMessage());
            str.append("\n");
            for(StackTraceElement ste : e.getCause().getStackTrace()) {
                str.append("    at ").append(ste.toString()).append("\n");
            }
        }
        return str.toString();
    }

    public static String capitalizeFirst(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    //Adrian porrinha para hora
    public static String getTime(long duration) {
        final long
                years = duration / 31104000000L,
                months = duration / 2592000000L % 12,
                days = duration / 86400000L % 30,
                hours = duration / 3600000L % 24,
                minutes = duration / 60000L % 60,
                seconds = duration / 1000L % 60;
        String uptime = (years == 0 ? "" : years + " Year" + cS(years) + ", ") + (months == 0 ? "" : months + " Month" + cS(months) + ", ")
                + (days == 0 ? "" : days + " Day" + cS(days) + ", ") + (hours == 0 ? "" : hours + " Hour" + cS(hours) + ", ")
                + (minutes == 0 ? "" : minutes + " Minute" + cS(minutes) + ", ") + (seconds == 0 ? "" : seconds + " Second" + cS(seconds) + ", ");

        uptime = replaceLast(uptime, ", ", "");
        uptime = replaceLast(uptime, ",", " and");

        return uptime;
    }

    //Adrian porrinha para hora
    public static String replaceLast(String text, String regex, String replacement) {
        return text.replaceFirst("(?s)(.*)" + regex, "$1" + replacement);
    }

    //Adrian porrinha para hora
    public static String cS(long value) {
        if(value == 1) { return "";}
        return "s";
    }

}
