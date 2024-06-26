package com.safjnest.SlashCommands.Settings.LevelUp;

import java.util.Arrays;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.safjnest.Utilities.CommandsLoader;
import com.safjnest.Utilities.Guild.GuildSettings;

import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class LevelUpChannelToggleSlash extends SlashCommand{
    private GuildSettings gs;

    public LevelUpChannelToggleSlash(String father, GuildSettings gs){
        this.gs = gs;
        this.name = this.getClass().getSimpleName().replace("Slash", "").replace(father, "").toLowerCase();
        this.help = new CommandsLoader().getString(this.name, "help", father.toLowerCase());
        this.cooldown = new CommandsLoader().getCooldown(this.name, father.toLowerCase());
        this.category = new Category(new CommandsLoader().getString(father.toLowerCase(), "category"));
        this.options = Arrays.asList(
            new OptionData(OptionType.CHANNEL, "channel", "Channel to enable/disable exp gain", true)
                .setChannelTypes(ChannelType.TEXT),
            new OptionData(OptionType.STRING, "toggle", "on or off", true)
                .addChoice("on", "on")
                .addChoice("off", "off")
        );
    }

    @Override
    protected void execute(SlashCommandEvent event) {
        String channelId = event.getOption("channel").getAsChannel().getId();
        boolean toggle = event.getOption("toggle").getAsString().equalsIgnoreCase("on") ? true : false;

        String guildId = event.getGuild().getId();

        if(!gs.getServer(guildId).getChannelData(channelId).setExpEnabled(toggle)) {
            event.deferReply(true).addContent("Something went wrong.").queue();
            return;
        }

        if(!toggle){
            event.deferReply(false).addContent("This channel no longer gives exp.").queue();
            return;
        }
        event.deferReply(false).addContent("This channel gives exp now.").queue();
    }
}