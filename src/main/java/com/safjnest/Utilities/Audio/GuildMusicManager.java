package com.safjnest.Utilities.Audio;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;

/**
 * Copilot doenst know what this class does
 * @author <a href="https://github.com/Leon412">Leon412</a>
 * @since 5.0
 */
public class GuildMusicManager {
    private TrackScheduler trackScheduler;
    private AudioForwarder audioForwarder;

    public GuildMusicManager(AudioPlayerManager manager) {
        AudioPlayer player = manager.createPlayer();
        this.trackScheduler = new TrackScheduler(player);
        player.addListener(trackScheduler);
        audioForwarder = new AudioForwarder(player);
    }

    public TrackScheduler getTrackScheduler() {
        return trackScheduler;
    }

    public AudioForwarder getAudioForwarder() {
        return audioForwarder;
    }
    
}
