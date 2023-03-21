package com.safjnest.SlashCommands.Math;

import java.util.Arrays;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.safjnest.Utilities.CommandsHandler;

import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

/**
 * @author <a href="https://github.com/NeutronSun">NeutronSun</a>
 * @author <a href="https://github.com/Leon412">Leon412</a>
 * 
 * @since 1.0
 */
public class DiceSlash extends SlashCommand {

    public DiceSlash(){
        this.name = this.getClass().getSimpleName().replace("Slash", "").toLowerCase();
        this.aliases = new CommandsHandler().getArray(this.name, "alias");
        this.help = new CommandsHandler().getString(this.name, "help");
        this.cooldown = new CommandsHandler().getCooldown(this.name);
        this.category = new Category(new CommandsHandler().getString(this.name, "category"));
        this.arguments = new CommandsHandler().getString(this.name, "arguments");
        this.options = Arrays.asList(
            new OptionData(OptionType.INTEGER, "nface", "Number of faces", false)
                .setMinValue(2),
            new OptionData(OptionType.INTEGER, "ndice", "Number of dices", false)
                .setMinValue(1));
    }

	@Override
	protected void execute(SlashCommandEvent event) {
        int sum = 0;
        int ndice = (event.getOption("ndice") == null) ? 1 :  event.getOption("ndice").getAsInt();
        int nface = (event.getOption("nface") == null) ? 6 :  event.getOption("ndice").getAsInt();
        for(int i = 0; i < ndice; i++)
            sum+=(int)(Math.random() * nface) + 1;
        
        event.deferReply(false).addContent((ndice == 1)
                    ? "Rolled a " + nface + "-sided dice: " + sum        
                    : "Rolled " + ndice +" "+nface + "-sided dices: " + sum).queue();
	}
}