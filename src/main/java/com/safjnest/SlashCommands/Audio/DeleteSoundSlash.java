package com.safjnest.SlashCommands.Audio;

import java.util.ArrayList;
import java.util.Arrays;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.safjnest.Utilities.DatabaseHandler;
import com.safjnest.Utilities.SQL;
import com.safjnest.Utilities.Commands.CommandsHandler;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

/**
 * The command lets you delete a sound from the server.
 * <p>You have to be a server admin to use the command.</p>
 * @author <a href="https://github.com/NeutronSun">NeutronSun</a>
 * 
 * @since 1.3
 */
public class DeleteSoundSlash extends SlashCommand{
    private SQL sql;
    
    public DeleteSoundSlash(){
        this.name = this.getClass().getSimpleName().replace("Slash", "").toLowerCase();
        this.aliases = new CommandsHandler().getArray(this.name, "alias");
        this.help = new CommandsHandler().getString(this.name, "help");
        this.cooldown = new CommandsHandler().getCooldown(this.name);
        this.category = new Category(new CommandsHandler().getString(this.name, "category"));
        this.arguments = new CommandsHandler().getString(this.name, "arguments");
        this.options = Arrays.asList(
            new OptionData(OptionType.STRING, "sound", "Sound to delete", true));
        this.sql = DatabaseHandler.getSql();
    }
    
	@Override
	protected void execute(SlashCommandEvent event) {
        String fileName = event.getOption("sound").getAsString();
        String query = null;
        String id = null, name, userId;
        ArrayList<ArrayList<String>> arr = null;


        if(fileName.matches("[0123456789]*"))
            query = "SELECT id, name, user_id FROM sound WHERE id = '" + fileName + "';";
        else
            query = "SELECT id, name, user_id FROM sound WHERE name = '" + fileName + "' AND (user_id = '" + event.getUser().getId() + "' OR guild_id = '" + event.getGuild().getId() + "');";

        if((arr = sql.getAllRows(query, 3)) == null || arr.isEmpty()){
            event.deferReply(true).addContent("There is no sound with that name/id").queue();
            return;
        }

        if(arr.size() > 1){
            event.deferReply(true).addContent("There is more than one sound with that name in this server, please use IDs to choose the sound you want to delete").queue();
            return;
        }

        id = arr.get(0).get(0);
        name = arr.get(0).get(1);
        userId = arr.get(0).get(2);

        if(!event.getUser().getId().equals(userId) && !event.getMember().hasPermission(Permission.ADMINISTRATOR)){
            event.deferReply(true).addContent("You don't have permission to delete this sound").queue();
            return;
        }

       

        query = "DELETE FROM sound WHERE id = " + id + ";";

        sql.runQuery(query);

        event.deferReply(false).addContent(name + " (ID: " + id +  ") has been deleted").queue();
	}
}
