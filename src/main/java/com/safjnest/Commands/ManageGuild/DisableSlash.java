package com.safjnest.Commands.ManageGuild;

import com.safjnest.Utilities.DatabaseHandler;
import com.safjnest.Utilities.Commands.CommandsHandler;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

public class DisableSlash extends Command {

    public DisableSlash() {
        this.name = this.getClass().getSimpleName();
        ;
        this.aliases = new CommandsHandler().getArray(this.name, "alias");
        this.help = new CommandsHandler().getString(this.name, "help");
        this.cooldown = new CommandsHandler().getCooldown(this.name);
        this.category = new Category(new CommandsHandler().getString(this.name, "category"));
        this.arguments = new CommandsHandler().getString(this.name, "arguments");
    }

    @Override
    protected void execute(CommandEvent event) {
        event.getGuild().updateCommands().queue();
        event.reply("Default commands are a poor alternative");

        String query = "INSERT INTO guild_settings (guild_id, bot_id, has_slash) VALUES (" + event.getGuild().getId()
                + ", " + event.getJDA().getSelfUser().getId() + ", true) ON DUPLICATE KEY UPDATE has_slash = false";
        DatabaseHandler.getSql().runQuery(query);
    }
}