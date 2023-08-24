package com.safjnest.Utilities.Guild;

import java.util.ArrayList;
import java.util.HashMap;

import com.safjnest.Utilities.DatabaseHandler;



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
    String botId;
    String prefix;
    final GuildData data;

    /**
     * Default constructor
     * @param input
     */
    public GuildSettings(GuildData input, String botId, String prefix) {
        data = input;
        this.botId = botId;
        this.prefix = prefix;
    }

    /**
     * This method checks if a guild is in the cache, otherwise will be called {@link GuildSettings#retrieveServer() retrievServer}
     * to search for it in the {@link com.safjnest.Utilities.SQL postgre database}.
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
     * Search the gived guild in the {@link com.safjnest.Utilities.SQL postgre database}.
     * If the query found it all the settings will be downloaded and saved in the cache, otherwise will be used
     * the default settings:
     * <ul>
     * <li>Guild ID</li>
     * <li>Default prefix, depends on the bot ($, %, P)</li>
     * </ul>
     * @param stringId guild's ID
     * @return
     * Always a {@link com.safjnest.Utilities.Guild.GuildData guildData}, never {@code null}
     */
    public GuildData retrieveServer(String stringId) {
        String query = "SELECT guild_id, prefix, exp_enabled FROM guild_settings WHERE guild_id = '" + stringId + "' AND bot_id = '" + botId + "';";
        ArrayList<String> guildArrayList = DatabaseHandler.getSql().getSpecifiedRow(query, 0);
        GuildData guild = (guildArrayList == null || guildArrayList.get(1) == null) 
                    ? new GuildData(Long.parseLong(stringId), prefix, false) 
                    : new GuildData(Long.parseLong(guildArrayList.get(0)), guildArrayList.get(1), (guildArrayList.get(2).equals("1")));
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
