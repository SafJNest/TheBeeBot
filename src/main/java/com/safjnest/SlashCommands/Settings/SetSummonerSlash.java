package com.safjnest.SlashCommands.Settings;

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
import no.stelar7.api.r4j.basic.constants.api.regions.LeagueShard;
import no.stelar7.api.r4j.impl.R4J;

/**
 * @author <a href="https://github.com/NeutronSun">NeutronSun</a>
 * @since 1.3
 */
public class SetSummonerSlash extends SlashCommand {
    
    private R4J r;
    /**
     * Constructor
     */
    public SetSummonerSlash(R4J r){
        this.name = this.getClass().getSimpleName().replace("Slash", "").toLowerCase();
        this.aliases = new CommandsLoader().getArray(this.name, "alias");
        this.help = new CommandsLoader().getString(this.name, "help");
        this.cooldown = new CommandsLoader().getCooldown(this.name);
        this.category = new Category(new CommandsLoader().getString(this.name, "category"));
        this.arguments = new CommandsLoader().getString(this.name, "arguments");
        this.options = Arrays.asList(
            new OptionData(OptionType.STRING, "sum", "Name of the summoner you want to connect to the bot", true),
            new OptionData(OptionType.BOOLEAN, "remove", "If you want to remove a summoner instead of adding it", true));
        this.r = r;
    }

    /**
     * This method is called every time a member executes the command.
     */
	@Override
	protected void execute(SlashCommandEvent event) {
        try {
            if(event.getOption("remove").getAsBoolean()){
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
            }
            no.stelar7.api.r4j.pojo.lol.summoner.Summoner s = r.getLoLAPI().getSummonerAPI().getSummonerByName(LeagueShard.EUW1, event.getOption("sum").getAsString());

            DatabaseHandler.addLOLAccount(event.getMember().getId(), s.getSummonerId(), s.getAccountId());
            event.deferReply(false).addContent("Connected " + s.getName() + " to your profile.").queue();
        } catch (Exception e) {
            e.printStackTrace();
        }

	}

}
