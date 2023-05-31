package com.safjnest.SlashCommands.Settings;


import java.util.ArrayList;
import java.util.Arrays;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
/* 
import net.rithms.riot.constant.Region;
import net.rithms.riot.dto.Summoner.Summoner;
import net.rithms.riot.api.RiotApi;
import net.rithms.riot.api.RiotApiException;
*/
import com.safjnest.Utilities.SQL;
import com.safjnest.Utilities.Commands.CommandsHandler;
import com.safjnest.Utilities.LOL.LOLHandler;

import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import no.stelar7.api.r4j.basic.constants.api.regions.LeagueShard;
import no.stelar7.api.r4j.impl.R4J;

/**
 * @author <a href="https://github.com/NeutronSun">NeutronSun</a>
 * @since 1.3
 */
public class SetSummonerSlash extends SlashCommand {
    
    private R4J r;
    private SQL sql;
    /**
     * Constructor
     */
    public SetSummonerSlash(R4J r, SQL sql){
        this.name = this.getClass().getSimpleName().replace("Slash", "").toLowerCase();
        this.aliases = new CommandsHandler().getArray(this.name, "alias");
        this.help = new CommandsHandler().getString(this.name, "help");
        this.cooldown = new CommandsHandler().getCooldown(this.name);
        this.category = new Category(new CommandsHandler().getString(this.name, "category"));
        this.arguments = new CommandsHandler().getString(this.name, "arguments");
        this.options = Arrays.asList(
            new OptionData(OptionType.STRING, "sum", "Summoner name you want to connect to your profile", true),
            new OptionData(OptionType.BOOLEAN, "remove", "If you want to remove a summoner", true));
        this.r = r;
        this.sql = sql;
    }

    /**
     * This method is called every time a member executes the command.
     */
	@Override
	protected void execute(SlashCommandEvent event) {
        try {
            if(event.getOption("remove").getAsBoolean()){
                String query = "SELECT account_id FROM lol_user WHERE discord_id = '" + event.getMember().getId() + "';";
                ArrayList<String> accountIds = sql.getAllRowsSpecifiedColumn(query, "account_id");
                if(accountIds == null){
                    event.deferReply(false).addContent("You dont have a Riot account connected, for more information /help setsummoner").queue();
                    return;
                }
                for(String id : accountIds){
                    if(LOLHandler.getSummonerByAccountId(id).getName().equalsIgnoreCase(event.getOption("sum").getAsString())){
                        query = "DELETE FROM lol_user WHERE account_id = '" + id + "' and discord_id = '" + event.getMember().getId() + "';";
                        sql.runQuery(query);
                        event.deferReply(false).addContent("Summoner removed").queue();
                        return;
                    }
                }
                event.deferReply(false).addContent("Summoner not found").queue();
                return;
            }
            no.stelar7.api.r4j.pojo.lol.summoner.Summoner s = r.getLoLAPI().getSummonerAPI().getSummonerByName(LeagueShard.EUW1, event.getOption("sum").getAsString());
            String query = "INSERT INTO lol_user(discord_id, summoner_id, account_id)"
                    + "VALUES('"+event.getMember().getId()+"','"+s.getSummonerId()+"','"+s.getAccountId()+"');";
            sql.runQuery(query);
            event.deferReply(false).addContent("Connected " + s.getName() + " to your profile.").queue();
        } catch (Exception e) {
            e.printStackTrace();
        }

	}

}
