package aimtrainer;

import java.net.URL;
import javax.sound.sampled.*;

public class SoundManager {

    private static Clip musicClip;

    // 🎵 เพลงพื้นหลัง
    public static void playMusic() {
        try {
            URL url = SoundManager.class.getResource("/aimtrainer/music.wav");
            if (url == null) {
                System.out.println("ไม่พบไฟล์ music.wav");
                return;
            }

            AudioInputStream audio = AudioSystem.getAudioInputStream(url);
            musicClip = AudioSystem.getClip();
            musicClip.open(audio);
            musicClip.loop(Clip.LOOP_CONTINUOUSLY);
            musicClip.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void stopMusic() {
        if (musicClip != null && musicClip.isRunning()) {
            musicClip.stop();
        }
    }

    // 🔊 เล่นเสียงเอฟเฟกต์ (ใช้ได้ทุกกรณี)
    public static void playSound(String fileName) {
        try {
            URL url = SoundManager.class.getResource("/aimtrainer/" + fileName);
            if (url == null) {
                System.out.println("ไม่พบไฟล์: " + fileName);
                return;
            }

            AudioInputStream audio = AudioSystem.getAudioInputStream(url);
            Clip clip = AudioSystem.getClip();
            clip.open(audio);
            clip.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}