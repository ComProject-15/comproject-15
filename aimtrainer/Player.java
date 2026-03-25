package aimtrainer;

import java.awt.*;

public class Player {

    int x = 450, y = 300;
    int speed = 5;

    // เดิน
    public void move(boolean up, boolean down, boolean left, boolean right){
        if(up) y -= speed;
        if(down) y += speed;
        if(left) x -= speed;
        if(right) x += speed;
    }

    // วาดตัวละคร + ปืน
    public void draw(Graphics2D g, int mouseX, int mouseY){

        // ตัวละคร
        g.setColor(Color.CYAN);
        g.fillOval(x-15, y-15, 30, 30);

        // ปืน (เล็งไปเมาส์)
        g.setColor(Color.WHITE);
        g.drawLine(x, y, mouseX, mouseY);
    }
}