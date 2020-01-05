package me.d4rk.fracassadobot.core.economy;

import com.rethinkdb.gen.ast.Table;
import com.rethinkdb.model.MapObject;
import javafx.util.Pair;
import me.d4rk.fracassadobot.Bot;
import me.d4rk.fracassadobot.core.DataHandler;
import me.d4rk.fracassadobot.core.RankSystemHandler;
import me.d4rk.fracassadobot.utils.RandomUtils;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class EconomySystemHandler {

    public static Table economyTable = DataHandler.database.table("guildEconomy");

    public static List<EconomyUser> getUsers(String guildId) {
        checkSystem(guildId);
        HashMap map = economyTable.get(guildId).run(DataHandler.conn);
        List<EconomyUser> userList = new ArrayList<>();
        for(Object key : map.keySet()) {
            if(map.get(key) instanceof HashMap) {
                HashMap userMap = (HashMap) map.get(key);
                List<Pair<String, Long>> userEffects = new ArrayList<>(), userCooldown = new ArrayList<>();
                for(HashMap<String, Object> hMap : (List<HashMap<String, Object>>) userMap.get("effects")) {
                    userEffects.add(new Pair<>((String) hMap.get("key"), (Long) hMap.get("value")));
                }
                for(HashMap<String, Object> hMap : (List<HashMap<String, Object>>) userMap.get("cooldown")) {
                    userCooldown.add(new Pair<>((String) hMap.get("key"), (Long) hMap.get("value")));
                }
                userList.add(
                    new EconomyUser(
                        key.toString(),
                        (long) userMap.get("money"),
                        (long) userMap.get("lastDaily"),
                        ((Long) userMap.get("streak")).intValue(),
                        (long) userMap.get("lastStreak"),
                        (List<String>) userMap.get("inventory"),
                        userEffects,
                        userCooldown
                ));
            }
        }
        return userList;
    }

    public static EconomyUser getUser(String guildId, String userId) {
        checkSystem(guildId);
        HashMap map = economyTable.get(guildId).run(DataHandler.conn);
        Object user = map.get(userId);

        if(user != null) {
            HashMap userMap = (HashMap) user;
            List<Pair<String, Long>> userEffects = new ArrayList<>(), userCooldown = new ArrayList<>();
            for(HashMap<String, Object> hMap : (List<HashMap<String, Object>>) userMap.get("effects")) {
                userEffects.add(new Pair<>((String) hMap.get("key"), (Long) hMap.get("value")));
            }
            for(HashMap<String, Object> hMap : (List<HashMap<String, Object>>) userMap.get("cooldown")) {
                userCooldown.add(new Pair<>((String) hMap.get("key"), (Long) hMap.get("value")));
            }
            return new EconomyUser(
                    userId,
                    (long) userMap.get("money"),
                    (long) userMap.get("lastDaily"),
                    ((Long) userMap.get("streak")).intValue(),
                    (long) userMap.get("lastStreak"),
                    (List<String>) userMap.get("inventory"),
                    userEffects,
                    userCooldown
            );
        } else {
            createUser(guildId, userId);
            return getUser(guildId, userId);
        }
    }

    public static void addMoney(String guildId, String userId, long money) {
        EconomyUser economyUser = getUser(guildId, userId);
        DataHandler.database.table("guildEconomy").get(guildId).update(
                DataHandler.r.hashMap(userId, DataHandler.r.hashMap("money", economyUser.getMoney()+money))
        ).run(DataHandler.conn);
    }

    public static void buyItem(String guildId, String userId, EconomyItem item) {
        EconomyUser economyUser = getUser(guildId, userId);
        List<String> newInventory = economyUser.getInventory();
        newInventory.add(item.getId());
        DataHandler.database.table("guildEconomy").get(guildId).update(
                DataHandler.r.hashMap(userId, DataHandler.r.hashMap("money", economyUser.getMoney()-item.getPrice()).with("inventory", newInventory))
        ).run(DataHandler.conn);
    }

    public static void addItem(String guildId, String userId, EconomyItem item) {
        EconomyUser economyUser = getUser(guildId, userId);
        List<String> newInventory = economyUser.getInventory();
        newInventory.add(item.getId());
        DataHandler.database.table("guildEconomy").get(guildId).update(
                DataHandler.r.hashMap(userId, DataHandler.r.hashMap("inventory", newInventory))
        ).run(DataHandler.conn);
    }

    public static void useItem(String guildId, String userId, String receiverId, EconomyItem item) {
        EconomyUser effectUser = getUser(guildId, userId);
        EconomyUser effectReceiver = getUser(guildId, receiverId);
        List<String> userInventory = effectUser.getInventory();
        List<Pair<String, Long>> receiverEffects = effectReceiver.getEffects();
        List<Pair<String, Long>> receiverCooldown = effectReceiver.getCooldown();
        userInventory.remove(item.getId());
        receiverEffects.add(new Pair<>(item.getEffect().name(), System.currentTimeMillis()+item.getEffect().getTime()));
        if(item.getEffect().getCooldown() != null) receiverCooldown.add(new Pair<>(item.getEffect().getCooldown().name(), System.currentTimeMillis()+item.getEffect().getCooldown().getCooldown()));
        if(userId.equals(receiverId)) {
            DataHandler.database.table("guildEconomy").get(guildId).update(
                    DataHandler.r.hashMap(userId, DataHandler.r.hashMap("inventory", userInventory).with("effects", receiverEffects).with("cooldown", receiverCooldown))
            ).run(DataHandler.conn);
        }else{
            DataHandler.database.table("guildEconomy").get(guildId).update(
                    DataHandler.r.hashMap(userId, DataHandler.r.hashMap("inventory", userInventory)).with(receiverId, DataHandler.r.hashMap("effects", receiverEffects).with("cooldown", receiverCooldown))
            ).run(DataHandler.conn);
        }
        //Se o item utilizado foi um de mute muta a pessoa. viva!
        if(item == EconomyItem.DEBUFF_MUTE5M || item == EconomyItem.DEBUFF_MUTE10M) {
            Guild guild = Bot.jda.getGuildById(guildId);
            Member member = guild.getMemberById(receiverId);
            Role mute = RandomUtils.getMuteRole(guild);
            if(mute != null) {
                guild.addRoleToMember(member, mute).queue();
            }
        }else if(item == EconomyItem.DEBUFF_VANISH && RankSystemHandler.isSystemEnabled(guildId)) {
            Guild guild = Bot.jda.getGuildById(guildId);
            Member member = guild.getMemberById(receiverId);
            for (Role role : member.getRoles())
                for (String id : RankSystemHandler.getRoles(guildId).keySet())
                    if(role.getId().equals(id)) guild.removeRoleFromMember(member, role).queue();
        }

    }

    public static void useItem(String guildId, String userId, EconomyItem item) {
        EconomyUser effectUser = getUser(guildId, userId);
        List<String> userInventory = effectUser.getInventory();
        userInventory.remove(item.getId());
        DataHandler.database.table("guildEconomy").get(guildId).update(
                DataHandler.r.hashMap(userId, DataHandler.r.hashMap("inventory", userInventory))
        ).run(DataHandler.conn);
    }

    public static void updateDaily(String guildId, String userId, boolean auto) {
        EconomyUser economyUser = getUser(guildId, userId);
        MapObject newUser = DataHandler.r.hashMap("lastDaily", System.currentTimeMillis());

        long newStreak = economyUser.getStreak();
        if(!auto) {
            if((System.currentTimeMillis() - economyUser.getLastStreak()) <= 172800000) newStreak++;
            else newStreak = 1;
            newUser = newUser.with("lastStreak", System.currentTimeMillis());
        }
        if(newStreak >= 7) {
            newUser = newUser.with("money", economyUser.getMoney()+1500);
            newStreak = 1;
        }else{
            newUser = newUser.with("money", economyUser.getMoney()+500);
        }

        newUser = newUser.with("streak", newStreak);
        DataHandler.database.table("guildEconomy").get(guildId).update(
                DataHandler.r.hashMap(userId, newUser)
        ).run(DataHandler.conn);
    }

    public static void createUser(String guildId, String userId) {
        DataHandler.database.table("guildEconomy").get(guildId).update(
                DataHandler.r.hashMap(userId, DataHandler.r.hashMap("money", 0).with("lastDaily", System.currentTimeMillis()-86400005).with("streak", 1).with("lastStreak", System.currentTimeMillis()-86400005).with("inventory", DataHandler.r.array()).with("effects", DataHandler.r.array()).with("cooldown", DataHandler.r.array()))
        ).run(DataHandler.conn);
    }

    public static void checkSystem(String guildId) {
        HashMap map = DataHandler.database.table("guildEconomy").get(guildId).run(DataHandler.conn);
        if(map == null) {
            DataHandler.database.table("guildEconomy").insert(
                    DataHandler.r.hashMap("id", guildId)
            ).run(DataHandler.conn);
        }
    }


}
