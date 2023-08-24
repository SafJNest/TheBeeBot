package com.safjnest.Commands.ManageGuild;

import com.safjnest.Utilities.CommandsLoader;
import com.safjnest.Utilities.SQL;

import java.util.ArrayList;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

/**
 * @author <a href="https://github.com/NeuntronSun">NeutronSun</a>
 * 
 * @since 1.3
 */
public class ListRoom extends Command {
    private SQL sql;

    public ListRoom(SQL sql) {
        this.name = this.getClass().getSimpleName();
        this.aliases = new CommandsLoader().getArray(this.name, "alias");
        this.help = new CommandsLoader().getString(this.name, "help");
        this.cooldown = new CommandsLoader().getCooldown(this.name);
        this.category = new Category(new CommandsLoader().getString(this.name, "category"));
        this.arguments = new CommandsLoader().getString(this.name, "arguments");
        this.sql = sql;
    }

    @Override
    protected void execute(CommandEvent event) {
        String query = "SELECT room_id FROM rooms_settings WHERE guild_id = '" + event.getGuild().getId() + "';";
        ArrayList<String> roomId= sql.getAllRowsSpecifiedColumn(query, "room_id");
        query = "SELECT room_name FROM rooms_settings WHERE guild_id = '" + event.getGuild().getId() + "';";
        ArrayList<String> roomName= sql.getAllRowsSpecifiedColumn(query, "room_name");
        String rooms = "";
        for(int i = 0; i < roomName.size(); i++){
            try {
                rooms+=event.getGuild().getVoiceChannelById(roomId.get(i)).getName() + " = " + roomName.get(i) + "\n";
            } catch (Exception e) {
            }
        }
        
        event.reply(rooms);
    
    }
}