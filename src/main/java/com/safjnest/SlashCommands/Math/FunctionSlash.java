package com.safjnest.SlashCommands.Math;

import java.util.Arrays;

import com.safjnest.Utilities.CommandsHandler;
import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;

import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;


/**
 * @author <a href="https://github.com/NeutronSun">NeutronSun</a>
 * 
 * @since 1.1
 */
public class FunctionSlash extends SlashCommand {

    public FunctionSlash() {
        this.name = this.getClass().getSimpleName().replace("Slash", "").toLowerCase();
        this.aliases = new CommandsHandler().getArray(this.name, "alias");
        this.help = new CommandsHandler().getString(this.name, "help");
        this.cooldown = new CommandsHandler().getCooldown(this.name);
        this.category = new Category(new CommandsHandler().getString(this.name, "category"));
        this.arguments = new CommandsHandler().getString(this.name, "arguments");
        this.options = Arrays.asList(
                new OptionData(OptionType.STRING, "function", "Function", true)
                        .addChoice("ln", "ln")
                        .addChoice("exp", "exp")
                        .addChoice("sin", "sin")
                        .addChoice("cos", "cos")
                        .addChoice("tan", "tan")
                        .addChoice("acos", "acos")
                        .addChoice("asin", "asin")
                        .addChoice("atan", "atan")
                        .addChoice("sinh", "sinh")
                        .addChoice("cosh", "cosh")
                        .addChoice("tanh", "tanh"),
                new OptionData(OptionType.NUMBER, "n", "Number", true));
    }

    @Override
    protected void execute(SlashCommandEvent event) {
        double n = event.getOption("n").getAsDouble();
        String fun = event.getOption("function").getAsString();
        double result = 0.0;
        switch (fun) {
            case "ln":
                if(n <= 0 ){
                    event.deferReply(false).addContent("Argument of logaritm must be positive.").queue();
                    return;
                }
                result = Math.log(n);
                break;
            case "exp":
                result = Math.exp(n);
                break;
            case "sin":
                result = Math.sin(n);
                break;
            case "cos":
                result = Math.cos(n);
                break;
            case "tan":
                result = Math.tan(n);
                break;
            case "asin":
                if(n > 1.0 || n < -1.0){
                    event.deferReply(false).addContent("Argument of asin must be between 1, -1.").queue();
                    return;
                }
                result = Math.asin(n);
                break;
            case "acos":
                if(n > 1.0 || n < -1.0){
                    event.deferReply(false).addContent("Argument of acos must be between 1, -1.").queue();
                    return;
                }
                result = Math.acos(n);
                break;
            case "atan":
                result = Math.atan(n);
                break;
            case "sinh":
                result = Math.sinh(n);
                break;
            case "cosh":
                result = Math.cosh(n);
                break;
            case "tanh":
                result = Math.tanh(n);
                break;
        }
        event.deferReply(false).addContent(String.valueOf(result)).queue();
    }
}