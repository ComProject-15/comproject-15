package aimtrainer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GamePanel extends JPanel {

    Target target = new Target();
    List<FloatingDamage> floatingDamages = new ArrayList<>();
    Image backgroundImage;
    Main main;

    int score = 0;
    int shots = 0;

    Mode currentMode;
    
    // Back button properties
    int backButtonX = 10;
    int backButtonY = 10;
    int backButtonWidth = 120;
    int backButtonHeight = 40;

    public GamePanel(Main main){
        this.main = main;

        try {
            backgroundImage = ImageIO.read(getClass().getResource("background.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Timer ให้ slime เคลื่อนที่
        new Timer(16, e -> {
            target.update(getWidth(), getHeight());
            
            // Update floating damage numbers
            for (int i = floatingDamages.size() - 1; i >= 0; i--) {
                FloatingDamage fd = floatingDamages.get(i);
                fd.update();
                if (!fd.isAlive()) {
                    floatingDamages.remove(i);
                }
            }
            
            repaint();
        }).start();

        // Back button and game interactions
        addMouseListener(new MouseAdapter(){
            public void mousePressed(MouseEvent e){
                // Check if back button clicked
                if (e.getX() >= backButtonX && e.getX() <= backButtonX + backButtonWidth &&
                    e.getY() >= backButtonY && e.getY() <= backButtonY + backButtonHeight) {
                    main.backToMenu();
                    return;
                }

                shots++;

                if(target.isHit(e.getX(), e.getY())){
                    // Create floating damage number
                    floatingDamages.add(new FloatingDamage(
                        target.x + target.size / 2.0,
                        target.y - 20,
                        target.takeDamage()
                    ));
                    if(target.isDead()){
                        score++;
                        target.spawn(getWidth(), getHeight(), currentMode.ordinal());
                    }
                }

                repaint();
            }
        });
    }

    public void setMode(Mode mode){
        this.currentMode = mode;

        switch(mode){
            case EASY:
                target.setSize(100);
                break;
            case NORMAL:
                target.setSize(70);
                break;
            case HARD:
                target.setSize(50);
                break;
        }

        score = 0;
        shots = 0;

        target.spawn(getWidth(), getHeight(), mode.ordinal());
    }

    @Override
    protected void paintComponent(Graphics g){
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;

        // Draw background
        if (backgroundImage != null) {
            g2.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), null);
        } else {
            g2.setColor(Color.DARK_GRAY);
            g2.fillRect(0, 0, getWidth(), getHeight());
        }

        target.draw(g2);
        
        // Draw floating damage numbers
        for (FloatingDamage fd : floatingDamages) {
            fd.draw(g2);
        }

        // Draw Back to Menu button
        g2.setColor(new Color(100, 100, 100, 200));
        g2.fillRect(backButtonX, backButtonY, backButtonWidth, backButtonHeight);
        g2.setColor(Color.WHITE);
        g2.setStroke(new BasicStroke(2));
        g2.drawRect(backButtonX, backButtonY, backButtonWidth, backButtonHeight);
        g2.setFont(new Font("SansSerif", Font.BOLD, 14));
        g2.drawString("Back to Menu", backButtonX + 10, backButtonY + 27);

        // Draw difficulty info
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("SansSerif", Font.BOLD, 14));
        String difficultyText = currentMode == Mode.EASY ? "EASY" : 
                               currentMode == Mode.NORMAL ? "NORMAL" : "HARD";
        g2.drawString("Difficulty: " + difficultyText, getWidth() - 180, 35);
        
        // Draw slime health info
        g2.drawString("Enemy HP: " + target.getHealth() + "/" + target.getMaxHealth(), 
                     getWidth() - 180, 60);

        // Draw game stats
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("SansSerif", Font.BOLD, 20));

        g2.drawString("Score: " + score, 20, 70);
        g2.drawString("Shots: " + shots, 20, 100);

        double acc = (shots == 0) ? 0 : (score * 100.0 / shots);
        g2.drawString("Accuracy: " + String.format("%.1f", acc) + "%", 20, 130);
    }
}