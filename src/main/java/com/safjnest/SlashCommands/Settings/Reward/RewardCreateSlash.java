package com.safjnest.SlashCommands.Settings.Reward;

import java.util.Arrays;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.safjnest.Bot;
import com.safjnest.Utilities.CommandsLoader;
import com.safjnest.Utilities.Guild.GuildData;
import com.safjnest.Utilities.Guild.Alert.AlertType;
import com.safjnest.Utilities.Guild.Alert.RewardData;

import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class RewardCreateSlash extends SlashCommand{

    public RewardCreateSlash(String father){
        this.name = this.getClass().getSimpleName().replace("Slash", "").replace(father, "").toLowerCase();
        this.help = new CommandsLoader().getString(name, "help", father.toLowerCase());
        this.cooldown = new CommandsLoader().getCooldown(this.name, father.toLowerCase());
        this.category = new Category(new CommandsLoader().getString(father.toLowerCase(), "category"));
        this.options = Arrays.asList(
            new OptionData(OptionType.STRING, "message", "Leave message", true),
            new OptionData(OptionType.INTEGER, "level", "Level to give the reward", true),
            new OptionData(OptionType.ROLE, "role", "Role to be given.", true),
            new OptionData(OptionType.BOOLEAN, "temporary", "If the role is temporary", false)
        );
    }

    @Override
    protected void execute(SlashCommandEvent event) {
        String message = event.getOption("message").getAsString();
        int level = event.getOption("level").getAsInt();
        String roleId = event.getOption("role").getAsRole().getId();
        boolean temporary = event.getOption("temporary") != null ? event.getOption("temporary").getAsBoolean() : false;
        
        String guildId = event.getGuild().getId();

        GuildData gs = Bot.getGuildData(guildId);

        RewardData reward = gs.getAlert(AlertType.REWARD, level);

        if(reward != null) {
            event.deferReply(true).addContent("A reward already exists for this level.").queue();
            return;
        }

        String[] roles = new String[]{roleId};

        RewardData newReward = RewardData.createRewardData(guildId, message, roles, level, temporary);
        
        if(newReward.getID() == 0) {
            event.deferReply(true).addContent("Something went wrong.").queue();
            return;
        }

        gs.getAlerts().put(newReward.getKey(), newReward);
        event.deferReply(true).addContent("Reward created.").queue();
    }
}