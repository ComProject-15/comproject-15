package aimtrainer;

import java.awt.*;
import java.net.URL;
import javax.imageio.ImageIO;

public class TreasureChest {

    public enum ItemType {
        SHIELD, HEAL, ATTACK_BUFF, GOLD
    }

    public int x, y;
    public int width = 70, height = 60;
    public boolean opened = false;
    public boolean active = false;
    public boolean collected = false;

    private int animTick = 0;
    private Image chestClosed, chestOpen;

    public ItemType itemType;
    public String itemName;
    public String itemDesc;
    public String itemIcon;

    public boolean showPickupHint = false;
    public boolean showItemDesc = false;
    private int descTimer = 0;

    public TreasureChest(int x, int y, boolean isFinalWave) {
        this.x = x;
        this.y = y;

        if (isFinalWave) {
            assignItem(ItemType.GOLD);
        } else {
            int roll = (int)(Math.random() * 3);
            switch (roll) {
                case 0: assignItem(ItemType.SHIELD); break;
                case 1: assignItem(ItemType.HEAL);   break;
                case 2: assignItem(ItemType.ATTACK_BUFF); break;
            }
        }

        try {
            chestClosed = ImageIO.read(new URL(
                "https://img.icons8.com/color/96/treasure-chest.png"));
            chestOpen = ImageIO.read(new URL(
                "https://img.icons8.com/color/96/open-treasure-chest.png"));
        } catch (Exception e) {
            chestClosed = null;
            chestOpen = null;
        }
    }

    private void assignItem(ItemType type) {
        this.itemType = type;
        switch (type) {
            case SHIELD:
                itemName = "Holy Shield";
                itemDesc = "Block all incoming damage\nfor 5 seconds.";
                itemIcon = "🛡";
                break;
            case HEAL:
                itemName = "Health Potion";
                itemDesc = "Restore 50 HP\ninstantly.";
                itemIcon = "🧪";
                break;
            case ATTACK_BUFF:
                itemName = "Power Rune";
                itemDesc = "Double attack damage\nfor 8 seconds.";
                itemIcon = "⚔";
                break;
            case GOLD:
                itemName = "Gold Coins";
                itemDesc = "You found a treasure!\n+500 bonus score.";
                itemIcon = "💰";
                break;
        }
    }

    public void activate() { active = true; }

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    public boolean isNearPlayer(Player player) {
        int cx = x + width / 2;
        int px = player.x + player.width / 2;
        return Math.abs(cx - px) < 120;
    }

    public void update(Player player) {
        if (!active || collected) return;
        animTick++;
        showPickupHint = isNearPlayer(player);
        if (descTimer > 0) {
            descTimer--;
            showItemDesc = true;
        } else {
            showItemDesc = false;
        }
    }

    public void open() {
        if (!opened) {
            opened = true;
            descTimer = 200;
        }
    }

    public void collect() { collected = true; }

    public void draw(Graphics2D g) {
        if (!active || collected) return;

        if (!opened) {
            Color glowColor = getGlowColor();
            float a = 0.15f + 0.1f * (float)Math.sin(animTick * 0.08);
            g.setColor(new Color(
                glowColor.getRed(), glowColor.getGreen(), glowColor.getBlue(),
                (int)(a * 255)));
            g.fillOval(x - 15, y - 10, width + 30, height + 20);
        }

        if (!opened && chestClosed != null) {
            g.drawImage(chestClosed, x, y, width, height, null);
        } else if (opened && chestOpen != null) {
            g.drawImage(chestOpen, x, y, width, height, null);
        } else {
            drawFallbackChest(g);
        }

        if (!opened && showPickupHint) {
            g.setColor(new Color(255, 255, 100, 220));
            g.setFont(new Font("Arial", Font.BOLD, 14));
            g.drawString("[E] Open Chest", x - 10, y - 12);
        }

        if (opened && !collected && showPickupHint) {
            int iconY = y - 22 + (int)(Math.sin(animTick * 0.1) * 5);
            g.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));
            g.drawString(itemIcon, x + width / 2 - 14, iconY);

            g.setColor(new Color(100, 220, 255, 220));
            g.setFont(new Font("Arial", Font.BOLD, 14));
            g.drawString("[R] Pick up " + itemName, x - 25, y - 42);
        }

        if (showItemDesc) drawItemDescription(g);
    }

    private Color getGlowColor() {
        switch (itemType) {
            case SHIELD:      return new Color(0, 150, 255);
            case HEAL:        return new Color(0, 220, 80);
            case ATTACK_BUFF: return new Color(255, 80, 0);
            case GOLD:        return new Color(255, 200, 0);
            default:          return Color.WHITE;
        }
    }

    private void drawFallbackChest(Graphics2D g) {
        g.setColor(opened ? new Color(160, 120, 50) : new Color(139, 90, 43));
        g.fillRoundRect(x, y, width, height, 8, 8);
        g.setColor(new Color(200, 150, 50));
        g.setStroke(new BasicStroke(3));
        g.drawRoundRect(x, y, width, height, 8, 8);
        g.setStroke(new BasicStroke(1));
        if (!opened) {
            g.setColor(new Color(255, 215, 0));
            g.fillOval(x + width/2 - 7, y + height/2 - 7, 14, 14);
        } else {
            g.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));
            g.drawString("✨", x + width/2 - 14, y + height/2 + 10);
        }
    }

    private void drawItemDescription(Graphics2D g) {
        int boxW = 230, boxH = 95;
        int boxX = x - 80;
        int boxY = y - boxH - 25;
        if (boxX < 5) boxX = 5;
        if (boxX + boxW > 895) boxX = 895 - boxW;
        if (boxY < 5) boxY = 5;

        g.setColor(new Color(15, 15, 35, 230));
        g.fillRoundRect(boxX, boxY, boxW, boxH, 12, 12);

        Color borderColor = getGlowColor();
        g.setColor(borderColor);
        g.setStroke(new BasicStroke(2));
        g.drawRoundRect(boxX, boxY, boxW, boxH, 12, 12);
        g.setStroke(new BasicStroke(1));

        g.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
        g.drawString(itemIcon, boxX + 10, boxY + 32);

        g.setColor(new Color(255, 220, 80));
        g.setFont(new Font("Arial", Font.BOLD, 15));
        g.drawString(itemName, boxX + 44, boxY + 28);

        g.setColor(new Color(borderColor.getRed(), borderColor.getGreen(),
                borderColor.getBlue(), 180));
        g.fillRoundRect(boxX + 44, boxY + 34, 80, 16, 6, 6);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 11));
        g.drawString(itemType.name(), boxX + 50, boxY + 46);

        g.setColor(new Color(210, 225, 255));
        g.setFont(new Font("Arial", Font.PLAIN, 13));
        String[] lines = itemDesc.split("\n");
        for (int i = 0; i < lines.length; i++) {
            g.drawString(lines[i], boxX + 10, boxY + 60 + i * 18);
        }
    }
}