package com.safjnest.Commands.LOL;

import java.awt.Color;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.safjnest.Utilities.CommandsHandler;
import com.safjnest.Utilities.SQL;
import com.safjnest.Utilities.Bot.BotSettingsHandler;
import com.safjnest.Utilities.LOL.LOLHandler;

import net.dv8tion.jda.api.EmbedBuilder;
import no.stelar7.api.r4j.basic.constants.api.regions.LeagueShard;
import no.stelar7.api.r4j.basic.constants.types.lol.TeamType;
import no.stelar7.api.r4j.impl.R4J;
import no.stelar7.api.r4j.pojo.lol.spectator.SpectatorParticipant;

/**
 * @author <a href="https://github.com/NeutronSun">NeutronSun</a>
 * @since 1.3
 */
public class RankMatch extends Command {
    private R4J r;
    private SQL sql;
    /**
     * Constructor
     */
    public RankMatch(R4J r, SQL sql  ){
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
        if(!args.equals("")){
            s = r.getLoLAPI().getSummonerAPI().getSummonerByName(LeagueShard.EUW1, args);
        }else{
            String query = "SELECT account_id FROM lol_user WHERE discord_id = '" + event.getAuthor().getId() + "';";
            s = r.getLoLAPI().getSummonerAPI().getSummonerByAccount(LeagueShard.EUW1, sql.getString(query, "account_id"));
        }
        try {
            EmbedBuilder builder = new EmbedBuilder();
            builder.setTitle(s.getName() + "'s Game");
            builder.setColor(Color.decode(
                BotSettingsHandler.map.get(event.getJDA().getSelfUser().getId()).color
            ));
            builder.setThumbnail("https://ddragon.leagueoflegends.com/cdn/12.16.1/img/profileicon/"+s.getProfileIconId()+".png");
            String blueSide = "";
            String redSide = "";
            for(SpectatorParticipant partecipant : s.getCurrentGame().getParticipants()){
                String sum = partecipant.getSummonerName();
                String stats = LOLHandler.getSoloQStats(LOLHandler.getSummonerById(partecipant.getSummonerId()));
                stats = stats.substring(0, stats.lastIndexOf("P")+1) + " | " +stats.substring(stats.lastIndexOf(":")+1);
                if(partecipant.getTeam() == TeamType.BLUE)
                    blueSide += "**" + sum + "** " + stats+ "\n";
                else
                    redSide += "**" + sum + "** " + stats+ "\n";
                
            }
            builder.addField("**BLUE SIDE**", blueSide, false);
            builder.addField("**RED SIDE**", redSide, true);
            event.reply(builder.build());
            
        } catch (Exception e) {
            event.reply(s.getName() + " is not in a match.");
        }
	}

}
