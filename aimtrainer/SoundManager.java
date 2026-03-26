package aimtrainer;

import java.net.URL;
import javax.sound.sampled.*;

public class SoundManager {

    private static Clip musicClip; // เก็บไว้ไม่ให้ถูก garbage

    public static void playMusic() {
        try {
            // ไฟล์ต้องอยู่ในโฟลเดอร์ aimtrainer (เดียวกับ Main + GamePanel)
            URL url = SoundManager.class.getResource("/aimtrainer/music.wav");
            if (url == null) {
                System.out.println("ไม่พบไฟล์ music.wav");
                return;
            }

            AudioInputStream audio = AudioSystem.getAudioInputStream(url);
            musicClip = AudioSystem.getClip();
            musicClip.open(audio);
            musicClip.loop(Clip.LOOP_CONTINUOUSLY); // เล่นวนซ้ำ
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
}