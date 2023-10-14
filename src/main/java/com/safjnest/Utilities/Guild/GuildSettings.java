package com.safjnest.Utilities.Guild;

import java.util.HashMap;

import com.safjnest.Utilities.SQL.DatabaseHandler;
import com.safjnest.Utilities.SQL.QueryResult;
import com.safjnest.Utilities.SQL.ResultRow;



/**
 * Class that stores in a {@link GuildSettings#cache cache} all the settings for a guild.
 * @author <a href="https://github.com/Leon412">Leon412</a>
 * @author <a href="https://github.com/NeutronSun">NeutronSun</a>
 */
public class GuildSettings {
    /**
     * {@code HashMap} that contains all the {@link com.safjnest.Utilities.Guild.GuildData settings} of every guild.
     * <p>The key of the map is the guild's id.
     */
    public HashMap<String, GuildData> cache = new HashMap<>();
    private String botId;
    private final String PREFIX;
    final GuildData data;

    /**
     * Default constructor
     * @param input
     */
    public GuildSettings(GuildData input, String botId, String PREFIX) {
        data = input;
        this.botId = botId;
        this.PREFIX = PREFIX;
    }

    /**
     * This method checks if a guild is in the cache, otherwise will be called {@link GuildSettings#retrieveServer() retrievServer}
     * to search for it in the {@link com.safjnest.Utilities.SQL.SQL mysql database}.
     * @param id Server ID
     * @return
     * The {@link com.safjnest.Utilities.Guild.GuildData guildData} if is stored in the cache(or is in the database), otherwise a defult {@link com.safjnest.Utilities.Guild.GuildData guildData}.
     * @see {@link com.safjnest.Utilities.Guild.GuildData guildData and default guildData}
     */
    public GuildData getServer(String id) {
        if(cache.containsKey(id)) 
            return cache.get(id);
         else 
            return retrieveServer(id);
         
        
    }

    /**
     * Born deprecated
     * @Deprecated
     * @param id
     * @return
     */
    public GuildData getServerIfCached(String id) {
        return cache.get(id);
    }

    /**
     * Search the gived guild in the {@link com.safjnest.Utilities.SQL.SQL postgre database}.
     * If the query found it all the settings will be downloaded and saved in the cache, otherwise will be used
     * the default settings:
     * <ul>
     * <li>Guild ID</li>
     * <li>Default PREFIX, depends on the bot ($, %, P)</li>
     * </ul>
     * @param stringId guild's ID
     * @return
     * Always a {@link com.safjnest.Utilities.Guild.GuildData guildData}, never {@code null}
     */
    public GuildData retrieveServer(String stringId) {
        System.out.println("[CACHE] Retriving guild from database => " + stringId);
        ResultRow guildData = DatabaseHandler.getGuildData(stringId, botId);
        
        if(guildData == null) {
            return insertGuild(stringId);
        }

        Long guildId = guildData.getAsLong("guild_id");
        String PREFIX = guildData.get("prefix");
        boolean expEnabled = guildData.getAsBoolean("exp_enabled");
        int threshold = guildData.getAsInt("threshold");
        String blacklistChannel = guildData.get("blacklist_channel");
        boolean blacklist_enabled = guildData.getAsBoolean("blacklist_enabled");

        GuildData guild = new GuildData(guildId, PREFIX, expEnabled, threshold, blacklistChannel, blacklist_enabled);
        saveData(guild);
        return guild;
    }

    /**
     * Search the gived guild in the {@link com.safjnest.Utilities.SQL.SQL postgre database}.
     * If the query found it all the settings will be downloaded and saved in the cache, otherwise will be used
     * the default settings:
     * <ul>
     * <li>Guild ID</li>
     * <li>Default PREFIX, depends on the bot ($, %, P)</li>
     * </ul>
     * @param stringId guild's ID
     * @return
     * Always a {@link com.safjnest.Utilities.Guild.GuildData guildData}, never {@code null}
     */
    public void retrieveAllServers() {
        QueryResult guilds = DatabaseHandler.getGuildData(botId);
        
        for(ResultRow guildData : guilds){
           Long guildId = guildData.getAsLong("guild_id");
            String PREFIX = guildData.get("prefix");
            boolean expEnabled = guildData.getAsBoolean("exp_enabled");
            int threshold = guildData.getAsInt("threshold");
            String blacklistChannel = guildData.get("blacklist_channel");
            boolean blacklist_enabled = guildData.getAsBoolean("blacklist_enabled");

            GuildData guild = new GuildData(guildId, PREFIX, expEnabled, threshold, blacklistChannel, blacklist_enabled);
            saveData(guild);
        }
    }

    public GuildData insertGuild(String guildId) {
        DatabaseHandler.insertGuild(guildId, guildId, PREFIX);
        System.out.println("[ERROR] Missing guild in database => " + guildId);

        GuildData guild = new GuildData(Long.parseLong(guildId), PREFIX, false, 0, null, false);
        saveData(guild);
        return guild;
    }

    /**
     * Saves in the {@link GuildSettings#cache cache} the {@link com.safjnest.Utilities.Guild.GuildData guildData}
     * @param guild guildData
     */
    public void saveData(GuildData guild) {
        cache.put(String.valueOf(guild.getId()), guild);
    }

    public String getId() {
        return data.getId().toString();
    }

    public String getPrefix() {
        return data.getPrefix();
    }

    public void doSomethingSoSunxIsNotHurtBySeeingTheFuckingThingSayItsNotUsed() {
        return;
	}
}
