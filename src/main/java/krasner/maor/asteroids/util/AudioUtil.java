package krasner.maor.asteroids.util;

import lombok.experimental.UtilityClass;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.io.File;

@UtilityClass
public class AudioUtil {

    // function that plays Audio
    public void playAudio(String filename) {
        try {
            Clip clip = AudioSystem.getClip();
            clip.open(AudioSystem.getAudioInputStream(new File(filename)));
            clip.start();
        } catch (Exception ignored) {

        }
    }

    // function that plays long Audio
    public void playAudio2(String filename) {
        try {
            Clip clip = AudioSystem.getClip();
            clip.open(AudioSystem.getAudioInputStream(new File(filename)));
            clip.start();

            while (!clip.isRunning())
                Thread.sleep(10);
            while (clip.isRunning())
                Thread.sleep(10);
            clip.close();
        } catch (Exception e) {

        }
    }
}
