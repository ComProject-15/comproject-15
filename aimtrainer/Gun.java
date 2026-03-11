package aimtrainer;

import java.awt.*;
import java.awt.geom.AffineTransform;

public class Gun {

    public void draw(Graphics2D g, int mouseX, int mouseY, int width, int height) {

        int centerX = width / 2;
        int centerY = height - 80;

        double angle = Math.atan2(mouseY - centerY, mouseX - centerX);

        AffineTransform old = g.getTransform();

        g.translate(centerX, centerY);
        g.rotate(angle);

        // ===== ลำกล้องปืน (3D gradient) =====
        GradientPaint barrel = new GradientPaint(
                0, -6, Color.LIGHT_GRAY,
                80, 6, Color.DARK_GRAY
        );

        g.setPaint(barrel);
        g.fillRect(0, -6, 80, 12);

        // ปากกระบอก
        GradientPaint muzzle = new GradientPaint(
                80, -7, Color.GRAY,
                95, 7, Color.BLACK
        );

        g.setPaint(muzzle);
        g.fillRect(80, -7, 15, 14);

        // ===== ตัวปืน =====
        GradientPaint body = new GradientPaint(
                -20, -12, Color.GRAY,
                20, 12, Color.BLACK
        );

        g.setPaint(body);
        g.fillRoundRect(-20, -12, 40, 24, 10, 10);

        // ===== ด้ามปืน =====
        GradientPaint handle = new GradientPaint(
                -5, 10, new Color(90, 90, 90),
                15, 40, Color.BLACK
        );

        g.setPaint(handle);
        g.fillRoundRect(-5, 10, 15, 35, 8, 8);

        // ===== เงาปืน =====
        g.setColor(new Color(0,0,0,60));
        g.fillOval(-15, 35, 40, 10);

        g.setTransform(old);
    }
}