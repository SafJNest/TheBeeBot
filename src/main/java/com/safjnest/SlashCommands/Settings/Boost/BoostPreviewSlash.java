package com.safjnest.SlashCommands.Settings.Boost;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.safjnest.Bot;
import com.safjnest.Utilities.CommandsLoader;
import com.safjnest.Utilities.Guild.GuildData;
import com.safjnest.Utilities.Guild.Alert.AlertData;
import com.safjnest.Utilities.Guild.Alert.AlertType;

public class BoostPreviewSlash extends SlashCommand{

    public BoostPreviewSlash(String father){
        this.name = this.getClass().getSimpleName().replace("Slash", "").replace(father, "").toLowerCase();
        this.help = new CommandsLoader().getString(name, "help", father.toLowerCase());
        this.cooldown = new CommandsLoader().getCooldown(this.name, father.toLowerCase());
        this.category = new Category(new CommandsLoader().getString(father.toLowerCase(), "category"));
    }

    @Override
    protected void execute(SlashCommandEvent event) {
        String guildId = event.getGuild().getId();

        GuildData gs = Bot.getGuildData(guildId);

        AlertData boost = gs.getAlert(AlertType.BOOST);

        if(boost == null) {
            event.deferReply(true).addContent("This guild doesn't have a boost message.").queue();
            return;
        }

        event.deferReply(false).addEmbeds(boost.getSampleEmbed(event.getGuild()).build()).queue();
    }
}