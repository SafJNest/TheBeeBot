package com.safjnest.Commands.ManageGuild;

import com.safjnest.Utilities.CommandsHandler;
import com.safjnest.Utilities.PostgreSQL;

import java.util.ArrayList;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

/**
 * @author <a href="https://github.com/NeuntronSun">NeutronSun</a>
 * 
 * @since 1.3
 */
public class SetWelcome extends Command {
    private PostgreSQL sql;

    public SetWelcome(PostgreSQL sql) {
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
        String message = event.getMessage().getContentRaw();
        String channel = null;
        ArrayList<String> roles = new ArrayList<>();
        System.out.println(event.getMessage().getContentRaw());
        for (int i = 0; i < message.indexOf("|"); i++) {
            if (message.charAt(i) == '#') {
                channel = message.substring(i + 1, i + 19);
                i += 19;
            } else if (message.charAt(i) == '@' && message.charAt(i + 1) == '&') {
                roles.add(message.substring(i + 2, i + 20));
                i += 20;
            }
        }
        if(channel == null){
            try {
                channel = event.getGuild().getSystemChannel().getId();
            } catch (Exception e) {
                event.reply("Mention or write the id of the channel you want to set as welcome or set a 'system channel' on your server's settings");
                return;
            }
        }
        String discordId = event.getGuild().getId();
        message = message.substring(message.indexOf("|")+1);
        String query = "INSERT INTO welcome_message(discord_id, channel_id, message_text)"
                            + "VALUES('" + discordId + "','" + channel +"','" + message + "');";
        sql.runQuery(query);
        for(String role : roles){
            query = "INSERT INTO welcome_roles(role_id, discord_id)"
                            + "VALUES('" + role + "','" + discordId +"');";
            sql.runQuery(query);
        }
        event.reply("All set correctly");
    }
}