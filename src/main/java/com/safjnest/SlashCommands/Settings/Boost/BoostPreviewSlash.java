package com.safjnest.SlashCommands.Settings.Boost;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.safjnest.Utilities.CommandsLoader;
import com.safjnest.Utilities.SQL.DatabaseHandler;
import com.safjnest.Utilities.SQL.ResultRow;

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
        String botId = event.getJDA().getSelfUser().getId();

        ResultRow boost = DatabaseHandler.getBoost(guildId, botId);

        if(boost.get("boost_message") == null) {
            event.deferReply(true).addContent("This guild doesn't have a boost message.").queue();
            return;
        }

        String boostMessage = boost.get("boost_message").replace("#user", event.getUser().getAsMention());
        boostMessage = boostMessage + "\nThis message would be sent to <#" + boost.get("boost_channel") + ">";

        event.deferReply(false).addContent(boostMessage).queue();
    }
}