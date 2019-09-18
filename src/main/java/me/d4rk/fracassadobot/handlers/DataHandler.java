package me.d4rk.fracassadobot.handlers;

import com.rethinkdb.RethinkDB;
import com.rethinkdb.net.Connection;
import com.rethinkdb.net.Cursor;
import me.d4rk.fracassadobot.utils.Customboard;
import me.d4rk.fracassadobot.utils.ReactionRole;
import me.d4rk.fracassadobot.utils.EnumPerms;
import org.json.simple.JSONObject;

import java.lang.management.MemoryManagerMXBean;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class DataHandler {

    public static final RethinkDB r = RethinkDB.r;
    public static Connection conn;

    public static void connect(String ip, int port) {
        conn = r.connection().hostname(ip).port(port).connect();
    }

    //Permission Stuff
    static void createGuildUserPerm(String userId, String guildId) {
        HashMap<String, List<String>> hashMap = loadGuildPerms(guildId);
        hashMap.put(userId, Collections.singletonList(EnumPerms.BASE.name()));
        saveGuildPerm(guildId, hashMap);
    }

    public static HashMap<String, List<String>> loadGuildPerms(String guildId) {
        HashMap map = r.db("fracassadobot").table("guildPerms").get(guildId).run(conn);

        if(map == null || map.get("perms") == null ) return new HashMap<>();
        else return (HashMap<String, List<String>>) map.get("perms");
    }

    static void saveGuildPerm(String guildId, HashMap<String, List<String>> hashMap) {
        HashMap map = r.db("fracassadobot").table("guildPerms").get(guildId).run(conn);

        if(map == null || map.get("perms") == null) r.db("fracassadobot").table("guildPerms").insert(r.hashMap("perms", hashMap).with("id", guildId)).run(conn);
        else r.db("fracassadobot").table("guildPerms").replace(r.hashMap("perms", hashMap).with("id", guildId)).run(conn);


    }

    //Customboard Stuff
    public static HashMap<String, Customboard> loadGuildCustomboard(String guildId) {
        HashMap map = r.db("fracassadobot").table("guildCustomboards").get(guildId).run(conn);

        if(map == null || map.get("customboards") == null) return new HashMap<>();
        else {
            HashMap<String, Customboard> star = new HashMap<>();
            for (Object o : ((HashMap)map.get("customboards")).keySet()) {
                HashMap jo = ((HashMap) ((HashMap)map.get("customboards")).get(o.toString()));
                HashMap<String, String> hm = new HashMap<>();
                for (Object o1 : ((HashMap) jo.get("entries")).keySet()) {
                    hm.put(o1.toString(), ((HashMap) jo.get("entries")).get(o1).toString());
                }
                star.put(o.toString(), new Customboard(jo.get("emote").toString(), jo.get("channel").toString(), ((Long) jo.get("minimum")).intValue()).setEntries(hm));
            }
            return star;
        }

    }

    public static void saveGuildCustomboard(String guildId, HashMap<String, Customboard> hashMap) {

        JSONObject cb = new JSONObject();

        for(String name : hashMap.keySet()) {
            JSONObject c = new JSONObject();
            c.put("emote", hashMap.get(name).getEmote());
            c.put("channel", hashMap.get(name).getChannelID());
            c.put("minimum", hashMap.get(name).getMinimum());
            JSONObject e = new JSONObject();
            if(hashMap.get(name).getEntries() != null) {
                for(String k : hashMap.get(name).getEntries().keySet()){
                    e.put(k, hashMap.get(name).getEntries().get(k));
                }
            }
            c.put("entries", e);
            cb.put(name, c);
        }

        HashMap map = r.db("fracassadobot").table("guildCustomboards").get(guildId).run(conn);
        if(map == null || map.get("customboards") == null) r.db("fracassadobot").table("guildCustomboards").insert(r.hashMap("customboards", cb).with("id", guildId)).run(conn);
        else r.db("fracassadobot").table("guildCustomboards").replace(r.hashMap("customboards", cb).with("id", guildId)).run(conn);

    }

    //REACTION ROLES CARALHO
    public static HashMap<String, ReactionRole> loadGuildReactionrole(String guildId) {
        HashMap map = r.db("fracassadobot").table("guildReactionroles").get(guildId).run(conn);

        if(map == null || map.get("reactionroles") == null) return new HashMap<>();
        else {

            HashMap<String, ReactionRole> star = new HashMap<>();

            for (Object s : ((HashMap) map.get("reactionroles")).keySet()) {
                HashMap jo = (HashMap) ((HashMap)map.get("reactionroles")).get(s);

                HashMap<String, String> hm = new HashMap<>();
                for (Object is : ((HashMap) jo.get("entries")).keySet()) {
                    hm.put(is.toString(), ((HashMap) jo.get("entries")).get(is).toString());
                }

                star.put(s.toString(), new ReactionRole(jo.get("messageid").toString(), jo.get("channelid").toString()).setEntries(hm));
            }

            return star;
        }
    }

    public static void saveGuildReactionrole(String guildId, HashMap<String, ReactionRole> hashMap) {

        JSONObject rr = new JSONObject();

        for(String name : hashMap.keySet()) {
            JSONObject c = new JSONObject();
            c.put("messageid", hashMap.get(name).getMessageId());
            c.put("channelid", hashMap.get(name).getChannelId());
            JSONObject e = new JSONObject();
            if(hashMap.get(name).getEntries() != null) {
                for(String k : hashMap.get(name).getEntries().keySet()){
                    e.put(k, hashMap.get(name).getEntries().get(k));
                }
            }
            c.put("entries", e);
            rr.put(name, c);
        }

        HashMap map = r.db("fracassadobot").table("guildReactionroles").get(guildId).run(conn);
        if(map == null || map.get("reactionroles") == null) r.db("fracassadobot").table("guildReactionroles").insert(r.hashMap("reactionroles", rr).with("id", guildId)).run(conn);
        else {
            r.db("fracassadobot").table("guildReactionroles").replace(r.hashMap("reactionroles", rr).with("id", guildId)).run(conn);
        }

    }

}
