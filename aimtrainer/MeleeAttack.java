package aimtrainer;

import java.awt.*;

public class MeleeAttack {
    int x, y, width, height;
    int duration = 10;
    boolean active = true;

    // ✅ กันเสียงซ้ำ
    boolean hasHit = false;

    public MeleeAttack(int playerX, int playerY, boolean facingRight) {
        width = 30;
        height = 20;
        y = playerY - 40;

        if (facingRight) {
            x = playerX + 40;
        } else {
            x = playerX - width;
        }
    }

    public void update() {
        duration--;
        if (duration <= 0) active = false;
    }

    public void draw(Graphics2D g) {
        g.setColor(Color.RED);
        g.fillRect(x, y, width, height);
    }

    public boolean hits(Target target) {
        Rectangle attackRect = new Rectangle(x, y, width, height);

        // ✅ เช็คโดน
        if (target.isHitByRect(attackRect)) {

            // 🔊 เล่นเสียงครั้งเดียว
            if (!hasHit) {
                SoundManager.playSound("hit.wav"); // 🔫 เสียงตีโดน
                hasHit = true;
            }

            return true;
        }

        return false;
    }
}