package com.safjnest.Commands.ManageGuild;

import com.safjnest.Utilities.DatabaseHandler;
import com.safjnest.Utilities.Commands.CommandsHandler;
import com.safjnest.Utilities.Commands.SlashCommandsHandler;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

public class EnableSlash extends Command {
    private SlashCommandsHandler sch;

    public EnableSlash(SlashCommandsHandler sch) {
        this.name = this.getClass().getSimpleName();
        ;
        this.aliases = new CommandsHandler().getArray(this.name, "alias");
        this.help = new CommandsHandler().getString(this.name, "help");
        this.cooldown = new CommandsHandler().getCooldown(this.name);
        this.category = new Category(new CommandsHandler().getString(this.name, "category"));
        this.arguments = new CommandsHandler().getString(this.name, "arguments");
        this.sch = sch;
    }

    @Override
    protected void execute(CommandEvent event) {
        event.getGuild().updateCommands().addCommands(sch.getCommandData()).queue();
        event.reply("Slash command are fired up and ready to serve!");
        
        String query = "INSERT INTO guild_settings (guild_id, bot_id, has_slash) VALUES (" + event.getGuild().getId()
                + ", " + event.getJDA().getSelfUser().getId() + ", true) ON DUPLICATE KEY UPDATE has_slash = true";
        DatabaseHandler.getSql().runQuery(query);
    }
}
