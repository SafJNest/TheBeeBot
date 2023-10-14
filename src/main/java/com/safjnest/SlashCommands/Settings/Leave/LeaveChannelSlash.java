package com.safjnest.SlashCommands.Settings.Leave;

import java.util.Arrays;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.safjnest.Utilities.CommandsLoader;
import com.safjnest.Utilities.SQL.DatabaseHandler;

import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class LeaveChannelSlash extends SlashCommand {

    public LeaveChannelSlash(String father) {
        this.name = this.getClass().getSimpleName().replace("Slash", "").replace(father, "").toLowerCase();
        this.help = new CommandsLoader().getString(name, "help", father.toLowerCase());
        this.cooldown = new CommandsLoader().getCooldown(this.name, father.toLowerCase());
        this.category = new Category(new CommandsLoader().getString(father.toLowerCase(), "category"));
        this.options = Arrays.asList(
            new OptionData(OptionType.CHANNEL, "channel", "The new leave channel (leave out to use the guild's system channel).", false)
                .setChannelTypes(ChannelType.TEXT)
        );
    }

    @Override
    protected void execute(SlashCommandEvent event) {
        String channelID;
        if(event.getOption("channel") != null) 
            channelID = event.getOption("channel").getAsString();
        else if(event.getGuild().getSystemChannel() != null) 
            channelID = event.getGuild().getSystemChannel().getId();
        else {
            event.deferReply(true).addContent("No channel specified and no system channel found.").queue();
            return;
        }

        String guildId = event.getGuild().getId();
        String botId = event.getJDA().getSelfUser().getId();

        if(!DatabaseHandler.hasLeave(guildId, botId)) {
            event.deferReply(true).addContent("This guild doesn't have a leave message. Use the create command.").queue();
            return;
        }

        if(!DatabaseHandler.updateLeaveChannel(guildId, botId, channelID)) {
            event.deferReply(true).addContent("Something went wrong.").queue();
            return;
        }
        event.deferReply(false).addContent("Changed leave channel.").queue();
    }
}