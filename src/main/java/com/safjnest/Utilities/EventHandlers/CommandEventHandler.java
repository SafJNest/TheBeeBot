package com.safjnest.Utilities.EventHandlers;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.command.CommandListener;
import com.safjnest.Utilities.Guild.GuildSettings;
import com.safjnest.Utilities.SQL.DatabaseHandler;

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
        String args = event.getArgs();
        DatabaseHandler.insertCommand(event.getGuild().getId(), event.getJDA().getSelfUser().getId(), event.getMember().getId(), commandName, args);
    }
}
