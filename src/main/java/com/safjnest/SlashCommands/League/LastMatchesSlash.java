package com.safjnest.SlashCommands.League;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.safjnest.App;
import com.safjnest.Utilities.CommandsLoader;
import com.safjnest.Utilities.LOL.RiotHandler;

import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import no.stelar7.api.r4j.basic.constants.api.regions.LeagueShard;
import no.stelar7.api.r4j.basic.constants.api.regions.RegionShard;
import no.stelar7.api.r4j.impl.R4J;
import no.stelar7.api.r4j.pojo.lol.match.v5.MatchParticipant;

/**
 * @author <a href="https://github.com/NeutronSun">NeutronSun</a>
 * @since 1.3
 */
public class LastMatchesSlash extends SlashCommand {
    private R4J r;
    
    /**
     * Constructor
     */
    public LastMatchesSlash(){
        this.name = this.getClass().getSimpleName().replace("Slash", "").toLowerCase();
        this.aliases = new CommandsLoader().getArray(this.name, "alias");
        this.help = new CommandsLoader().getString(this.name, "help");
        this.cooldown = new CommandsLoader().getCooldown(this.name);
        this.category = new Category(new CommandsLoader().getString(this.name, "category"));
        this.arguments = new CommandsLoader().getString(this.name, "arguments");
        this.options = Arrays.asList(
            new OptionData(OptionType.INTEGER, "games", "Number of games to analyze", true)
                .setMinValue(1)
                .setMaxValue(20),
            new OptionData(OptionType.STRING, "user", "Name of the summoner you want to get information on", false),
            new OptionData(OptionType.STRING, "tag", "Tag of the summoner you want to get information on", false)
        );
        this.r = App.getRiotApi();
    }

    /**
     * This method is called every time a member executes the command.
     */
	@Override
	protected void execute(SlashCommandEvent event) {
        event.deferReply(false).queue();
        HashMap<String, Integer> played = new HashMap<>();
        int gamesToAnalyze = event.getOption("games").getAsInt();
        no.stelar7.api.r4j.pojo.lol.summoner.Summoner s = null;

         if(event.getOption("user") == null){
            s = RiotHandler.getSummonerFromDB(event.getUser().getId());
            if(s == null){
                event.getHook().editOriginal("You dont have a Riot account connected, check /help setUser (or write the name of a summoner).").queue();
                return;
            }
        }else{
            String name = event.getOption("user").getAsString();
            String tag = (event.getOption("user") != null) ? event.getOption("tag").getAsString() : "";
            s = RiotHandler.getSummonerByName(name, tag);
            if(s == null){
                event.reply("Couldn't find the specified summoner.");
                return;
            }
        }

        int gamesNumber = gamesToAnalyze;
        try {
            for(int i = 0; i < gamesToAnalyze; i++){
                String ss = s.getLeagueGames().get().get(i);
                try {
                    String mySide = "";  
                    for(MatchParticipant searchMe : r.getLoLAPI().getMatchAPI().getMatch(RegionShard.EUROPE, ss).getParticipants()){
                        if(searchMe.getSummonerId().equals(s.getSummonerId()))
                        mySide = searchMe.getTeam().commonName();
                    }
                    for(MatchParticipant sum : r.getLoLAPI().getMatchAPI().getMatch(RegionShard.EUROPE, ss).getParticipants()){
                        String sumId = sum.getSummonerId();
                        if(sum.getTeam().commonName().equals(mySide) && !sum.getSummonerId().equals(s.getSummonerId())){
                            if(!played.containsKey(sumId))
                                played.put(sumId, 0);
                            played.put(sumId, played.get(sumId)+1);

                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                   gamesNumber = gamesNumber - 1;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Map<String, Integer> sorted = sortByValue(played);
        boolean aloneLikePanslung = true;
        String message = "Analyzing last "+gamesNumber+" "+s.getName()+"'s games:\n";
        for(int i = sorted.keySet().size()-1; i>0; i--){
            String key = (String) sorted.keySet().toArray()[i];
            if(sorted.get(key) > 1){
                message+=r.getLoLAPI().getSummonerAPI().getSummonerById(LeagueShard.EUW1, key).getName() +" "+ (sorted.get(key)) + " times.\n";
                aloneLikePanslung = false;
            }
        }
        if(aloneLikePanslung){
            message = "No summoners they played more than one game with.";
        }

        event.getHook().editOriginal(message).queue();
	}

    public static HashMap<String, Integer>
    sortByValue(HashMap<String, Integer> hm)
    {
        // Create a list from elements of HashMap
        List<Map.Entry<String, Integer> > list
            = new LinkedList<Map.Entry<String, Integer> >(
                hm.entrySet());
 
        // Sort the list using lambda expression
        Collections.sort(
            list,
            (i1, i2) -> i1.getValue().compareTo(i2.getValue())
        );
 
        // put data from sorted list to hashmap
        HashMap<String, Integer> temp
            = new LinkedHashMap<String, Integer>();
        for (Map.Entry<String, Integer> aa : list) {
            temp.put(aa.getKey(), aa.getValue());
        }
        return temp;
    }

}
