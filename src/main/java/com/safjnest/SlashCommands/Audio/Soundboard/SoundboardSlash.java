package com.safjnest.SlashCommands.Audio.Soundboard;

import java.util.ArrayList;
import java.util.Collections;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.safjnest.Utilities.CommandsLoader;

/**
 * @author <a href="https://github.com/NeutronSun">NeutronSun</a>
 * 
 * @since 1.1
 */
public class SoundboardSlash extends SlashCommand{

    public SoundboardSlash(){
        this.name = this.getClass().getSimpleName().replace("Slash", "").toLowerCase();
        String father = this.getClass().getSimpleName().replace("Slash", "");

        this.cooldown = new CommandsLoader().getCooldown(this.name);
        this.category = new Category(new CommandsLoader().getString(this.name, "category"));
        this.help = new CommandsLoader().getString(this.name, "help");
        
        ArrayList<SlashCommand> slashCommandsList = new ArrayList<SlashCommand>();
        Collections.addAll(slashCommandsList, new SoundboardCreateSlash(father), new SoundboardSelectSlash(father), new SoundboardAddSlash(father), new SoundboardRemoveSlash(father), new SoundboardDeleteSlash(father));
        this.children = slashCommandsList.toArray(new SlashCommand[slashCommandsList.size()]);
    }

	@Override
	public void execute(SlashCommandEvent event) {
        
    }
}