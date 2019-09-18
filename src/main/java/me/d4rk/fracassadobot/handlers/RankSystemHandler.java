package me.d4rk.fracassadobot.handlers;

import com.rethinkdb.gen.ast.Get;
import me.d4rk.fracassadobot.Bot;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static java.util.stream.Collectors.toMap;

public class RankSystemHandler {

    private static HashMap<String, Boolean> guildStatus = new HashMap<>();
    private static HashMap<String, Long> userCooldown = new HashMap<>();

    public static boolean isSystemEnabled(String guildId) {
        if(guildStatus.get(guildId) == null) {
            HashMap map = DataHandler.r.db("fracassadobot").table("guildRankSystem").get(guildId).run(DataHandler.conn);

            if(map == null) {
                guildStatus.put(guildId, false);
                return false;
            }
            else {
                boolean status = (boolean) map.get("enabled");
                guildStatus.put(guildId, status);
                return status;
            }
        }else return guildStatus.get(guildId);
    }

    public static void enableSystem(String guildId) {
        HashMap map = DataHandler.r.db("fracassadobot").table("guildRankSystem").get(guildId).run(DataHandler.conn);

        if(map == null)
            DataHandler.r.db("fracassadobot").table("guildRankSystem").insert(
                DataHandler.r.hashMap("id", guildId).with("enabled", true)
            ).run(DataHandler.conn);
        else
            DataHandler.r.db("fracassadobot").table("guildRankSystem").get(guildId).update(
                    DataHandler.r.hashMap("enabled", true)
            ).run(DataHandler.conn);

        guildStatus.put(guildId, true);

    }

    public static void disableSystem(String guildId) {
        if(isSystemEnabled(guildId)) {
            DataHandler.r.db("fracassadobot").table("guildRankSystem").get(guildId).update(
                    DataHandler.r.hashMap("enabled", false)
            ).run(DataHandler.conn);
            guildStatus.put(guildId, false);
        }
    }

    public static void resetEntries(String guildId) {
        if(isSystemEnabled(guildId)) {
            DataHandler.r.db("fracassadobot").table("guildRankSystem").get(guildId).replace(
                    row -> row.without("entries")
            ).run(DataHandler.conn);
        }

    }

    private static long getCooldown(String userid) {
        if(userCooldown.get(userid) == null) {
            userCooldown.put(userid, System.currentTimeMillis());
            return 0;
        }
        else return userCooldown.get(userid);
    }

    private static void updateUserCooldown(String userid) {
        userCooldown.put(userid, System.currentTimeMillis());
    }

    public static void setUserPoints(String guildId, String userId, long points) {
        Get request = DataHandler.r.db("fracassadobot").table("guildRankSystem").get(guildId);
        HashMap map = request.run(DataHandler.conn);
        HashMap entries = (HashMap) map.get("entries");

        request.update(
                DataHandler.r.hashMap("entries", DataHandler.r.hashMap(userId, points))
        ).run(DataHandler.conn);
    }

    public static void updateUser(String guildId, String userId) {

        long last = getCooldown(userId), eq = ((System.currentTimeMillis()-last))/10000;
        if(eq >= 1 && isSystemEnabled(guildId)) {
            Get request = DataHandler.r.db("fracassadobot").table("guildRankSystem").get(guildId);
            HashMap map = request.run(DataHandler.conn);
            HashMap entries = (HashMap) map.get("entries");

            if (entries == null || entries.get(userId) == null)
                request.update(
                        DataHandler.r.hashMap("entries", DataHandler.r.hashMap(userId, 0))
                ).run(DataHandler.conn);
            else {
                long points = (long) entries.get(userId);
                if(eq > 3) points += 10;
                else points += eq*3;
                updateUserCooldown(userId);
                request.update(
                        DataHandler.r.hashMap("entries", DataHandler.r.hashMap(userId, points))
                ).run(DataHandler.conn);

                LinkedHashMap<String, Long> sortedRoles = getRoles(guildId)
                        .entrySet()
                        .stream()
                        .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                        .collect(toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2, LinkedHashMap::new));
                int index = 0;
                for(String s : sortedRoles.keySet()) {
                    if(points >= sortedRoles.get(s)) {
                        Guild guild = Bot.jda.getGuildById(guildId);
                        Role newRole = guild.getRoleById(s);
                        Role oldRole = null;
                        if(index < sortedRoles.keySet().size()-1) oldRole = guild.getRoleById((String) sortedRoles.keySet().toArray()[index+1]);
                        Member member = guild.getMemberById(userId);
                        if(!member.getRoles().contains(newRole)) {
                            guild.addRoleToMember(member, newRole).queue();
                            if(oldRole != null && member.getRoles().contains(oldRole)) {
                                guild.removeRoleFromMember(member, oldRole).queue();
                            }
                        }
                        break;
                    }
                    index++;
                }
            }
        }

    }

    public static void addRole(String guildId, String roleId, long points) {
        DataHandler.r.db("fracassadobot").table("guildRankSystem").get(guildId).update(
                DataHandler.r.hashMap("roles", DataHandler.r.hashMap(roleId, points))
        ).run(DataHandler.conn);
    }

    public static void removeRole(String guildId, String roleId) {
        Get request = DataHandler.r.db("fracassadobot").table("guildRankSystem").get(guildId);
        HashMap map = request.run(DataHandler.conn);
        HashMap roles = (HashMap) map.get("roles");
        roles.remove(roleId);
        DataHandler.r.db("fracassadobot").table("guildRankSystem").get(guildId).update(
                DataHandler.r.hashMap("roles", roles)
        ).run(DataHandler.conn);
    }

    public static HashMap<String, Long> getRoles(String guildId) {
        Get request = DataHandler.r.db("fracassadobot").table("guildRankSystem").get(guildId);
        HashMap map = request.run(DataHandler.conn);
        HashMap roles = (HashMap) map.get("roles");

        if (roles == null) return new HashMap<>();
        else return roles;
    }

    public static HashMap<String, Long> getEntries(String guildId) {
        Get request = DataHandler.r.db("fracassadobot").table("guildRankSystem").get(guildId);
        HashMap map = request.run(DataHandler.conn);
        HashMap entries = (HashMap) map.get("entries");

        if (entries == null) return new HashMap<>();
        else return entries;
    }

    public static long getPoints(String guildId, String userId) {
        Get request = DataHandler.r.db("fracassadobot").table("guildRankSystem").get(guildId);
        HashMap map = request.run(DataHandler.conn);
        HashMap entries = (HashMap) map.get("entries");

        if (entries == null || entries.get(userId) == null) return 0;
        else return (long) entries.get(userId);
    }
}
