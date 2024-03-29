package com.safjnest.SlashCommands.Settings.LevelUp;

import java.util.Arrays;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.safjnest.Utilities.CommandsLoader;
import com.safjnest.Utilities.Guild.GuildSettings;

import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class LevelUpUpdateTimeSlash extends SlashCommand{
    private GuildSettings gs;

    public LevelUpUpdateTimeSlash(String father, GuildSettings gs){
        this.gs = gs;
        this.name = this.getClass().getSimpleName().replace("Slash", "").replace(father, "").toLowerCase();
        this.help = new CommandsLoader().getString(this.name, "help", father.toLowerCase());
        this.cooldown = new CommandsLoader().getCooldown(this.name, father.toLowerCase());
        this.category = new Category(new CommandsLoader().getString(father.toLowerCase(), "category"));
        this.options = Arrays.asList(
            new OptionData(OptionType.INTEGER, "second", "Set the update interval in seconds", true)
                .setMinValue(1)
                .setMaxValue(60 * 60)
        );
    }

    @Override
    protected void execute(SlashCommandEvent event) {
        int updateTime = event.getOption("second").getAsInt();
        //TODO: this command will be only for vip user
        String guildId = event.getGuild().getId();
        String userId = event.getUser().getId();
        if(!gs.getServer(guildId).getUserData(userId).setUpdateTime(updateTime)) { 
            event.deferReply(true).addContent("Something went wrong.").queue();
            return;
        }

        event.deferReply(false).addContent("Now you will gain experience every " + updateTime + " seconds").queue();
    }
}