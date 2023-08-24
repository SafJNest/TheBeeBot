package com.safjnest.Utilities;

import java.io.File;
import java.io.FileOutputStream;

import org.voicerss.tts.Languages;
import org.voicerss.tts.Audio.AudioCodec;
import org.voicerss.tts.Audio.AudioFormat;
import org.voicerss.tts.Voice.VoiceParameters;
import org.voicerss.tts.Voice.VoiceProvider;
import org.voicerss.tts.Voice.Voices;

/**
 * Class that provides to generate {@code .mp3} files using TTS
 * @author <a href="https://github.com/NeutronSun">NeutronSun</a>
 */
public class TTSHandler {
    /**
     * The TTS provider
     * @see <a href="https://www.voicerss.org/sdk/java.aspx"> Voice RSS</a>
     */
    private VoiceProvider tts;
    String path = "rsc" + File.separator + "tts";

    /**
     * Default constructor
     * @param token
     */
    public TTSHandler(String token){
        tts = new VoiceProvider(token);
    }

    /**
     * Generates a {@code .mp3} gived a speech and the italian voice name
     * @param speech {@code String} with the text to be read
     * @param userName {@code String} name's voice
     */
    public void makeSpeech(String speech, String userName) {
        for (File file : new File(path).listFiles())
            file.delete();

        VoiceParameters params = new VoiceParameters(speech, Languages.Italian);
        params.setCodec(AudioCodec.MP3);
        params.setVoice(Voices.Italian.Pietro);
        params.setFormat(AudioFormat.Format_44KHZ.AF_44khz_16bit_stereo);
        params.setBase64(false);
        params.setSSML(false);
        params.setRate(0);
		try {
            byte[] voice = tts.speech(params);
            FileOutputStream fos = new FileOutputStream(path + File.separator + userName + ".mp3");
            fos.write(voice, 0, voice.length);
            fos.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Generates a {@code .mp3} gived a speech, the language and voice name
     * @param speech {@code String} with the text to be read
     * @param userName {@code String} user who ask the tts
     * @param voiceName {@code String} voice name
     * @param language {@code String} languages of the voice
     */
    public void makeSpeech(String speech, String userName, String voiceName, String language) {
        for (File file : new File(path).listFiles())
            file.delete();

        VoiceParameters params = new VoiceParameters(speech, language);
        params.setCodec(AudioCodec.MP3);
        params.setVoice(voiceName);
        params.setFormat(AudioFormat.Format_44KHZ.AF_44khz_16bit_stereo);
        params.setBase64(false);
        params.setSSML(false);
        params.setRate(0);
		try {
            byte[] voice = tts.speech(params);
            FileOutputStream fos = new FileOutputStream(path + File.separator + userName + ".mp3");
            fos.write(voice, 0, voice.length);
            fos.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
