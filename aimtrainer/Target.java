package aimtrainer;

import java.awt.*;
import javax.swing.*;
import java.util.Random;

public class Target {

    int x, y;
    int size = 80;

    int dx = 2;
    int dy = 0;

    int health = 100;
    int maxHealth = 100;
    final int DAMAGE = 10;

    double animTime = 0;

    Random rand = new Random();

    boolean facingRight = true;

    Image slime = new ImageIcon(getClass().getResource("slime.png")).getImage();

    // Attack properties
    boolean isAttacking = false;
    int attackTimer = 0;
    int attackCooldown = 0;
    final int ATTACK_DURATION = 12;
    final int ATTACK_COOLDOWN = 90;
    final int ATTACK_RANGE = 80;
    final int SLIME_DAMAGE = 10;

    // ===== กันโดนดาเมจซ้ำในการฟันเดียวกัน =====
    boolean hitThisSwing = false;
    // ============================================

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

    public int getHealth(){ return health; }
    public int getMaxHealth(){ return maxHealth; }

    // ===== เรียกตอน player เริ่ม attack ใหม่ =====
    public void resetHitThisSwing(){
        hitThisSwing = false;
    }

    // ===== takeDamageOnce — ตีได้ครั้งเดียวต่อ swing =====
    public int takeDamageOnce(){
        if(hitThisSwing) return 0;   // โดนแล้วในการฟันนี้ — ข้ามไป
        hitThisSwing = true;
        return takeDamage();
    }
    // =====================================================

    public void spawn(int width, int height, int difficulty){
        x = width - size;
        y = (int)(height * 0.75) - size / 2;

        switch(difficulty){
            case 0: health = maxHealth = 50; break;
            case 1: health = maxHealth = 100; break;
            case 2: health = maxHealth = 150; break;
        }
    }

    public void update(int width, int height){
        y = (int)(height * 0.75) - size / 2;
        animTime += 0.2;

        if(attackCooldown > 0) attackCooldown--;
        if(attackTimer > 0){
            attackTimer--;
            if(attackTimer <= 0) isAttacking = false;
        }
    }

    public void update(int width, int height, Player player){
        y = (int)(height * 0.75) - size / 2;
        animTime += 0.2;

        if(attackCooldown > 0) attackCooldown--;
        if(attackTimer > 0){
            attackTimer--;
            if(attackTimer <= 0) isAttacking = false;
        }

        int distX = player.x - x;
        int absDistX = Math.abs(distX);

        if(absDistX <= ATTACK_RANGE){
            if(attackCooldown <= 0){
                isAttacking = true;
                attackTimer = ATTACK_DURATION;
                attackCooldown = ATTACK_COOLDOWN;
                player.takeDamage(SLIME_DAMAGE);
            }
        } else {
            if(distX > 0){
                x += dx;
                facingRight = true;
            } else {
                x -= dx;
                facingRight = false;
            }
        }

        if(x <= 0) x = 0;
        if(x + size >= width) x = width - size;
    }

    public void draw(Graphics2D g){
        if(slime == null){
            g.setColor(isAttacking ? Color.RED : Color.GREEN);
            g.fillOval(x, y, size, size);
        } else {
            double sx = 1 + 0.15 * Math.sin(animTime);
            double sy = 1 - 0.15 * Math.sin(animTime);
            double scaleX = facingRight ? sx : -sx;
            int w = (int)(size * Math.abs(scaleX));
            int h = (int)(size * sy);
            int drawX = x - (size - Math.abs(w)) / 2;
            int drawY = y - (h - size) / 2;
            g.drawImage(slime, drawX, drawY, w, h, null);
        }

        if(isAttacking){
            float alpha = attackTimer / (float)ATTACK_DURATION;
            g.setColor(new Color(255, 50, 50, (int)(180 * alpha)));
            int atkX = facingRight ? x - ATTACK_RANGE : x + size;
            g.fillRect(atkX, y + 10, ATTACK_RANGE, size - 20);
        }

        int barWidth = size;
        int barHeight = 8;
        int barX = x;
        int barY = y - 15;
        g.setColor(Color.RED);
        g.fillRect(barX, barY, barWidth, barHeight);
        g.setColor(Color.GREEN);
        int healthWidth = (int)(barWidth * health / (double)maxHealth);
        g.fillRect(barX, barY, healthWidth, barHeight);
        g.setColor(Color.WHITE);
        g.setStroke(new BasicStroke(1));
        g.drawRect(barX, barY, barWidth, barHeight);
    }

    public boolean isHit(int mx, int my){
        return mx >= x && mx <= x + size && my >= y && my <= y + size;
    }

    public boolean isHitByRect(Rectangle rect){
        return rect.intersects(new Rectangle(x, y, size, size));
    }
}