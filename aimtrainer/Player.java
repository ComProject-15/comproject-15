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
    int baseAttackDamage = 12;
    int attackDamage = 12;

    int invincibleTimer = 0;
    final int INVINCIBLE_DURATION = 60;

    float swordAngle = 0f;

    ShieldItem shield = new ShieldItem();

    boolean attackBuffActive = false;
    int attackBuffTimer = 0;
    final int ATTACK_BUFF_DURATION = 480;

    // ── Skill & Level ──────────────────────────────────────────────────────
    public SkillSystem skills = new SkillSystem();
    public LevelSystem  levelSys = new LevelSystem();

    public Player(int x, int y) {
        this.x = x;
        this.y = y;
        try {
            sprite = ImageIO.read(getClass().getResource("/aimtrainer/player.png"));
        } catch (IOException | IllegalArgumentException e) {
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

    public void heal(int amount) {
        health = Math.min(health + amount, maxHealth);
    }

    public void activateAttackBuff() {
        attackBuffActive = true;
        attackBuffTimer = ATTACK_BUFF_DURATION;
        attackDamage = baseAttackDamage * 2;
    }

    public Rectangle getAttackHitbox() {
        if (!attacking) return null;
        int attackWidth = 60, attackHeight = 40;
        int hitboxY = y + height / 2 - attackHeight / 2;
        if (facingRight)
            return new Rectangle(x + width - 10, hitboxY, attackWidth, attackHeight);
        else
            return new Rectangle(x - attackWidth + 10, hitboxY, attackWidth, attackHeight);
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

        shield.update();

        if (attackBuffActive) {
            attackBuffTimer--;
            if (attackBuffTimer <= 0) {
                attackBuffActive = false;
                attackDamage = baseAttackDamage;
            }
        }

        if (attacking) {
            float progress = 1f - (attackTimer / (float) ATTACK_DURATION);
            swordAngle = facingRight ? -60 + progress * 120 : 60 - progress * 120;
        } else {
            swordAngle = facingRight ? -30 : 30;
        }

        // อัพเดต Skill System
        skills.update(this);
    }

    public void draw(Graphics2D g) {
        // วาด afterimage ของ Dash
        skills.drawAfterimages(g, width, height);

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

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int pivotX = facingRight ? x + width - 20 : x + 20;
        int pivotY = y + height / 2 + 5;

        AffineTransform old = g.getTransform();
        g.rotate(Math.toRadians(swordAngle), pivotX, pivotY);

        // Power Strike effect – ดาบเรืองแสงแดงเมื่อ active
        boolean powerActive = skills.powerStriking;
        Color bladeColor = powerActive ? new Color(255, 80, 0)
                : (attackBuffActive ? new Color(255, 150, 50) : new Color(200, 200, 230));
        Color tipColor = powerActive ? new Color(255, 200, 0)
                : (attackBuffActive ? new Color(255, 80, 0) : new Color(220, 220, 255));

        g.setColor(new Color(120, 80, 40));
        g.fillRect(pivotX - 6, pivotY - 8, 12, 16);

        // Power Strike ทำให้ดาบยาวขึ้น
        int bladeLen = powerActive ? 60 : 40;
        g.setColor(bladeColor);
        if (facingRight) g.fillRect(pivotX, pivotY - 4, bladeLen, 8);
        else             g.fillRect(pivotX - bladeLen, pivotY - 4, bladeLen, 8);

        g.setColor(tipColor);
        int[] tipX, tipY;
        if (facingRight) {
            tipX = new int[]{pivotX+bladeLen, pivotX+bladeLen+15, pivotX+bladeLen};
            tipY = new int[]{pivotY-5, pivotY, pivotY+5};
        } else {
            tipX = new int[]{pivotX-bladeLen, pivotX-bladeLen-15, pivotX-bladeLen};
            tipY = new int[]{pivotY-5, pivotY, pivotY+5};
        }
        g.fillPolygon(tipX, tipY, 3);

        // Power Strike glow
        if (powerActive) {
            g.setColor(new Color(255, 100, 0, 80));
            if (facingRight) g.fillRect(pivotX, pivotY - 12, bladeLen + 20, 24);
            else             g.fillRect(pivotX - bladeLen - 20, pivotY - 12, bladeLen + 20, 24);
        }

        g.setTransform(old);

        drawStatusBars(g);

        // วาด EXP bar
        levelSys.drawExpBar(g, this);

        if (shield.active) {
            float alpha = 0.3f + 0.3f * (float)Math.sin(System.currentTimeMillis() * 0.01);
            g.setColor(new Color(0, 150, 255, (int)(alpha * 255)));
            g.setStroke(new BasicStroke(4));
            g.drawOval(x - 10, y - 10, width + 20, height + 20);
            g.setStroke(new BasicStroke(1));
        }

        if (attackBuffActive) {
            float alpha = 0.2f + 0.2f * (float)Math.sin(System.currentTimeMillis() * 0.015);
            g.setColor(new Color(255, 100, 0, (int)(alpha * 255)));
            g.setStroke(new BasicStroke(3));
            g.drawOval(x - 8, y - 8, width + 16, height + 16);
            g.setStroke(new BasicStroke(1));
        }

        // Whirlwind effect (วาดใน GamePanel แทนเพราะต้องใช้ animTick)
    }

    public void drawStatusBars(Graphics2D g) {
        int barX = x;
        int barW = width;
        int barH = 8;

        int hpY = y - 15;
        g.setColor(Color.RED);
        g.fillRect(barX, hpY, barW, barH);
        g.setColor(Color.GREEN);
        g.fillRect(barX, hpY, (int)(barW * health / (double)maxHealth), barH);
        g.setColor(Color.WHITE);
        g.drawRect(barX, hpY, barW, barH);

        if (shield.active) {
            int sY = hpY - 12;
            g.setColor(new Color(0, 80, 180));
            g.fillRect(barX, sY, barW, barH);
            g.setColor(new Color(0, 200, 255));
            g.fillRect(barX, sY, (int)(barW * shield.getProgress()), barH);
            g.setColor(Color.WHITE);
            g.drawRect(barX, sY, barW, barH);
            g.setFont(new Font("Arial", Font.BOLD, 9));
            g.drawString("SHIELD", barX + 2, sY + 7);
        }

        if (attackBuffActive) {
            int aY = (shield.active ? hpY - 24 : hpY - 12);
            float progress = attackBuffTimer / (float) ATTACK_BUFF_DURATION;
            g.setColor(new Color(150, 50, 0));
            g.fillRect(barX, aY, barW, barH);
            g.setColor(new Color(255, 150, 0));
            g.fillRect(barX, aY, (int)(barW * progress), barH);
            g.setColor(Color.WHITE);
            g.drawRect(barX, aY, barW, barH);
            g.setFont(new Font("Arial", Font.BOLD, 9));
            g.drawString("ATK x2", barX + 2, aY + 7);
        }
    }

    public int takeDamage(int damage) {
        if (invincibleTimer > 0) return 0;
        if (shield.active) return 0;
        health -= damage;
        if (health < 0) health = 0;
        invincibleTimer = INVINCIBLE_DURATION;
        return damage;
    }

    public boolean isInvincible() { return invincibleTimer > 0; }
    public int getHealth() { return health; }
    public int getMaxHealth() { return maxHealth; }
}