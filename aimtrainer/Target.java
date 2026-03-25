package aimtrainer;

import java.awt.*;
import javax.swing.*;
import java.util.Random;

public class Target {

    int x, y;
    int size = 80;

    int dx = 3;
    int dy = 3;

    double animTime = 0; // 🎯 ใช้ทำ animation

    Random rand = new Random();

    Image slime = new ImageIcon(getClass().getResource("/aimtrainer/slime.png")).getImage();

    public void setSize(int size){
        this.size = size;
    }

    public void spawn(int width, int height){
        x = rand.nextInt(width - size);
        y = rand.nextInt(height - size);

        dx = rand.nextInt(5) + 2;
        dy = rand.nextInt(5) + 2;
    }

    // 🎮 อัปเดตตำแหน่ง + animation
    public void update(int width, int height){

        x += dx;
        y += dy;

        if(x <= 0 || x + size >= width){
            dx *= -1;
        }

        if(y <= 0 || y + size >= height){
            dy *= -1;
        }

        // 🔥 ทำให้ slime ขยับ
        animTime += 0.2;
    }

    // 🎨 วาดแบบยืด-หด (นุ่ม ๆ)
    public void draw(Graphics2D g){

        // 🎯 ยืดแนวนอน/แนวตั้งสลับกัน
        double sx = 1 + 0.15 * Math.sin(animTime);
        double sy = 1 - 0.15 * Math.sin(animTime);

        int w = (int)(size * sx);
        int h = (int)(size * sy);

        // วาดจากกลาง (ไม่สั่น)
        int drawX = x - (w - size)/2;
        int drawY = y - (h - size)/2;

        g.drawImage(slime, drawX, drawY, w, h, null);
    }

    // 🔫 ยิงโดนไหม
    public boolean isHit(int mx, int my){
        return mx >= x && mx <= x + size &&
               my >= y && my <= y + size;
    }
}