package me.d4rk.fracassadobot.utils.command;

import me.d4rk.fracassadobot.utils.EnumPerms;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;

public class RegisteredCommand {

    private String nam;
    private String dsc;
    private String ctg;
    private String use;
    private List<EnumPerms> prm;

    private Method cmd;
    private HashMap<String, RegisteredSubCommand> scm;

    public RegisteredCommand(String name, String description, String category, String usage, List<EnumPerms> perms, Method command, HashMap<String, RegisteredSubCommand> subCommands){
        nam = name;
        dsc = description;
        ctg = category;
        prm = perms;
        cmd = command;
        scm = subCommands;
        use = usage;
    }

    public String getName(){
        return nam;
    }

    public String getDescription(){
        return dsc;
    }

    public String getCategory(){
        return ctg;
    }

    public String getUsage() { return use; }

    public List<EnumPerms> getPerms(){
        return prm;
    }

    public Method getCommand(){
        return cmd;
    }

    public HashMap<String, RegisteredSubCommand> getSubCommands(){
        return scm;
    }



}
