package com.safjnest.SlashCommands.Settings.Leave;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.safjnest.Utilities.CommandsLoader;
import com.safjnest.Utilities.DatabaseHandler;

public class LeavePreviewSlash extends SlashCommand{

    public LeavePreviewSlash(String father){
        this.name = this.getClass().getSimpleName().replace("Slash", "").replace(father, "").toLowerCase();
        this.help = new CommandsLoader().getString(name, "help", father.toLowerCase());
        this.cooldown = new CommandsLoader().getCooldown(this.name, father.toLowerCase());
        this.category = new Category(new CommandsLoader().getString(father.toLowerCase(), "category"));
    }

    @Override
    protected void execute(SlashCommandEvent event) {
        String query = "SELECT message_text FROM left_message WHERE guild_id = '" + event.getGuild().getId()
                            + "' AND bot_id = '" + event.getJDA().getSelfUser().getId() + "';";
        String message = DatabaseHandler.getSql().getString(query, "message_text");
        if(message == null){
            event.deferReply(false).addContent("You have not set a leave message yet.").queue();
            return;
        }

        query = "SELECT channel_id FROM left_message WHERE guild_id = '" + event.getGuild().getId()
                            + "' AND bot_id = '" + event.getJDA().getSelfUser().getId() + "';";
        String channel = DatabaseHandler.getSql().getString(query, "channel_id");

        message = message.replace("#user", event.getMember().getAsMention());
        message += "\n\nThis is a preview of the message that will be sent to the channel <#" + channel + ">.";
        event.deferReply(false).addContent(message).queue();
    }
    
}
