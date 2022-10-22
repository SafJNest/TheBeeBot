package com.safjnest.Utilities;

import java.io.File;
import java.io.FileOutputStream;

import com.safjnest.Utilities.tts.AudioCodec;
import com.safjnest.Utilities.tts.AudioFormat;
import com.safjnest.Utilities.tts.Languages;
import com.safjnest.Utilities.tts.VoiceParameters;
import com.safjnest.Utilities.tts.VoiceProvider;
import com.safjnest.Utilities.tts.Voices;

public class TTSHandler {
    private VoiceProvider tts;
    String path = "rsc" + File.separator + "tts";

    public TTSHandler(String token){
        tts = new VoiceProvider(token);
    }

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
