package com.safjnest.SlashCommands.Misc;

import java.util.Arrays;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.safjnest.Utilities.CommandsLoader;

import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class SpecialCharSlash extends SlashCommand{

    public SpecialCharSlash(){
        this.name = this.getClass().getSimpleName().replace("Slash", "").toLowerCase();
        this.aliases = new CommandsLoader().getArray(this.name, "alias");
        this.help = new CommandsLoader().getString(this.name, "help");
        this.cooldown = new CommandsLoader().getCooldown(this.name);
        this.category = new Category(new CommandsLoader().getString(this.name, "category"));
        this.arguments = new CommandsLoader().getString(this.name, "arguments");
        this.options = Arrays.asList(
            new OptionData(OptionType.STRING, "command", "Name of the bugged command", true)
                .addChoice("`", "grave_accent")
                .addChoice("~", "tilde"));
    }

    @Override
    protected void execute(SlashCommandEvent event) {
        String command = event.getOption("command").getAsString();
        switch(command){
            case "grave_accent":
                event.deferReply(false).addContent("`").queue();
                break;
            case "tilde":
                event.deferReply(false).addContent("~").queue();
                break;
        }
    }
}
