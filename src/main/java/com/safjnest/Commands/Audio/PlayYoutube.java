package com.safjnest.Commands.Audio;

import java.awt.Color;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.safjnest.Utilities.SafJNest;
import com.safjnest.Utilities.Audio.PlayerManager;
import com.safjnest.Utilities.Bot.BotSettingsHandler;
import com.safjnest.Utilities.Commands.CommandsHandler;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import org.json.simple.parser.JSONParser;

import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.managers.AudioManager;

/**
 * @author <a href="https://github.com/NeutronSun">NeutronSun</a>
 * @author <a href="https://github.com/Leon412">Leon412</a>
 * 
 * @since 1.0
 */
public class PlayYoutube extends Command {
    private String youtubeApiKey;
    private HashMap<String,String> tierOneLink;
    private PlayerManager pm;

    public PlayYoutube(String youtubeApiKey, HashMap<String,String> tierOneLink){
        this.name = this.getClass().getSimpleName();
        this.aliases = new CommandsHandler().getArray(this.name, "alias");
        this.help = new CommandsHandler().getString(this.name, "help");
        this.cooldown = new CommandsHandler().getCooldown(this.name);
        this.category = new Category(new CommandsHandler().getString(this.name, "category"));
        this.arguments = new CommandsHandler().getString(this.name, "arguments");
        this.youtubeApiKey = youtubeApiKey;
        this.tierOneLink = tierOneLink;
    }

    public static String getVideoIdFromYoutubeUrl(String youtubeUrl) {
        /*
            Possibile Youtube urls.
            http://www.youtube.com/watch?v=dQw4w9WgXcQ
            http://www.youtube.com/embed/dQw4w9WgXcQ
            http://www.youtube.com/v/dQw4w9WgXcQ
            http://www.youtube-nocookie.com/v/dQw4w9WgXcQ?version=3&hl=en_US&rel=0
            http://www.youtube.com/watch?v=dQw4w9WgXcQ
            http://www.youtube.com/watch?feature=player_embedded&v=dQw4w9WgXcQ
            http://www.youtube.com/e/dQw4w9WgXcQ
            http://youtu.be/dQw4w9WgXcQ
        */
        String pattern = "(?<=watch\\?v=|/videos/|embed\\/|youtu.be\\/|\\/v\\/|\\/e\\/|watch\\?v%3D|watch\\?feature=player_embedded&v=|%2Fvideos%2F|embed%\u200C\u200B2F|youtu.be%2F|%2Fv%2F)[^#\\&\\?\\n]*";
        Pattern compiledPattern = Pattern.compile(pattern);
        //url is youtube url for which you want to extract the id.
        Matcher matcher = compiledPattern.matcher(youtubeUrl);
        if (matcher.find()) {
            return matcher.group();
        }
        return null;
    }

	@Override
	protected void execute(CommandEvent event) {
        if(event.getMember().getVoiceState().getChannel() == null){
            event.reply("You need to be in a voice channel to use this command");
            return;
        }

        if(event.getSelfMember().getVoiceState().getChannel() != null && (event.getMember().getVoiceState().getChannel() != event.getSelfMember().getVoiceState().getChannel())){
            event.reply("The bot is used by someone else, dont be annoying and use another beebot instance.");
            return;
        }

        String toPlay = getVideoIdFromYoutubeUrl(event.getArgs());
        if(toPlay == null){
            try {
                URL theUrl = new URL("https://www.googleapis.com/youtube/v3/search?part=snippet&type=video&maxResults=1&q=" + event.getArgs().replace(" ", "+") + "&key=" + youtubeApiKey);
                System.out.println(theUrl);
                URLConnection request = theUrl.openConnection();
                request.connect();
                JSONParser parser = new JSONParser();
                JSONObject json = (JSONObject) parser.parse(new InputStreamReader((InputStream) request.getContent()));
                JSONArray items = (JSONArray) json.get("items");
                JSONObject item = (JSONObject) items.get(0);
                JSONObject id = (JSONObject) item.get("id");
                toPlay = (String) id.get("videoId");
            } catch (Exception e) {
                event.reply("No video found");
                return;
            }
        }

        pm = new PlayerManager();
        
        MessageChannel channel = event.getChannel();
        AudioChannel myChannel = event.getMember().getVoiceState().getChannel();
        AudioManager audioManager = event.getGuild().getAudioManager();
        audioManager.setSendingHandler(pm.getAudioHandler());
        audioManager.openAudioConnection(myChannel);
        
        pm.getAudioPlayerManager().loadItem(toPlay, new AudioLoadResultHandler() {
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
                channel.sendMessage("Not found").queue();
                pm.getTrackScheduler().addQueue(null);
            }

            @Override
            public void loadFailed(FriendlyException throwable) {
                event.reply(throwable.getMessage());
            }
        });

        pm.getPlayer().playTrack(pm.getTrackScheduler().getTrack());

        EmbedBuilder eb = new EmbedBuilder();
        eb = new EmbedBuilder();
        eb.setTitle("Playing now:");
        eb.addField("Lenght", SafJNest.getFormattedDuration(pm.getPlayer().getPlayingTrack().getInfo().length) , true);
        eb.setAuthor(event.getJDA().getSelfUser().getName(), "https://github.com/SafJNest",event.getJDA().getSelfUser().getAvatarUrl());
        eb.setFooter("*Questo non e' rhythm, questa e' perfezione cit. steve jobs (probabilmente)", null);
        eb.setColor(Color.decode(
                BotSettingsHandler.map.get(event.getJDA().getSelfUser().getId()).color
        ));
        eb.setDescription("[" + pm.getPlayer().getPlayingTrack().getInfo().title + "](" + pm.getPlayer().getPlayingTrack().getInfo().uri + ")");
        eb.setThumbnail("https://img.youtube.com/vi/" + pm.getPlayer().getPlayingTrack().getIdentifier() + "/hqdefault.jpg");
        event.reply(eb.build());
        
        if(tierOneLink.containsKey(pm.getPlayer().getPlayingTrack().getIdentifier()))
            channel.sendMessage(tierOneLink.get(pm.getPlayer().getPlayingTrack().getIdentifier())).queue();
	
    }
}