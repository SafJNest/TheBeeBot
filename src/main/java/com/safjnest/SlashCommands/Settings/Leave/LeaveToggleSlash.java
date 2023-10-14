package com.safjnest.SlashCommands.Settings.Leave;

import java.util.Arrays;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.safjnest.Utilities.CommandsLoader;
import com.safjnest.Utilities.SQL.DatabaseHandler;

import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class LeaveToggleSlash extends SlashCommand{

    public LeaveToggleSlash(String father){
        this.name = this.getClass().getSimpleName().replace("Slash", "").replace(father, "").toLowerCase();
        this.help = new CommandsLoader().getString(this.name, "help", father.toLowerCase());
        this.cooldown = new CommandsLoader().getCooldown(this.name, father.toLowerCase());
        this.category = new Category(new CommandsLoader().getString(father.toLowerCase(), "category"));
        this.options = Arrays.asList(
            new OptionData(OptionType.STRING, "toggle", "On or off.", true)
                .addChoice("on", "on")
                .addChoice("off", "off")
        );
    }

    @Override
    protected void execute(SlashCommandEvent event) {
        boolean toggle = event.getOption("toggle").getAsString().equalsIgnoreCase("on") ? true : false;

        String guildId = event.getGuild().getId();
        String botId = event.getJDA().getSelfUser().getId();

        if(!DatabaseHandler.hasLeave(guildId, botId)) {
            event.deferReply(true).addContent("This guild doesn't have a leave message.").queue();
            return;
        }

        if(!DatabaseHandler.toggleLeave(guildId, botId, toggle)) {
            event.deferReply(true).addContent("Something went wrong.").queue();
            return;
        }

        event.deferReply(false).addContent("Toggled leave message " + (toggle ? "on" : "off") + ".").queue();
    }
}