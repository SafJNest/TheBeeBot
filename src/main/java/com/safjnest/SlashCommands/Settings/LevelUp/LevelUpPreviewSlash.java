package com.safjnest.SlashCommands.Settings.LevelUp;

import java.util.ArrayList;
import java.util.HashMap;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.safjnest.Bot;
import com.safjnest.Utilities.CommandsLoader;
import com.safjnest.Utilities.Guild.ChannelData;
import com.safjnest.Utilities.Guild.GuildData;
import com.safjnest.Utilities.Guild.Alert.AlertData;
import com.safjnest.Utilities.Guild.Alert.AlertType;

import net.dv8tion.jda.api.EmbedBuilder;

public class LevelUpPreviewSlash extends SlashCommand{

    public LevelUpPreviewSlash(String father){
        this.name = this.getClass().getSimpleName().replace("Slash", "").replace(father, "").toLowerCase();
        this.help = new CommandsLoader().getString(name, "help", father.toLowerCase());
        this.cooldown = new CommandsLoader().getCooldown(this.name, father.toLowerCase());
        this.category = new Category(new CommandsLoader().getString(father.toLowerCase(), "category"));
    }

    @Override
    protected void execute(SlashCommandEvent event) {
        String guildId = event.getGuild().getId();

        GuildData gs = Bot.getGuildData(guildId);

        AlertData level = gs.getAlert(AlertType.LEVEL_UP);

        if(!gs.isExpSystemEnabled()) {
            event.deferReply(true).addContent("This guild doesn't have the exp system enabled.").queue();
            return;
        }

        if(level == null){
            event.deferReply(true).addContent("No level up message found.").queue();
            return;
        }
        
        HashMap<Long, ChannelData> channels = gs.getChannels();

        ArrayList<String> expChannels = new ArrayList<>();
        ArrayList<String> noExpChannels = new ArrayList<>();

        for (ChannelData channel : channels.values()) {
            if (!channel.isExpSystemEnabled()) {
                noExpChannels.add(String.valueOf(channel.getRoomId()));
            }
            if (channel.getExpValue() != 1.0) {
                expChannels.add(String.valueOf(channel.getRoomId()));
            }
        }

        EmbedBuilder eb = level.getSampleEmbed(event.getGuild());
        eb.addBlankField(true);
        if(!expChannels.isEmpty()) {
            String modifiedChannels= "";

            for(String channel_id : expChannels) {
                modifiedChannels += event.getGuild().getTextChannelById(channel_id).getName() + ": " + channels.get(Long.parseLong(channel_id)).getExpValue() + " exp\n";
            }
            eb.addField("Modified Exp Channels", "```" + modifiedChannels + "```", true);
        }

        if(!noExpChannels.isEmpty()){
            String disabledChannels = "";

            for(String channel_id : noExpChannels){
                disabledChannels += event.getGuild().getTextChannelById(channel_id).getName() + " \n";
            }

            eb.addField("Disabled Exp Channels", "```" + disabledChannels + "```", true);
        }

        event.deferReply(false).addEmbeds(eb.build()).queue();
    }
}