package com.safjnest.SlashCommands.Settings.Welcome;

import java.util.Arrays;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.safjnest.Bot;
import com.safjnest.Utilities.CommandsLoader;
import com.safjnest.Utilities.Guild.GuildData;
import com.safjnest.Utilities.Guild.Alert.AlertData;
import com.safjnest.Utilities.Guild.Alert.AlertType;

import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class WelcomeChannelSlash extends SlashCommand{

    public WelcomeChannelSlash(String father) {
        this.name = this.getClass().getSimpleName().replace("Slash", "").replace(father, "").toLowerCase();
        this.help = new CommandsLoader().getString(this.name, "help", father.toLowerCase());
        this.cooldown = new CommandsLoader().getCooldown(this.name, father.toLowerCase());
        this.category = new Category(new CommandsLoader().getString(father.toLowerCase(), "category"));
        this.options = Arrays.asList(
            new OptionData(OptionType.CHANNEL, "channel", "The new welcome channel (leave out to use the guild's system channel).", false)
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

        GuildData gs = Bot.getGuildData(guildId);

        AlertData welcome = gs.getAlert(AlertType.WELCOME);

        if(welcome == null) {
            event.deferReply(true).addContent("This guild doesn't have a welcome message. Use the create command.").queue();
            return;
        }

        if (!welcome.setAlertChannel(channelID)) {
            event.deferReply(true).addContent("Something went wrong.").queue();
            return;
        }
        event.deferReply(false).addContent("Changed welcome channel.").queue();
    }
}
