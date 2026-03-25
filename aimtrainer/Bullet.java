package aimtrainer;

import java.awt.*;

public class Bullet {
    int x, y;
    double dx, dy;

    public Bullet(int startX, int startY, int targetX, int targetY) {
        x = startX;
        y = startY;
        double angle = Math.atan2(targetY - startY, targetX - startX);
        double speed = 10;
        dx = speed * Math.cos(angle);
        dy = speed * Math.sin(angle);
    }

    public void update() {
        x += (int) dx;
        y += (int) dy;
    }

    public void draw(Graphics2D g) {
        g.setColor(Color.YELLOW);
        g.fillOval(x - 2, y - 2, 4, 4);
    }

    public boolean isOffScreen(int width, int height) {
        return x < 0 || x > width || y < 0 || y > height;
    }

    public boolean hits(Target target) {
        return target.isHit(x, y);
    }
}