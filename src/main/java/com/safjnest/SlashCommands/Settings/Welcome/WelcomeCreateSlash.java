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

public class WelcomeCreateSlash extends SlashCommand{

    public WelcomeCreateSlash(String father){
        this.name = this.getClass().getSimpleName().replace("Slash", "").replace(father, "").toLowerCase();
        this.help = new CommandsLoader().getString(name, "help", father.toLowerCase());
        this.cooldown = new CommandsLoader().getCooldown(this.name, father.toLowerCase());
        this.category = new Category(new CommandsLoader().getString(father.toLowerCase(), "category"));
        this.options = Arrays.asList(
            new OptionData(OptionType.STRING, "message", "Welcome message", true),
            new OptionData(OptionType.CHANNEL, "channel", "Welcome channel (leave out to use the guild's system channel).", false)
                .setChannelTypes(ChannelType.TEXT),
            new OptionData(OptionType.ROLE, "role", "Role that will be given to the new members.", false)
        );
    }

    @Override
    protected void execute(SlashCommandEvent event) {
        String welcomeText = event.getOption("message").getAsString();

        String channelID;
        if(event.getOption("channel") != null)
            channelID = event.getOption("channel").getAsString();
        else if(event.getGuild().getSystemChannel() != null)
            channelID = event.getGuild().getSystemChannel().getId();
        else{
            event.deferReply(true).addContent("No channel specified and no system channel found.").queue();
            return;
        }

        String roleID = event.getOption("role") != null ? event.getOption("role").getAsString() : null;

        String guildId = event.getGuild().getId();

        GuildData gs = Bot.getGuildData(guildId);

        AlertData welcome = gs.getAlert(AlertType.WELCOME);

        if(welcome != null) {
            event.deferReply(true).addContent("A welcome message already exists.").queue();
            return;
        }

        String[] roles = new String[]{roleID};

        AlertData newWelcome = new AlertData(guildId, welcomeText, channelID, roles);

        if(newWelcome.getID() == 0) {
            event.deferReply(true).addContent("Something went wrong.").queue();
            return;
        }

        gs.getAlerts().put(newWelcome.getKey(), newWelcome);
        event.deferReply(false).addContent("Welcome message created.").queue();
    }
}
