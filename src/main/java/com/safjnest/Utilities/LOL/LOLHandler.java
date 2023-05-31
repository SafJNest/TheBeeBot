package com.safjnest.Utilities.LOL;

import java.net.URL;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.HashMap;
import java.util.Locale;

import org.apache.commons.io.IOUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.safjnest.Utilities.DatabaseHandler;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.emoji.RichCustomEmoji;
import no.stelar7.api.r4j.basic.constants.api.regions.LeagueShard;
import no.stelar7.api.r4j.impl.R4J;
import no.stelar7.api.r4j.pojo.lol.championmastery.ChampionMastery;
import no.stelar7.api.r4j.pojo.lol.league.LeagueEntry;
import no.stelar7.api.r4j.pojo.lol.spectator.SpectatorParticipant;
import no.stelar7.api.r4j.pojo.lol.summoner.Summoner;


/**
 * This class is used to handle all the League of Legends related stuff
 * 
 * @author <a href="https://github.com/NeutronSun">NeutronSun</a>
 */

 public class LOLHandler {
    
    /**
     * The main object for make requests and get responses from the Riot API.
     */
    private static R4J riotApi;
    /**
     * An hashmap that contains all the runes ids and names.
     */
    private static HashMap<String, PageRunes> runesHandler = new HashMap<String, PageRunes>();

    /**
     * The current data dragon version.
     */
    private static String dataDragonVersion = "13.10.1";

    //fammi il coso per likrare una pagina
    /**
     * url for get the suggested runes from lolanalytics.
     * @see {@link <a href="https://lolanalytics.com/">lolanalytics</a>}
     */
    private static String runesURL = "https://ddragon.leagueoflegends.com/cdn/"+dataDragonVersion+"/data/en_US/runesReforged.json";

    public LOLHandler(R4J riotApi){
        LOLHandler.riotApi = riotApi;
        loadRunes();
        System.out.println("[R4J-Runes] INFO Runes Successful! Ryze is happy :)");
    }

    /**
    * Useless method but {@link <a href="https://github.com/NeutronSun">NeutronSun</a>} is one
    * of the biggest bellsprout ever made
    */
	public void doSomethingSoSunxIsNotHurtBySeeingTheFuckingThingSayItsNotUsed() {
        return;
	}


    /**
     * Load all the runes data into {@link #runesHandler runesHandler}
     */
    public void loadRunes(){
        try {
            URL url = new URL(runesURL);
            String json = IOUtils.toString(url, Charset.forName("UTF-8"));
            JSONParser parser = new JSONParser();
            JSONArray file = (JSONArray) parser.parse(json);

            for(int i = 0; i < 5; i++){
                String nPage = "";
                JSONObject page = (JSONObject)file.get(i);
                switch(String.valueOf(page.get("id"))){
                    case "8000":
                        nPage = "0";
                        break;
                    case "8100":
                        nPage = "1";
                        break;
                    case "8200":
                        nPage = "2";
                        break;
                    case "8400":
                        nPage = "3";
                        break;
                    case "8300":;
                        nPage = "4";
                        break;
                }
                runesHandler.put(nPage, new PageRunes(
                    nPage,
                    String.valueOf(page.get("id")),
                    String.valueOf(page.get("key")),
                    String.valueOf(page.get("icon")),
                    String.valueOf(page.get("name"))
                ));
                JSONArray slots = (JSONArray)page.get("slots");
                for(int j=0; j<slots.size(); j++) {
                    JSONObject rowRunes = (JSONObject)slots.get(j);
                    JSONArray runes = (JSONArray)rowRunes.get("runes");
                    for(int k = 0; k < runes.size(); k++) {
                        JSONObject rune = (JSONObject)runes.get(k);
                        Rune r = new Rune(
                            String.valueOf(rune.get("id")),
                            String.valueOf(rune.get("key")),
                            String.valueOf(rune.get("icon")),
                            String.valueOf(rune.get("name")),
                            String.valueOf(rune.get("shortDesc")),
                            String.valueOf(rune.get("longDesc"))
                        );
                        runesHandler.get(nPage).insertRune(r.getId(), r);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static HashMap<String, PageRunes> getRunesHandler() {
        return runesHandler;
    } 

    public static R4J getRiotApi(){
        return riotApi;
    }


    public static Summoner getSummonerFromDB(String discordId){
        String query = "SELECT account_id FROM lol_user WHERE discord_id = '" + discordId + "';";
        try { 
            return riotApi.getLoLAPI().getSummonerAPI().getSummonerByAccount(LeagueShard.EUW1, DatabaseHandler.getSql().getString(query, "account_id")); 
        } catch (Exception e) { return null; }
    }


    public static String getAccountIdByName(String name){
        try {
            return riotApi.getLoLAPI().getSummonerAPI().getSummonerByName(LeagueShard.EUW1, name).getAccountId();
        } catch (Exception e) {
            return null;
        }
    }

    public static int getNumberOfProfile(String discordId){
        String query = "SELECT count(discord_id) as count FROM lol_user WHERE discord_id = '" + discordId + "';";
        try { 
            return Integer.valueOf(DatabaseHandler.getSql().getString(query, "count"));
        } catch (Exception e) { return 0; }
    }

    public static Summoner getSummonerByName(String nameAccount){
        try {
            return riotApi.getLoLAPI().getSummonerAPI().getSummonerByName(LeagueShard.EUW1, nameAccount);
        } catch (Exception e) { return null; }
    }

    public static Summoner getSummonerBySummonerId(String id){
        try { 
            return riotApi.getLoLAPI().getSummonerAPI().getSummonerById(LeagueShard.EUW1, id);
        } catch (Exception e) { return null; }
    }
    
    public static Summoner getSummonerByAccountId(String id){
        try { 
            return riotApi.getLoLAPI().getSummonerAPI().getSummonerByAccount(LeagueShard.EUW1, id);
        } catch (Exception e) { return null; }
    }

    public static String getSummonerProfilePic(Summoner s){
        return "https://ddragon.leagueoflegends.com/cdn/"+dataDragonVersion+"/img/profileicon/"+s.getProfileIconId()+".png";
    }

    public static String getChampionProfilePic(String champ){
        return "https://ddragon.leagueoflegends.com/cdn/"+dataDragonVersion+"/img/champion/"+champ+".png";
    }

    public static String getSoloQStats(JDA jda, Summoner s){
        String stats = "";
        for(int i = 0; i < 2; i++){
            try {
                LeagueEntry entry = riotApi.getLoLAPI().getLeagueAPI().getLeagueEntries(LeagueShard.EUW1, s.getSummonerId()).get(i);
                if(entry.getQueueType().commonName().equals("5v5 Ranked Solo"))
                    stats = getStatsByEntry(jda, entry);

            } catch (Exception e) { }
        }
        return (stats.equals("")) ? "Unranked" : stats;
    }

    public static String getFlexStats(JDA jda, Summoner s){
        String stats = "";
        for(int i = 0; i < 2; i++){
            try {
                LeagueEntry entry = riotApi.getLoLAPI().getLeagueAPI().getLeagueEntries(LeagueShard.EUW1, s.getSummonerId()).get(i);
                if(entry.getQueueType().commonName().equals("5v5 Ranked Flex Queue"))
                    stats = getStatsByEntry(jda, entry);
            } catch (Exception e) { }
        }
        return (stats.equals("")) ? "Unranked" : stats;
    }

    private static String getStatsByEntry(JDA jda, LeagueEntry entry){
        return getFormattedEmoji(jda, entry.getTier()) + " " + entry.getTier() + " " + entry.getRank()+ " " +String.valueOf(entry.getLeaguePoints()) + " LP\n"
        + entry.getWins() + "W/"+entry.getLosses()+"L\n"
        + "Winrate:" + Math.ceil((Double.valueOf(entry.getWins())/Double.valueOf(entry.getWins()+entry.getLosses()))*100)+"%";
    }

    public static String getMastery(JDA jda, Summoner s, int nChamp){
        DecimalFormat df = new DecimalFormat("#,##0", 
        new DecimalFormatSymbols(Locale.US));
        String masteryString = "";
        int cont = 1;
        try {
            for(ChampionMastery mastery : s.getChampionMasteries()){
                if(cont == nChamp){
                    masteryString += (mastery.getChampionLevel() > 4) ? LOLHandler.getFormattedEmoji(jda, "mastery" + mastery.getChampionLevel()) + " " : "";
                    masteryString +=  LOLHandler.getFormattedEmoji(jda, riotApi.getDDragonAPI().getChampion(mastery.getChampionId()).getName()) 
                                    + " **[" + mastery.getChampionLevel()+ "]** " 
                                    + riotApi.getDDragonAPI().getChampion(mastery.getChampionId()).getName() 
                                    + " " + df.format(mastery.getChampionPoints()) 
                                    + " points";
                    break;
                }
                cont++;
            }
            
        } catch (Exception e) { }
        return masteryString;
    }

    public static String getActivity(JDA jda, Summoner s){
        try {
            for(SpectatorParticipant partecipant : s.getCurrentGame().getParticipants()){
                if(partecipant.getSummonerId().equals(s.getSummonerId()))
                    return "Playing a " + s.getCurrentGame().getGameQueueConfig().commonName()+ " as " + getFormattedEmoji(jda, riotApi.getDDragonAPI().getChampion(partecipant.getChampionId()).getName()) + " " + riotApi.getDDragonAPI().getChampion(partecipant.getChampionId()).getName(); 
            }
        } catch (Exception e) {
            return "Not in a game";
        }
        return "Not in a game";
    }

    public static String getFormattedEmoji(JDA jda, String name){
        String[] ids = {"1106615853660766298", "1106615897952636930", "1106615926578761830", "1106615956685475991", "1106632568561991690", "1106648439221133354", "1106648490911739975", "1106648568489594990", "1106648612039041064", "1108673762708172811"};
        name = transposeChampionNameForDataDragon(name);
        try {    
            for(String id : ids){
                Guild g = jda.getGuildById(id);
                for(RichCustomEmoji em: g.getEmojisByName(name, true))
                    return "<:"+name+":"+em.getId()+">";
                
            }   
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    public static String getEmojiId(JDA jda, String name){
        String[] ids = {"1106615853660766298", "1106615897952636930", "1106615926578761830", "1106615956685475991", "1106632568561991690", "1106648439221133354", "1106648490911739975", "1106648568489594990", "1106648612039041064", "1108673762708172811"};
        name = transposeChampionNameForDataDragon(name);
        try {    
            for(String id : ids){
                Guild g = jda.getGuildById(id);
                for(RichCustomEmoji em: g.getEmojisByName(name, true))
                    return em.getId();
                
            }   
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    public static String transposeChampionNameForDataDragon(String champName) {
        champName = champName.replace(".", "");
        champName = champName.replace("i'S", "is");
        champName = champName.replace("a'Z", "az");
        champName = champName.replace("l'K", "lk");
        champName = champName.replace("o'G", "og");
        champName = champName.replace("g'M", "gm");
        champName = champName.replace("'", "");
        champName = champName.replace(" & Willump", "");
        champName = champName.replace(" ", "");
        return champName;
    }
}
