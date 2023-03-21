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

import no.stelar7.api.r4j.basic.constants.api.regions.LeagueShard;
import no.stelar7.api.r4j.impl.R4J;
import no.stelar7.api.r4j.pojo.lol.championmastery.ChampionMastery;
import no.stelar7.api.r4j.pojo.lol.league.LeagueEntry;
import no.stelar7.api.r4j.pojo.lol.spectator.SpectatorParticipant;
import no.stelar7.api.r4j.pojo.lol.summoner.Summoner;

public class LOLHandler {
    

    private static R4J riotApi;
    private static HashMap<String, PageRunes> runesHandler = new HashMap<String, PageRunes>();


    private static String dataDragonVersion = "13.3.1";

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

    public static Summoner getSummonerById(String id){
        try { 
            return riotApi.getLoLAPI().getSummonerAPI().getSummonerById(LeagueShard.EUW1, id);
        } catch (Exception e) { return null; }
    }

    public static String getSummonerProfilePic(Summoner s){
        return "https://ddragon.leagueoflegends.com/cdn/"+dataDragonVersion+"/img/profileicon/"+s.getProfileIconId()+".png";
    }

    public static String getChampionProfilePic(String champ){
        return "https://ddragon.leagueoflegends.com/cdn/"+dataDragonVersion+"/img/champion/"+champ+".png";
    }

    public static String getSoloQStats(Summoner s){
        String stats = "";
        for(int i = 0; i < 2; i++){
            try {
                LeagueEntry entry = riotApi.getLoLAPI().getLeagueAPI().getLeagueEntries(LeagueShard.EUW1, s.getSummonerId()).get(i);
                if(entry.getQueueType().commonName().equals("5v5 Ranked Solo"))
                    stats = getStatsByEntry(entry);

            } catch (Exception e) { }
        }
        return (stats.equals("")) ? "Unranked" : stats;
    }

    public static String getFlexStats(Summoner s){
        String stats = "";
        for(int i = 0; i < 2; i++){
            try {
                LeagueEntry entry = riotApi.getLoLAPI().getLeagueAPI().getLeagueEntries(LeagueShard.EUW1, s.getSummonerId()).get(i);
                if(entry.getQueueType().commonName().equals("5v5 Ranked Flex Queue"))
                    stats = getStatsByEntry(entry);
            } catch (Exception e) { }
        }
        return (stats.equals("")) ? "Unranked" : stats;
    }

    private static String getStatsByEntry(LeagueEntry entry){
        return entry.getTier() + " " + entry.getRank()+ " " +String.valueOf(entry.getLeaguePoints()) + " LP\n"
        + entry.getWins() + "W/"+entry.getLosses()+"L\n"
        + "Winrate:" + Math.ceil((Double.valueOf(entry.getWins())/Double.valueOf(entry.getWins()+entry.getLosses()))*100)+"%";
    }

    public static String getMastery(Summoner s, int nChamp){
        DecimalFormat df = new DecimalFormat("#,##0", 
        new DecimalFormatSymbols(Locale.US));
        String masteryString = "";
        int cont = 1;
        try {
            for(ChampionMastery mastery : s.getChampionMasteries()){
                if(cont == nChamp){
                    masteryString = "[" + mastery.getChampionLevel()+ "] " + riotApi.getDDragonAPI().getChampion(mastery.getChampionId()).getName() + " " + df.format(mastery.getChampionPoints()) + " points";
                    break;
                }
                cont++;
            }
            
        } catch (Exception e) { }
        return masteryString;
    }

    public static String getActivity(Summoner s){
        try {
            for(SpectatorParticipant partecipant : s.getCurrentGame().getParticipants()){
                if(partecipant.getSummonerId().equals(s.getSummonerId())){
                    return "Playing a " + s.getCurrentGame().getGameMode().prettyName()+ " as " + riotApi.getDDragonAPI().getChampion(partecipant.getChampionId()).getName(); 
                }
            }
        } catch (Exception e) {
            return "Not in a game";
        }
        return "Not in a game";
    }

      
      
}
