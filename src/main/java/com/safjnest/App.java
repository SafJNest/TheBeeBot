package com.safjnest;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.amazonaws.auth.BasicAWSCredentials;
import com.safjnest.Utilities.AwsS3;
import com.safjnest.Utilities.DatabaseHandler;
import com.safjnest.Utilities.SQL;
import com.safjnest.Utilities.SafJNest;
import com.safjnest.Utilities.Bot.BotSettingsHandler;
import com.safjnest.Utilities.LOL.LOLHandler;
import com.safjnest.Utilities.tts.TTSHandler;

import no.stelar7.api.r4j.basic.APICredentials;
import no.stelar7.api.r4j.impl.R4J;

public class App {
    
    public static void main(String args[]) throws InterruptedException{
        
        SafJNest.bee();
        boolean isExtremeTesting = true;
        
        JSONParser parser = new JSONParser();
        JSONObject settings = null, awsSettings = null, SQLSettings = null;
        try (Reader reader = new FileReader("rsc" + File.separator + "settings.json")) {
            settings = (JSONObject) parser.parse(reader);
            settings = (JSONObject) settings.get("settings");
            awsSettings = (JSONObject) settings.get("AmazonAWS");
            SQLSettings = (JSONObject) settings.get("MySQL");
        } catch (Exception e) {
            e.printStackTrace();
        }

        TTSHandler tts = new TTSHandler(settings.get("ttsApiKey").toString());
        
        SQL sql = new SQL(
            SQLSettings.get("HostName").toString(), 
            SQLSettings.get("database").toString(), 
            SQLSettings.get("user").toString(), 
            SQLSettings.get("password").toString()
        );
        
        AwsS3 s3Client = new AwsS3(
            new BasicAWSCredentials(
                awsSettings.get("AWSAccesKey").toString(), 
                awsSettings.get("AWSSecretKey").toString()
            ), 
            awsSettings.get("AWSbucketName").toString(), 
            sql
        );
        s3Client.initialize();

        R4J riotApi = null;
        try {
            riotApi = new R4J(new APICredentials(settings.get("riotKey").toString()));
            System.out.println("[R4J] INFO Connection Successful!");
        } catch (Exception e) {
            System.out.println("[R4J] INFO Annodam Not Successful!");
        }
        
        DatabaseHandler dbh = new DatabaseHandler(sql);
        LOLHandler lolHandler = new LOLHandler(riotApi);

        dbh.doSomethingSoSunxIsNotHurtBySeeingTheFuckingThingSayItsNotUsed();
        lolHandler.doSomethingSoSunxIsNotHurtBySeeingTheFuckingThingSayItsNotUsed();

        

        BotSettingsHandler bs = new BotSettingsHandler();
        if(!isExtremeTesting){
            Thread b1 = new Thread(new Bot(bs, tts, sql, s3Client, riotApi));
            b1.setName("beebot");
            b1.start();
            Thread b2 = new Thread(new Bot(bs, tts, sql, s3Client, riotApi));
            b2.setName("beebot 2");
            b2.start();
            Thread bm = new Thread(new Bot(bs, tts, sql, s3Client, riotApi));
            bm.setName("beebot music");
            bm.start();
        }else{
            Thread bc = new Thread(new Bot(bs, tts, sql, s3Client, riotApi));
            bc.setName("canary");
            bc.start();

        }
        /* 
        Thread b3 = new Thread(new Bot(bs));
        b3.setName("beebot 3");
        b3.start();
        */
    }

   
}
