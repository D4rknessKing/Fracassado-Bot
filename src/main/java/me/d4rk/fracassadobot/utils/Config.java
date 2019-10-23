package me.d4rk.fracassadobot.utils;

import me.d4rk.fracassadobot.Bot;
import org.json.JSONObject;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Config {

    public static String suggestionsChannel = null;
    public static String loggingChannel = null;
    public static String prefix = null;
    public static String defaultPlaying = null;
    public static boolean isStreaming = false;

    public static String token = null;

    public static void loadConfig() {

        Path globalDataPath = Paths.get(getConfigFile().getAbsolutePath());

        if(Files.notExists(globalDataPath)) {
            createConfig();
        }

        String jsons;
        try {
            jsons = new String(Files.readAllBytes(globalDataPath),"UTF-8");
        }catch (Exception e) {
            System.out.println(e);
            jsons = "{}";
        }

        JSONObject json = new JSONObject(jsons);

        try{suggestionsChannel = json.getString("suggestionsChannel");}catch(Exception ignored){}
        try{loggingChannel = json.getString("loggingChannel");}catch(Exception ignored){}
        try{prefix = json.getString("prefix");}catch(Exception ignored){}
        try{defaultPlaying = json.getString("defaultPlaying");}catch(Exception ignored){}
        try{defaultPlaying = defaultPlaying.replace("%", prefix);}catch(Exception ignored){}
        try{isStreaming = json.getBoolean("isStreaming");}catch(Exception ignored){}

        try{token = json.getString("token");}catch(Exception ignored){}
    }


    public static void createConfig() {
        Path globalDataPath = Paths.get(getConfigFile().getAbsolutePath());

        JSONObject jfile = new JSONObject();
        jfile.put("suggestionsChannel","");
        jfile.put("loggingChannel", "");
        jfile.put("prefix", "");
        jfile.put("defaultPlaying", "");
        jfile.put("isStreaming", false);
        jfile.put("token", "");

        try{
            Files.write(globalDataPath, jfile.toString().getBytes("UTF-8"));
        }catch (Exception e){
            System.out.println(e);
        }
    }

    public static File getGuildDataFolder() {
        if(System.getProperty("os.name").startsWith("Windows")){
            return new File(new File(Bot.class.getProtectionDomain().getCodeSource().getLocation().getFile()).getParentFile(), "\\guildData");
        }else{
            return new File(new File(Bot.class.getProtectionDomain().getCodeSource().getLocation().getFile()).getParentFile(), "/guildData");
        }
    }

    public static File getGuildDataFile(String guildId) {
        if(System.getProperty("os.name").startsWith("Windows")){
            return new File(new File(Bot.class.getProtectionDomain().getCodeSource().getLocation().getFile()).getParentFile(), "\\guildData\\"+guildId+".json");
        }else{
            return new File(new File(Bot.class.getProtectionDomain().getCodeSource().getLocation().getFile()).getParentFile(), "/guildData/"+guildId+".json");
        }
    }

    public static File getGlobalDataFile() {
        return new File(new File(Bot.class.getProtectionDomain().getCodeSource().getLocation().getFile()).getParentFile(), "globalData.json");
    }

    public static File getConfigFile() {
        return new File(new File(Bot.class.getProtectionDomain().getCodeSource().getLocation().getFile()).getParentFile(), "config.json");
    }
}
