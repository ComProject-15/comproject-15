package aimtrainer;

import java.net.URL;
import javax.sound.sampled.*;

public class TestPlay {
    public static void main(String[] args) {
        try {
            URL url = SoundManager.class.getResource("/aimtrainer/music.wav");
            System.out.println("URL = " + url);

            AudioInputStream audio = AudioSystem.getAudioInputStream(url);
            Clip clip = AudioSystem.getClip();
            clip.open(audio);
            clip.start();

            Thread.sleep(5000); // ให้เล่น 5 วินาที
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}