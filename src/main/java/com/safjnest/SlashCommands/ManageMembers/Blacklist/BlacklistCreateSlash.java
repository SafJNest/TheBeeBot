package com.safjnest.SlashCommands.ManageMembers.Blacklist;

import java.util.Arrays;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.safjnest.Utilities.CommandsLoader;
import com.safjnest.Utilities.Guild.GuildSettings;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class BlacklistCreateSlash extends SlashCommand{

    private GuildSettings gs;

    public BlacklistCreateSlash(String father, GuildSettings gs){
        this.name = this.getClass().getSimpleName().replace("Slash", "").replace(father, "").toLowerCase();
        this.help = new CommandsLoader().getString(name, "help", father.toLowerCase());
        this.cooldown = new CommandsLoader().getCooldown(this.name, father.toLowerCase());
        this.category = new Category(new CommandsLoader().getString(father.toLowerCase(), "category"));
        this.userPermissions = new Permission[]{Permission.BAN_MEMBERS};
        this.botPermissions = new Permission[]{Permission.BAN_MEMBERS};
        this.options = Arrays.asList(
            new OptionData(OptionType.INTEGER, "threshold", "Ban threshold", true)
                .setMinValue(3)    
                .setMaxValue(100),
            new OptionData(OptionType.CHANNEL, "channel", "Notification channel", true).setChannelTypes(ChannelType.TEXT)
        );
        
        this.gs = gs;
    }

    @Override
    protected void execute(SlashCommandEvent event) {
        String threshold = event.getOption("threshold").getAsString();
        String channelId = event.getOption("channel").getAsChannel().getId();

        if (!gs.getServer(event.getGuild().getId()).setBlackListData(Integer.parseInt(threshold), channelId)) {
            event.deferReply(false).addContent("Something went wrong.").queue();
            return;  
        }
        
        event.deferReply(false).addContent("Blacklist enabled with a ban threshold of " + threshold + ".\nNotification will be sent in " + event.getGuild().getTextChannelById(channelId).getAsMention() + ".").queue();
    }
}