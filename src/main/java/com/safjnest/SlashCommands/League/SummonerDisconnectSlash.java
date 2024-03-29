package com.safjnest.SlashCommands.League;

import java.util.Arrays;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.safjnest.Utilities.CommandsLoader;
import com.safjnest.Utilities.LOL.RiotHandler;
import com.safjnest.Utilities.SQL.DatabaseHandler;
import com.safjnest.Utilities.SQL.QueryResult;
import com.safjnest.Utilities.SQL.ResultRow;

import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

/**
 * @author <a href="https://github.com/NeutronSun">NeutronSun</a>
 * @since 1.3
 */
public class SummonerDisconnectSlash extends SlashCommand {
    

    /**
     * Constructor
     */
    public SummonerDisconnectSlash(String father){
        this.name = this.getClass().getSimpleName().replace("Slash", "").replace(father, "").toLowerCase();
        this.help = new CommandsLoader().getString(name, "help", father.toLowerCase());
        this.cooldown = new CommandsLoader().getCooldown(this.name, father.toLowerCase());
        this.category = new Category(new CommandsLoader().getString(father.toLowerCase(), "category"));
        this.options = Arrays.asList(
            new OptionData(OptionType.STRING, "sum", "Name of the summoner you want to disconnect to the bot", true));
    }

    /**
     * This method is called every time a member executes the command.
     */
	@Override
	protected void execute(SlashCommandEvent event) {
        try {
            QueryResult accountIds = DatabaseHandler.getLOLAccountsByUserId(event.getMember().getId());
            if(accountIds == null){
                event.deferReply(false).addContent("You dont have a Riot account connected, for more information use /help setsummoner").queue();
                return;
            }
            for(ResultRow id : accountIds){
                if(RiotHandler.getSummonerByAccountId(id.get("account_id")).getName().equalsIgnoreCase(event.getOption("sum").getAsString())){;
                    DatabaseHandler.deleteLOLaccount(event.getMember().getId(), id.get("account_id"));
                    event.deferReply(false).addContent("Summoner removed").queue();
                    return;
                }
            }
            event.deferReply(false).addContent("Summoner not found").queue();
            return;
            
        } catch (Exception e) {
            e.printStackTrace();
        }

	}

}
