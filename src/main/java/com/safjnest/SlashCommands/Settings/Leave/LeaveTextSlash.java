package com.safjnest.SlashCommands.Settings.Leave;

import java.util.Arrays;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.safjnest.Utilities.CommandsLoader;
import com.safjnest.Utilities.SQL.DatabaseHandler;

import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class LeaveTextSlash extends SlashCommand {
    
    public LeaveTextSlash(String father){
        this.name = this.getClass().getSimpleName().replace("Slash", "").replace(father, "").toLowerCase();
        this.help = new CommandsLoader().getString(name, "help", father.toLowerCase());
        this.cooldown = new CommandsLoader().getCooldown(this.name, father.toLowerCase());
        this.category = new Category(new CommandsLoader().getString(father.toLowerCase(), "category"));
        this.options = Arrays.asList(
            new OptionData(OptionType.STRING, "message", "Leave message", true)
        );
    }

    @Override
    protected void execute(SlashCommandEvent event) {
        String message = event.getOption("message") != null ? event.getOption("message").getAsString() : null;

        String guildId = event.getGuild().getId();
        String botId = event.getJDA().getSelfUser().getId();

        if(!DatabaseHandler.hasLeave(guildId, botId)) {
            event.deferReply(true).addContent("This guild doesn't have a leave message. Use the create command.").queue();
            return;
        }

        if(!DatabaseHandler.updateLeaveMessage(guildId, botId, message)) {
            event.deferReply(true).addContent("Something went wrong.").queue();
            return;
        }

        event.deferReply(false).addContent("Changed leave message.").queue();
    }
}