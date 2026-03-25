package aimtrainer;

import java.awt.*;

public class Sword {

    public void draw(Graphics2D g, int playerX, int playerY, boolean facingRight, boolean isAttacking) {

        if (!isAttacking) {
            // Idle sword position (on back or by player's side)
            drawIdleSword(g, playerX, playerY, facingRight);
        } else {
            // Attack motion
            drawAttackingSword(g, playerX, playerY, facingRight);
        }
    }

    private void drawIdleSword(Graphics2D g, int playerX, int playerY, boolean facingRight) {
        g.setColor(new Color(180, 180, 180)); // Silver blade
        
        int swordX = facingRight ? playerX + 15 : playerX - 25;
        int swordY = playerY - 20;
        
        // Blade
        g.fillRect(swordX, swordY, 30, 8);
        
        // Handle
        g.setColor(new Color(139, 69, 19)); // Brown
        g.fillRect(swordX + 25, swordY + 2, 8, 4);
    }

    private void drawAttackingSword(Graphics2D g, int playerX, int playerY, boolean facingRight) {
        // Attacking sword motion
        int centerX = playerX;
        int centerY = playerY;
        
        if (facingRight) {
            // Draw sword extending to the right
            // Blade
            g.setColor(new Color(200, 200, 200)); // Bright silver
            g.fillRect(centerX + 15, centerY - 12, 50, 10);
            
            // Blade shine
            g.setColor(new Color(255, 255, 255, 100));
            g.fillRect(centerX + 15, centerY - 10, 50, 3);
            
            // Handle
            g.setColor(new Color(139, 69, 19));
            g.fillRect(centerX + 5, centerY - 6, 10, 12);
        } else {
            // Draw sword extending to the left
            // Blade
            g.setColor(new Color(200, 200, 200)); // Bright silver
            g.fillRect(centerX - 65, centerY - 12, 50, 10);
            
            // Blade shine
            g.setColor(new Color(255, 255, 255, 100));
            g.fillRect(centerX - 65, centerY - 10, 50, 3);
            
            // Handle
            g.setColor(new Color(139, 69, 19));
            g.fillRect(centerX - 15, centerY - 6, 10, 12);
        }
    }

    public Rectangle getAttackHitbox(int playerX, int playerY, boolean facingRight) {
        if (facingRight) {
            return new Rectangle(playerX + 15, playerY - 12, 50, 10);
        } else {
            return new Rectangle(playerX - 65, playerY - 12, 50, 10);
        }
    }
}
