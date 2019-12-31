package me.d4rk.fracassadobot.utils.command;

import me.d4rk.fracassadobot.commands.*;

public class CommandRegistry {

    public static void registerCmds() {
        CommandRegister.register(CmdCustomboard.class);
        CommandRegister.register(CmdReactionrole.class);
        CommandRegister.register(CmdRanksystem.class);
        CommandRegister.register(CmdColor.class);
        CommandRegister.register(CmdGuild.class);
        CommandRegister.register(CmdHelp.class);
        CommandRegister.register(CmdJava.class);
        CommandRegister.register(CmdMessage.class);
        CommandRegister.register(CmdPerm.class);
        CommandRegister.register(CmdPing.class);
        CommandRegister.register(CmdQuote.class);
        CommandRegister.register(CmdRestart.class);
        CommandRegister.register(CmdRole.class);
        CommandRegister.register(CmdShutdown.class);
        CommandRegister.register(CmdStats.class);
        CommandRegister.register(CmdUser.class);
        CommandRegister.register(CmdProfile.class);
        CommandRegister.register(CmdTop.class);
        CommandRegister.register(CmdDaily.class);
        CommandRegister.register(CmdShop.class);
        CommandRegister.register(CmdItem.class);
    }

}
