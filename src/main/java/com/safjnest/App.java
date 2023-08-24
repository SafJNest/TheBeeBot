package com.safjnest;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.safjnest.Utilities.DatabaseHandler;
import com.safjnest.Utilities.OpenAIHandler;
import com.safjnest.Utilities.SQL;
import com.safjnest.Utilities.SafJNest;
import com.safjnest.Utilities.TTSHandler;
import com.safjnest.Utilities.Bot.BotSettingsHandler;
import com.safjnest.Utilities.LOL.RiotHandler;

import no.stelar7.api.r4j.basic.APICredentials;
import no.stelar7.api.r4j.impl.R4J;

public class App {
    public static ArrayList<Thread> botsArr = new ArrayList<>(); 
    private static TTSHandler tts;
    private static SQL sql;
    private static R4J riotApi;
    private static OpenAIHandler openAIHandler;
    private static RiotHandler lolHandler;
    private static DatabaseHandler dbh;
    private static BotSettingsHandler bs;

    public static void main(String args[]) {
        
        SafJNest.bee();
        boolean isExtremeTesting = false;
        
        JSONParser parser = new JSONParser();
        JSONObject settings = null, SQLSettings = null, openAISettins = null, riotSettings = null;
        JSONArray bots = null;
        try (Reader reader = new FileReader("rsc" + File.separator + "settings.json")) {
            settings = (JSONObject) parser.parse(reader);
            bots = (JSONArray) settings.get("startup");
            settings = (JSONObject) settings.get("settings");
            SQLSettings = (JSONObject) settings.get("MySQL");
            openAISettins = (JSONObject) settings.get("OpenAI");
            riotSettings = (JSONObject) settings.get("Riot");
        } catch (Exception e) {
            e.printStackTrace();
        }

        tts = new TTSHandler(settings.get("ttsApiKey").toString());
        
        sql = new SQL(
            SQLSettings.get("HostName").toString(), 
            SQLSettings.get("database").toString(), 
            SQLSettings.get("user").toString(), 
            SQLSettings.get("password").toString()
        );
        
        riotApi = null;
        try {
            riotApi = new R4J(new APICredentials(
                riotSettings.get("riotKey").toString()));
            System.out.println("[R4J] INFO Connection Successful!");
        } catch (Exception e) {
            System.out.println("[R4J] INFO Annodam Not Successful!");
        }
        
        openAIHandler = new OpenAIHandler(
            openAISettins.get("key").toString(), 
            openAISettins.get("maxTokens").toString(), 
            openAISettins.get("model").toString()
        );

        
        dbh = new DatabaseHandler(sql);
        lolHandler = new RiotHandler(riotApi, riotSettings.get("lolVersion").toString());

        dbh.doSomethingSoSunxIsNotHurtBySeeingTheFuckingThingSayItsNotUsed();
        lolHandler.doSomethingSoSunxIsNotHurtBySeeingTheFuckingThingSayItsNotUsed();
        openAIHandler.doSomethingSoSunxIsNotHurtBySeeingTheFuckingThingSayItsNotUsed();

        bs = new BotSettingsHandler();

        if(!isExtremeTesting){
            try {
                for (int i = 0; i < bots.size(); i++) {
                    Thread t = new Thread(new Bot(bs, tts, sql, riotApi));
                    t.setName((String)bots.get(i));
                    botsArr.add(t);
                }
                for(Thread t : botsArr){
                    t.start();
                    Thread.sleep(6117); //pebble non riesce a gestire più di un bot che si loada contemporaneamente
                }
            } catch (Exception e) {e.printStackTrace(); return;}
        }else{
            Thread bc = new Thread(new Bot(bs, tts, sql, riotApi));
            bc.setName("beebot canary");
            bc.start();
        }
    }

    public static void shutdown(String bot){
        System.out.println("Shutting down " + bot);
        for(int i = 0; i < botsArr.size(); i++){
            if(botsArr.get(i).getName().equals(bot)){
                botsArr.get(i).interrupt();
                botsArr.remove(i);
            }
        }
    }

    public static void restart(String bot){
        System.out.println("Shutting down " + bot);
        for(int i = 0; i < botsArr.size(); i++){
            if(botsArr.get(i).getName().equals(bot)){
                botsArr.get(i).interrupt();
                botsArr.remove(i);
            }
        }
        Thread t = new Thread(new Bot(bs, tts, sql, riotApi));
        t.setName(bot);
        t.start();
        botsArr.add(t);
        return;
    }
   
}
