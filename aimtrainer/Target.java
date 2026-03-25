package aimtrainer;

import java.awt.*;
import javax.swing.*;
import java.util.Random;

public class Target {

    int x, y;
    int size = 80;

    int dx = 1;
    int dy = 0;

    int health = 100;
    int maxHealth = 100;
    final int DAMAGE = 10;

    double animTime = 0; // 🎯 ใช้ทำ animation

    Random rand = new Random();

    boolean facingRight = true;

    Image slime = new ImageIcon(getClass().getResource("slime.png")).getImage();

    public void setSize(int size){
        this.size = size;
    }

    public int takeDamage(){
        health -= DAMAGE;
        return DAMAGE;
    }

    public boolean isDead(){
        return health <= 0;
    }

    public int getHealth(){
        return health;
    }

    public int getMaxHealth(){
        return maxHealth;
    }

    public void spawn(int width, int height, int difficulty){
        x = width - size; // right side
        y = (int)(height * 0.75) - size / 2; // on yellow highlighted area
        
        // Set health based on difficulty
        switch(difficulty){
            case 0: // EASY
                health = maxHealth = 50;
                break;
            case 1: // NORMAL
                health = maxHealth = 100;
                break;
            case 2: // HARD
                health = maxHealth = 150;
                break;
        }
    }

    // 🎮 อัปเดตตำแหน่ง + animation - walk left right on ground
    public void update(int width, int height){

        x += dx;

        if(x <= 0){
            x = 0;
            dx = Math.abs(dx); // go right
            facingRight = true;
        } else if(x + size >= width){
            x = width - size;
            dx = -Math.abs(dx); // go left
            facingRight = false;
        }

        y = (int)(height * 0.75) - size / 2; // keep on yellow highlighted area

        // 🔥 ทำให้ slime ขยับ
        animTime += 0.2;
    }

    // 🎨 วาดแบบยืด-หด (นุ่ม ๆ)
    public void draw(Graphics2D g){
        if (slime == null) {
            g.setColor(Color.GREEN);
            g.fillOval(x, y, size, size);
        } else {
            // 🎯 ยืดแนวนอน/แนวตั้งสลับกัน
            double sx = 1 + 0.15 * Math.sin(animTime);
            double sy = 1 - 0.15 * Math.sin(animTime);

            double scaleX = facingRight ? sx : -sx;

            int w = (int)(size * Math.abs(scaleX));
            int h = (int)(size * sy);

            // วาดจากกลาง (ไม่สั่น)
            int drawX = x - (size - Math.abs(w))/2; // adjust for flip
            int drawY = y - (h - size)/2;

            g.drawImage(slime, drawX, drawY, w, h, null);
        }
        
        // Draw health bar above slime
        int barWidth = size;
        int barHeight = 8;
        int barX = x;
        int barY = y - 15;
        
        // Background (red)
        g.setColor(Color.RED);
        g.fillRect(barX, barY, barWidth, barHeight);
        
        // Health (green)
        g.setColor(Color.GREEN);
        int healthWidth = (int)(barWidth * health / (double)maxHealth);
        g.fillRect(barX, barY, healthWidth, barHeight);
        
        // Border
        g.setColor(Color.WHITE);
        g.setStroke(new BasicStroke(1));
        g.drawRect(barX, barY, barWidth, barHeight);
    }

    // 🔫 ยิงโดนไหม
    public boolean isHit(int mx, int my){
        return mx >= x && mx <= x + size &&
               my >= y && my <= y + size;
    }
}