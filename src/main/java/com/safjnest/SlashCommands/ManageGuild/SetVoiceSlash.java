package com.safjnest.SlashCommands.ManageGuild;

import com.safjnest.Commands.Audio.TTS;
import com.safjnest.Utilities.CommandsHandler;
import com.safjnest.Utilities.SQL;

import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.Arrays;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;

/**
 * @author <a href="https://github.com/NeuntronSun">NeutronSun</a>
 * 
 * @since 1.3
 */
public class SetVoiceSlash extends SlashCommand {
    private SQL sql;

    public SetVoiceSlash(SQL sql) {
        this.name = this.getClass().getSimpleName().replace("Slash", "").toLowerCase();
        this.aliases = new CommandsHandler().getArray(this.name, "alias");
        this.help = new CommandsHandler().getString(this.name, "help");
        this.cooldown = new CommandsHandler().getCooldown(this.name);
        this.category = new Category(new CommandsHandler().getString(this.name, "category"));
        this.arguments = new CommandsHandler().getString(this.name, "arguments");
        this.options = Arrays.asList(
            new OptionData(OptionType.STRING, "voice", "Speaker name's voice", true));
        this.sql = sql;
    }

    @Override
    protected void execute(SlashCommandEvent event) {
        String language = null;
        String voice = "keria";
        for(String key : TTS.voices.keySet()){
            if(TTS.voices.get(key).contains(event.getOption("voice").getAsString())){
                language = key;
                voice = event.getOption("voice").getAsString();
                break;
            }
        }
        if(voice.equals("keria")){
            event.deferReply(true).addContent("Voice not found, use command /t list").queue();
            return;
        }
        String query = "SELECT name_tts FROM tts_guilds WHERE discord_id = '" + event.getGuild().getId() + "';";
        if(sql.getString(query, "name_tts") == null){
            query = "INSERT INTO tts_guilds(discord_id, name_tts, language_tts)"
                                + "VALUES('" + event.getGuild().getId() + "','" + voice + "','" + language + "');";
            
            if(sql.runQuery(query))
                event.deferReply(true).addContent("All set correctly").queue();
            else
                event.deferReply(true).addContent("Error: wrong voice name probably").queue();
        }else{
            query = "UPDATE tts_guilds SET name_tts = '" + voice + "' WHERE discord_id = '" + event.getGuild().getId() + "';";
            String query2 = "UPDATE tts_guilds SET language_tts = '" + language + "' WHERE discord_id = '" + event.getGuild().getId() + "';";
            if(sql.runQuery(query) && sql.runQuery(query2))
                event.deferReply(false).addContent("Default voice modified correctly").queue();
            else 
                event.deferReply(true).addContent("Error: wrong voice name probably").queue();
        }
    }
}