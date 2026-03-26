import java.net.URL;
import javax.sound.sampled.*;

public class SoundManager {

    public static void playMusic() {
        try {
            URL url = SoundManager.class.getResource("/music.wav");
            if (url == null) {
                System.out.println("ไม่พบไฟล์ music.wav");
                return;
            }

            AudioInputStream audio = AudioSystem.getAudioInputStream(url);
            Clip clip = AudioSystem.getClip();
            clip.open(audio);
            clip.loop(Clip.LOOP_CONTINUOUSLY);
            clip.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void playSound(String soundFile) {
        try {
            URL url = SoundManager.class.getResource("/" + soundFile);
            if (url == null) {
                System.out.println("ไม่พบไฟล์ " + soundFile);
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