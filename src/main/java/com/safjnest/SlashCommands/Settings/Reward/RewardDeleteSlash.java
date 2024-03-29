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

public class RewardDeleteSlash extends SlashCommand {
    
    public RewardDeleteSlash(String father){
        this.name = this.getClass().getSimpleName().replace("Slash", "").replace(father, "").toLowerCase();
        this.help = new CommandsLoader().getString(name, "help", father.toLowerCase());
        this.cooldown = new CommandsLoader().getCooldown(this.name, father.toLowerCase());
        this.category = new Category(new CommandsLoader().getString(father.toLowerCase(), "category"));
        this.options = Arrays.asList(
            new OptionData(OptionType.STRING, "reward_level", "Select the reward to delete", true)
                .setAutoComplete(true)
        );
    }

    @Override
    protected void execute(SlashCommandEvent event) {
        int rewardLevel = event.getOption("reward_level").getAsInt();

        String guildId = event.getGuild().getId();

        GuildData gs = Bot.getGuildData(guildId);
        
        RewardData reward = (RewardData) gs.getAlert(AlertType.REWARD, rewardLevel);

        if(reward == null) {
            event.deferReply(true).addContent("There is no reward set for this level.").queue();
            return;
        }

        if(!gs.deleteAlert(reward.getType(), rewardLevel)) {
            event.deferReply(true).addContent("Something went wrong.").queue();
            return;
        }

        event.deferReply(false).addContent("The reward has been deleted").queue();
    }
}