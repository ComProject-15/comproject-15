package aimtrainer;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.*;

public class GamePanel extends JPanel implements Runnable, KeyListener {
    List<Target> targets = new ArrayList<>();
    Player player;
    List<FloatingDamage> floatingDamages = new ArrayList<>();
    Image backgroundImage;
    Main main;

    int score = 0;
    int shots = 0;

    Mode currentMode;

    boolean stageClear = false;
    int stageClareTimer = 0;
    final int STAGE_CLEAR_DELAY = 180;

    int backButtonX = 10, backButtonY = 10, backButtonWidth = 120, backButtonHeight = 40;

    boolean leftPressed = false, rightPressed = false, upPressed = false;
    boolean wasAttacking = false;

    boolean playedEndSound = false;

    // 🔥 ตัวกัน win ตอนยังไม่เริ่มเกม
    boolean gameStarted = false;

    public GamePanel(Main main) {
        this.main = main;
        player = new Player(100, 300);
        setFocusable(true);
        addKeyListener(this);
        requestFocusInWindow();

        try {
            backgroundImage = ImageIO.read(getClass().getResource("background.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        new Timer(16, e -> {
            if (!stageClear) {

                for (Target target : targets) {
                    target.update(getWidth(), getHeight(), player);
                }

                if (leftPressed)  player.moveLeft();
                if (rightPressed) player.moveRight();
                if (upPressed)    { player.jump(); upPressed = false; }
                player.update(getWidth(), getHeight());

                boolean isAttackingNow = player.attacking;
                if (isAttackingNow && !wasAttacking) {
                    for (Target t : targets) t.resetHitThisSwing();
                }
                wasAttacking = isAttackingNow;

                Rectangle swordHitbox = player.getAttackHitbox();
                if (swordHitbox != null) {
                    for (int i = targets.size() - 1; i >= 0; i--) {
                        Target target = targets.get(i);
                        if (target.isDying) continue;

                        if (target.isHitByRect(swordHitbox)) {
                            int dmg = target.takeDamageOnce();

                            if (dmg > 0) {

                                // 🔊 เสียงตีโดนจริง
                                SoundManager.playSound("hit.wav");

                                floatingDamages.add(new FloatingDamage(
                                    target.x + target.size / 2.0,
                                    target.y - 20,
                                    dmg
                                ));

                                if (target.isDead()) {
                                    score++;
                                    target.startDeathEffect();
                                }
                            }
                            break;
                        }
                    }
                }

                targets.removeIf(Target::deathEffectDone);

                for (int i = floatingDamages.size() - 1; i >= 0; i--) {
                    FloatingDamage fd = floatingDamages.get(i);
                    fd.update();
                    if (!fd.isAlive()) floatingDamages.remove(i);
                }

                // 😵 แพ้
                if (player.getHealth() <= 0 && !playedEndSound) {
                    SoundManager.playSound("lose.wav");
                    playedEndSound = true;

                    stageClear = true;
                    stageClareTimer = STAGE_CLEAR_DELAY;
                }

                boolean allGone = targets.stream().allMatch(t -> t.isDying);

                // 🏆 ชนะ (ต้อง gameStarted ก่อน)
                if (gameStarted && (targets.isEmpty() || allGone) && player.getHealth() > 0 && !stageClear && !playedEndSound) {
                    SoundManager.playSound("win.wav");
                    playedEndSound = true;

                    stageClear = true;
                    stageClareTimer = STAGE_CLEAR_DELAY;
                }

            } else {
                stageClareTimer--;
                if (stageClareTimer <= 0) {
                    main.backToMenu();
                    stageClear = false;
                    return;
                }
            }

            repaint();
        }).start();

        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                if (e.getX() >= backButtonX && e.getX() <= backButtonX + backButtonWidth &&
                    e.getY() >= backButtonY && e.getY() <= backButtonY + backButtonHeight) {

                    SoundManager.playSound("click.wav");

                    main.backToMenu();
                }
            }
        });
    }

    @Override public void addNotify() { super.addNotify(); requestFocusInWindow(); }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_A: leftPressed = true; break;
            case KeyEvent.VK_D: rightPressed = true; break;
            case KeyEvent.VK_W: upPressed = true; break;

            case KeyEvent.VK_SPACE:
                player.attack();
                shots++;
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_A: leftPressed = false; break;
            case KeyEvent.VK_D: rightPressed = false; break;
            case KeyEvent.VK_W: upPressed = false; break;
        }
    }

    @Override public void keyTyped(KeyEvent e) {}

    public void setMode(Mode mode) {
        this.currentMode = mode;

        // ✅ เริ่มเกมจริง
        gameStarted = true;

        targets.clear();
        stageClear = false;
        stageClareTimer = 0;
        player.health = player.maxHealth;
        wasAttacking = false;
        playedEndSound = false;

        int numTargets, targetSize;
        switch (mode) {
            case EASY:   numTargets = 1; targetSize = 100; break;
            case NORMAL: numTargets = 2; targetSize = 70;  break;
            case HARD:   numTargets = 3; targetSize = 50;  break;
            default:     numTargets = 1; targetSize = 80;
        }

        for (int i = 0; i < numTargets; i++) {
            Target t = new Target();
            t.setSize(targetSize);
            t.spawn(getWidth(), getHeight(), mode.ordinal());
            if (numTargets > 1) t.x = (int)(getWidth() * (0.3 + i * 0.35));
            targets.add(t);
        }

        score = 0; shots = 0;
        player.x = 50;
        player.y = (int)(getHeight() * 0.75) - 15;
        requestFocusInWindow();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        if (backgroundImage != null)
            g2.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), null);
        else {
            g2.setColor(Color.DARK_GRAY);
            g2.fillRect(0, 0, getWidth(), getHeight());
        }

        for (Target target : targets) target.draw(g2);
        player.draw(g2);
        for (FloatingDamage fd : floatingDamages) fd.draw(g2);

        g2.setColor(Color.WHITE);
        g2.drawString("Score: " + score, 20, 70);
    }

    @Override public void run() {}
}