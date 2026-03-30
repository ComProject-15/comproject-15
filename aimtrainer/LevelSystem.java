package aimtrainer;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * LevelSystem — ระบบ EXP / Level Up / Upgrade
 *
 * EXP เพิ่มขึ้นทุกครั้งที่ฆ่า slime
 * เมื่อ level up จะแสดง Upgrade Menu ให้เลือก 3 ตัวเลือก
 */
public class LevelSystem {

    public int  level  = 1;
    public int  exp    = 0;
    public int  expToNext = 100;   // EXP ที่ต้องการถึง level ถัดไป

    private static final int MAX_LEVEL = 10;

    // ── Upgrade Menu State ──────────────────────────────────────────────────
    public boolean showUpgradeMenu = false;
    private List<Upgrade> currentChoices = new ArrayList<>();
    private int hoveredIndex = -1;
    private int mouseX, mouseY;

    // ขนาด / ตำแหน่งของการ์ดอัพเกรด (คำนวณตอน draw)
    private Rectangle[] cardRects = new Rectangle[3];

    // ── Upgrade Definitions ─────────────────────────────────────────────────
    public enum UpgradeType {
        MAX_HP, ATTACK, SPEED, DASH_CD, POWER_CD, REGEN
    }

    public static class Upgrade {
        public UpgradeType type;
        public String title;
        public String desc;
        public Color  color;

        public Upgrade(UpgradeType t, String title, String desc, Color color) {
            this.type = t; this.title = title; this.desc = desc; this.color = color;
        }
    }

    private static final Upgrade[] ALL_UPGRADES = {
        new Upgrade(UpgradeType.MAX_HP,   "❤ Vitality",   "+30 Max HP & Heal 30",      new Color(220,60,80)),
        new Upgrade(UpgradeType.ATTACK,   "⚔ Might",      "+5 Attack Damage",           new Color(255,140,40)),
        new Upgrade(UpgradeType.SPEED,    "👟 Agility",   "+1 Move Speed",              new Color(80,200,120)),
        new Upgrade(UpgradeType.DASH_CD,  "💨 Dash CD",   "Dash cooldown -20%",         new Color(80,160,255)),
        new Upgrade(UpgradeType.POWER_CD, "💥 Power CD",  "Power Strike CD -20%",       new Color(255,100,60)),
        new Upgrade(UpgradeType.REGEN,    "🌿 Regen",     "Restore 25 HP now",          new Color(100,220,180)),
    };

    private final Random rng = new Random();

    // ── Public API ───────────────────────────────────────────────────────────

    /** เรียกเมื่อฆ่า slime — คืน true ถ้า level up */
    public boolean gainExp(int amount) {
        if (level >= MAX_LEVEL) return false;
        exp += amount;
        if (exp >= expToNext) {
            exp -= expToNext;
            level++;
            expToNext = (int)(expToNext * 1.4);
            rollUpgradeChoices();
            showUpgradeMenu = true;
            return true;
        }
        return false;
    }

    /** เรียกเมื่อคลิกเลือก upgrade */
    public void selectUpgrade(int index, Player player, SkillSystem skills) {
        if (index < 0 || index >= currentChoices.size()) return;
        applyUpgrade(currentChoices.get(index), player, skills);
        showUpgradeMenu = false;
        currentChoices.clear();
    }

    public void handleMouseMove(int x, int y) {
        mouseX = x; mouseY = y;
        hoveredIndex = -1;
        for (int i = 0; i < cardRects.length; i++) {
            if (cardRects[i] != null && cardRects[i].contains(x, y)) {
                hoveredIndex = i;
                break;
            }
        }
    }

    public int handleMouseClick(int x, int y, Player player, SkillSystem skills) {
        if (!showUpgradeMenu) return -1;
        for (int i = 0; i < cardRects.length; i++) {
            if (cardRects[i] != null && cardRects[i].contains(x, y)) {
                selectUpgrade(i, player, skills);
                return i;
            }
        }
        return -1;
    }

    // ── Private Helpers ──────────────────────────────────────────────────────

    private void rollUpgradeChoices() {
        currentChoices.clear();
        List<Upgrade> pool = new ArrayList<>(List.of(ALL_UPGRADES));
        for (int i = 0; i < 3 && !pool.isEmpty(); i++) {
            int idx = rng.nextInt(pool.size());
            currentChoices.add(pool.remove(idx));
        }
    }

    private void applyUpgrade(Upgrade u, Player player, SkillSystem skills) {
        switch (u.type) {
            case MAX_HP:
                player.maxHealth += 30;
                player.heal(30);
                break;
            case ATTACK:
                player.baseAttackDamage += 5;
                player.attackDamage = player.attackBuffActive
                        ? player.baseAttackDamage * 2 : player.baseAttackDamage;
                break;
            case SPEED:
                player.speed = Math.min(player.speed + 1, 12);
                break;
            case DASH_CD:
                // ลด cooldown max — clamp ไม่ต่ำกว่า 30
                // (เราแก้ค่า field โดยตรงผ่าน SkillSystem)
                // ใช้ wrapper เก็บ reduction
                skills.dashCooldownReduction = Math.min(skills.dashCooldownReduction + 0.2f, 0.6f);
                break;
            case POWER_CD:
                skills.powerCooldownReduction = Math.min(skills.powerCooldownReduction + 0.2f, 0.6f);
                break;
            case REGEN:
                player.heal(25);
                break;
        }
        SoundManager.playSound("click.wav");
    }

    // ── Draw ─────────────────────────────────────────────────────────────────

    /** วาด EXP Bar ใต้ HP Bar ของ player */
    public void drawExpBar(Graphics2D g, Player player) {
        int barX = player.x;
        int barW = player.width;
        int barH = 5;
        int barY = player.y - 4;  // ต่ำกว่า HP bar

        g.setColor(new Color(30, 30, 50));
        g.fillRect(barX, barY, barW, barH);
        float ratio = expToNext > 0 ? (float)exp / expToNext : 1f;
        g.setColor(new Color(120, 220, 255));
        g.fillRect(barX, barY, (int)(barW * ratio), barH);
        g.setColor(new Color(80, 180, 220));
        g.drawRect(barX, barY, barW, barH);
    }

    /** วาด Level + EXP ใน HUD */
    public void drawHUDLevel(Graphics2D g, int x, int y) {
        g.setFont(new Font("Arial", Font.BOLD, 16));
        g.setColor(new Color(120, 220, 255));
        g.drawString("Lv " + level, x, y);
        g.setFont(new Font("Arial", Font.PLAIN, 12));
        g.setColor(new Color(180, 240, 255));
        g.drawString("EXP " + exp + "/" + expToNext, x, y + 16);
    }

    /** วาด Upgrade Menu */
    public void drawUpgradeMenu(Graphics2D g, int panelW, int panelH, int animTick) {
        if (!showUpgradeMenu || currentChoices.isEmpty()) return;

        // Overlay
        g.setColor(new Color(0, 0, 0, 170));
        g.fillRect(0, 0, panelW, panelH);

        // Title
        String title = "LEVEL UP!  Choose an Upgrade";
        g.setFont(new Font("Arial", Font.BOLD, 30));
        FontMetrics fm = g.getFontMetrics();
        int bounce = (int)(Math.sin(animTick * 0.15) * 5);
        g.setColor(new Color(120, 220, 255));
        g.drawString(title, panelW / 2 - fm.stringWidth(title) / 2, 120 + bounce);
        g.setColor(new Color(255, 255, 255, 60));
        g.drawString(title, panelW / 2 - fm.stringWidth(title) / 2 + 2, 122 + bounce);

        // Cards
        int cardW = 180, cardH = 210;
        int totalW = cardW * 3 + 30 * 2;
        int startX = panelW / 2 - totalW / 2;
        int cardY = panelH / 2 - cardH / 2 + 20;

        for (int i = 0; i < currentChoices.size(); i++) {
            int cx = startX + i * (cardW + 30);
            cardRects[i] = new Rectangle(cx, cardY, cardW, cardH);
            drawCard(g, currentChoices.get(i), cx, cardY, cardW, cardH, hoveredIndex == i, animTick);
        }

        // hint
        g.setFont(new Font("Arial", Font.ITALIC, 13));
        g.setColor(new Color(200, 200, 200, 180));
        String hint = "Click to select";
        FontMetrics fm2 = g.getFontMetrics();
        g.drawString(hint, panelW / 2 - fm2.stringWidth(hint) / 2, cardY + cardH + 35);
    }

    private void drawCard(Graphics2D g, Upgrade u, int x, int y, int w, int h,
                          boolean hovered, int animTick) {
        int lift = hovered ? -8 : 0;
        y += lift;

        // เงา
        g.setColor(new Color(0, 0, 0, hovered ? 120 : 60));
        g.fillRoundRect(x + 6, y + 8, w, h, 18, 18);

        // พื้นหลังการ์ด
        Color bg = hovered ? u.color.darker() : new Color(20, 22, 40);
        g.setColor(bg);
        g.fillRoundRect(x, y, w, h, 18, 18);

        // ขอบ glow
        float glow = hovered ? 0.6f + 0.4f * (float)Math.sin(animTick * 0.15) : 0.3f;
        g.setColor(new Color(
                (int)(u.color.getRed()   * glow),
                (int)(u.color.getGreen() * glow),
                (int)(u.color.getBlue()  * glow), 255));
        g.setStroke(new BasicStroke(hovered ? 3 : 2));
        g.drawRoundRect(x, y, w, h, 18, 18);
        g.setStroke(new BasicStroke(1));

        // แถบสีบน
        g.setColor(u.color);
        g.fillRoundRect(x, y, w, 6, 4, 4);

        // ไอคอน emoji-like (ใช้ Font fallback)
        g.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 38));
        String icon = u.title.substring(0, 2);
        FontMetrics fm = g.getFontMetrics();
        g.drawString(icon, x + w / 2 - fm.stringWidth(icon) / 2, y + 70);

        // ชื่อ upgrade
        g.setFont(new Font("Arial", Font.BOLD, 16));
        g.setColor(hovered ? Color.WHITE : new Color(220, 220, 220));
        String name = u.title.substring(2).trim();
        fm = g.getFontMetrics();
        g.drawString(name, x + w / 2 - fm.stringWidth(name) / 2, y + 105);

        // คำอธิบาย
        g.setFont(new Font("Arial", Font.PLAIN, 13));
        g.setColor(new Color(180, 180, 200));
        FontMetrics fm2 = g.getFontMetrics();
        g.drawString(u.desc, x + w / 2 - fm2.stringWidth(u.desc) / 2, y + 130);

        // ปุ่ม SELECT
        Color btnColor = hovered ? u.color : new Color(60, 60, 90);
        g.setColor(btnColor);
        g.fillRoundRect(x + 20, y + h - 48, w - 40, 32, 10, 10);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 14));
        FontMetrics fm3 = g.getFontMetrics();
        String sel = "SELECT";
        g.drawString(sel, x + w / 2 - fm3.stringWidth(sel) / 2, y + h - 26);
    }
}