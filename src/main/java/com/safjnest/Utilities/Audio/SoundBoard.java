package com.safjnest.Utilities.Audio;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.UnsupportedTagException;

import org.gagravarr.ogg.OggFile;
import org.gagravarr.opus.OpusFile;
import org.gagravarr.opus.OpusStatistics;

/**
 * Contains the methods to manage the soundboard.
 * @author <a href="https://github.com/NeutronSun">NeutronSun</a>
 * @since 1.1
 */
public class SoundBoard {
    /**Path of the folder where the sound are downlaoded */
    private static String path = "rsc" + File.separator + "SoundBoard";
    private static File folder = new File(path);

    /**
     * Check if a sound is already in the soundboard.
     * @param nameFile
     * name of the file without the extension
     * @return
     * a {@code String} with the extension otherwise {@code null}
     */
    public static String containsFile(String nameFile){
        String[] names = getAllNamesNoExc();
        String[] namesEx = getAllNames();
        for(int i = 0; i < names.length; i++)
            if(names[i].equalsIgnoreCase(nameFile))
                return namesEx[i];
        return null;
    }

    /**
     * @deprecated
     * Get all the mp3 files in the soundboard.
     * @return
     * an array of {@code MP3File}
     */
    public static Mp3File[] getMP3File(){
        Mp3File[] files = new Mp3File[folder.listFiles().length];
        int i = 0;
        for(File file : folder.listFiles()){
            try {
                files[i] = new Mp3File(file);
                i++;
            } catch (UnsupportedTagException | InvalidDataException | IOException e) {e.printStackTrace();}
        }
        return files;
    }

    /**
     * Get a specific file from the soundboard.
     * @param name
     * name of the file without the extension
     * @return
     * the {@code MP3File} or {@code null}
     */
    public static Mp3File getMp3FileByName(String name){
        for(File file : folder.listFiles()){
            if(name.equalsIgnoreCase(file.getName().substring(0, file.getName().indexOf(".")))){
                try {
                    return new Mp3File(file);
                } catch (UnsupportedTagException | InvalidDataException | IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    /**
     * @deprecated
     * Get all the names of the mp3 files in the soundboard with the extension.
     * @return
     * an array of {@code String}
     */
    public static String[] getAllNames(){
        File[] arr = folder.listFiles();
        String[] names = new String[arr.length];
        for(int i = 0; i < arr.length; i++){
            names[i] = arr[i].getName();
        }
        return names;
    }

    /**
     * 
     * Get all the names of the mp3 files in the soundboard without the extension.
     * @return
     * an array of {@code String}
     */
    public static String[] getAllNamesNoExc(){
        File[] arr = folder.listFiles();
        String[] names = new String[arr.length];
        for(int i = 0; i < arr.length; i++){
            names[i] = arr[i].getName().replaceFirst("[.][^.]+$", "");
        }
        return names;
    }

    public static String getExtension(String name){
        File[] arr = folder.listFiles();
        for(File file : arr){
            if(name.equalsIgnoreCase(file.getName().substring(0, file.getName().indexOf("."))))
                return file.getName().substring(file.getName().indexOf(".") + 1);
        }
        return null;
    }

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
        File initialFile = new File(path);
        InputStream targetStream = new FileInputStream(initialFile);
        OggFile ogg = new OggFile(targetStream);
        OpusFile opus = new OpusFile(ogg);
        OpusStatistics stats = null;
        stats = new OpusStatistics(opus);
        stats.calculate();
        return stats.getDurationSeconds();
    }


}
