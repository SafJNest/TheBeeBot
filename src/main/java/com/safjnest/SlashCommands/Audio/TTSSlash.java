//TODO RIFARE QUESTA CLASSE DI MERDA INGUARADFBILE :D | tu sei inguardabile
package com.safjnest.SlashCommands.Audio;

import java.awt.Color;
import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.safjnest.Utilities.tts.TTSHandler;
import com.safjnest.Utilities.tts.Voices;
import com.safjnest.Utilities.CommandsHandler;
import com.safjnest.Utilities.SQL;
import com.safjnest.Utilities.SafJNest;
import com.safjnest.Utilities.Audio.PlayerManager;
import com.safjnest.Utilities.Bot.BotSettingsHandler;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.managers.AudioManager;
import net.dv8tion.jda.api.utils.FileUpload;

import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;

public class TTSSlash extends SlashCommand{
    private String speech;
    private TTSHandler tts;
    private SQL sql;
    private PlayerManager pm;
    
    public static final HashMap<String, Set<String>> voices = new HashMap<String, Set<String>>();
    
    public TTSSlash(TTSHandler tts, SQL sql){
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


        this.name = this.getClass().getSimpleName().replace("Slash", "").toLowerCase();
        this.aliases = new CommandsHandler().getArray(this.name, "alias");
        this.help = new CommandsHandler().getString(this.name, "help");
        this.cooldown = new CommandsHandler().getCooldown(this.name);
        this.category = new Category(new CommandsHandler().getString(this.name, "category"));
        this.arguments = new CommandsHandler().getString(this.name, "arguments");
        this.options = Arrays.asList(
            new OptionData(OptionType.STRING, "text", "Text to be read", true),
            new OptionData(OptionType.STRING, "voice", "Change the reader's voice", false));
        this.tts = tts;
        this.sql = sql;

    
    }

    @Override
    protected void execute(SlashCommandEvent event) {
        String language = "it-it";
        String voice = "keria";
        String defaultVoice = "keria";
        EmbedBuilder eb = null;

        speech = event.getOption("text").getAsString();


        File file = new File("rsc" + File.separator + "tts");
        if(!file.exists())
            file.mkdirs();

        //checking the selected voice, otherwise default is used
        String query = "SELECT name_tts FROM tts_guilds WHERE discord_id = '" + event.getGuild().getId() + "';";
        if(sql.getString(query, "name_tts") != null && voice.equals("keria"))
            defaultVoice = sql.getString(query, "name_tts");
        
        //if the user chose a voice
        if(event.getOption("voice") != null){
            for(String key : voices.keySet()){ 
                if(voices.get(key).contains(event.getOption("voice").getAsString())){
                    language = key;
                    voice = event.getOption("voice").getAsString();
                }
            }
            if(voice.equals("keria")){
                event.deferReply(true).addContent("voice not found.").queue();
                return;
            }
        }//check if there is a default voice and the user hasnt asked for a specific speaker
        else if(!defaultVoice.equals("keria")){
            voice = defaultVoice;
            query = "SELECT language_tts FROM tts_guilds WHERE discord_id = '" + event.getGuild().getId() + "';"; 
            language = sql.getString(query, "language_tts");
        //used the default system voice
        }else{
            voice = "Mia"; 
            defaultVoice = "Not setted"; 
        }
        tts.makeSpeech(speech, event.getMember().getEffectiveName(), voice, language);
        
        String nameFile = "rsc" + File.separator + "tts" + File.separator + event.getMember().getEffectiveName() + ".mp3";
        
        pm = new PlayerManager();
        AudioChannel myChannel = event.getMember().getVoiceState().getChannel();
        AudioManager audioManager = event.getGuild().getAudioManager();

        audioManager.setSendingHandler(pm.getAudioHandler());

        audioManager.openAudioConnection(myChannel);

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

        pm.getPlayer().playTrack(pm.getTrackScheduler().getTrack());;
        
        eb = new EmbedBuilder();
        eb.setTitle("Playing now:");
        eb.addField("Lenght",SafJNest.getFormattedDuration(pm.getPlayer().getPlayingTrack().getInfo().length),true);
        eb.setAuthor(event.getMember().getEffectiveName(), "https://github.com/SafJNest",event.getMember().getAvatarUrl());
        eb.setFooter("*This is not SoundFx, this is much worse cit. steve jobs (probably)", null); //Questo non e' SoundFx, questa e' perfezione cit. steve jobs (probabilmente)

        eb.setDescription(speech);
        eb.addField("Language", language, true);
        eb.addBlankField(true);
        eb.addField("Voice", voice, true);
        eb.addField("Default voice", defaultVoice, true);
        eb.addBlankField(true);
        String img = "tts.png";
        eb.setColor(Color.decode(
            BotSettingsHandler.map.get(event.getJDA().getSelfUser().getId()).color
        ));
            

        File path = new File("rsc" + File.separator + "img" + File.separator + img);
        eb.setThumbnail("attachment://" + img);
        event.deferReply(false).addEmbeds(eb.build())
            .addFiles(FileUpload.fromData(path))
            .queue();
        
    }
}
