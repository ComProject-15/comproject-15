package aimtrainer;

import java.awt.*;
import java.awt.geom.AffineTransform;

public class Sword {

    // attackProgress: 0.0 = เริ่มแกว่ง, 1.0 = จบแกว่ง
    public void draw(Graphics2D g, int playerX, int playerY, int playerW, int playerH,
                     boolean facingRight, boolean isAttacking, int attackTimer, int attackDuration) {

        if (!isAttacking) {
            drawIdleSword(g, playerX, playerY, playerW, playerH, facingRight);
        } else {
            float progress = 1f - (attackTimer / (float) attackDuration); // 0→1
            drawSwingSword(g, playerX, playerY, playerW, playerH, facingRight, progress);
        }
    }

    private void drawIdleSword(Graphics2D g, int px, int py, int pw, int ph, boolean facingRight) {
        AffineTransform old = g.getTransform();

        // จุดหมุน = บริเวณมือ player
        int pivotX = facingRight ? px + pw - 10 : px + 10;
        int pivotY = py + ph / 2;

        g.translate(pivotX, pivotY);
        if (!facingRight) g.scale(-1, 1); // กลับซ้าย-ขวา

        // ดาบเอียงพักไว้บนหลัง
        g.rotate(Math.toRadians(-30));
        drawSwordShape(g, false);

        g.setTransform(old);
    }

    private void drawSwingSword(Graphics2D g, int px, int py, int pw, int ph,
                                boolean facingRight, float progress) {
        AffineTransform old = g.getTransform();

        int pivotX = facingRight ? px + pw - 10 : px + 10;
        int pivotY = py + ph / 2 - 5;

        g.translate(pivotX, pivotY);
        if (!facingRight) g.scale(-1, 1);

        // แกว่งจาก -80° → +30° (overhead → followthrough)
        double startAngle = Math.toRadians(-80);
        double endAngle   = Math.toRadians(30);
        double angle = startAngle + (endAngle - startAngle) * easeOut(progress);
        g.rotate(angle);

        drawSwordShape(g, true);

        // Trail effect ตามการแกว่ง
        if (progress > 0.05f && progress < 0.85f) {
            drawSwingTrail(g, progress);
        }

        g.setTransform(old);
    }

    // วาดรูปดาบ (origin = จุดด้ามจับ)
    private void drawSwordShape(Graphics2D g, boolean bright) {
        // ใบดาบ (ยาวไปทางขวา/บน จาก origin)
        Color bladeColor  = bright ? new Color(220, 220, 230) : new Color(160, 160, 170);
        Color shineColor  = new Color(255, 255, 255, bright ? 160 : 80);
        Color handleColor = new Color(110, 60, 15);
        Color guardColor  = new Color(180, 140, 30);

        // ใบดาบ
        g.setColor(bladeColor);
        g.fillRect(0, -5, 48, 10);

        // ปลายดาบเรียว
        int[] tipX = { 48, 48, 62 };
        int[] tipY = { -5, 5, 0 };
        g.fillPolygon(tipX, tipY, 3);

        // แสงวาวบนใบดาบ
        g.setColor(shineColor);
        g.fillRect(4, -3, 40, 3);

        // Guard (กากบาท)
        g.setColor(guardColor);
        g.fillRoundRect(-6, -10, 8, 20, 4, 4);

        // ด้ามจับ
        g.setColor(handleColor);
        g.fillRoundRect(-22, -4, 18, 8, 4, 4);

        // หัวด้าม
        g.setColor(guardColor);
        g.fillOval(-26, -5, 10, 10);
    }

    // Trail โปร่งใสตามการแกว่ง
    private void drawSwingTrail(Graphics2D g, float progress) {
        int steps = 3;
        for (int i = 1; i <= steps; i++) {
            float t = progress - i * 0.08f;
            if (t < 0) continue;

            double startAngle = Math.toRadians(-80);
            double endAngle   = Math.toRadians(30);
            double trailAngle = startAngle + (endAngle - startAngle) * easeOut(t);

            AffineTransform cur = g.getTransform();
            g.rotate(trailAngle - (startAngle + (endAngle - startAngle) * easeOut(progress)));

            int alpha = (int)(60 * (1f - i / (float)(steps + 1)));
            g.setColor(new Color(200, 220, 255, alpha));
            g.fillRect(0, -4, 60, 8);

            g.setTransform(cur);
        }
    }

    // easing ให้การแกว่งรู้สึก snappy
    private double easeOut(double t) {
        t = Math.max(0, Math.min(1, t));
        return 1 - Math.pow(1 - t, 2);
    }

    public Rectangle getAttackHitbox(int playerX, int playerY, int playerW, int playerH, boolean facingRight) {
        if (facingRight) {
            return new Rectangle(playerX + playerW - 10, playerY + playerH / 2 - 20, 62, 40);
        } else {
            return new Rectangle(playerX + 10 - 62, playerY + playerH / 2 - 20, 62, 40);
        }
    }
}