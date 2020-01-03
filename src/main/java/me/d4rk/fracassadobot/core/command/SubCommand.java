package me.d4rk.fracassadobot.core.command;

import me.d4rk.fracassadobot.core.permission.BotPerms;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface SubCommand {

    String name();
    String description();
    String usage();
    BotPerms[] perms();

}
