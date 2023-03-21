package com.safjnest.Commands.Audio;

import java.util.ArrayList;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.safjnest.Utilities.AwsS3;
import com.safjnest.Utilities.CommandsHandler;
import com.safjnest.Utilities.SQL;

import net.dv8tion.jda.api.Permission;

/**
 * The command lets you delete a sound from the server.
 * <p>You have to be a server admin to use the command.</p>
 * @author <a href="https://github.com/NeutronSun">NeutronSun</a>
 * 
 * @since 1.3
 */
public class DeleteSound extends Command{
    private AwsS3 s3Client;
    private SQL sql;
    
    public DeleteSound(AwsS3 s3Client, SQL sql){
        this.name = this.getClass().getSimpleName();
        this.aliases = new CommandsHandler().getArray(this.name, "alias");
        this.help = new CommandsHandler().getString(this.name, "help");
        this.cooldown = new CommandsHandler().getCooldown(this.name);
        this.category = new Category(new CommandsHandler().getString(this.name, "category"));
        this.arguments = new CommandsHandler().getString(this.name, "arguments");
        this.s3Client = s3Client;
        this.sql = sql;
    }
    
	@Override
	protected void execute(CommandEvent event) {
        String fileName = "";
        String query = null;
        String id = null, name, userId;
        ArrayList<ArrayList<String>> arr = null;

        if((fileName = event.getArgs()) == ""){
            event.reply("Missing sound's name.");
            return;
        }

        if(fileName.matches("[0123456789]*"))
            query = "SELECT id, name, user_id FROM sound WHERE id = '" + fileName + "';";
        else
            query = "SELECT id, name, user_id FROM sound WHERE name = '" + fileName + "' AND (user_id = '" + event.getAuthor().getId() + "' OR guild_id = '" + event.getGuild().getId() + "');";

        if((arr = sql.getTuple(query, 3)) == null || arr.isEmpty()){
            event.reply("There is no sound with that name/id");
            return;
        }

        if(arr.size() > 1){
            event.reply("There is more than one sound with that name in this server, please use IDs to choose the sound you want to delete");
            return;
        }

        id = arr.get(0).get(0);
        name = arr.get(0).get(1);
        userId = arr.get(0).get(2);

        if(!event.getAuthor().getId().equals(userId) && !event.getMember().hasPermission(Permission.ADMINISTRATOR)){
            event.reply("You don't have permission to delete this sound");
            return;
        }

        try{
            s3Client.getS3Client().deleteObject("thebeebot", id);
        }catch(Exception e){
            e.printStackTrace();
            event.reply("An error occured while deleting the sound from aws s3");
            return;
        }

        query = "DELETE FROM sound WHERE id = " + id + ";";

        sql.runQuery(query);

        event.reply(name + " (ID: " + id +  ") has been deleted");
	}
}
