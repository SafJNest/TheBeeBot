package com.safjnest.Commands.Settings;

import com.safjnest.Utilities.SQL;
import com.safjnest.Utilities.Commands.CommandsHandler;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

/**
 * @author <a href="https://github.com/NeuntronSun">NeutronSun</a>
 * 
 * @since 1.3
 */
public class SetRoom extends Command {
    private SQL sql;

    public SetRoom(SQL sql) {
        this.name = this.getClass().getSimpleName();
        this.aliases = new CommandsHandler().getArray(this.name, "alias");
        this.help = new CommandsHandler().getString(this.name, "help");
        this.cooldown = new CommandsHandler().getCooldown(this.name);
        this.category = new Category(new CommandsHandler().getString(this.name, "category"));
        this.arguments = new CommandsHandler().getString(this.name, "arguments");
        this.sql = sql;
    }

    @Override
    protected void execute(CommandEvent event) {
        String name = null;
        String channel = null;
        String argsArr[] = event.getArgs().split(" ",2);
        String args = event.getArgs();
        if(args.contains("<")){
            channel = args.substring(args.indexOf("<")+2, args.indexOf("<")+20);
        }else{
            if(event.getGuild().getVoiceChannelById(argsArr[0])==null){
                event.reply("Channel ID not valid");
                return;
            }
            channel = argsArr[0];
        }
        name = argsArr[1];
        String query = "SELECT room_name FROM rooms_nickname WHERE discord_id = '" + event.getGuild().getId() + "' AND room_id = '" + argsArr[0] +"';";
        if(sql.getString(query, "room_name") == null){
            query = "INSERT INTO rooms_nickname(discord_id, room_id, room_name)" + "VALUES('" + event.getGuild().getId() + "','" + channel +"','" + name + "');";
            if(sql.runQuery(query))
                event.reply("All set correctly");
            else
                event.reply("Error: wrong id probably");
        }else{
            query = "UPDATE rooms_nickname SET room_name = '" + argsArr[1] + "' WHERE discord_id = '" + event.getGuild().getId() + "' AND room_id = '" + argsArr[0] +"';";
            if(sql.runQuery(query))
                event.reply("Channel nickname changed correctly");
            else 
                event.reply("Error: wrong id probably");
        }
    }
}