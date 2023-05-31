package com.safjnest.Utilities.Bot;


/**
 * This class is used to store the data of a bot and all of its attributes.
 */
public class BotSettings {
    /**
     * The id of the bot.
     */
    public String botId;
    /**
     * The global prefix of the bot.
     */
    public String prefix;
    /**
     * The color of the bot.
     */
    public String color;

    /**
     * Constructor for the BotSettings class.
     * 
     * @param botId The id of the bot.
     * @param prefix The global prefix of the bot.
     * @param color The color of the bot.
     */
    public BotSettings(String botId, String prefix, String color){
        this.botId = botId;
        this.prefix = prefix;
        this.color = color;
    }
}
