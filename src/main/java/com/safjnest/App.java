package com.safjnest;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.security.SecureRandom;
import java.util.Collections;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.safjnest.Utilities.PermissionHandler;
import com.safjnest.Utilities.SafJNest;
import com.safjnest.Utilities.Audio.TTSHandler;
import com.safjnest.Utilities.LOL.RiotHandler;
import com.safjnest.Utilities.PalWorld.PalHandler;
import com.safjnest.Utilities.SQL.DatabaseHandler;

import no.stelar7.api.r4j.basic.APICredentials;
import no.stelar7.api.r4j.impl.R4J;

@SpringBootApplication
public class App {
    private static TTSHandler tts;
    private static R4J riotApi;
    public static String key;

    private static Bot extreme_safj_beebot;

    /**
     * Insane beebot core
     */
    private static boolean extremeTesting = false;

    public static boolean isExtremeTesting() {
        return extremeTesting;
    }

    public static TTSHandler getTTS() {
        return tts;
    }

    public static R4J getRiotApi() {
        return riotApi;
    }



    public static void main(String args[]) {
        
        SafJNest.bee();

        if(args.length > 0) {
            if(args[0].equalsIgnoreCase("true")) {
                extremeTesting = true;
                System.out.println("[INFO] Beebot Canary is turning to outplay jelly");
            }
            else if(args[0].equalsIgnoreCase("false")) {
                extremeTesting = false;
                System.out.println("[INFO] Beebot is set to normal mode");
            }
        }

        if (!extremeTesting) {
            SpringApplication app = new SpringApplication(App.class);
            app.setDefaultProperties(Collections.singletonMap("server.port", "8096"));
            app.run(args);
        }
        
        new PalHandler();

        SecureRandom secureRandom = new SecureRandom();
        System.out.println("[System]: System Entropy: " + secureRandom.getProvider());//thx copilot
        
        JSONParser parser = new JSONParser();
        JSONObject settings = null, SQLSettings = null, riotSettings = null;
        try (Reader reader = new FileReader("rsc" + File.separator + "settings.json")) {
            settings = (JSONObject) parser.parse(reader);
            settings = (JSONObject) settings.get("settings");
            SQLSettings = (JSONObject) settings.get("MySQL");
            riotSettings = (JSONObject) settings.get("Riot");
        } catch (Exception e) {
            e.printStackTrace();
        }
        tts = new TTSHandler(settings.get("ttsApiKey").toString());
        
        new DatabaseHandler(
            SQLSettings.get("HostName").toString(), 
            SQLSettings.get("database").toString(), 
            SQLSettings.get("user").toString(), 
            SQLSettings.get("password").toString()
        );
        
        riotApi = null;
        try {
            riotApi = new R4J(new APICredentials(riotSettings.get("riotKey").toString()));
            System.out.println("[R4J] INFO Connection Successful!");
        } catch (Exception e) {
            System.out.println("[R4J] INFO Annodam Not Successful!");
        }
        
        new RiotHandler(riotApi, riotSettings.get("lolVersion").toString());

        System.out.println("[CANNUCCIA] INFO " + DatabaseHandler.getCannuccia());
        System.out.println("[EPRIA] ID " + PermissionHandler.getEpria());


        extreme_safj_beebot = new Bot();
        extreme_safj_beebot.il_risveglio_della_bestia();
    }

    public static void shutdown() {
        System.out.println("Shutting down the bot");
        extreme_safj_beebot.distruzione_demoniaca();
    }

    public static void restart() {
        System.out.println("Restarting the bot");
        extreme_safj_beebot.distruzione_demoniaca();
        extreme_safj_beebot.il_risveglio_della_bestia();
    }
}