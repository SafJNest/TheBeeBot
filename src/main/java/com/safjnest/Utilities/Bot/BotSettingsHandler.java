package com.safjnest.Utilities.Bot;

import java.util.HashMap;


/**
 * This class is used to store the data of all the bots and all of their attributes.
 * <p>This is usually setupped in the main class of the bot during the startup 
 * and the attributes are loaded in the {@link com.safjnest.Bot bot} class.</p>
 */
public class BotSettingsHandler {
    
    /**
     * The map of all the bots and their settings.
     * <p>The key is the id of the bot and the value is the {@link com.safjnest.Utilities.Bot.BotSettings BotSettings} object.</p>
     */
    public static HashMap<String, BotSettings> map;

    /**
     * Constructor for the BotSettingsHandler class.
     */
    public BotSettingsHandler(){
        map = new HashMap<String, BotSettings>();
    }


    /**
     * Returns the settings of a bot.
     * 
     * @param botId The id of the bot.
     * @return The settings of the bot.
     */
    public synchronized void setSettings(BotSettings bs, String botId){
        map.put(botId, bs);
    }

    public void doSomethingSoSunxIsNotHurtBySeeingTheFuckingThingSayItsNotUsed() {
        return;
	}

}
