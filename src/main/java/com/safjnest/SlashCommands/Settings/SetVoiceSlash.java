package com.safjnest.SlashCommands.Settings;

import com.safjnest.Commands.Audio.TTS;
import com.safjnest.Utilities.CommandsLoader;
import com.safjnest.Utilities.DatabaseHandler;
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

    public SetVoiceSlash() {
        this.name = this.getClass().getSimpleName().replace("Slash", "").toLowerCase();
        this.aliases = new CommandsLoader().getArray(this.name, "alias");
        this.help = new CommandsLoader().getString(this.name, "help");
        this.cooldown = new CommandsLoader().getCooldown(this.name);
        this.category = new Category(new CommandsLoader().getString(this.name, "category"));
        this.arguments = new CommandsLoader().getString(this.name, "arguments");
        this.options = Arrays.asList(
            new OptionData(OptionType.STRING, "voice", "Speaker name's voice", true));
        this.sql = DatabaseHandler.getSql();
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
        String query = "INSERT INTO guild_settings (guild_id, bot_id, language_tts, name_tts) VALUES ('" + event.getGuild().getId() + "', '" + event.getJDA().getSelfUser().getId() + "', '" + language + "', '" + voice + "') ON DUPLICATE KEY UPDATE language_tts = '" + language + "', name_tts = '" + voice + "'";
        sql.runQuery(query);
        event.deferReply(true).addContent("Voice set to " + voice).queue();
    }
}