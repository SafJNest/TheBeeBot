package com.safjnest.SlashCommands.Math;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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
            new OptionData(OptionType.INTEGER, "value", "Number of bits", true)
            .setMaxValue(maxPrime)
            .setMinValue(1));
    }

	@Override
	protected void execute(SlashCommandEvent event) {
        try {
                String primi = SafJNest.getFirstPrime(SafJNest.randomBighi(event.getOption("value").getAsInt()));
                if (primi.length() > 2000) {
                    File supp = new File("primi.txt");
                    FileWriter app;
                    try {
                        app = new FileWriter(supp);
                        app.write(primi);
                        app.flush();
                        app.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    event.deferReply(true).addContent("Your prime number is too insane for discord, we need a file to hold it").addFiles(FileUpload.fromData(supp)).queue();
                } else {
                    event.deferReply(false).addContent(primi).queue();
                }
            
        } catch (Exception e) {
            e.printStackTrace();
            
        }
	}
}