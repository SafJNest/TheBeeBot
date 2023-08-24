package com.safjnest.Commands.Audio;

import java.util.ArrayList;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.safjnest.Utilities.CommandsLoader;
import com.safjnest.Utilities.DatabaseHandler;

import net.dv8tion.jda.api.Permission;

/**
 * The command lets you delete a sound from the server.
 * <p>You have to be a server admin to use the command.</p>
 * @author <a href="https://github.com/NeutronSun">NeutronSun</a>
 * 
 * @since 1.3
 */
public class CustomizeSound extends Command{
    
    public CustomizeSound(){
        this.name = this.getClass().getSimpleName();
        this.aliases = new CommandsLoader().getArray(this.name, "alias");
        this.help = new CommandsLoader().getString(this.name, "help");
        this.cooldown = new CommandsLoader().getCooldown(this.name);
        this.category = new Category(new CommandsLoader().getString(this.name, "category"));
        this.arguments = new CommandsLoader().getString(this.name, "arguments");
    }
    
	@Override
	protected void execute(CommandEvent event) {
        String fileName = "";
        String query = null;
        String id = null, name, userId;
        ArrayList<ArrayList<String>> arr = null;
        String newName = "";
        if((fileName = event.getArgs().split(" ", 2)[0]) == ""){
            event.reply("Missing sound's name.");
            return;
        }
        newName = event.getArgs().split(" ", 2)[1];
        if(fileName.matches("[0123456789]*"))
            query = "SELECT id, name, user_id FROM sound WHERE id = '" + fileName + "';";
        else{
            event.reply("Please use IDs to choose the sound you want to modify");
            return;
        }
        if((arr = DatabaseHandler.getSql().getAllRows(query, 3)) == null || arr.isEmpty()){
            event.reply("There is no sound with that name/id");
            return;
        }

        if(arr.size() > 1){
            event.reply("You have uploaded more than one sound with that name in this server, please use IDs to choose the sound you want to modify");
            return;
        }

        id = arr.get(0).get(0);
        name = arr.get(0).get(1);
        userId = arr.get(0).get(2);

        if(!event.getAuthor().getId().equals(userId) && !event.getMember().hasPermission(Permission.ADMINISTRATOR)){
            event.reply("You don't have permission to modify this sound");
            return;
        }

        query = "UPDATE sound SET name = '" + newName + "' WHERE id = '" + id + "';"; 

        DatabaseHandler.getSql().runQuery(query);

        event.reply(name + " (ID: " + id +  ") has been modified in: " + newName + " (ID: " + id +  ")");
	}
}
