package com.safjnest.SlashCommands.League;

import java.util.Arrays;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.safjnest.Utilities.CommandsLoader;
import com.safjnest.Utilities.LOL.RiotHandler;
import com.safjnest.Utilities.SQL.DatabaseHandler;

import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import no.stelar7.api.r4j.basic.constants.api.regions.LeagueShard;
import no.stelar7.api.r4j.impl.R4J;

/**
 * @author <a href="https://github.com/NeutronSun">NeutronSun</a>
 * @since 1.3
 */
public class SummonerConnectSlash extends SlashCommand {
    
    private R4J r;
    /**
     * Constructor
     */
    public SummonerConnectSlash(String father){
        this.name = this.getClass().getSimpleName().replace("Slash", "").replace(father, "").toLowerCase();
        this.help = new CommandsLoader().getString(name, "help", father.toLowerCase());
        this.cooldown = new CommandsLoader().getCooldown(this.name, father.toLowerCase());
        this.category = new Category(new CommandsLoader().getString(father.toLowerCase(), "category"));
        this.options = Arrays.asList(
            new OptionData(OptionType.STRING, "sum", "Name of the summoner you want to connect to the bot", true));
        this.r = RiotHandler.getRiotApi();
    }

    /**
     * This method is called every time a member executes the command.
     */
	@Override
	protected void execute(SlashCommandEvent event) {
        try {
            no.stelar7.api.r4j.pojo.lol.summoner.Summoner s = r.getLoLAPI().getSummonerAPI().getSummonerByName(LeagueShard.EUW1, event.getOption("sum").getAsString());
            DatabaseHandler.addLOLAccount(event.getMember().getId(), s.getSummonerId(), s.getAccountId());
            event.deferReply(false).addContent("Connected " + s.getName() + " to your profile.").queue();
        } catch (Exception e) {
            e.printStackTrace();
        }

	}

}
