package me.d4rk.fracassadobot.core.permission;

import me.d4rk.fracassadobot.core.DataHandler;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PermissionHandler {

    public static boolean hasPerm(User user, Guild guild, List<BotPerms> lperm) {

        List<BotPerms> copy = new ArrayList<>();

        for (BotPerms lp : lperm) {
            if(lp == BotPerms.GUILD) {
                if (!guild.getMember(user).hasPermission(Permission.ADMINISTRATOR)) {
                    copy.add(lp);
                }
            }else if(lp == BotPerms.DJ) {
                boolean djCheck = false;
                for(Role role : guild.getMember(user).getRoles()){
                    if(role.getName().equalsIgnoreCase("dj")){
                        djCheck = true;
                    }
                }
                if(!djCheck){
                    copy.add(lp);
                }
            }else{
                copy.add(lp);
            }
        }

        return hasGuildPerm(user.getId(), guild.getId(), copy);
    }

    public static boolean hasGuildPerm(String userId, String guildId, List<BotPerms> permsList) {
        HashMap<String, List<String>> hashMap = DataHandler.loadGuildPerms(guildId);
        List<String> bestList = new ArrayList<>();
        for (BotPerms ep: permsList) {
            bestList.add(ep.name());
        }
        if(hashMap.get(userId) == null) {
            DataHandler.createGuildUserPerm(userId, guildId);
            hashMap = DataHandler.loadGuildPerms(guildId);
        }
        return hashMap.get(userId).containsAll(bestList);
    }

    public static boolean hasGuildPerm(String userId, String guildId, BotPerms perm) {
        HashMap<String, List<String>> hashMap = DataHandler.loadGuildPerms(guildId);
        if(hashMap.get(userId) == null) {
            DataHandler.createGuildUserPerm(userId, guildId);
            hashMap = DataHandler.loadGuildPerms(guildId);
        }
        return hashMap.get(userId).contains(perm.name());
    }

    public static boolean hasGuildPerm(String userId, String guildId, String perm) {
        HashMap<String, List<String>> hashMap = DataHandler.loadGuildPerms(guildId);
        if(hashMap.get(userId) == null) {
            DataHandler.createGuildUserPerm(userId, guildId);
            hashMap = DataHandler.loadGuildPerms(guildId);
        }
        return hashMap.get(userId).contains(perm);
    }

    public static void addGuildPerm(String userId, String guildId, BotPerms lperm) {
        HashMap<String, List<String>> hashMap = DataHandler.loadGuildPerms(guildId);
        if(hashMap.get(userId) == null) {
            DataHandler.createGuildUserPerm(userId, guildId);
            hashMap = DataHandler.loadGuildPerms(guildId);
        }
        if(hashMap.get(userId).contains(lperm.name())) {
            return;
        }
        hashMap.get(userId).add(lperm.name());
        DataHandler.saveGuildPerm(guildId, hashMap);
    }

    public static void addGuildPerm(String userId, String guildId, String lperm) {
        HashMap<String, List<String>> hashMap = DataHandler.loadGuildPerms(guildId);
        if(hashMap.get(userId) == null) {
            DataHandler.createGuildUserPerm(userId, guildId);
            hashMap = DataHandler.loadGuildPerms(guildId);
        }
        if(hashMap.get(userId).contains(lperm)) {
            return;
        }
        hashMap.get(userId).add(lperm);
        DataHandler.saveGuildPerm(guildId, hashMap);
    }

    public static void removeGuildPerm(String userId, String guildId, BotPerms lperm) {
        HashMap<String, List<String>> hashMap = DataHandler.loadGuildPerms(guildId);
        if(hashMap.get(userId) == null) {
            DataHandler.createGuildUserPerm(userId, guildId);
            hashMap = DataHandler.loadGuildPerms(guildId);
        }
        if(!hashMap.get(userId).contains(lperm.name())) {
            return;
        }
        hashMap.get(userId).remove(lperm.name());
        DataHandler.saveGuildPerm(guildId, hashMap);
    }

    public static void removeGuildPerm(String userId, String guildId, String lperm) {
        HashMap<String, List<String>> hashMap = DataHandler.loadGuildPerms(guildId);
        if(hashMap.get(userId) == null) {
            DataHandler.createGuildUserPerm(userId, guildId);
            hashMap = DataHandler.loadGuildPerms(guildId);
        }
        if(!hashMap.get(userId).contains(lperm)) {
            return;
        }
        hashMap.get(userId).remove(lperm);
        DataHandler.saveGuildPerm(guildId, hashMap);
    }

}
