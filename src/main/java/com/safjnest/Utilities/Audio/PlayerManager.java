package com.safjnest.Utilities.Audio;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.local.LocalAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;

/**
 * I really would to know what this class does but i think quantum mechanics its easier to explain.
 * @author <a href="https://github.com/Leon412">Leon412</a>
 */
public class PlayerManager {
    private AudioPlayerManager playerManager;
    private AudioPlayer player;
    private AudioHandler audioPlayerSendHandler;
    private TrackScheduler trackScheduler;

    public PlayerManager(){
      this.playerManager = new DefaultAudioPlayerManager();
      this.player = playerManager.createPlayer();
      this.audioPlayerSendHandler = new AudioHandler(player);

      this.trackScheduler = new TrackScheduler(player, this);
      player.addListener(trackScheduler);

      playerManager.registerSourceManager(new LocalAudioSourceManager());
      playerManager.registerSourceManager(new YoutubeAudioSourceManager());
    }

    /**
     * EXTREME METHOD
     */
    public void terminator3LeMacchineRibelli(){
      player.destroy();
      playerManager.shutdown();
      audioPlayerSendHandler.stop();
    }

    public AudioHandler getAudioHandler(){
      return audioPlayerSendHandler;
    }

    public void stopAudioHandler(){
      audioPlayerSendHandler.stop();
    }

    public AudioPlayerManager getAudioPlayerManager(){
      return playerManager;
    }

    public TrackScheduler getTrackScheduler() {
      return trackScheduler;
    }

    public AudioPlayer getPlayer() {
      return player;
    }
}
