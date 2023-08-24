package com.safjnest.Commands.Settings;

import com.safjnest.Utilities.CommandsLoader;
import com.safjnest.Utilities.DatabaseHandler;
import com.safjnest.Utilities.SQL;

import java.util.ArrayList;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

/**
 * @author <a href="https://github.com/NeuntronSun">NeutronSun</a>
 * 
 * @since 1.3
 */
public class Welcome extends Command {
    private SQL sql;

    public Welcome(SQL sql) {
        this.name = this.getClass().getSimpleName();
        this.aliases = new CommandsLoader().getArray(this.name, "alias");
        this.help = new CommandsLoader().getString(this.name, "help");
        this.cooldown = new CommandsLoader().getCooldown(this.name);
        this.category = new Category(new CommandsLoader().getString(this.name, "category"));
        this.arguments = new CommandsLoader().getString(this.name, "arguments");
        this.sql = sql;
    }

    @Override
    protected void execute(CommandEvent event) {
        String message = event.getMessage().getContentRaw();
        String channel = null;
        if(event.getArgs().equalsIgnoreCase("disable")){
            String query = "DELETE from welcome_message WHERE guild_id = '" + event.getGuild().getId()
                           + "' AND bot_id = '" + event.getJDA().getSelfUser().getId() + "';";
            DatabaseHandler.getSql().runQuery(query);
            query = "DELETE from welcome_roles WHERE guild_id = '" + event.getGuild().getId()
                           + "' AND bot_id = '" + event.getJDA().getSelfUser().getId() + "';";
            DatabaseHandler.getSql().runQuery(query);
            event.reply("Welcome message disable successfully");
            return;
        }
        ArrayList<String> roles = new ArrayList<>();
        for (int i = 0; i < message.indexOf("|"); i++) {
            if (message.charAt(i) == '#') {
                System.out.println(message.charAt(i + 20));
                if(message.charAt(i + 20) == '>'){
                    channel = message.substring(i + 1, i + 20);
                    System.out.println(channel);
                    i += 20;
                }
                else{
                    channel = message.substring(i + 1, i + 19);
                    i += 19;
                }
                
            } else if (message.charAt(i) == '@' && message.charAt(i + 1) == '&') {
                roles.add(message.substring(i + 2, message.lastIndexOf(">")));
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
        String query = "INSERT INTO welcome_message(guild_id, channel_id, message_text, bot_id)"
                            + "VALUES('" + discordId + "','" + channel +"','" + message + "','"+event.getJDA().getSelfUser().getId()+"');";
        sql.runQuery(query);
        for(String role : roles){
            query = "INSERT INTO welcome_roles(role_id, guild_id, bot_id)"
                            + "VALUES('" + role + "','" + discordId + "','"+event.getJDA().getSelfUser().getId()+"');";
            sql.runQuery(query);
        }
        event.reply("All set correctly");
    }
}