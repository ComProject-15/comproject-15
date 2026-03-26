package aimtrainer;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import java.util.Random;

public class Target {

    int x, y;
    int size = 80;
    int dx = 2;

    int health = 100;
    int maxHealth = 100;
    final int DAMAGE = 10;

    double animTime = 0;
    Random rand = new Random();
    boolean facingRight = true;

    Image slime = new ImageIcon(getClass().getResource("slime.png")).getImage();

    boolean isAttacking = false;
    int attackTimer = 0;
    int attackCooldown = 0;
    final int ATTACK_DURATION  = 12;
    final int ATTACK_COOLDOWN  = 90;
    final int ATTACK_RANGE     = 80;
    final int SLIME_DAMAGE     = 10;

    boolean hitThisSwing = false;

    // ===== Death particles =====
    boolean isDying = false;
    int deathTimer  = 0;
    final int DEATH_DURATION = 35;

    static class Particle {
        float x, y, vx, vy, size, alpha;
        Color color;

        Particle(float x, float y, Random rand) {
            this.x = x;
            this.y = y;
            float angle = (float)(rand.nextFloat() * Math.PI * 2);
            float speed = 2f + rand.nextFloat() * 5f;
            vx = (float)Math.cos(angle) * speed;
            vy = (float)Math.sin(angle) * speed - 2f; // พุ่งขึ้นนิดนึง
            this.size = 6 + rand.nextFloat() * 12f;
            alpha = 1f;
            // สีน้ำเงิน/ฟ้าแบบ slime
            int r = 20  + rand.nextInt(60);
            int g = 160 + rand.nextInt(80);
            int b = 200 + rand.nextInt(55);
            color = new Color(r, g, b);
        }

        void update() {
            x += vx;
            y += vy;
            vy += 0.3f; // gravity
            vx *= 0.92f;
            alpha -= 1f / 30f;
            size  *= 0.95f;
        }

        boolean isAlive() { return alpha > 0 && size > 1; }
    }

    List<Particle> particles = new ArrayList<>();
    // ===========================

    public void setSize(int size) { this.size = size; }

    public int takeDamage() { health -= DAMAGE; return DAMAGE; }

    public boolean isDead() { return health <= 0; }

    // เรียกเพื่อเริ่ม death animation (จาก GamePanel ก่อน remove)
    public void startDeathEffect() {
        if (isDying) return;
        isDying = true;
        deathTimer = DEATH_DURATION;
        // spawn particles
        int cx = x + size / 2;
        int cy = y + size / 2;
        for (int i = 0; i < 28; i++) {
            particles.add(new Particle(cx, cy, rand));
        }
    }

    public boolean deathEffectDone() {
        return isDying && deathTimer <= 0 && particles.isEmpty();
    }

    public int getHealth()    { return health; }
    public int getMaxHealth() { return maxHealth; }

    public void resetHitThisSwing() { hitThisSwing = false; }

    public int takeDamageOnce() {
        if (hitThisSwing) return 0;
        hitThisSwing = true;
        return takeDamage();
    }

    public void spawn(int width, int height, int difficulty) {
        x = width - size;
        y = (int)(height * 0.75) - size / 2;
        switch (difficulty) {
            case 0: health = maxHealth = 50;  break;
            case 1: health = maxHealth = 100; break;
            case 2: health = maxHealth = 150; break;
        }
    }

    public void update(int width, int height) {
        if (isDying) { updateDeath(); return; }
        y = (int)(height * 0.75) - size / 2;
        animTime += 0.2;
        if (attackCooldown > 0) attackCooldown--;
        if (attackTimer > 0)    { attackTimer--; if (attackTimer <= 0) isAttacking = false; }
    }

    public void update(int width, int height, Player player) {
        if (isDying) { updateDeath(); return; }
        y = (int)(height * 0.75) - size / 2;
        animTime += 0.2;
        if (attackCooldown > 0) attackCooldown--;
        if (attackTimer > 0)    { attackTimer--; if (attackTimer <= 0) isAttacking = false; }

        int distX  = player.x - x;
        int absDistX = Math.abs(distX);

        if (absDistX <= ATTACK_RANGE) {
            if (attackCooldown <= 0) {
                isAttacking = true;
                attackTimer = ATTACK_DURATION;
                attackCooldown = ATTACK_COOLDOWN;
                player.takeDamage(SLIME_DAMAGE);
            }
        } else {
            if (distX > 0) { x += dx; facingRight = true;  }
            else            { x -= dx; facingRight = false; }
        }

        if (x <= 0)           x = 0;
        if (x + size >= width) x = width - size;
    }

    private void updateDeath() {
        deathTimer--;
        for (int i = particles.size() - 1; i >= 0; i--) {
            Particle p = particles.get(i);
            p.update();
            if (!p.isAlive()) particles.remove(i);
        }
    }

    public void draw(Graphics2D g) {
        // ถ้ากำลัง dying — วาดแค่ particles + ไม่วาด slime body
        if (isDying) {
            drawParticles(g);
            return;
        }

        // วาด slime ปกติ
        if (slime == null) {
            g.setColor(isAttacking ? Color.RED : Color.CYAN);
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

        // Attack effect
        if (isAttacking) {
            float alpha = attackTimer / (float)ATTACK_DURATION;
            g.setColor(new Color(255, 50, 50, (int)(180 * alpha)));
            int atkX = facingRight ? x - ATTACK_RANGE : x + size;
            g.fillRect(atkX, y + 10, ATTACK_RANGE, size - 20);
        }

        // ===== Hit flash เมื่อโดนตี (health < max และยังไม่ตาย) =====
        if (health < maxHealth && health > 0) {
            // วาด overlay ขาวจางๆ บน slime
            float flashAlpha = 0.25f;
            g.setColor(new Color(255, 255, 255, (int)(255 * flashAlpha)));
            g.fillOval(x, y, size, size);
        }

        // Health bar
        int barW = size, barH = 8, barX = x, barY = y - 15;
        g.setColor(Color.RED);   g.fillRect(barX, barY, barW, barH);
        g.setColor(Color.GREEN); g.fillRect(barX, barY, (int)(barW * health / (double)maxHealth), barH);
        g.setColor(Color.WHITE); g.setStroke(new BasicStroke(1)); g.drawRect(barX, barY, barW, barH);
    }

    private void drawParticles(Graphics2D g) {
        for (Particle p : particles) {
            int a = (int)(255 * Math.max(0, p.alpha));
            g.setColor(new Color(p.color.getRed(), p.color.getGreen(), p.color.getBlue(), a));
            int s = (int) p.size;
            g.fill(new Ellipse2D.Float(p.x - s / 2f, p.y - s / 2f, s, s));
        }
    }

    public boolean isHit(int mx, int my) {
        return mx >= x && mx <= x + size && my >= y && my <= y + size;
    }

    public boolean isHitByRect(Rectangle rect) {
        return rect.intersects(new Rectangle(x, y, size, size));
    }
}