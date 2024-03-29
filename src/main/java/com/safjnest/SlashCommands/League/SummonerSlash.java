package com.safjnest.SlashCommands.League;

import java.util.ArrayList;
import java.util.Collections;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.safjnest.Utilities.CommandsLoader;;

/**
 * @author <a href="https://github.com/NeutronSun">NeutronSun</a>
 * @since 1.3
 */
public class SummonerSlash extends SlashCommand {
 
    public SummonerSlash(){
        this.name = this.getClass().getSimpleName().replace("Slash", "").toLowerCase();
        String father = this.getClass().getSimpleName().replace("Slash", "");

        this.cooldown = new CommandsLoader().getCooldown(this.name);
        this.category = new Category(new CommandsLoader().getString(this.name, "category"));
        this.help = new CommandsLoader().getString(this.name, "help");
        
        ArrayList<SlashCommand> slashCommandsList = new ArrayList<SlashCommand>();
        Collections.addAll(slashCommandsList, new SummonerConnectSlash(father), new SummonerInfoSlash(father), new SummonerDisconnectSlash(father));
        this.children = slashCommandsList.toArray(new SlashCommand[slashCommandsList.size()]);
    }

	@Override
	protected void execute(SlashCommandEvent event) {

	}

}
