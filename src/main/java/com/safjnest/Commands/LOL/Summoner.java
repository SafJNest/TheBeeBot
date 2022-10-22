package com.safjnest.Commands.LOL;

import java.awt.Color;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.safjnest.App;
import com.safjnest.Utilities.CommandsHandler;
/* 
import net.rithms.riot.constant.Region;
import net.rithms.riot.dto.Summoner.Summoner;
import net.rithms.riot.api.RiotApi;
import net.rithms.riot.api.RiotApiException;
*/
import com.safjnest.Utilities.PostgreSQL;

import net.dv8tion.jda.api.EmbedBuilder;
import no.stelar7.api.r4j.basic.constants.api.regions.LeagueShard;
import no.stelar7.api.r4j.impl.R4J;
import no.stelar7.api.r4j.pojo.lol.championmastery.ChampionMastery;
import no.stelar7.api.r4j.pojo.lol.league.LeagueEntry;
import no.stelar7.api.r4j.pojo.lol.spectator.SpectatorParticipant;

/**
 * @author <a href="https://github.com/NeutronSun">NeutronSun</a>
 * @since 1.3
 */
public class Summoner extends Command {
    
    private R4J r;
    private PostgreSQL sql;
    /**
     * Constructor
     */
    public Summoner(R4J r, PostgreSQL sql){
        this.name = this.getClass().getSimpleName();
        this.aliases = new CommandsHandler().getArray(this.name, "alias");
        this.help = new CommandsHandler().getString(this.name, "help");
        this.cooldown = new CommandsHandler().getCooldown(this.name);
        this.category = new Category(new CommandsHandler().getString(this.name, "category"));
        this.arguments = new CommandsHandler().getString(this.name, "arguments");
        this.r = r;
        this.sql = sql;
    }

    /**
     * This method is called every time a member executes the command.
     */
	@Override
	protected void execute(CommandEvent event) {
        String args = event.getArgs();
        no.stelar7.api.r4j.pojo.lol.summoner.Summoner s = null;
        if(args.equals("")){
            String query = "SELECT account_id FROM lol_user WHERE discord_id = '" + event.getAuthor().getId() + "';";
            try {
                s = r.getLoLAPI().getSummonerAPI().getSummonerByAccount(LeagueShard.EUW1, sql.getString(query, "account_id"));
            } catch (Exception e) {
               event.reply("You dont have connected your Riot account.");
               return;
            }
        }
        else if(event.getMessage().getMentions().getMembers().size() != 0){
            String query = "SELECT account_id FROM lol_user WHERE discord_id = '" + event.getMessage().getMentions().getMembers().get(0).getId() + "';";
            try {
                s = r.getLoLAPI().getSummonerAPI().getSummonerByAccount(LeagueShard.EUW1, sql.getString(query, "account_id"));
            } catch (Exception e) {
                event.reply(event.getMessage().getMentions().getMembers().get(0).getEffectiveName() + " has not connected his Riot account.");
                return;
            }
        }else{
            s = r.getLoLAPI().getSummonerAPI().getSummonerByName(LeagueShard.EUW1, args);
        }
        
        try {
            EmbedBuilder builder = new EmbedBuilder();
            builder.setAuthor(s.getName());
            builder.setColor(Color.decode(App.color));
            builder.setThumbnail("https://ddragon.leagueoflegends.com/cdn/12.16.1/img/profileicon/"+s.getProfileIconId()+".png");
            builder.addField("Level:", String.valueOf(s.getSummonerLevel()), false);
            
            for(int i = 0; i < 2; i++){
                try {
                    LeagueEntry entry = r.getLoLAPI().getLeagueAPI().getLeagueEntries(LeagueShard.EUW1, s.getSummonerId()).get(i);
                    builder.addField(entry.getQueueType().commonName(), 
                    entry.getTier() + " " + entry.getRank()+ " " +String.valueOf(entry.getLeaguePoints()) + " LP\n"
                    + entry.getWins() + "W/"+entry.getLosses()+"L\n"
                    + "Winrate:" + Math.ceil((Double.valueOf(entry.getWins())/Double.valueOf(entry.getWins()+entry.getLosses()))*100)+"%", true); 
                } catch (Exception e) {
                    if(i == 0){
                        builder.addField("5v5 Ranked Solo", "Unranked", true);
                        builder.addField("5v5 Ranked Flex Queue", "Unranked", true);
                        break;
                    }else{
                        String sup = r.getLoLAPI().getLeagueAPI().getLeagueEntries(LeagueShard.EUW1, s.getSummonerId()).get(0).getQueueType().commonName();
                        builder.addField((sup.equals("5v5 Ranked Solo"))?"5v5 Ranked Flex Queue":"5v5 Ranked Solo", "Unranked", true);
                    }
                }
            }
            String masteryString = "";
            int cont = 0;
            DecimalFormat df = new DecimalFormat("#,##0", 
            new DecimalFormatSymbols(Locale.US));
            try {
                for(ChampionMastery mastery : s.getChampionMasteries()){
                    masteryString += "[" + mastery.getChampionLevel()+ "] " + r.getDDragonAPI().getChampion(mastery.getChampionId()).getName() + " " + df.format(mastery.getChampionPoints()) + " points\n";
                    if(cont == 2)
                        break;
                    cont++;
                }
                builder.addField("Top 3 Champ", masteryString, false);
                
            } catch (Exception e) {
                e.printStackTrace();
            }
            String activity = "";
            try {
                for(SpectatorParticipant partecipant : s.getCurrentGame().getParticipants()){
                    if(partecipant.getSummonerId().equals(s.getSummonerId())){
                        activity = "Playing a " + s.getCurrentGame().getGameMode().name()+ " as " + r.getDDragonAPI().getChampion(partecipant.getChampionId()).getName();
                        break;
                    }
                }
            } catch (Exception e) {
                activity = "Not in a game";
            }
            builder.addField("Activity", activity, true);
            event.reply(builder.build());
            
        } catch (Exception e) {
            e.printStackTrace();
        }

	}

}
