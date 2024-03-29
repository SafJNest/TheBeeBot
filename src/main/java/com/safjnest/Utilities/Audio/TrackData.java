package com.safjnest.Utilities.Audio;

public class TrackData {
    private AudioType type;
    public TrackData(AudioType type) {
        this.type = type;
    }

    public boolean isQueueable() {
        return type == AudioType.AUDIO;
    }
    
}
