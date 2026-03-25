package aimtrainer;

import java.awt.*;
import javax.imageio.ImageIO;
import java.io.IOException;

public class Player {

    int x, y;
    int speed = 5;
    Image playerImage;

    public Player(int startX, int startY) {
        this.x = startX;
        this.y = startY;
        try {
            playerImage = ImageIO.read(getClass().getResource("Player.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Movement using WASD (called from GamePanel with key states)
    public void move(boolean up, boolean down, boolean left, boolean right){
        if(up) y -= speed;
        if(down) y += speed;
        if(left) x -= speed;
        if(right) x += speed;
    }

    public Bullet shoot(int mouseX, int mouseY) {
        return new Bullet(x, y, mouseX, mouseY);
    }

    // Draw the player as a small character
    public void draw(Graphics2D g){
        if (playerImage != null) {
            int imgWidth = playerImage.getWidth(null);
            int imgHeight = playerImage.getHeight(null);
            g.drawImage(playerImage, x - imgWidth/2, y - imgHeight/2, null);
        } else {
            // Fallback if image fails to load
            g.setColor(Color.CYAN);
            g.fillOval(x-15, y-15, 30, 30);
        }
    }
}