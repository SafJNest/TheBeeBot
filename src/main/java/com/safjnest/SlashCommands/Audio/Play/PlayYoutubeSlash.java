package com.safjnest.SlashCommands.Audio.Play;

import java.awt.Color;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.safjnest.Utilities.CommandsLoader;
import com.safjnest.Utilities.SafJNest;
import com.safjnest.Utilities.Audio.PlayerManager;
import com.safjnest.Utilities.Bot.BotSettingsHandler;
import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import org.json.simple.parser.JSONParser;

import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.managers.AudioManager;

/**
 * @author <a href="https://github.com/NeutronSun">NeutronSun</a>
 * @author <a href="https://github.com/Leon412">Leon412</a>
 * 
 * @since 1.0
 */
public class PlayYoutubeSlash extends SlashCommand {
    private String youtubeApiKey;
    private PlayerManager pm;

    public PlayYoutubeSlash(String youtubeApiKey, String father){
        this.name = this.getClass().getSimpleName().replace("Slash", "").replace(father, "").toLowerCase();
        this.help = new CommandsLoader().getString(name, "help", father.toLowerCase());
        this.cooldown = new CommandsLoader().getCooldown(this.name, father.toLowerCase());
        this.category = new Category(new CommandsLoader().getString(father.toLowerCase(), "category"));
        this.options = Arrays.asList(
            new OptionData(OptionType.STRING, "video", "Link or video name", true));
        this.youtubeApiKey = youtubeApiKey;
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
	protected void execute(SlashCommandEvent event) {
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

        String video = event.getOption("video").getAsString();
        String toPlay = getVideoIdFromYoutubeUrl(video);
        if(toPlay == null){
            try {
                URL theUrl = new URL("https://www.googleapis.com/youtube/v3/search?part=snippet&type=video&maxResults=1&q=" + video.replace(" ", "+") + "&key=" + youtubeApiKey);
                URLConnection request = theUrl.openConnection();
                request.connect();
                JSONParser parser = new JSONParser();
                JSONObject json = (JSONObject) parser.parse(new InputStreamReader((InputStream) request.getContent()));
                JSONArray items = (JSONArray) json.get("items");
                JSONObject item = (JSONObject) items.get(0);
                JSONObject id = (JSONObject) item.get("id");
                toPlay = (String) id.get("videoId");
            } catch (Exception e) {
                event.deferReply(true).addContent("Couldn't find a video for the given search.").queue();
                return;
            }
        }

        pm = new PlayerManager();
        
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
                event.deferReply(true).addContent("Not found").queue();
                pm.getTrackScheduler().addQueue(null);
            }

            @Override
            public void loadFailed(FriendlyException throwable) {
                event.deferReply(true).addContent(throwable.getMessage()).queue();
            }
        });

        pm.getPlayer().playTrack(pm.getTrackScheduler().getTrack());
        if(pm.getPlayer().getPlayingTrack() == null) {
            return;
        }

        AudioManager audioManager = event.getGuild().getAudioManager();
        audioManager.setSendingHandler(pm.getAudioHandler());
        audioManager.openAudioConnection(myChannel);

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Playing now:");
        eb.setAuthor(event.getJDA().getSelfUser().getName(), "https://github.com/SafJNest",event.getJDA().getSelfUser().getAvatarUrl());
        eb.setColor(Color.decode(BotSettingsHandler.map.get(event.getJDA().getSelfUser().getId()).color));
        eb.setDescription("[" + pm.getPlayer().getPlayingTrack().getInfo().title + "](" + pm.getPlayer().getPlayingTrack().getInfo().uri + ")");
        eb.setThumbnail("https://img.youtube.com/vi/" + pm.getPlayer().getPlayingTrack().getIdentifier() + "/hqdefault.jpg");
        eb.addField("Lenght", SafJNest.getFormattedDuration(pm.getPlayer().getPlayingTrack().getInfo().length) , true);

        event.deferReply(false).addEmbeds(eb.build()).queue();
	
    }
}