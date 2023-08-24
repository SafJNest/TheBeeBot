package com.safjnest.SlashCommands.Settings.Leave;

import java.util.Arrays;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.safjnest.Utilities.CommandsLoader;
import com.safjnest.Utilities.DatabaseHandler;

import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class LeaveMoveSlash extends SlashCommand {

    public LeaveMoveSlash(String father) {
        this.name = this.getClass().getSimpleName().replace("Slash", "").replace(father, "").toLowerCase();
        this.help = new CommandsLoader().getString(name, "help", father.toLowerCase());
        this.cooldown = new CommandsLoader().getCooldown(this.name, father.toLowerCase());
        this.category = new Category(new CommandsLoader().getString(father.toLowerCase(), "category"));
        this.options = Arrays.asList(
                new OptionData(OptionType.CHANNEL, "channel",
                        "Set a different channel where the message will be sent. Null to use default channel.", false)
                        .setChannelTypes(ChannelType.TEXT));
    }

    @Override
    protected void execute(SlashCommandEvent event) {
        String channel = null;
        if (event.getOption("channel") == null) {
            try {
                channel = event.getGuild().getSystemChannel().getId();
            } catch (Exception e) {
                event.deferReply(true).addContent(
                        "No channel was selected and there isn't a system channel (check your server discord settings). Be sure to select a channel next time.")
                        .queue();
                return;
            }
        } else {
            channel = event.getOption("channel").getAsChannel().getId();
        }

        String discordId = event.getGuild().getId();
        String query = "UPDATE left_message SET channel_id = '" + channel + "' WHERE guild_id = '" + discordId
                + "' AND bot_id = '" + event.getJDA().getSelfUser().getId() + "';";
        DatabaseHandler.getSql().runQuery(query);
        event.deferReply(false).addContent("All set correctly").queue();
    }

}
