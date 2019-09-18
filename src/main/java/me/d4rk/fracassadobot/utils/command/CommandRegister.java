package me.d4rk.fracassadobot.utils.command;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class CommandRegister {

    private static HashMap<String, RegisteredCommand> commandMap = new HashMap<>();

    public static void register(Class cla) {
        RegisteredCommand rc = null;

        Method main = null;
        List<Method> subs = new ArrayList<>();

        for(Method m : cla.getMethods()){
            if(m.getAnnotation(Command.class) != null) {
                main = m;
            }
            if(m.getAnnotation(SubCommand.class) != null) {
                subs.add(m);
            }
        }

        HashMap<String, RegisteredSubCommand> registeredSubs = new HashMap<>();
        for(Method m : subs){
            SubCommand s = m.getAnnotation(SubCommand.class);
            registeredSubs.put(s.name(), new RegisteredSubCommand(s.name(), s.description(), s.usage(), Arrays.asList(s.perms()), m));
        }

        if(main != null) {
            if(registeredSubs.size() > 0){
                Command c = main.getAnnotation(Command.class);
                rc = new RegisteredCommand(c.name(), c.description(), c.category(), c.usage(), Arrays.asList(c.perms()), main, registeredSubs);
            }else{
                Command c = main.getAnnotation(Command.class);
                rc = new RegisteredCommand(c.name(), c.description(), c.category(), c.usage(), Arrays.asList(c.perms()), main, null);
            }
        }

        if(rc != null){
            commandMap.put(rc.getName(), rc);
        }


    }

    public static HashMap<String, RegisteredCommand> getCommandMap() {
        return commandMap;
    }
}
