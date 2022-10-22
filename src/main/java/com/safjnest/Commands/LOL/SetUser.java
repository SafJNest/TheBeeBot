package com.safjnest.Commands.LOL;


import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.safjnest.Utilities.CommandsHandler;
/* 
import net.rithms.riot.constant.Region;
import net.rithms.riot.dto.Summoner.Summoner;
import net.rithms.riot.api.RiotApi;
import net.rithms.riot.api.RiotApiException;
*/
import com.safjnest.Utilities.PostgreSQL;

import no.stelar7.api.r4j.basic.constants.api.regions.LeagueShard;
import no.stelar7.api.r4j.impl.R4J;

/**
 * @author <a href="https://github.com/NeutronSun">NeutronSun</a>
 * @since 1.3
 */
public class SetUser extends Command {
    
    private R4J r;
    private PostgreSQL sql;
    /**
     * Constructor
     */
    public SetUser(R4J r, PostgreSQL sql){
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
        no.stelar7.api.r4j.pojo.lol.summoner.Summoner s = r.getLoLAPI().getSummonerAPI().getSummonerByName(LeagueShard.EUW1, args);
        try {
            String query = "INSERT INTO LOL_user(discord_id, summoner_id, account_id, sum_name)"
                    + "VALUES('"+event.getAuthor().getId()+"','"+s.getSummonerId()+"','"+s.getAccountId()+"','"+s.getName()+"');";
            sql.runQuery(query);
        } catch (Exception e) {
            e.printStackTrace();
        }

	}

}
