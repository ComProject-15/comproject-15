package aimtrainer;

import java.awt.*;
import java.awt.geom.AffineTransform;
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

    float swordAngle = 0f;

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

        // อัพเดทมุมดาบ
        if (attacking) {
            float progress = 1f - (attackTimer / (float) ATTACK_DURATION);
            swordAngle = facingRight
                ? -60 + progress * 120
                : 60 - progress * 120;
        } else {
            swordAngle = facingRight ? -30 : 30;
        }
    }

    public void draw(Graphics2D g) {
        boolean visible = !(invincibleTimer > 0 && (invincibleTimer / 5) % 2 == 0);

        if (visible) {
            if (sprite == null) {
                g.setColor(Color.BLUE);
                g.fillRect(x, y, width, height);
            } else {
                if (facingRight)
                    g.drawImage(sprite, x, y, width, height, null);
                else
                    g.drawImage(sprite, x + width, y, -width, height, null);
            }
        }

        // วาดดาบแกว่งได้
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int pivotX = facingRight ? x + width - 20 : x + 20;
        int pivotY = y + height / 2 + 5;

        AffineTransform old = g.getTransform();
        g.rotate(Math.toRadians(swordAngle), pivotX, pivotY);

        // ด้ามจับ
        g.setColor(new Color(120, 80, 40));
        g.fillRect(pivotX - 6, pivotY - 8, 12, 16);

        // ใบดาบ
        g.setColor(new Color(200, 200, 230));
        if (facingRight)
            g.fillRect(pivotX, pivotY - 4, 40, 8);
        else
            g.fillRect(pivotX - 40, pivotY - 4, 40, 8);

        // ปลายดาบ
        g.setColor(new Color(220, 220, 255));
        int[] tipX, tipY;
        if (facingRight) {
            tipX = new int[]{pivotX + 40, pivotX + 55, pivotX + 40};
            tipY = new int[]{pivotY - 5, pivotY, pivotY + 5};
        } else {
            tipX = new int[]{pivotX - 40, pivotX - 55, pivotX - 40};
            tipY = new int[]{pivotY - 5, pivotY, pivotY + 5};
        }
        g.fillPolygon(tipX, tipY, 3);

        g.setTransform(old);

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