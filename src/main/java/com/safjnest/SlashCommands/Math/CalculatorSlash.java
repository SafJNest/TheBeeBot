package com.safjnest.SlashCommands.Math;

import java.util.Arrays;

import com.safjnest.Commands.Math.Calculator;
import com.safjnest.Utilities.CommandsLoader;
import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;

import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;


/**
 * @author <a href="https://github.com/NeutronSun">NeutronSun</a>
 * 
 * @since 1.1
 */
public class CalculatorSlash extends SlashCommand {

    public CalculatorSlash() {
        this.name = this.getClass().getSimpleName().replace("Slash", "").toLowerCase();
        this.aliases = new CommandsLoader().getArray(this.name, "alias");
        this.help = new CommandsLoader().getString(this.name, "help");
        this.cooldown = new CommandsLoader().getCooldown(this.name);
        this.category = new Category(new CommandsLoader().getString(this.name, "category"));
        this.arguments = new CommandsLoader().getString(this.name, "arguments");
        this.options = Arrays.asList(
                new OptionData(OptionType.STRING, "n", "Number", true));
    }

    @Override
    protected void execute(SlashCommandEvent event) {
        event.deferReply(false).addContent(String.valueOf(Calculator.evaluateExpression(event.getOption("n").getAsString()))).queue();
    }

    
}