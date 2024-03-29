package com.safjnest.Utilities.Audio;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;//everybody was mocking you?

import org.gagravarr.ogg.OggFile;
import org.gagravarr.opus.OpusFile;
import org.gagravarr.opus.OpusStatistics;

/**
 * Contains the methods to manage the soundboard.
 * @author <a href="https://github.com/NeutronSun">NeutronSun</a>
 * @since 1.1
 */
public class SoundBoard {

    public static OpusFile getOpus(String path) throws IOException{
        File initialFile = new File(path);
        InputStream targetStream = new FileInputStream(initialFile);
        OggFile ogg = new OggFile(targetStream);
        return new OpusFile(ogg);
    }

    public static double getOpusDuration(OpusFile opus) throws IOException {
        OpusStatistics stats = null;
        stats = new OpusStatistics(opus);
        stats.calculate();
        return stats.getDurationSeconds();
    }

    public static double getOpusDuration(String path) throws IOException {
        OpusFile opus = getOpus(path);
        return getOpusDuration(opus);
    }

}