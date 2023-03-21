package com.safjnest.Commands.ManageGuild;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.safjnest.Utilities.CommandsHandler;
import com.safjnest.Utilities.SQL;
import com.safjnest.Utilities.Guild.GuildData;
import com.safjnest.Utilities.Guild.GuildSettings;

import net.dv8tion.jda.api.Permission;

public class SetPrefix extends Command{
    private SQL sql;
    GuildSettings gs;
    
    public SetPrefix(SQL sql, GuildSettings gs){
        this.name = this.getClass().getSimpleName();
        this.aliases = new CommandsHandler().getArray(this.name, "alias");
        this.help = new CommandsHandler().getString(this.name, "help");
        this.cooldown = new CommandsHandler().getCooldown(this.name);
        this.category = new Category(new CommandsHandler().getString(this.name, "category"));
        this.arguments = new CommandsHandler().getString(this.name, "arguments");
        this.sql = sql;
        this.gs = gs;
    }

    @Override
    protected void execute(CommandEvent event) {
        if(event.getArgs() == "") {
            event.reply("You have to write the new prefix");
            return;
        }
        if(!event.getMember().hasPermission(Permission.ADMINISTRATOR)) {
            event.reply("Only admins can change the prefix of the guild");
            return;
        }
        
        String query = "INSERT INTO guild_settings(guild_id, bot_id, prefix)" + "VALUES('" + event.getGuild().getId() + "','" + event.getSelfUser().getId() + "','" + event.getArgs() +"') ON DUPLICATE KEY UPDATE prefix = '" + event.getArgs() + "';";
        if(sql.runQuery(query))
            event.reply("New prefix is: " + event.getArgs());
        else
            event.reply("Error");
        
        gs.saveData(new GuildData(event.getGuild().getIdLong(), event.getArgs()));
    }
}
