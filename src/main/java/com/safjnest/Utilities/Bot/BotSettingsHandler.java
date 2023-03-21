package com.safjnest.Utilities.Bot;

import java.util.HashMap;

public class BotSettingsHandler {
    
    public static HashMap<String, BotSettings> map;
    
    public BotSettingsHandler(){
        map = new HashMap<String, BotSettings>();
    }

    public synchronized void setSettings(BotSettings bs, String botId){
        map.put(botId, bs);
    }
}
