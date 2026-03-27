package aimtrainer;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
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
    boolean playedEndSound = false;
    boolean gameStarted = false;

    boolean leftPressed = false, rightPressed = false, upPressed = false;
    boolean wasAttacking = false;

    String endState = ""; // WIN / LOSE

    Rectangle playAgainBtn = new Rectangle(300, 350, 200, 50);
    Rectangle menuBtn = new Rectangle(300, 420, 200, 50);

    int mouseX = 0, mouseY = 0;
    boolean hoverPlay = false;
    boolean hoverMenu = false;

    int animTick = 0;

    public GamePanel(Main main) {
        this.main = main;
        player = new Player(100, 300);

        setFocusable(true);
        addKeyListener(this);
        requestFocusInWindow();

        // ซ่อนเมาส์
        BufferedImage cursorImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        Cursor blankCursor = Toolkit.getDefaultToolkit()
                .createCustomCursor(cursorImg, new Point(0, 0), "blank");
        setCursor(blankCursor);

        try {
            backgroundImage = ImageIO.read(getClass().getResource("background.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseMoved(MouseEvent e) {
                mouseX = e.getX();
                mouseY = e.getY();
                hoverPlay = playAgainBtn.contains(e.getPoint());
                hoverMenu = menuBtn.contains(e.getPoint());
            }
        });

        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                if (stageClear) {
                    if (playAgainBtn.contains(e.getPoint())) {
                        SoundManager.playSound("click.wav");
                        setMode(currentMode);
                        stageClear = false;
                    }
                    if (menuBtn.contains(e.getPoint())) {
                        SoundManager.playSound("click.wav");
                        main.backToMenu();
                    }
                }
            }
        });

        new Timer(16, e -> {

            animTick++;

            if (!stageClear) {

                for (Target target : targets) {
                    target.update(getWidth(), getHeight(), player);
                }

                if (leftPressed) player.moveLeft();
                if (rightPressed) player.moveRight();
                if (upPressed) { player.jump(); upPressed = false; }

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

                // แพ้
                if (player.getHealth() <= 0 && !playedEndSound) {
                    SoundManager.playSound("lose.wav");
                    playedEndSound = true;
                    endState = "LOSE";
                    stageClear = true;
                }

                boolean allGone = targets.stream().allMatch(t -> t.isDying);

                // ชนะ
                if (gameStarted && (targets.isEmpty() || allGone)
                        && player.getHealth() > 0 && !stageClear && !playedEndSound) {

                    SoundManager.playSound("win.wav");
                    playedEndSound = true;
                    endState = "WIN";
                    stageClear = true;
                }
            }

            repaint();
        }).start();
    }

    @Override public void addNotify() {
        super.addNotify();
        requestFocusInWindow();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (stageClear) return;

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

        gameStarted = true;
        targets.clear();
        stageClear = false;
        player.health = player.maxHealth;
        playedEndSound = false;

        int numTargets, targetSize;
        switch (mode) {
            case EASY: numTargets = 1; targetSize = 100; break;
            case NORMAL: numTargets = 2; targetSize = 70; break;
            case HARD: numTargets = 3; targetSize = 50; break;
            default: numTargets = 1; targetSize = 80;
        }

        for (int i = 0; i < numTargets; i++) {
            Target t = new Target();
            t.setSize(targetSize);
            t.spawn(getWidth(), getHeight(), mode.ordinal());
            if (numTargets > 1)
                t.x = (int)(getWidth() * (0.3 + i * 0.35));
            targets.add(t);
        }

        score = 0;
        shots = 0;

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
        g2.setFont(new Font("Arial", Font.BOLD, 18));
        g2.drawString("Score: " + score, 20, 40);

        if (stageClear) {
            g2.setColor(new Color(0, 0, 0, 200));
            g2.fillRect(0, 0, getWidth(), getHeight());

            int bounce = (int)(Math.sin(animTick * 0.1) * 8);

            g2.setFont(new Font("Arial", Font.BOLD, 64));
            String text = endState.equals("WIN") ? "YOU WIN!" : "YOU LOSE!";

            g2.setColor(Color.BLACK);
            g2.drawString(text, getWidth()/2 - 170 + 4, 230 + bounce + 4);

            g2.setColor(endState.equals("WIN") ? new Color(0,255,150) : new Color(255,80,80));
            g2.drawString(text, getWidth()/2 - 170, 230 + bounce);

            drawButton(g2, playAgainBtn, "PLAY AGAIN", hoverPlay);
            drawButton(g2, menuBtn, "MENU", hoverMenu);
        }

        // crosshair
        g2.setColor(Color.RED);
        g2.drawLine(mouseX - 10, mouseY, mouseX + 10, mouseY);
        g2.drawLine(mouseX, mouseY - 10, mouseX, mouseY + 10);
    }

    private void drawButton(Graphics2D g2, Rectangle r, String text, boolean hover) {
        g2.setColor(hover ? Color.LIGHT_GRAY : Color.WHITE);
        g2.fillRoundRect(r.x, r.y, r.width, r.height, 30, 30);

        g2.setColor(Color.BLACK);
        g2.setFont(new Font("Arial", Font.BOLD, 18));
        g2.drawString(text, r.x + 40, r.y + 30);
    }

    @Override public void run() {}
}