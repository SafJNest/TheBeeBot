package com.safjnest.SlashCommands.Audio.Play;

import java.awt.Color;
import java.util.Arrays;

import com.safjnest.Bot;
import com.safjnest.Utilities.CommandsLoader;
import com.safjnest.Utilities.SafJNest;
import com.safjnest.Utilities.Audio.PlayerManager;
import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

/**
 * @author <a href="https://github.com/NeutronSun">NeutronSun</a>
 * @author <a href="https://github.com/Leon412">Leon412</a>
 * 
 * @since 1.0
 */
public class PlayYoutubeSlash extends SlashCommand {
    private PlayerManager pm;

    public PlayYoutubeSlash(String father) {
        this.name = this.getClass().getSimpleName().replace("Slash", "").replace(father, "").toLowerCase();
        this.help = new CommandsLoader().getString(name, "help", father.toLowerCase());
        this.cooldown = new CommandsLoader().getCooldown(this.name, father.toLowerCase());
        this.category = new Category(new CommandsLoader().getString(father.toLowerCase(), "category"));
        this.options = Arrays.asList(
            new OptionData(OptionType.STRING, "video", "Link or video name", true),
            new OptionData(OptionType.BOOLEAN, "force", "Force play", false)
        );
        this.pm = PlayerManager.get();
    }

	@Override
	protected void execute(SlashCommandEvent event) {
        String query = event.getOption("video").getAsString();
        Guild guild = event.getGuild();
        User self = event.getJDA().getSelfUser();
        AudioChannel myChannel = event.getMember().getVoiceState().getChannel();
        AudioChannel botChannel = guild.getSelfMember().getVoiceState().getChannel();

        if(myChannel == null){
            event.deferReply(true).addContent("You need to be in a voice channel to use this command.").queue();
            return;
        }

        if(botChannel != null && (myChannel != botChannel)){
            event.deferReply(true).addContent("The bot is already being used in another voice channel.").queue();
            return;
        }
        
        pm.loadItemOrdered(guild, self, query, new ResultHandler(event, false));
    }

    private class ResultHandler implements AudioLoadResultHandler {
        private final SlashCommandEvent event;
        private final Guild guild;
        private final User self;
        private final Member author;
        private final String args;
        private final boolean youtubeSearch;
        private final boolean force;
        
        private ResultHandler(SlashCommandEvent event, boolean youtubeSearch) {
            this.event = event;
            this.guild = event.getGuild();
            this.self = event.getJDA().getSelfUser();
            this.author = event.getMember();
            this.args = event.getOption("video").getAsString();
            this.youtubeSearch = youtubeSearch;
            this.force = event.getOption("force") != null && event.getOption("force").getAsBoolean();
        }
        
        @Override
        public void trackLoaded(AudioTrack track) {
            
        }

        @Override
        public void playlistLoaded(AudioPlaylist playlist) {
            EmbedBuilder eb = new EmbedBuilder();

            if(youtubeSearch) {
                AudioTrack track = playlist.getTracks().get(0);
                if(force) {
                    int seconds = SafJNest.extractSeconds(args);
                    if(seconds != -1)
                        pm.getGuildMusicManager(guild, self).getTrackScheduler().playForce(track, seconds*1000);
                    else
                        pm.getGuildMusicManager(guild, self).getTrackScheduler().playForce(track);

                    guild.getAudioManager().openAudioConnection(author.getVoiceState().getChannel());

                    
                    eb.setTitle("Added to play:");
                    eb.setDescription("[" + track.getInfo().title + "](" + track.getInfo().uri + ")");
                    eb.setThumbnail("https://img.youtube.com/vi/" + track.getIdentifier() + "/hqdefault.jpg");
                    eb.setColor(Color.decode(Bot.getColor()));
                    eb.setFooter("Queued by " + event.getMember().getEffectiveName(), event.getMember().getAvatarUrl());

                }
                else {
            
                    pm.getGuildMusicManager(guild, self).getTrackScheduler().queue(track);

                    guild.getAudioManager().openAudioConnection(author.getVoiceState().getChannel());

                    eb.setTitle("Added to queue:");
                    eb.setDescription("[" + track.getInfo().title + "](" + track.getInfo().uri + ")");
                    eb.setThumbnail("https://img.youtube.com/vi/" + track.getIdentifier() + "/hqdefault.jpg");
                    eb.setColor(Color.decode(Bot.getColor()));
                    eb.setFooter("Queued by " + event.getMember().getEffectiveName(), event.getMember().getAvatarUrl());

                }
            }
            else {
                java.util.List<AudioTrack> tracks = playlist.getTracks();
                for(AudioTrack track : tracks) {
                    pm.getGuildMusicManager(guild, self).getTrackScheduler().queueNoPlay(track);
                }
                
                guild.getAudioManager().openAudioConnection(author.getVoiceState().getChannel());

                eb.setTitle("Playlist queued (" + tracks.size() + " tracks):");
                eb.setDescription("[" + playlist.getName() + "](" + args + ")");
                eb.setThumbnail("https://img.youtube.com/vi/" + playlist.getTracks().get(0).getIdentifier() + "/hqdefault.jpg");
                eb.setColor(Color.decode(Bot.getColor()));
                eb.setFooter("Queued by " + event.getMember().getEffectiveName(), event.getMember().getAvatarUrl());
            }
            event.deferReply(false).addEmbeds(eb.build()).queue();
        }

        @Override
        public void noMatches() {
            if(!youtubeSearch) {
                pm.loadItemOrdered(guild, self, "ytsearch:" + args, new ResultHandler(event, true));
                return;
            }
            event.deferReply(true).addContent("No matches").queue();
        }

        @Override
        public void loadFailed(FriendlyException throwable) {
            event.reply(throwable.getMessage());
        }
    }
}