package com.safjnest.Commands.Settings;

import com.safjnest.Utilities.CommandsLoader;
import com.safjnest.Utilities.DatabaseHandler;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;


public class LevelUp extends Command {

    public LevelUp() {
        this.name = this.getClass().getSimpleName();
        this.aliases = new CommandsLoader().getArray(this.name, "alias");
        this.help = new CommandsLoader().getString(this.name, "help");
        this.cooldown = new CommandsLoader().getCooldown(this.name);
        this.category = new Category(new CommandsLoader().getString(this.name, "category"));
        this.arguments = new CommandsLoader().getString(this.name, "arguments");
    }

    @Override
    protected void execute(CommandEvent event) {
        String message = event.getMessage().getContentRaw();
        String discordId = event.getGuild().getId();
        message = message.substring(message.indexOf("|")+1);
        String query = "INSERT INTO levelup_message(guild_id, message_text)"
                            + "VALUES('" + discordId + "','" + message +"');";
        if(!DatabaseHandler.getSql().runQuery(query)){
            query = "UPDATE levelup_message SET message_text = '" + message + "' WHERE guild_id = '" + discordId + "';"; 
            DatabaseHandler.getSql().runQuery(query);
            event.reply("Set a new LevelUp message.");
            return;
        }
        event.reply("All set correctly");
    }
}