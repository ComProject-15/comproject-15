package aimtrainer;

import java.net.URL;
import javax.sound.sampled.*;

public class SoundManager {

    private static Clip musicClip;
    private static float globalVolume = 1.0f; // 0.0 - 1.0

    // ===== ปรับ volume ทั้งหมด =====
    public static void setVolume(float volume) {
        globalVolume = Math.max(0f, Math.min(1f, volume));

        // ถ้าเพลง background กำลังเล่นอยู่ ให้ปรับทันที
        if (musicClip != null && musicClip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
            applyGain((FloatControl) musicClip.getControl(FloatControl.Type.MASTER_GAIN));
        }
    }

    // แปลง 0.0-1.0 → decibel แล้วเซ็ตลง FloatControl
    private static void applyGain(FloatControl gain) {
        float dB = (globalVolume == 0f)
            ? gain.getMinimum()
            : (float)(20.0 * Math.log10(Math.max(globalVolume, 0.0001f)));
        dB = Math.max(gain.getMinimum(), Math.min(gain.getMaximum(), dB));
        gain.setValue(dB);
    }

    // ===== เพลงพื้นหลัง =====
    public static void playMusic() {
        try {
            URL url = SoundManager.class.getResource("/aimtrainer/music.wav");
            if (url == null) { System.out.println("ไม่พบไฟล์ music.wav"); return; }

            AudioInputStream audio = AudioSystem.getAudioInputStream(url);
            musicClip = AudioSystem.getClip();
            musicClip.open(audio);

            // ปรับ volume ตาม globalVolume
            if (musicClip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                applyGain((FloatControl) musicClip.getControl(FloatControl.Type.MASTER_GAIN));
            }

            musicClip.loop(Clip.LOOP_CONTINUOUSLY);
            musicClip.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void stopMusic() {
        if (musicClip != null && musicClip.isRunning()) musicClip.stop();
    }

    // ===== เสียง SFX =====
    public static void playSound(String fileName) {
        if (globalVolume == 0f) return; // muted → ไม่เล่น

        try {
            URL url = SoundManager.class.getResource("/aimtrainer/" + fileName);
            if (url == null) { System.out.println("ไม่พบไฟล์: " + fileName); return; }

            AudioInputStream audio = AudioSystem.getAudioInputStream(url);
            Clip clip = AudioSystem.getClip();
            clip.open(audio);

            // ปรับ volume
            if (clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                applyGain((FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN));
            }

            clip.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}