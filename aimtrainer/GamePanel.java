package aimtrainer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GamePanel extends JPanel implements Runnable, KeyListener {
    List<Target> targets = new ArrayList<>();
    Player player;
    List<FloatingDamage> floatingDamages = new ArrayList<>();
    Image backgroundImage;
    Main main;

    int score = 0;
    int shots = 0;

    Mode currentMode;
    
    // Stage cleared properties
    boolean stageClear = false;
    int stageClareTimer = 0;
    final int STAGE_CLEAR_DELAY = 180; // 3 seconds at 60 FPS
    
    // Back button properties
    int backButtonX = 10;
    int backButtonY = 10;
    int backButtonWidth = 120;
    int backButtonHeight = 40;
    
    // Player movement
    boolean leftPressed = false, rightPressed = false, upPressed = false;

    public GamePanel(Main main){
        this.main = main;
        
        player = new Player(100, 300);
        setFocusable(true);
        addKeyListener(this);

        try {
            backgroundImage = ImageIO.read(getClass().getResource("background.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Timer ให้ slime เคลื่อนที่
        new Timer(16, e -> {
            if (!stageClear) {
                for (Target target : targets) {
                    target.update(getWidth(), getHeight());
                }
                
                // Update player
                if (leftPressed) player.moveLeft();
                if (rightPressed) player.moveRight();
                if (upPressed) {
                    player.jump();
                    upPressed = false; // Jump only once per press
                }
                player.update(getWidth(), getHeight());
                
                // Update floating damage numbers
                for (int i = floatingDamages.size() - 1; i >= 0; i--) {
                    FloatingDamage fd = floatingDamages.get(i);
                    fd.update();
                    if (!fd.isAlive()) {
                        floatingDamages.remove(i);
                    }
                }
                
                // Check if all targets cleared
                if (targets.isEmpty()) {
                    stageClear = true;
                    stageClareTimer = STAGE_CLEAR_DELAY;
                }
            } else {
                // Stage cleared, countdown to return to menu
                stageClareTimer--;
                if (stageClareTimer <= 0) {
                    main.backToMenu();
                    stageClear = false;
                    return;
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

                // Check if sword attack hits any slime
                Rectangle swordHitbox = player.getAttackHitbox();
                for (int i = targets.size() - 1; i >= 0; i--) {
                    Target target = targets.get(i);
                    if (swordHitbox != null && target.isHitByRect(swordHitbox)) {
                        floatingDamages.add(new FloatingDamage(
                            target.x + target.size / 2.0,
                            target.y - 20,
                            target.takeDamage()
                        ));
                        if (target.isDead()) {
                            score++;
                            targets.remove(i);
                        }
                        break;
                    } else if (target.isHit(e.getX(), e.getY())) {
                        // Click-based attack (original mechanic)
                        floatingDamages.add(new FloatingDamage(
                            target.x + target.size / 2.0,
                            target.y - 20,
                            target.takeDamage()
                        ));
                        if (target.isDead()) {
                            score++;
                            targets.remove(i);
                        }
                        break;
                    }
                }

                repaint();
            }
        });
        
        // WASD/Arrow movement for player - handled by KeyListener implementation below
    }
    
    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT:
            case KeyEvent.VK_A: leftPressed = true; break;
            case KeyEvent.VK_RIGHT:
            case KeyEvent.VK_D: rightPressed = true; break;
            case KeyEvent.VK_UP:
            case KeyEvent.VK_W: upPressed = true; break;
            case KeyEvent.VK_SPACE: player.attack(); break;
        }
    }
    
    @Override
    public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT:
            case KeyEvent.VK_A: leftPressed = false; break;
            case KeyEvent.VK_RIGHT:
            case KeyEvent.VK_D: rightPressed = false; break;
            case KeyEvent.VK_UP:
            case KeyEvent.VK_W: upPressed = false; break;
        }
    }
    
    @Override
    public void keyTyped(KeyEvent e) {
    }

    public void setMode(Mode mode){
        this.currentMode = mode;
        targets.clear();
        stageClear = false;
        stageClareTimer = 0;

        int numTargets;
        switch(mode) {
            case EASY: numTargets = 1; break;
            case NORMAL: numTargets = 2; break;
            case HARD: numTargets = 3; break;
            default: numTargets = 1;
        }
        
        int targetSize = 0;
        
        switch(mode){
            case EASY:
                targetSize = 100;
                break;
            case NORMAL:
                targetSize = 70;
                break;
            case HARD:
                targetSize = 50;
                break;
        }

        // Spawn targets
        for (int i = 0; i < numTargets; i++) {
            Target t = new Target();
            t.setSize(targetSize);
            t.spawn(getWidth(), getHeight(), mode.ordinal());
            
            // Offset positions for multiple targets in hard mode
            if (numTargets > 1) {
                t.x = (int)(getWidth() * (0.3 + i * 0.35));
            }
            targets.add(t);
        }

        score = 0;
        shots = 0;

        player.x = 50;
        player.y = (int)(getHeight() * 0.75) - 15;
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

        for (Target target : targets) {
            target.draw(g2);
        }
        player.draw(g2);
        
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
        if (!targets.isEmpty()) {
            Target firstTarget = targets.get(0);
            g2.drawString("Enemies: " + targets.size() + " | HP: " + firstTarget.getHealth() + "/" + firstTarget.getMaxHealth(), 
                         getWidth() - 280, 60);
        }

        // Draw game stats
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("SansSerif", Font.BOLD, 20));

        g2.drawString("Score: " + score, 20, 70);
        g2.drawString("Shots: " + shots, 20, 100);

        double acc = (shots == 0) ? 0 : (score * 100.0 / shots);
        g2.drawString("Accuracy: " + String.format("%.1f", acc) + "%", 20, 130);
        
        // Draw stage clear message
        if (stageClear) {
            g2.setColor(new Color(0, 0, 0, 200));
            g2.fillRect(0, 0, getWidth(), getHeight());
            
            g2.setColor(Color.YELLOW);
            g2.setFont(new Font("SansSerif", Font.BOLD, 80));
            FontMetrics fm = g2.getFontMetrics();
            String clearText = "STAGE CLEARED!";
            int textX = (getWidth() - fm.stringWidth(clearText)) / 2;
            int textY = (getHeight() / 2) - 40;
            g2.drawString(clearText, textX, textY);
            
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("SansSerif", Font.PLAIN, 20));
            String returnText = "Returning to menu...";
            fm = g2.getFontMetrics();
            textX = (getWidth() - fm.stringWidth(returnText)) / 2;
            g2.drawString(returnText, textX, textY + 60);
        }
    }

    @Override
    public void run() {
        // Empty - Timer handles game loop
    }
}