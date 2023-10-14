package com.safjnest.SlashCommands.Audio;

import java.util.Arrays;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.safjnest.Utilities.CommandsLoader;
import com.safjnest.Utilities.SQL.DatabaseHandler;
import com.safjnest.Utilities.SQL.ResultRow;

import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

/**
 * The command lets you modify one of your sounds.
 * 
 * @author <a href="https://github.com/NeutronSun">NeutronSun</a>
 * 
 * @since 1.3
 */
public class CustomizeSoundSlash extends SlashCommand {

    public CustomizeSoundSlash() {
        this.name = this.getClass().getSimpleName().replace("Slash", "").toLowerCase();
        this.aliases = new CommandsLoader().getArray(this.name, "alias");
        this.help = new CommandsLoader().getString(this.name, "help");
        this.cooldown = new CommandsLoader().getCooldown(this.name);
        this.category = new Category(new CommandsLoader().getString(this.name, "category"));
        this.arguments = new CommandsLoader().getString(this.name, "arguments");
        this.options = Arrays.asList(
            new OptionData(OptionType.STRING, "sound", "Sound to modify (name or id)", true).setAutoComplete(true),
            new OptionData(OptionType.STRING, "name", "New name of the sound", false),
            new OptionData(OptionType.BOOLEAN, "public", "true or false", false)
        );
    }

    @Override
    protected void execute(SlashCommandEvent event) {
        String fileName = event.getOption("sound").getAsString();

        ResultRow sound = fileName.matches("[0123456789]*") 
                           ? DatabaseHandler.getAuthorSoundById(fileName, event.getMember().getId()) 
                           : DatabaseHandler.getAuthorSoundByName(fileName, event.getMember().getId());

        if(sound == null) {
            event.reply("Couldn't find a sound with that name/id (you can only change one of your sounds).");
            return;
        }

        String newName = (event.getOption("name") != null) ? event.getOption("name").getAsString() : sound.get("name");
        boolean newPublic = (event.getOption("public") != null) ? event.getOption("public").getAsBoolean() : sound.getAsBoolean("public");

        DatabaseHandler.updateSound(sound.get("id"), newName, newPublic);

        String locket = sound.getAsBoolean("public") ? ":unlock:" : ":lock:";
        String newLocket = newPublic ? ":unlock:" : ":lock:";

        event.deferReply(false).addContent(sound.get("name") + " (ID: " + sound.get("id") + ") " + locket +" has been modified to: " + name + " (ID: " + sound.get("id") + ") " + newLocket).queue();
    }
}
