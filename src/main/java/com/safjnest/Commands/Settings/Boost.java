package com.safjnest.Commands.Settings;

import com.safjnest.Utilities.CommandsLoader;
import com.safjnest.Utilities.DatabaseHandler;
import com.safjnest.Utilities.SQL;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

/**
 * @author <a href="https://github.com/NeuntronSun">NeutronSun</a>
 * 
 * @since 1.3
 */
public class Boost extends Command {
    private SQL sql;

    public Boost(SQL sql) {
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
            String query = "DELETE from boost_message WHERE guild_id = '" + event.getGuild().getId()
                           + "' AND bot_id = '" + event.getJDA().getSelfUser().getId() + "';";
            DatabaseHandler.getSql().runQuery(query);
            event.reply("Boost message disable successfully");
            return;
        }
        for (int i = 0; i < message.indexOf("|"); i++) {
            if (message.charAt(i) == '#') {
                if(message.charAt(i + 20) == '>'){
                    channel = message.substring(i + 1, i + 20);
                    i += 20;
                }
                else{
                    channel = message.substring(i + 1, i + 19);
                    i += 19;
                }
                
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
        String query = "INSERT INTO boost_message(guild_id, channel_id, message_text, bot_id)"
                            + "VALUES('" + discordId + "','" + channel +"','" + message + "','"+event.getJDA().getSelfUser().getId()+"');";
        sql.runQuery(query);
        event.reply("All set correctly");
    }
}