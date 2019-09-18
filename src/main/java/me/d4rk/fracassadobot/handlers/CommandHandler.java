package me.d4rk.fracassadobot.handlers;

import me.d4rk.fracassadobot.utils.Config;
import me.d4rk.fracassadobot.utils.RandomUtils;
import me.d4rk.fracassadobot.utils.Stats;
import me.d4rk.fracassadobot.utils.command.CommandRegister;
import me.d4rk.fracassadobot.utils.command.RegisteredCommand;
import me.d4rk.fracassadobot.utils.command.RegisteredSubCommand;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;


import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class CommandHandler {

    public static void handle(GuildMessageReceivedEvent event) {

        if (!event.getChannel().canTalk() || event.getAuthor().isBot()) {
            return;
        }

        String rawContent = event.getMessage().getContentRaw();

        HashMap<String, RegisteredCommand> commandMap = CommandRegister.getCommandMap();

        String prefix = Config.prefix;

        if (rawContent.startsWith(prefix)) {

            if (rawContent.length() < prefix.length() + 1) {
                return;
            }

            rawContent = rawContent.substring(prefix.length());

            String[] args = rawContent.split("\\s+");

            if(commandMap.keySet().contains(args[0])){

                RegisteredCommand cmd = commandMap.get(args[0]);

                args = Arrays.copyOfRange(args, 1, args.length);

                if (cmd.getSubCommands() != null && cmd.getSubCommands().size() > 0 && args.length > 0) {
                    if(cmd.getSubCommands().keySet().contains(args[0])){

                        RegisteredSubCommand s = cmd.getSubCommands().get(args[0]);
                        args = Arrays.copyOfRange(args, 1, args.length);

                        //Here we run the subcommand

                        List<Boolean> requiredArgs = Arrays.stream(s
                                .getUsage()
                                .replaceAll("[^()\\[\\]]", "")
                                .replace("()", "true\n")
                                .replace("[]", "false\n")
                                .split("\n"))
                                .map(Boolean::valueOf)
                                .collect(Collectors.toList()
                                );

                        for(int i = 0 ; i < requiredArgs.size() ; i++){
                            if(requiredArgs.get(i) && args.length < i+1){
                                event.getChannel().sendMessage("**Error: **Missing arguments!" + "\n" + "**Correct usage: **"+prefix+cmd.getName()+" "+s.getName()+" "+s.getUsage()).queue();
                                return;
                            }
                        }

                        if (PermissionHandler.hasPerm(event.getAuthor(), event.getGuild(), s.getPerms())) {
                            try{
                                //LoggingHandler.logCommand(event);
                                s.getCommand().invoke(null, event, args);
                                Stats.executedCommands++;
                            }catch (Exception e) {
                                //LoggingHandler.logCommandError(event, e);
                                e.printStackTrace();
                            }
                        }else{
                            event.getChannel().sendMessage("**Error: **You don't have all the required permissions: `" + s.getPerms().toString() + "`").queue();
                        }

                        return;

                    }
                }

                if(cmd.getUsage().equals("null")){
                    String availables = "";
                    for(RegisteredSubCommand s : cmd.getSubCommands().values()){
                        availables = availables+"`"+s.getName()+"`, ";
                    }
                    availables = availables.substring(0, availables.length()-2);
                    if(args.length > 0){
                        event.getChannel().sendMessage("**Unknown option `"+args[0]+"`, available ones are: "+availables+"**").queue();
                    }else{
                        event.getChannel().sendMessage("**Unknown option, available ones are: "+availables+"**").queue();
                    }
                    return;
                }

                //Here we run the command

                List<Boolean> requiredArgs = Arrays.stream(cmd
                        .getUsage()
                        .replaceAll("[^()\\[\\]]", "")
                        .replace("()", "true\n")
                        .replace("[]", "false\n")
                        .split("\n"))
                        .map(Boolean::valueOf)
                        .collect(Collectors.toList()
                        );

                for(int i = 0 ; i < requiredArgs.size() ; i++){
                    if(requiredArgs.get(i) && args.length < i+1){
                        event.getChannel().sendMessage("**Error: **Missing arguments!" + "\n" + "**Correct usage: **"+prefix+cmd.getName()+" "+cmd.getUsage()).queue();
                        return;
                    }
                }

                if (PermissionHandler.hasPerm(event.getAuthor(), event.getGuild(), cmd.getPerms())) {
                    try{
                        //LoggingHandler.logCommand(event);
                        cmd.getCommand().invoke(null, event, args);
                        Stats.executedCommands++;
                    }catch (Exception e) {
                        //LoggingHandler.logCommandError(event, e);
                        e.printStackTrace();
                    }
                }else{
                event.getChannel().sendMessage("**Error: **You don't have all the required permissions: `" + cmd.getPerms().toString() + "`").queue();
                }

            }


        }

    }

}
