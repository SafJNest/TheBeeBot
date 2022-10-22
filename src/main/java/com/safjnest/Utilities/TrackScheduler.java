package com.safjnest.Utilities;

import java.util.LinkedList;
import java.util.Queue;
import javax.annotation.OverridingMethodsMustInvokeSuper;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;

/**
 * 
 * This class schedules tracks for the audio player, handles the queue and manages
 * all the tracks.
 * <p>
 * Handles all the events that could occur during the listening:
 * <ul>
 * <li>start a track</li>
 * <li>stop a track</li>
 * <li>pause the track</li>
 * <li>resume the track</li>
 * <li>catch a TrackException</li>
 * </ul>
 * 
 * @author <a href="https://github.com/NeutronSun">NeutronSun</a>
 * @author <a href="https://github.com/Leon412">Leon412</a>
 * @since 1.0
 */
public class TrackScheduler extends AudioEventAdapter {

  /**Audioplayer that allows the bot reproduce a sound */
  AudioPlayer player;
  /**Queue that contains all the track */
  private Queue<AudioTrack> queue = new LinkedList<>();
  private PlayerManager publicMagister;

  /**
   *  constructor
   * @param player Comes from {@link com.safjnest.Commands.Audio.PlayYoutube Play} or {@link com.safjnest.Commands.Audio.PlaySound PlaySound}
   */
  public TrackScheduler(AudioPlayer player, PlayerManager pm) {
    this.player = player;
    this.publicMagister = pm;
  }

  /**
   * Add the track to the {@link com.safjnest.Utilities.TrackScheduler#queue queue}
   * @param track Comes from {@link com.safjnest.Commands.Audio.PlayYoutube Play} or {@link com.safjnest.Commands.Audio.PlaySound PlaySound}
   */
  @OverridingMethodsMustInvokeSuper
  public void addQueue(AudioTrack track) {
    queue.add(track);
  }

  /**
   * When a new track is required from 
   * {@link com.safjnest.Commands.Audio.PlayYoutube Play} 
   * or {@link com.safjnest.Commands.Audio.PlaySound PlaySound}
   * the method polls the first track in the {@link com.safjnest.Utilities.TrackScheduler#queue queue}
   * @return
   * {@code AudioTrack}
   * @throws InterruptedException
   */
  @OverridingMethodsMustInvokeSuper
  public AudioTrack getTrack() {
    try {
      while (queue.isEmpty()) {
        Thread.sleep(2);
      }
      Thread.sleep(5);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    return queue.poll();
  }

  @Override
  public void onPlayerPause(AudioPlayer player) {
    // Player was paused
  }

  @Override
  public void onPlayerResume(AudioPlayer player) {
    // Player was resumed
  }

  @Override
  public void onTrackStart(AudioPlayer player, AudioTrack track) {
    // A track started playing
  }

  @Override
  public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
      if(endReason.name().equals("FINISHED")){
        publicMagister.terminator3LeMacchineRibelli();
      }
      else if(endReason.name().equals("CLEANUP")){
        publicMagister.terminator3LeMacchineRibelli();
      }
    

    // endReason == FINISHED: A track finished or died by an exception (mayStartNext
    // = true).
    // endReason == LOAD_FAILED: Loading of a track failed (mayStartNext = true).
    // endReason == STOPPED: The player was stopped.
    // endReason == REPLACED: Another track started playing while this had not
    // finished
    // endReason == CLEANUP: Player hasn't been queried for a while, if you want you
    // can put a
    // clone of this back to your queue
  }

  @Override
  public void onTrackException(AudioPlayer player, AudioTrack track, FriendlyException exception) {
    System.out.println("Track exception");
  }

  @Override
  public void onTrackStuck(AudioPlayer player, AudioTrack track, long thresholdMs) {
    System.out.println("Track stuck");
  }
}