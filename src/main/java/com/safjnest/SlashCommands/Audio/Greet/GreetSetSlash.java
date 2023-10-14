package com.safjnest.SlashCommands.Audio.Greet;

import java.util.Arrays;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.safjnest.Utilities.CommandsLoader;
import com.safjnest.Utilities.SQL.DatabaseHandler;

import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

/**
 * @author <a href="https://github.com/NeutronSun">NeutronSun</a>
 * @author <a href="https://github.com/Leon412">Leon412</a>
 * 
 * @since 1.1
 */
public class GreetSetSlash extends SlashCommand {

    public GreetSetSlash(String father) {
        this.name = this.getClass().getSimpleName().replace("Slash", "").replace(father, "").toLowerCase();
        this.help = new CommandsLoader().getString(name, "help", father.toLowerCase());
        this.cooldown = new CommandsLoader().getCooldown(this.name, father.toLowerCase());
        this.category = new Category(new CommandsLoader().getString(father.toLowerCase(), "category"));
        this.options = Arrays.asList(
            new OptionData(OptionType.STRING, "sound", "Sound to use", true)
                .setAutoComplete(true),
            new OptionData(OptionType.BOOLEAN, "global", "true for global, false for guild only, false by default", false)
        );
    }

	@Override
	public void execute(SlashCommandEvent event) {
        String sound = event.getOption("sound").getAsString();

        String guildId = (event.getOption("global") != null && event.getOption("global").getAsBoolean()) ? "0" : event.getGuild().getId();

        DatabaseHandler.setGreet(event.getUser().getId(), guildId, event.getJDA().getSelfUser().getId(), sound);

        event.deferReply(false).addContent("Greet has been set").queue();
    }
}