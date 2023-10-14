package com.safjnest.SlashCommands.Math;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import com.safjnest.Utilities.CommandsLoader;
import com.safjnest.Utilities.SafJNest;
import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;

import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.utils.FileUpload;

/**
 * @author <a href="https://github.com/NeutronSun">NeutronSun</a>
 * 
 * @since 1.1
 */
public class PrimeSlash extends SlashCommand {

    public PrimeSlash(int maxPrime){
        this.name = this.getClass().getSimpleName().replace("Slash", "").toLowerCase();
        this.aliases = new CommandsLoader().getArray(this.name, "alias");
        this.help = new CommandsLoader().getString(this.name, "help");
        this.cooldown = new CommandsLoader().getCooldown(this.name);
        this.category = new Category(new CommandsLoader().getString(this.name, "category"));
        this.arguments = new CommandsLoader().getString(this.name, "arguments");
        this.options = Arrays.asList(
            new OptionData(OptionType.INTEGER, "value", "Number of bits of the prime to generate", true)
                .setMinValue(2)
                .setMaxValue(maxPrime)
        );
    }

	@Override
	protected void execute(SlashCommandEvent event) {
        event.deferReply(false).queue();
        String primi = SafJNest.getRandomPrime(event.getOption("value").getAsInt()).toString();
        if (primi.length() > 2000) {
            event.getHook().editOriginal("The prime number is too big for discord, so here's a file:")
                .setFiles(FileUpload.fromData(
                    primi.getBytes(StandardCharsets.UTF_8),
                    "prime.txt"
                )
            ).queue();
        } 
        else {
            event.getHook().editOriginal(primi).queue();
        }    
	}
}