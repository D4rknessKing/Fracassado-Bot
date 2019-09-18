package me.d4rk.fracassadobot.utils.command;

import me.d4rk.fracassadobot.utils.EnumPerms;

import java.lang.reflect.Method;
import java.util.List;

public class RegisteredSubCommand {

    private String nam;
    private String dsc;
    private String use;
    private List<EnumPerms> prm;

    private Method cmd;

    public RegisteredSubCommand(String name, String description, String usage, List<EnumPerms> perms, Method command){
        nam = name;
        dsc = description;
        use = usage;
        prm = perms;
        cmd = command;
    }

    public String getName(){
        return nam;
    }

    public String getDescription() {return dsc;}

    public String getUsage() {return use;}

    public List<EnumPerms> getPerms(){
        return prm;
    }

    public Method getCommand(){
        return cmd;
    }


}
