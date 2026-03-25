package aimtrainer;

import java.awt.*;

public class MeleeAttack {
    int x, y, width, height;
    int duration = 10; // จำนวน frame ที่ hitbox อยู่
    boolean active = true;

    public MeleeAttack(int playerX, int playerY, boolean facingRight) {
        width = 30;
        height = 20;
        y = playerY - 40; // ปรับให้สูงตรงกับตัวละคร
        if (facingRight) {
            x = playerX + 40; // ข้างขวาตัวละคร
        } else {
            x = playerX - width; // ข้างซ้ายตัวละคร
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
       return target.isHitByRect(attackRect);
    }
}