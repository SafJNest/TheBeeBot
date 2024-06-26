package com.safjnest.SlashCommands.Settings.LevelUp;

import java.util.Arrays;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.safjnest.Utilities.CommandsLoader;
import com.safjnest.Utilities.Guild.GuildSettings;

import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class LevelUpModifierSlash extends SlashCommand{
    private GuildSettings gs;

    public LevelUpModifierSlash(String father, GuildSettings gs){
        this.gs = gs;
        this.name = this.getClass().getSimpleName().replace("Slash", "").replace(father, "").toLowerCase();
        this.help = new CommandsLoader().getString(this.name, "help", father.toLowerCase());
        this.cooldown = new CommandsLoader().getCooldown(this.name, father.toLowerCase());
        this.category = new Category(new CommandsLoader().getString(father.toLowerCase(), "category"));
        this.options = Arrays.asList(
            new OptionData(OptionType.CHANNEL, "channel", "Channel to change the level up modifier of", true)
                .setChannelTypes(ChannelType.TEXT),
            new OptionData(OptionType.NUMBER, "modifier", "The experience modifier (e.g. 0.6, 1.5, 2).", true)
                .setRequiredRange(0.0, 5.0)
        );
    }

    @Override
    protected void execute(SlashCommandEvent event) {
        String channelId = event.getOption("channel").getAsChannel().getId();
        double modifier = event.getOption("modifier").getAsDouble();

        String guildId = event.getGuild().getId();

        if(!gs.getServer(guildId).getChannelData(channelId).setExpModifier(modifier)) {
            event.deferReply(true).addContent("Something went wrong.").queue();
            return;
        }

        event.deferReply(false).addContent("Exp gain set to " + modifier + " times the normal amount in " + event.getGuild().getTextChannelById(channelId).getAsMention()).queue();
    }
}