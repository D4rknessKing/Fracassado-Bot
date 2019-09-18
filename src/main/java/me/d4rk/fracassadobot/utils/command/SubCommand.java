package me.d4rk.fracassadobot.utils.command;

import me.d4rk.fracassadobot.utils.EnumPerms;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface SubCommand {

    String name();
    String description();
    String usage();
    EnumPerms[] perms();

}
