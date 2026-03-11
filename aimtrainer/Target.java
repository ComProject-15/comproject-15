package aimtrainer;

import java.awt.*;
import java.util.Random;

public class Target {

    int x;
    int y;
    int size = 60;

    Random rand = new Random();

    public void setSize(int size){
        this.size = size;
    }

    // เป้าเริ่มกลางจอ
    public void spawnCenter(int width,int height){

        x = width/2 - size/2;
        y = height/2 - size/2;
    }

    // สุ่มตำแหน่งเป้า
    public void spawn(int width,int height){

        x = rand.nextInt(width - size);
        y = rand.nextInt(height - size);
    }

    // วาดเป้า
    public void draw(Graphics2D g){

        g.setColor(Color.RED);
        g.fillOval(x,y,size,size);

        g.setColor(Color.WHITE);
        g.fillOval(x+10,y+10,size-20,size-20);

        g.setColor(Color.RED);
        g.fillOval(x+20,y+20,size-40,size-40);
    }

    // ตรวจว่ายิงโดนไหม
    public boolean isHit(int mx,int my){

        return mx >= x && mx <= x + size &&
               my >= y && my <= y + size;
    }
}