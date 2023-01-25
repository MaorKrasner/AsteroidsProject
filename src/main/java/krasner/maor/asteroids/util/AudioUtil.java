package krasner.maor.asteroids.util;

import lombok.experimental.UtilityClass;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.io.File;

/***
 * class that contains functions to display audio in the game
 */

@UtilityClass
public class AudioUtil {

    /***
     * function that play audio
     * @param filename - the name of the file to play
     */
    public void playAudio(String filename) {
        try {
            Clip clip = AudioSystem.getClip();
            clip.open(AudioSystem.getAudioInputStream(new File(filename)));
            clip.start();
        } catch (Exception ignored) {}
    }
}
