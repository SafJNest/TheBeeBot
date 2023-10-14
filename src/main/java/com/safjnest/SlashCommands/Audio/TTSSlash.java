package com.safjnest.SlashCommands.Audio;

import java.awt.Color;
import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;

import org.voicerss.tts.Voice.Voices;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.safjnest.Utilities.CommandsLoader;
import com.safjnest.Utilities.SafJNest;
import com.safjnest.Utilities.TTSHandler;
import com.safjnest.Utilities.Audio.PlayerManager;
import com.safjnest.Utilities.Bot.BotSettingsHandler;
import com.safjnest.Utilities.SQL.DatabaseHandler;
import com.safjnest.Utilities.SQL.ResultRow;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.managers.AudioManager;

import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;

public class TTSSlash extends SlashCommand{
    private TTSHandler tts;
    private PlayerManager pm;
    
    public static final HashMap<String, Set<String>> voices = new HashMap<String, Set<String>>();
    
    public TTSSlash(TTSHandler tts){
        this.name = this.getClass().getSimpleName().replace("Slash", "").toLowerCase();
        this.aliases = new CommandsLoader().getArray(this.name, "alias");
        this.help = new CommandsLoader().getString(this.name, "help");
        this.cooldown = new CommandsLoader().getCooldown(this.name);
        this.category = new Category(new CommandsLoader().getString(this.name, "category"));
        this.arguments = new CommandsLoader().getString(this.name, "arguments");
        this.options = Arrays.asList(
            new OptionData(OptionType.STRING, "text", "Text to be read", true),
            new OptionData(OptionType.STRING, "voice", "Reader's voice (also language)", false)
                .setAutoComplete(true)
        );

        this.tts = tts;

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
    protected void execute(SlashCommandEvent event) {
        String voice = null, defaultVoice = null, language = null;
        String speech = event.getOption("text").getAsString();
        EmbedBuilder eb;

        AudioChannel myChannel = event.getMember().getVoiceState().getChannel();
        AudioChannel botChannel = event.getGuild().getSelfMember().getVoiceState().getChannel();
        
        if(myChannel == null){
            event.deferReply(true).addContent("You need to be in a voice channel to use this command.").queue();
            return;
        }

        if(botChannel != null && (myChannel != botChannel)){
            event.deferReply(true).addContent("The bot is already being used in another voice channel.").queue();
            return;
        }

        ResultRow defaultVoiceRow = DatabaseHandler.getDefaultVoice(event.getGuild().getId(), event.getJDA().getSelfUser().getId());
        if(!defaultVoiceRow.emptyValues())
            defaultVoice = defaultVoiceRow.get("name_tts");

        if(event.getOption("voice") != null) {
            String possibleVoice = event.getOption("voice").getAsString();
            for(String key : voices.keySet()) {
                if(voices.get(key).contains(possibleVoice)) {
                    language = key;
                    voice = possibleVoice;
                    break;
                }
            }
        }

        if(voice == null) {
            if(defaultVoice != null) {
                voice = defaultVoice;
                language = defaultVoiceRow.get("language_tts");
            }
            else {
                voice = "mia";
                language = "it-it";
            }
        }

        File file = new File("rsc" + File.separator + "tts");
        if(!file.exists())
            file.mkdirs();

        tts.makeSpeech(speech, event.getMember().getEffectiveName(), voice, language);
        
        String nameFile = "rsc" + File.separator + "tts" + File.separator + event.getMember().getEffectiveName() + ".mp3";
        
        pm = new PlayerManager();
        
        AudioManager audioManager = event.getGuild().getAudioManager();
        audioManager.setSendingHandler(pm.getAudioHandler());
        

        pm.getAudioPlayerManager().loadItem(nameFile, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                pm.getTrackScheduler().addQueue(track);
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                /*
                 * for (AudioTrack track : playlist.getTracks()) {
                 * trackScheduler.queue(track);
                 * }
                 */
            }
            
            @Override
            public void noMatches() {
                event.deferReply(true).addContent("Not found").queue();
                pm.getTrackScheduler().addQueue(null);
            }

            @Override
            public void loadFailed(FriendlyException throwable) {
                System.out.println("error: " + throwable.getMessage());
            }
        });

        pm.getPlayer().playTrack(pm.getTrackScheduler().getTrack());
        if(pm.getPlayer().getPlayingTrack() == null) {
            return;
        }

        audioManager.openAudioConnection(myChannel);
        
        eb = new EmbedBuilder();
        eb.setTitle("Playing now:");
        eb.setAuthor(event.getMember().getEffectiveName(), "https://github.com/SafJNest",event.getMember().getAvatarUrl());
        eb.setColor(Color.decode(BotSettingsHandler.map.get(event.getJDA().getSelfUser().getId()).color));
        eb.setThumbnail(event.getJDA().getSelfUser().getAvatarUrl());
        eb.setDescription(speech);

        eb.addField("Lenght",SafJNest.getFormattedDuration(pm.getPlayer().getPlayingTrack().getInfo().length),true);
        eb.addField("Language", language, true);
        eb.addBlankField(true);
        eb.addField("Voice", voice, true);
        eb.addField("Default voice", (defaultVoice == null ? "Not setted" : defaultVoice), true);
        eb.addBlankField(true);

        event.deferReply(false).addEmbeds(eb.build()).queue();
    }
}
