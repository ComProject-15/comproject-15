package aimtrainer;

import java.awt.*;
import javax.imageio.ImageIO;
import java.io.IOException;

public class Player {

    int x, y;
    int width = 80, height = 80;
    int speed = 5;
    int velocityY = 0;
    boolean onGround = true;
    final int GRAVITY = 1;
    final int JUMP_POWER = -15;

    boolean attacking = false;
    int attackTimer = 0;
    final int ATTACK_DURATION = 10;

    int attackCooldown = 0;
    final int ATTACK_COOLDOWN = 30;

    boolean facingRight = true;

    Image sprite;

    // Player stats
    int health = 100;
    int maxHealth = 100;
    int attackDamage = 10;

    // ===== Invincibility frames หลังโดนตี =====
    int invincibleTimer = 0;
    final int INVINCIBLE_DURATION = 60; // 60 frame = ~1 วิ — ปรับได้
    // ============================================

    public Player(int x, int y){
        this.x = x;
        this.y = y;

        try {
            sprite = ImageIO.read(getClass().getResource("/aimtrainer/player.png"));
        } catch (IOException | IllegalArgumentException e) {
            System.out.println("ไม่พบไฟล์ player.png! ใช้ fallback สี่เหลี่ยมสีน้ำเงินแทน");
            sprite = null;
        }
    }

    // ============ MOVE ============
    public void moveLeft(){ x -= speed; facingRight = false; }
    public void moveRight(){ x += speed; facingRight = true; }
    public void jump(){ if(onGround){ velocityY = JUMP_POWER; onGround = false; } }

    // ============ ATTACK ============
    public void attack(){
        if(!attacking && attackCooldown <= 0){
            attacking = true;
            attackTimer = ATTACK_DURATION;
            attackCooldown = ATTACK_COOLDOWN;
        }
    }

    public Rectangle getAttackHitbox(){
        if(!attacking) return null;
        return facingRight ? new Rectangle(x + width, y + 20, 50, 40)
                           : new Rectangle(x - 50, y + 20, 50, 40);
    }

    public void drawAttackEffect(Graphics2D g){
        if(!attacking) return;
        float alpha = attackTimer / (float)ATTACK_DURATION;
        g.setColor(new Color(255, 200, 0, (int)(255 * alpha * 0.5f)));
        Rectangle hitbox = getAttackHitbox();
        if(hitbox != null){
            g.fillRect(hitbox.x, hitbox.y, hitbox.width, hitbox.height);
        }
        g.setColor(new Color(255, 150, 0, (int)(255 * alpha * 0.7f)));
        g.setStroke(new BasicStroke(2));
        if(facingRight){
            g.drawLine(x + width, y + 20, x + width + 50, y + 60);
            g.drawLine(x + width + 10, y + 10, x + width + 60, y + 50);
        } else {
            g.drawLine(x - 50, y + 20, x, y + 60);
            g.drawLine(x - 60, y + 10, x - 10, y + 50);
        }
    }

    // ============ UPDATE ============
    public void update(int panelWidth, int panelHeight){
        velocityY += GRAVITY;
        y += velocityY;
        int groundY = (int)(panelHeight * 0.75) - height / 2;
        if(y >= groundY){ y = groundY; velocityY = 0; onGround = true; }

        if(x < 0) x = 0;
        if(x + width > panelWidth) x = panelWidth - width;

        if(attacking){ attackTimer--; if(attackTimer <= 0) attacking = false; }

        if(attackCooldown > 0) attackCooldown--;

        // ===== ลด invincible timer ทุก frame =====
        if(invincibleTimer > 0) invincibleTimer--;
        // ==========================================
    }

    // ============ DRAW ============
    public void draw(Graphics2D g){
        // ===== กระพริบเมื่อ invincible =====
        if(invincibleTimer > 0 && (invincibleTimer / 5) % 2 == 0){
            drawHealthBar(g);
            drawAttackEffect(g);
            return;
        }
        // =====================================

        if(sprite == null){
            g.setColor(Color.BLUE);
            g.fillRect(x, y, width, height);
        } else {
            if(facingRight) g.drawImage(sprite, x, y, width, height, null);
            else g.drawImage(sprite, x + width, y, -width, height, null);
        }
        drawAttackEffect(g);
        drawHealthBar(g);
    }

    public void drawHealthBar(Graphics2D g){
        int barWidth = width;
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

    // ===== takeDamage เช็ค invincible ก่อนหักเลือด =====
    public int takeDamage(int damage){
        if(invincibleTimer > 0) return 0; // กำลัง invincible — ไม่รับดาเมจ

        health -= damage;
        if(health < 0) health = 0;

        invincibleTimer = INVINCIBLE_DURATION; // เริ่ม invincible หลังโดนตี
        return damage;
    }
    // =====================================================

    public boolean isInvincible(){ return invincibleTimer > 0; }

    public int getHealth(){ return health; }
    public int getMaxHealth(){ return maxHealth; }
}