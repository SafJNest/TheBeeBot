package com.safjnest.SlashCommands.Audio.Soundboard;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.safjnest.Utilities.CommandsLoader;
import com.safjnest.Utilities.SQL.DatabaseHandler;

import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

/**
 * @author <a href="https://github.com/NeutronSun">NeutronSun</a>
 * @author <a href="https://github.com/Leon412">Leon412</a>
 * 
 * @since 3.0
 */
public class SoundboardAddSlash extends SlashCommand{
    private static final int maxSounds = 20;

    public SoundboardAddSlash(String father){
        this.name = this.getClass().getSimpleName().replace("Slash", "").replace(father, "").toLowerCase();
        this.help = new CommandsLoader().getString(name, "help", father.toLowerCase());
        this.cooldown = new CommandsLoader().getCooldown(this.name, father.toLowerCase());
        this.category = new Category(new CommandsLoader().getString(father.toLowerCase(), "category"));
        this.options = new ArrayList<>();
        this.options.add(new OptionData(OptionType.STRING, "name", "Soundboard to add the sound(s) to.", true).setAutoComplete(true));
        for(int i = 1; i <= maxSounds-1; i++) {
            this.options.add(new OptionData(OptionType.STRING, "sound-" + i, "Sound " + i, false).setAutoComplete(true));
        }
    }

	@Override
	protected void execute(SlashCommandEvent event) {
        Set<String> soundIDs = new HashSet<String>();
        for(OptionMapping option : event.getOptions())
            if(option != null && !option.getName().equals("name"))
                soundIDs.add(option.getAsString());

        if(soundIDs.isEmpty()) {
            event.deferReply(true).addContent("You need to insert at least a sound.").queue();
            return;
        }

        String soundboardName = event.getOption("name").getAsString();
        if(!DatabaseHandler.soundboardExists(soundboardName, event.getGuild().getId())) {
            event.deferReply(true).addContent("A soundboard with that name does not exist in this guild.").queue();
            return;
        }

        String soundboardID = event.getOption("name").getAsString();
        int soundCount = DatabaseHandler.getSoundInSoundboardCount(soundboardID);

        if(soundCount >= maxSounds) {
            event.deferReply(true).addContent("The soundboard is already full.").queue();
            return;
        }

        if(soundCount + soundIDs.size() >= maxSounds) {
            event.deferReply(true).addContent("Too many sounds, the soundboard contains " + soundCount + "/" + maxSounds + " sounds and you tried to add " + soundIDs.size() + " sounds.").queue();
            return;
        }

        DatabaseHandler.insertSoundsInSoundBoard(soundboardID, soundIDs.toArray(new String[0]));

        event.deferReply(false).addContent("Sound added correctly").queue();
    }    
}