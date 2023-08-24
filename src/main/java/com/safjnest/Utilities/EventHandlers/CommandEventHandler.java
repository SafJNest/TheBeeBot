package com.safjnest.Utilities.EventHandlers;

import java.sql.Timestamp;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.command.CommandListener;
import com.safjnest.Utilities.DatabaseHandler;
import com.safjnest.Utilities.Guild.GuildSettings;

public class CommandEventHandler implements CommandListener{
    private GuildSettings settings;

    public CommandEventHandler(GuildSettings gs){
        this.settings = gs;
    }

    @Override
    public void onCommand(CommandEvent event, Command command){
        if(!settings.getServer(event.getGuild().getId()).getCommandStatsRoom(event.getChannel().getIdLong()))
            return;
        String commandName = command.getName();
        String query = "INSERT INTO command_analytic(name, time, user_id) VALUES ('" + commandName + "', '" + new Timestamp(System.currentTimeMillis()) + "', '" + event.getAuthor().getId()+ "');";
        DatabaseHandler.getSql().runQuery(query);
    }
}
