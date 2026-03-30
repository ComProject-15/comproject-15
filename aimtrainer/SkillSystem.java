package aimtrainer;

import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

/**
 * SkillSystem — จัดการ skill ทั้งหมดของ Player
 *
 * Skill 1 (Q) — Dash          : พุ่งตัวไปข้างหน้าอย่างรวดเร็ว พร้อม afterimage
 * Skill 2 (F) — Power Strike  : โจมตีหนักครั้งเดียว ดาเมจ 3× + knockback
 * Skill 3 (R) — Whirlwind     : หมุนโจมตีรอบตัว (unlock ที่ Lv 3)
 */
public class SkillSystem {

    // ── Skill Icons ───────────────────────────────────────────────────────────
    private Image iconDash;
    private Image iconWhirl;
    private Image iconPower;

    public SkillSystem() {
        try {
            iconDash  = ImageIO.read(getClass().getResource("/aimtrainer/skill_dash.png"));
            iconWhirl = ImageIO.read(getClass().getResource("/aimtrainer/skill_whirl.png"));
            iconPower = ImageIO.read(getClass().getResource("/aimtrainer/skill_power.png"));
        } catch (Exception e) {
            iconDash = iconWhirl = iconPower = null;
        }
    }

    // ── Dash (Q) ──────────────────────────────────────────────────────────────
    public static final int DASH_COOLDOWN_MAX   = 90;
    public static final int DASH_DURATION       = 10;
    public static final int DASH_SPEED          = 18;

    public int  dashCooldown  = 0;
    public int  dashTimer     = 0;
    public boolean dashing    = false;

    private final int MAX_AFTERIMAGE = 6;
    private final int[][] afterimagePos = new int[MAX_AFTERIMAGE][2];
    private int afterimageCount = 0;
    private int afterimageTick  = 0;

    // ── Power Strike (F) ──────────────────────────────────────────────────────
    public static final int POWER_COOLDOWN_MAX  = 180;
    public static final int POWER_DURATION      = 18;

    public int  powerCooldown = 0;
    public int  powerTimer    = 0;
    public boolean powerStriking = false;

    // cooldown reduction (จาก upgrade)
    public float dashCooldownReduction  = 0f;
    public float powerCooldownReduction = 0f;

    // ── Whirlwind (R – unlock Lv 3) ───────────────────────────────────────────
    public static final int WHIRL_COOLDOWN_MAX  = 300;
    public static final int WHIRL_DURATION      = 40;

    public int  whirlCooldown = 0;
    public int  whirlTimer    = 0;
    public boolean whirlwinding = false;

    // ──────────────────────────────────────────────────────────────────────────

    public void update(Player player) {
        if (dashCooldown  > 0) dashCooldown--;
        if (powerCooldown > 0) powerCooldown--;
        if (whirlCooldown > 0) whirlCooldown--;

        if (dashing) {
            if (afterimageTick++ % 2 == 0 && afterimageCount < MAX_AFTERIMAGE) {
                afterimagePos[afterimageCount][0] = player.x;
                afterimagePos[afterimageCount][1] = player.y;
                afterimageCount++;
            }
            player.x += player.facingRight ? DASH_SPEED : -DASH_SPEED;
            dashTimer--;
            if (dashTimer <= 0) { dashing = false; afterimageCount = 0; afterimageTick = 0; }
        }

        if (powerStriking) {
            powerTimer--;
            if (powerTimer <= 0) powerStriking = false;
        }

        if (whirlwinding) {
            whirlTimer--;
            if (whirlTimer <= 0) whirlwinding = false;
        }
    }

    // ── Activate ──────────────────────────────────────────────────────────────

    public boolean tryDash(Player player) {
        if (dashCooldown > 0 || dashing) return false;
        dashing      = true;
        dashTimer    = DASH_DURATION;
        dashCooldown = (int)(DASH_COOLDOWN_MAX * (1f - dashCooldownReduction));
        player.invincibleTimer = DASH_DURATION + 5;
        SoundManager.playSound("click.wav");
        return true;
    }

    public boolean tryPowerStrike(Player player) {
        if (powerCooldown > 0 || powerStriking) return false;
        powerStriking = true;
        powerTimer    = POWER_DURATION;
        powerCooldown = (int)(POWER_COOLDOWN_MAX * (1f - powerCooldownReduction));
        SoundManager.playSound("hit.wav");
        return true;
    }

    public boolean tryWhirlwind(int playerLevel) {
        if (playerLevel < 3) return false;
        if (whirlCooldown > 0 || whirlwinding) return false;
        whirlwinding  = true;
        whirlTimer    = WHIRL_DURATION;
        whirlCooldown = WHIRL_COOLDOWN_MAX;
        SoundManager.playSound("hit.wav");
        return true;
    }

    // ── Hitbox ────────────────────────────────────────────────────────────────

    public Rectangle getPowerHitbox(Player player) {
        if (!powerStriking) return null;
        int w = 100, h = 60;
        int hy = player.y + player.height / 2 - h / 2;
        return player.facingRight
                ? new Rectangle(player.x + player.width - 10, hy, w, h)
                : new Rectangle(player.x - w + 10, hy, w, h);
    }

    public Rectangle getWhirlHitbox(Player player) {
        if (!whirlwinding) return null;
        int radius = 90;
        return new Rectangle(
                player.x + player.width / 2 - radius,
                player.y + player.height / 2 - radius,
                radius * 2, radius * 2);
    }

    // ── Draw ──────────────────────────────────────────────────────────────────

    public void drawAfterimages(Graphics2D g, int playerW, int playerH) {
        for (int i = 0; i < afterimageCount; i++) {
            float alpha = (i + 1) / (float)(afterimageCount + 1) * 0.4f;
            g.setColor(new Color(100, 180, 255, (int)(alpha * 255)));
            g.fillRect(afterimagePos[i][0], afterimagePos[i][1], playerW, playerH);
        }
    }

    public void drawWhirlEffect(Graphics2D g, Player player, int animTick) {
        if (!whirlwinding) return;
        int cx = player.x + player.width / 2;
        int cy = player.y + player.height / 2;
        float progress = whirlTimer / (float) WHIRL_DURATION;
        for (int i = 0; i < 8; i++) {
            double angle = Math.toRadians(i * 45 + animTick * 12);
            int r = 70 + (int)(Math.sin(animTick * 0.2 + i) * 15);
            int sx = cx + (int)(Math.cos(angle) * r);
            int sy = cy + (int)(Math.sin(angle) * r);
            g.setColor(new Color(255, 220, 60, (int)(180 * progress)));
            g.fillOval(sx - 8, sy - 8, 16, 16);
        }
        g.setColor(new Color(255, 200, 0, (int)(80 * progress)));
        g.fillOval(cx - 90, cy - 90, 180, 180);
    }

    public void drawSkillBar(Graphics2D g, int panelW, int panelH, int playerLevel) {
        int barY = panelH - 70;
        int startX = panelW / 2 - 140;
        int slotSize = 52;
        int gap = 14;

        g.setColor(new Color(0, 0, 0, 160));
        g.fillRoundRect(startX - 10, barY - 10, 3 * (slotSize + gap) + 6, slotSize + 20, 14, 14);

        drawSlot(g, startX, barY, slotSize, "Q", "Dash",
                dashCooldown, DASH_COOLDOWN_MAX, true, dashing, new Color(60, 160, 255), iconDash);

        drawSlot(g, startX + slotSize + gap, barY, slotSize, "F", "Strike",
                powerCooldown, POWER_COOLDOWN_MAX, true, powerStriking, new Color(255, 100, 60), iconPower);

        boolean whirlUnlocked = playerLevel >= 3;
        drawSlot(g, startX + (slotSize + gap) * 2, barY, slotSize, "T", "Whirl",
                whirlCooldown, WHIRL_COOLDOWN_MAX, whirlUnlocked, whirlwinding,
                new Color(255, 220, 60), iconWhirl);
    }

    private void drawSlot(Graphics2D g, int x, int y, int size,
                          String key, String name,
                          int cd, int cdMax, boolean unlocked, boolean active,
                          Color accent, Image icon) {

        // พื้นหลัง slot
        g.setColor(active ? accent.darker() : new Color(30, 30, 50));
        g.fillRoundRect(x, y, size, size, 10, 10);

        // วาดรูป icon
        if (icon != null) {
            Composite oldComp = g.getComposite();
            float iconAlpha = unlocked ? 1.0f : 0.35f;
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, iconAlpha));
            g.drawImage(icon, x + 4, y + 4, size - 8, size - 8, null);
            g.setComposite(oldComp);
        }

        if (!unlocked) {
            // overlay มืดทับ + เครื่องหมาย lock
            g.setColor(new Color(0, 0, 0, 120));
            g.fillRoundRect(x, y, size, size, 10, 10);
            g.setColor(new Color(220, 220, 220));
            g.setFont(new Font("Arial", Font.BOLD, 20));
            g.drawString("?", x + size / 2 - 6, y + size / 2 + 7);
        } else if (cd > 0) {
            // cooldown overlay ทับบนรูป
            float ratio = cd / (float) cdMax;
            g.setColor(new Color(0, 0, 0, (int)(180 * ratio)));
            g.fillRoundRect(x, y, size, (int)(size * ratio), 10, 10);
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 14));
            String cdStr = String.format("%.1f", cd / 60.0f);
            FontMetrics fm = g.getFontMetrics();
            g.drawString(cdStr, x + (size - fm.stringWidth(cdStr)) / 2, y + size / 2 + 5);
        }

        g.setColor(unlocked ? accent.brighter() : Color.GRAY);
        g.setStroke(new BasicStroke(2));
        g.drawRoundRect(x, y, size, size, 10, 10);
        g.setStroke(new BasicStroke(1));

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 11));
        g.drawString(key, x + 3, y + 13);

        g.setFont(new Font("Arial", Font.PLAIN, 10));
        FontMetrics fm = g.getFontMetrics();
        g.drawString(name, x + (size - fm.stringWidth(name)) / 2, y + size + 14);
    }
}