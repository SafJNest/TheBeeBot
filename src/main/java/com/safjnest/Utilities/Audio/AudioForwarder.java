package com.safjnest.Utilities.Audio;
import java.nio.ByteBuffer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.playback.MutableAudioFrame;

import net.dv8tion.jda.api.audio.AudioSendHandler;

/**
 * Copilot doenst know what this class does
 * 
 * @author <a href="https://github.com/Leon412">Leon412</a>
 * @since 5.0
 */
public class AudioForwarder implements AudioSendHandler {
    private final AudioPlayer player;
    private final ByteBuffer buffer;
    private final MutableAudioFrame frame;
  
    public AudioForwarder(AudioPlayer player) {
      this.player = player;
      this.buffer = ByteBuffer.allocate(1024);
      this.frame = new MutableAudioFrame();
      this.frame.setBuffer(buffer);
    }
  
    @Override
    public boolean canProvide() {
        return player.provide(frame);
    }
  
    @Override
    public ByteBuffer provide20MsAudio() {
        return (ByteBuffer) buffer.flip();
    }
  
    @Override
    public boolean isOpus() {
      return true;
    }
    
}