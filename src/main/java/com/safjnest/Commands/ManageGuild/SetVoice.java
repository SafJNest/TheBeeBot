package com.safjnest.Commands.ManageGuild;

import com.safjnest.Commands.Audio.TTS;
import com.safjnest.Utilities.CommandsHandler;
import com.safjnest.Utilities.SQL;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

/**
 * @author <a href="https://github.com/NeuntronSun">NeutronSun</a>
 * 
 * @since 1.3
 */
public class SetVoice extends Command {
    private SQL sql;

    public SetVoice(SQL sql) {
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
        String language = null;
        String voice = "keria";
        for(String key : TTS.voices.keySet()){
            if(TTS.voices.get(key).contains(event.getArgs().split(" ")[0])){
                language = key;
                voice = event.getArgs().split(" ")[0];
                break;
            }
        }
        if(voice.equals("keria")){
            event.reply("Voice not found, use command" + event.getPrefix() + "t list");
            return;
        }
        String query = "SELECT name_tts FROM tts_guilds WHERE discord_id = '" + event.getGuild().getId() + "';";
        if(sql.getString(query, "name_tts") == null){
            query = "INSERT INTO tts_guilds(discord_id, name_tts, language_tts)"
                                + "VALUES('" + event.getGuild().getId() + "','" + voice + "','" + language + "');";
            
            if(sql.runQuery(query))
                event.reply("All set correctly");
            else
                event.reply("Error: wrong voice name probably");
        }else{
            query = "UPDATE tts_guilds SET name_tts = '" + voice + "' WHERE discord_id = '" + event.getGuild().getId() + "';";
            String query2 = "UPDATE tts_guilds SET language_tts = '" + language + "' WHERE discord_id = '" + event.getGuild().getId() + "';";
            if(sql.runQuery(query) && sql.runQuery(query2))
                event.reply("Default voice modified correctly");
            else 
                event.reply("Error: wrong voice name probably");
        }
    }
}