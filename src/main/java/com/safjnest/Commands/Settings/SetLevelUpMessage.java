package com.safjnest.Commands.Settings;

import com.safjnest.Utilities.DatabaseHandler;
import com.safjnest.Utilities.Commands.CommandsHandler;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;


public class SetLevelUpMessage extends Command {

    public SetLevelUpMessage() {
        this.name = this.getClass().getSimpleName();
        this.aliases = new CommandsHandler().getArray(this.name, "alias");
        this.help = new CommandsHandler().getString(this.name, "help");
        this.cooldown = new CommandsHandler().getCooldown(this.name);
        this.category = new Category(new CommandsHandler().getString(this.name, "category"));
        this.arguments = new CommandsHandler().getString(this.name, "arguments");
    }

    @Override
    protected void execute(CommandEvent event) {
        String message = event.getMessage().getContentRaw();
        String discordId = event.getGuild().getId();
        message = message.substring(message.indexOf("|")+1);
        String query = "INSERT INTO levelup_message(discord_id, message_text)"
                            + "VALUES('" + discordId + "','" + message +"');";
        if(!DatabaseHandler.getSql().runQuery(query)){
            query = "UPDATE levelup_message SET message_text = '" + message + "' WHERE discord_id = '" + discordId + "';"; 
            DatabaseHandler.getSql().runQuery(query);
            event.reply("Set a new LevelUp message.");
            return;
        }
        event.reply("All set correctly");
    }
}