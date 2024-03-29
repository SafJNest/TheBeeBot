package com.safjnest.Commands.Audio;

import java.awt.Color;
import java.io.File;
import java.util.HashMap;
import java.util.Set;
import java.util.Map.Entry;

import org.voicerss.tts.Voice.Voices;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.safjnest.App;
import com.safjnest.Bot;
import com.safjnest.Utilities.CommandsLoader;
import com.safjnest.Utilities.SafJNest;
import com.safjnest.Utilities.Audio.PlayerManager;
import com.safjnest.Utilities.Audio.TTSHandler;
import com.safjnest.Utilities.SQL.DatabaseHandler;
import com.safjnest.Utilities.SQL.ResultRow;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;

import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;

/**
 * @author <a href="https://github.com/NeutronSun">NeutronSun</a>
 * @author <a href="https://github.com/Leon412">Leon412</a>
 * 
 * @since 1.0
 */
public class TTS extends Command{
    private TTSHandler tts;
    private PlayerManager pm;
    
    public static final HashMap<String, Set<String>> voices = new HashMap<String, Set<String>>();
    
    public TTS(){
        this.name = this.getClass().getSimpleName().toLowerCase();
        this.aliases = new CommandsLoader().getArray(this.name, "alias");
        this.help = new CommandsLoader().getString(this.name, "help");
        this.cooldown = new CommandsLoader().getCooldown(this.name);
        this.category = new Category(new CommandsLoader().getString(this.name, "category"));
        this.arguments = new CommandsLoader().getString(this.name, "arguments");

        this.tts = App.getTTS();
        this.pm = PlayerManager.get();

        voices.put(Voices.Arabic_Egypt.id, Set.of(Voices.Arabic_Egypt.array));
        voices.put(Voices.Chinese_China.id, Set.of(Voices.Chinese_China.array));
        voices.put(Voices.Dutch_Netherlands.id, Set.of(Voices.Dutch_Netherlands.array));
        voices.put(Voices.English_GreatBritain.id, Set.of(Voices.English_GreatBritain.array));
        voices.put(Voices.English_India.id, Set.of(Voices.English_India.array));
        voices.put(Voices.English_UnitedStates.id, Set.of(Voices.English_UnitedStates.array));
        voices.put(Voices.French_France.id, Set.of(Voices.French_France.array));
        voices.put(Voices.German_Germany.id, Set.of(Voices.German_Germany.array));
        voices.put(Voices.Greek.id, Set.of(Voices.Greek.array));
        voices.put(Voices.Italian.id, Set.of(Voices.Italian.array));
        voices.put(Voices.Japanese.id, Set.of(Voices.Japanese.array));
        voices.put(Voices.Korean.id, Set.of(Voices.Korean.array));
        voices.put(Voices.Polish.id, Set.of(Voices.Polish.array));
        voices.put(Voices.Portuguese_Portugal.id, Set.of(Voices.Portuguese_Portugal.array));
        voices.put(Voices.Romanian.id, Set.of(Voices.Romanian.array));
        voices.put(Voices.Russian.id, Set.of(Voices.Russian.array));
        voices.put(Voices.Swedish.id, Set.of(Voices.Swedish.array));
        voices.put(Voices.Spanish_Spain.id, Set.of(Voices.Spanish_Spain.array));
    }

    @Override
    protected void execute(CommandEvent event) {
        String voice = null, defaultVoice = null, language = null;
        String speech = event.getArgs();

        Guild guild = event.getGuild();
        User self = event.getSelfUser();
        AudioChannel myChannel = event.getMember().getVoiceState().getChannel();
        AudioChannel botChannel = guild.getSelfMember().getVoiceState().getChannel();

        if(speech.equals("")) {
            event.reply("Write the text you want to turn into speech (or list to get the list of voices).");
            return;
        }

        String firstWord = speech.split(" ", 2)[0];

        if(firstWord.equalsIgnoreCase("list")) {
            EmbedBuilder eb = new EmbedBuilder();
            eb.setTitle("Available languages:");
            eb.setColor(new Color(255, 196, 0));
            for(Entry<String, Set<String>> entry : voices.entrySet()){
                StringBuilder lang = new StringBuilder();
                StringBuilder voiceString = new StringBuilder();
                lang.append("**" + entry.getKey().toUpperCase() + "**:\n");
                for(String s : entry.getValue()){
                    voiceString.append(s + " - ");
                }
                eb.addField(lang.toString(), voiceString.toString(), true);
            }
            eb.setThumbnail(event.getSelfUser().getAvatarUrl());
            event.reply(eb.build());
            return;
        }

        if(myChannel == null){
            event.reply("You need to be in a voice channel to use this command.");
            return;
        }

        if(botChannel != null && (myChannel != botChannel)){
            event.reply("The bot is already being used in another voice channel.");
            return;
        }

        ResultRow defaultVoiceRow = DatabaseHandler.getDefaultVoice(guild.getId());
        if(!defaultVoiceRow.emptyValues())
            defaultVoice = defaultVoiceRow.get("name_tts");

        for(String key : voices.keySet()){
            if(voices.get(key).contains(firstWord)) {
                language = key;
                voice = firstWord;
                break;
            }
        }

        if(voice != null){
            speech = speech.split(" ", 2)[1];
        }
        else {
            if(defaultVoice != null) {
                voice = defaultVoice;
                language = defaultVoiceRow.get("language_tts");
            }
            else {
                voice = "Mia";
                language = "it-it";
            }
        }

        File file = new File("rsc" + File.separator + "tts");
        if(!file.exists())
            file.mkdirs();

        tts.makeSpeech(speech, event.getAuthor().getName(), voice, language);
        
        String ttsFileName = "rsc" + File.separator + "tts" + File.separator + event.getAuthor().getName() + ".mp3";

        pm.loadItemOrdered(guild, self, ttsFileName, new ResultHandler(event, voice, defaultVoice, language));
    }

    private class ResultHandler implements AudioLoadResultHandler {
        private final CommandEvent event;
        private final Guild guild;
        private final User self;
        private final Member author;
        private final String voice;
        private final String defaultVoice;
        private final String language;

        private ResultHandler(CommandEvent event, String voice, String defaultVoice, String language) {
            this.event = event;
            this.guild = event.getGuild();
            this.self = event.getSelfUser();
            this.author = event.getMember();
            this.voice = voice;
            this.defaultVoice = defaultVoice;
            this.language = language;
        }
        
        @Override
        public void trackLoaded(AudioTrack track) {
            pm.getGuildMusicManager(guild, self).getTrackScheduler().playForce(track);

            guild.getAudioManager().openAudioConnection(author.getVoiceState().getChannel());

            EmbedBuilder eb = new EmbedBuilder();
        
            eb.setTitle("Playing now:");
            eb.setDescription(event.getArgs());
            eb.setColor(Color.decode(Bot.getColor()));
            eb.setThumbnail(event.getSelfUser().getAvatarUrl());
            eb.setAuthor(event.getAuthor().getName(), "https://github.com/SafJNest", event.getAuthor().getAvatarUrl());
            
            eb.addField("Lenght", SafJNest.getFormattedDuration(track.getInfo().length),true);
            eb.addField("Language", language, true);
            eb.addBlankField(true);
            eb.addField("Voice", voice, true);
            eb.addField("Default voice", (defaultVoice == null ? "Not set" : defaultVoice), true);
            eb.addBlankField(true);

            event.reply(eb.build());
        }

        @Override
        public void playlistLoaded(AudioPlaylist playlist) {}

        @Override
        public void noMatches() {
            event.reply("No matches");
        }

        @Override
        public void loadFailed(FriendlyException throwable) {
            event.reply(throwable.getMessage());
        }
    }
}
