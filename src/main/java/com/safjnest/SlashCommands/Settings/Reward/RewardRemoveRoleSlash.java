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

public class RewardRemoveRoleSlash extends SlashCommand {
    
    public RewardRemoveRoleSlash(String father){
        this.name = this.getClass().getSimpleName().replace("Slash", "").replace(father, "").toLowerCase();
        this.help = new CommandsLoader().getString(name, "help", father.toLowerCase());
        this.cooldown = new CommandsLoader().getCooldown(this.name, father.toLowerCase());
        this.category = new Category(new CommandsLoader().getString(father.toLowerCase(), "category"));
        this.options = Arrays.asList(
            new OptionData(OptionType.STRING, "reward_level", "Select the reward to change", true)
                .setAutoComplete(true),
            new OptionData(OptionType.STRING, "reward_roles", "Role to remove for the current reward", true)
                .setAutoComplete(true)
        );
    }

    @Override
    protected void execute(SlashCommandEvent event) {
        int rewardLevel = event.getOption("reward_level").getAsInt();
        String roleID = event.getOption("reward_roles") != null ? event.getOption("reward_roles").getAsString() : null;

        String guildId = event.getGuild().getId();

        GuildData gs = Bot.getGuildData(guildId);
        
        RewardData reward = (RewardData) gs.getAlert(AlertType.REWARD, rewardLevel);

        if(reward == null) {
            event.deferReply(true).addContent("There is no reward set for this level.").queue();
            return;
        }
        System.out.println(rewardLevel + " " + roleID);
        if (reward.getRoles() == null || !reward.getRoles().containsValue(roleID)) {
            event.deferReply(true).addContent("This role is not set as reward").queue();
            return;
        }

        if (reward.getRoles().size() == 1) {
            event.deferReply(true).addContent("A reward must have at least one role.").queue();
            return;
        }


        if(!reward.removeRole(roleID)) {
            event.deferReply(true).addContent("Something went wrong.").queue();
            return;
        }

        event.deferReply(false).addContent("Remove role as reward.").queue();
    }
}