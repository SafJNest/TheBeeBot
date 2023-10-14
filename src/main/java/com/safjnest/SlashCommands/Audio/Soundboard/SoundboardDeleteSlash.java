package com.safjnest.SlashCommands.Audio.Soundboard;

import java.util.Arrays;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.safjnest.Utilities.CommandsLoader;
import com.safjnest.Utilities.SQL.DatabaseHandler;

import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

/**
 * @author <a href="https://github.com/NeutronSun">NeutronSun</a>
 * 
 * @since 3.0
 */
public class SoundboardDeleteSlash extends SlashCommand{

    public SoundboardDeleteSlash(String father){
        this.name = this.getClass().getSimpleName().replace("Slash", "").replace(father, "").toLowerCase();
        this.help = new CommandsLoader().getString(name, "help", father.toLowerCase());
        this.cooldown = new CommandsLoader().getCooldown(this.name, father.toLowerCase());
        this.category = new Category(new CommandsLoader().getString(father.toLowerCase(), "category"));
        this.options = Arrays.asList(
            new OptionData(OptionType.STRING, "name", "Soundboard to delete", true).setAutoComplete(true)
        );
    }

	@Override
	protected void execute(SlashCommandEvent event) {
        String soundboardID = event.getOption("name").getAsString();
        
        DatabaseHandler.deleteSoundboard(soundboardID);
        
        event.deferReply(false).addContent("Soundboard deleted correctly.").queue();
    }    
}