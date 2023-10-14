package com.safjnest.SlashCommands.Settings.Leave;

import java.util.Arrays;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.safjnest.Utilities.CommandsLoader;
import com.safjnest.Utilities.SQL.DatabaseHandler;

import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class LeaveCreateSlash extends SlashCommand{

    public LeaveCreateSlash(String father){
        this.name = this.getClass().getSimpleName().replace("Slash", "").replace(father, "").toLowerCase();
        this.help = new CommandsLoader().getString(name, "help", father.toLowerCase());
        this.cooldown = new CommandsLoader().getCooldown(this.name, father.toLowerCase());
        this.category = new Category(new CommandsLoader().getString(father.toLowerCase(), "category"));
        this.options = Arrays.asList(
            new OptionData(OptionType.STRING, "message", "Leave message", true),
            new OptionData(OptionType.CHANNEL, "channel", "Leave channel (leave out to use the guild's system channel).", false)
                .setChannelTypes(ChannelType.TEXT)
        );
    }

    @Override
    protected void execute(SlashCommandEvent event) {
        String leaveText = event.getOption("message").getAsString();
        
        String channelID;
        if(event.getOption("channel") != null) 
            channelID = event.getOption("channel").getAsString();
        else if(event.getGuild().getSystemChannel() != null) 
            channelID = event.getGuild().getSystemChannel().getId();
        else{
            event.deferReply(true).addContent("No channel specified and no system channel found.").queue();
            return;
        }

        String guildId = event.getGuild().getId();
        String botId = event.getJDA().getSelfUser().getId();

        if(DatabaseHandler.hasLeave(guildId, botId)) {
            event.deferReply(true).addContent("A leave message already exists.").queue();
            return;
        }

        if(!DatabaseHandler.setLeave(guildId, botId, channelID, leaveText)) {
            event.deferReply(true).addContent("Something went wrong.").queue();
            return;
        }

        event.deferReply(false).addContent("leave message created.").queue();
    }
}