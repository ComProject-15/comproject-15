package aimtrainer;

import java.awt.*;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Player {

    int x, y;
    int width = 120, height = 120;
    int speed = 5;
    int velocityY = 0;
    boolean onGround = true;
    final int GRAVITY = 1;
    final int JUMP_POWER = -15;

    boolean attacking = false;
    int attackTimer = 0;
    final int ATTACK_DURATION = 14;

    int attackCooldown = 0;
    final int ATTACK_COOLDOWN = 30;

    boolean facingRight = true;

    Image sprite;

    int health = 150;
    int maxHealth = 150;
    int attackDamage = 12;

    int invincibleTimer = 0;
    final int INVINCIBLE_DURATION = 60;

    public Player(int x, int y) {
        this.x = x;
        this.y = y;
        try {
            sprite = ImageIO.read(getClass().getResource("/aimtrainer/player.png"));
        } catch (IOException | IllegalArgumentException e) {
            System.out.println("ไม่พบไฟล์ player.png! ใช้ fallback");
            sprite = null;
        }
    }

    public void moveLeft()  { x -= speed; facingRight = false; }
    public void moveRight() { x += speed; facingRight = true; }
    public void jump() {
        if (onGround) {
            velocityY = JUMP_POWER;
            onGround = false;
        }
    }

    public void attack() {
        if (!attacking && attackCooldown <= 0) {
            attacking = true;
            attackTimer = ATTACK_DURATION;
            attackCooldown = ATTACK_COOLDOWN;
        }
    }

    // 🔥 HITBOX ใหม่ (ใช้เอง ไม่ง้อ Sword)
    public Rectangle getAttackHitbox() {
        if (!attacking) return null;

        int attackWidth = 60;
        int attackHeight = 40;

        int hitboxY = y + height / 2 - attackHeight / 2;

        if (facingRight) {
            return new Rectangle(x + width - 10, hitboxY, attackWidth, attackHeight);
        } else {
            return new Rectangle(x - attackWidth + 10, hitboxY, attackWidth, attackHeight);
        }
    }

    public void update(int panelWidth, int panelHeight) {
        velocityY += GRAVITY;
        y += velocityY;

        int groundY = (int)(panelHeight * 0.75) - height / 2;
        if (y >= groundY) {
            y = groundY;
            velocityY = 0;
            onGround = true;
        }

        if (x < 0) x = 0;
        if (x + width > panelWidth) x = panelWidth - width;

        if (attacking) {
            attackTimer--;
            if (attackTimer <= 0) attacking = false;
        }

        if (attackCooldown > 0) attackCooldown--;

        if (invincibleTimer > 0) invincibleTimer--;
    }

    public void draw(Graphics2D g) {

        // กระพริบตอนโดนตี
        if (invincibleTimer > 0 && (invincibleTimer / 5) % 2 == 0) {
            drawHealthBar(g);
            return;
        }

        if (sprite == null) {
            g.setColor(Color.BLUE);
            g.fillRect(x, y, width, height);
        } else {
            if (facingRight)
                g.drawImage(sprite, x, y, width, height, null);
            else
                g.drawImage(sprite, x + width, y, -width, height, null);
        }

        // 🔥 วาด hitbox ตอนตี (debug มองเห็นเลย)
        Rectangle hitbox = getAttackHitbox();
        if (hitbox != null) {
            g.setColor(new Color(255, 0, 0, 120));
            g.fillRect(hitbox.x, hitbox.y, hitbox.width, hitbox.height);
        }

        drawHealthBar(g);
    }

    public void drawHealthBar(Graphics2D g) {
        int barX = x;
        int barY = y - 15;
        int barW = width;
        int barH = 8;

        g.setColor(Color.RED);
        g.fillRect(barX, barY, barW, barH);

        g.setColor(Color.GREEN);
        g.fillRect(barX, barY, (int)(barW * health / (double)maxHealth), barH);

        g.setColor(Color.WHITE);
        g.drawRect(barX, barY, barW, barH);
    }

    public int takeDamage(int damage) {
        if (invincibleTimer > 0) return 0;

        health -= damage;
        if (health < 0) health = 0;

        invincibleTimer = INVINCIBLE_DURATION;
        return damage;
    }

    public boolean isInvincible() { return invincibleTimer > 0; }
    public int getHealth() { return health; }
    public int getMaxHealth() { return maxHealth; }
}