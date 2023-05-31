package com.safjnest.Commands.Settings;

import com.safjnest.Commands.Audio.TTS;
import com.safjnest.Utilities.SQL;
import com.safjnest.Utilities.Commands.CommandsHandler;
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
        //do on conflict updatge
        String query = "INSERT INTO guild_settings (guild_id, bot_id, language_tts, name_tts) VALUES ('" + event.getGuild().getId() + "', '" + event.getJDA().getSelfUser().getId() + "', '" + language + "', '" + voice + "') ON DUPLICATE KEY UPDATE language_tts = '" + language + "', name_tts = '" + voice + "'";
        sql.runQuery(query);
        event.reply("Voice set to " + voice);
        


        
    }
}