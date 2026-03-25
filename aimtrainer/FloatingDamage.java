package aimtrainer;

import java.awt.*;

public class FloatingDamage {
    
    double x, y;
    int damage;
    double lifetime = 0;
    final double MAX_LIFETIME = 0.8; // seconds
    final double RISE_SPEED = 2.0; // pixels per frame
    
    public FloatingDamage(double startX, double startY, int damageAmount) {
        this.x = startX;
        this.y = startY;
        this.damage = damageAmount;
    }
    
    public void update() {
        lifetime += 0.016; // approximately 60 FPS (1/60 = 0.0167)
        y -= RISE_SPEED; // rise upward
    }
    
    public boolean isAlive() {
        return lifetime < MAX_LIFETIME;
    }
    
    public float getAlpha() {
        // Fade out towards the end
        float progress = (float)(lifetime / MAX_LIFETIME);
        return 1.0f - progress;
    }
    
    public void draw(Graphics2D g) {
        float alpha = getAlpha();
        
        // Set up for semi-transparent text
        AlphaComposite composite = AlphaComposite.getInstance(
            AlphaComposite.SRC_OVER, alpha
        );
        Composite originalComposite = g.getComposite();
        g.setComposite(composite);
        
        // Set color to red for damage
        g.setColor(new Color(255, 50, 50));
        g.setFont(new Font("SansSerif", Font.BOLD, 24));
        
        // Calculate scale effect (slightly smaller as it fades)
        float scale = 1.0f + (float)((MAX_LIFETIME - lifetime) / MAX_LIFETIME) * 0.2f;
        
        // Draw the damage text
        String damageText = "-" + damage;
        FontMetrics fm = g.getFontMetrics();
        int textWidth = fm.stringWidth(damageText);
        
        // Create temporary graphics for scaling effect
        int scaledX = (int)(x - textWidth / 2 * scale);
        int scaledY = (int)(y + 10);
        
        g.drawString(damageText, scaledX, scaledY);
        
        // Restore original composite
        g.setComposite(originalComposite);
    }
}
