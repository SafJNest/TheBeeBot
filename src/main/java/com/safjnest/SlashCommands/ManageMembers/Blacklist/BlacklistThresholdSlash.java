package com.safjnest.SlashCommands.ManageMembers.Blacklist;

import java.util.Arrays;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.safjnest.Utilities.CommandsLoader;
import com.safjnest.Utilities.Guild.GuildSettings;
import com.safjnest.Utilities.SQL.DatabaseHandler;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class BlacklistThresholdSlash extends SlashCommand{

    private GuildSettings gs;

    public BlacklistThresholdSlash(String father, GuildSettings gs){
        this.name = this.getClass().getSimpleName().replace("Slash", "").replace(father, "").toLowerCase();
        this.help = new CommandsLoader().getString(name, "help", father.toLowerCase());
        this.cooldown = new CommandsLoader().getCooldown(this.name, father.toLowerCase());
        this.category = new Category(new CommandsLoader().getString(father.toLowerCase(), "category"));
        this.userPermissions = new Permission[]{Permission.BAN_MEMBERS};
        this.botPermissions = new Permission[]{Permission.BAN_MEMBERS};
        this.options = Arrays.asList(
            new OptionData(OptionType.INTEGER, "threshold", "Ban threshold", true)
                .setMinValue(3)    
                .setMaxValue(100));
        
        this.gs = gs;
    }

    @Override
    protected void execute(SlashCommandEvent event) {
        String threshold = event.getOption("threshold").getAsString();

        if(!DatabaseHandler.setBlacklistThreshold(threshold, event.getGuild().getId(), event.getJDA().getSelfUser().getId())) {
            event.deferReply(true).addContent("Something went wrong.").queue();
            return;
        }
        
        event.deferReply(false).addContent("Blacklist threshold set to " + threshold + ".\n").queue();
        
        gs.getServer(event.getGuild().getId()).setThreshold(Integer.parseInt(threshold));
    }
}