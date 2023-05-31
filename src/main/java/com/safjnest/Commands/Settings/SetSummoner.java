package com.safjnest.Commands.Settings;


import java.util.ArrayList;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
/* 
import net.rithms.riot.constant.Region;
import net.rithms.riot.dto.Summoner.Summoner;
import net.rithms.riot.api.RiotApi;
import net.rithms.riot.api.RiotApiException;
*/
import com.safjnest.Utilities.SQL;
import com.safjnest.Utilities.Commands.CommandsHandler;
import com.safjnest.Utilities.LOL.LOLHandler;

import no.stelar7.api.r4j.basic.constants.api.regions.LeagueShard;
import no.stelar7.api.r4j.impl.R4J;

/**
 * @author <a href="https://github.com/NeutronSun">NeutronSun</a>
 * @since 1.3
 */
public class SetSummoner extends Command {
    
    private R4J r;
    private SQL sql;
    /**
     * Constructor
     */
    public SetSummoner(R4J r, SQL sql){
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
        if(event.getArgs().equals("")){
            event.reply("You need to specify a summoner name");
            return;
        }
        if(event.getArgs().startsWith("remove:")){
            String summonerName = event.getArgs().replace("remove:", "");
            String query = "SELECT account_id FROM lol_user WHERE discord_id = '" + event.getAuthor().getId() + "';";
            ArrayList<String> accountIds = sql.getAllRowsSpecifiedColumn(query, "account_id");
            if(accountIds == null){
                event.reply("You dont have a Riot account connected, for more information /help setsummoner");
                return;
            }
            for(String id : accountIds){
                if(LOLHandler.getSummonerByAccountId(id).getName().equalsIgnoreCase(summonerName)){
                    query = "DELETE FROM lol_user WHERE account_id = '" + id + "' and discord_id = '" + event.getMember().getId() + "';";
                    sql.runQuery(query);
                    event.reply("Summoner removed");
                    return;
                }
            }
            event.reply("Summoner not found");
            return;
        }
        String args = event.getArgs();
        no.stelar7.api.r4j.pojo.lol.summoner.Summoner s = r.getLoLAPI().getSummonerAPI().getSummonerByName(LeagueShard.EUW1, args);
        try {
            String query = "INSERT INTO lol_user(discord_id, summoner_id, account_id)"
                    + "VALUES('"+event.getAuthor().getId()+"','"+s.getSummonerId()+"','"+s.getAccountId()+"');";
            sql.runQuery(query);
            event.reply("Connected " + s.getName() + " to your profile.");
        } catch (Exception e) {
            e.printStackTrace();
        }


	}

}
