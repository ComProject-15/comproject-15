package aimtrainer;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.*;

public class GamePanel extends JPanel implements Runnable, KeyListener {

    List<Target> targets = new ArrayList<>();
    Player player;
    List<FloatingDamage> floatingDamages = new ArrayList<>();

    Image[] backgrounds = new Image[3];
    int currentBg = 0;

    Main main;

    int score = 0;
    int shots = 0;
    int totalSlimesKilled = 0;
    int wave = 1;

    Mode currentMode;

    boolean stageClear = false;
    boolean playedEndSound = false;
    boolean gameStarted = false;

    boolean leftPressed = false, rightPressed = false, upPressed = false;
    boolean wasAttacking = false;

    String endState = "";

    boolean showPortal = false;
    Rectangle portalRect = new Rectangle(820, 200, 60, 200);
    boolean transitioning = false;
    int transitionAlpha = 0;

    TreasureChest chest = null;
    boolean chestSpawned = false;

    long startTime = 0;
    long clearTime = 0;

    Rectangle playAgainBtn = new Rectangle(300, 430, 200, 50);
    Rectangle menuBtn = new Rectangle(300, 495, 200, 50);

    int mouseX = 0, mouseY = 0;
    boolean hoverPlay = false, hoverMenu = false;
    int animTick = 0;

    public GamePanel(Main main) {
        this.main = main;
        player = new Player(100, 300);

        setFocusable(true);
        addKeyListener(this);
        requestFocusInWindow();

        BufferedImage cursorImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        Cursor blankCursor = Toolkit.getDefaultToolkit()
                .createCustomCursor(cursorImg, new Point(0, 0), "blank");
        setCursor(blankCursor);

        try {
            Image bg = ImageIO.read(getClass().getResource("background.jpg"));
            backgrounds[0] = bg;
            backgrounds[1] = bg;
            backgrounds[2] = bg;
        } catch (Exception e) {
            e.printStackTrace();
        }

        addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseMoved(MouseEvent e) {
                mouseX = e.getX();
                mouseY = e.getY();
                hoverPlay = playAgainBtn.contains(e.getPoint());
                hoverMenu = menuBtn.contains(e.getPoint());
                player.levelSys.handleMouseMove(e.getX(), e.getY());
            }
        });

        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                // Upgrade menu รับก่อน
                if (player.levelSys.showUpgradeMenu) {
                    player.levelSys.handleMouseClick(e.getX(), e.getY(),
                            player, player.skills);
                    return;
                }

                if (stageClear && (endState.equals("WIN") || endState.equals("LOSE"))) {
                    if (playAgainBtn.contains(e.getPoint())) {
                        SoundManager.playSound("click.wav");
                        setMode(currentMode);
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

            if (transitioning) {
                transitionAlpha += 8;
                if (transitionAlpha >= 255) {
                    transitionAlpha = 255;
                    transitioning = false;
                    nextWave();
                }
                repaint();
                return;
            }

            if (!transitioning && transitionAlpha > 0) {
                transitionAlpha -= 8;
                if (transitionAlpha < 0) transitionAlpha = 0;
            }

            // หยุด game loop ขณะ upgrade menu เปิดอยู่
            if (player.levelSys.showUpgradeMenu) {
                repaint();
                return;
            }

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

                // ── Normal Attack ──────────────────────────────────────────────
                Rectangle swordHitbox = player.getAttackHitbox();
                if (swordHitbox != null) {
                    checkHitTargets(swordHitbox, player.attackDamage, false);
                }

                // ── Power Strike ───────────────────────────────────────────────
                Rectangle powerHitbox = player.skills.getPowerHitbox(player);
                if (powerHitbox != null) {
                    // Power Strike ดาเมจ 3× + knockback
                    int powerDmg = player.attackDamage * 3;
                    for (int i = targets.size() - 1; i >= 0; i--) {
                        Target t = targets.get(i);
                        if (t.isDying) continue;
                        if (t.isHitByRect(powerHitbox)) {
                            int dmg = t.takeDamageOnce(powerDmg);
                            if (dmg > 0) {
                                SoundManager.playSound("hit.wav");
                                floatingDamages.add(new FloatingDamage(
                                        t.x + t.size / 2.0, t.y - 30, dmg));
                                // knockback
                                t.x += player.facingRight ? 60 : -60;
                                if (t.isDead()) {
                                    onSlimeKilled();
                                    t.startDeathEffect();
                                }
                            }
                        }
                    }
                }

                // ── Whirlwind ──────────────────────────────────────────────────
                Rectangle whirlHitbox = player.skills.getWhirlHitbox(player);
                if (whirlHitbox != null) {
                    int whirlDmg = (int)(player.attackDamage * 1.5);
                    for (int i = targets.size() - 1; i >= 0; i--) {
                        Target t = targets.get(i);
                        if (t.isDying) continue;
                        if (t.isHitByRect(whirlHitbox)) {
                            int dmg = t.takeDamageOnce(whirlDmg);
                            if (dmg > 0) {
                                SoundManager.playSound("hit.wav");
                                floatingDamages.add(new FloatingDamage(
                                        t.x + t.size / 2.0, t.y - 30, dmg));
                                if (t.isDead()) {
                                    onSlimeKilled();
                                    t.startDeathEffect();
                                }
                            }
                        }
                    }
                }

                targets.removeIf(Target::deathEffectDone);

                for (int i = floatingDamages.size() - 1; i >= 0; i--) {
                    FloatingDamage fd = floatingDamages.get(i);
                    fd.update();
                    if (!fd.isAlive()) floatingDamages.remove(i);
                }

                if (chest != null) chest.update(player);

                // แพ้
                if (player.getHealth() <= 0 && !playedEndSound) {
                    SoundManager.playSound("lose.wav");
                    playedEndSound = true;
                    endState = "LOSE";
                    stageClear = true;
                    clearTime = (System.currentTimeMillis() - startTime) / 1000;
                    StatsManager.saveRecord(new StatsManager.Record(
                            currentMode.name(), totalSlimesKilled, clearTime));
                }

                boolean allGone = targets.isEmpty() ||
                        targets.stream().allMatch(t -> t.isDying);

                if (gameStarted && allGone && player.getHealth() > 0
                        && !stageClear && !playedEndSound) {

                    if (!chestSpawned) {
                        chestSpawned = true;
                        int chestY = (int)(getHeight() * 0.75) - 65;
                        chest = new TreasureChest(getWidth() / 2 - 35, chestY, wave >= 3);
                        chest.activate();
                    }

                    if (chest != null && chest.collected) {
                        showPortal = true;
                    }

                    if (wave >= 3 && chest != null && chest.collected && !playedEndSound) {
                        SoundManager.playSound("win.wav");
                        playedEndSound = true;
                        endState = "WIN";
                        stageClear = true;
                        clearTime = (System.currentTimeMillis() - startTime) / 1000;
                        StatsManager.saveRecord(new StatsManager.Record(
                                currentMode.name(), totalSlimesKilled, clearTime));
                    }

                    if (showPortal && wave < 3
                            && player.x + player.width >= portalRect.x && !transitioning) {
                        transitioning = true;
                        transitionAlpha = 0;
                        showPortal = false;
                    }
                }
            }

            repaint();
        }).start();
    }

    // ── ตรวจชนและหัก HP ──────────────────────────────────────────────────────
    private void checkHitTargets(Rectangle hitbox, int damage, boolean multiHit) {
        for (int i = targets.size() - 1; i >= 0; i--) {
            Target target = targets.get(i);
            if (target.isDying) continue;
            if (target.isHitByRect(hitbox)) {
                int dmg = target.takeDamageOnce(damage);
                if (dmg > 0) {
                    SoundManager.playSound("hit.wav");
                    floatingDamages.add(new FloatingDamage(
                            target.x + target.size / 2.0, target.y - 20, dmg));
                    if (target.isDead()) {
                        onSlimeKilled();
                        target.startDeathEffect();
                    }
                }
                if (!multiHit) break;
            }
        }
    }

    /** เรียกทุกครั้งที่ slime ตาย */
    private void onSlimeKilled() {
        score++;
        totalSlimesKilled++;
        int expGain = 30 + wave * 10; // EXP มากขึ้นตาม wave
        player.levelSys.gainExp(expGain);
    }

    private void nextWave() {
        wave++;
        currentBg = (currentBg + 1) % backgrounds.length;
        targets.clear();
        chestSpawned = false;
        chest = null;
        playedEndSound = false;
        showPortal = false;

        int numTargets, targetSize;
        switch (currentMode) {
            case EASY:   numTargets = wave;     targetSize = Math.max(100 - wave * 15, 40); break;
            case NORMAL: numTargets = wave + 1; targetSize = Math.max(80  - wave * 12, 35); break;
            case HARD:   numTargets = wave + 2; targetSize = Math.max(60  - wave * 10, 30); break;
            default:     numTargets = wave;     targetSize = 70;
        }

        for (int i = 0; i < numTargets; i++) {
            Target t = new Target();
            t.setSize(targetSize);
            t.spawn(getWidth(), getHeight(), currentMode.ordinal());
            if (numTargets > 1)
                t.x = (int)(getWidth() * (0.3 + i * 0.25));
            targets.add(t);
        }

        player.x = 50;
    }

    @Override
    public void addNotify() {
        super.addNotify();
        requestFocusInWindow();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (stageClear) return;
        if (player.levelSys.showUpgradeMenu) return; // หยุดรับ input เกมขณะเลือก upgrade

        switch (e.getKeyCode()) {
            case KeyEvent.VK_A: leftPressed = true; break;
            case KeyEvent.VK_D: rightPressed = true; break;
            case KeyEvent.VK_W: upPressed = true; break;

            case KeyEvent.VK_SPACE:
                player.attack();
                shots++;
                break;

            // ── Skills ─────────────────────────────────────────────────────
            case KeyEvent.VK_Q:
                player.skills.tryDash(player);
                break;

            case KeyEvent.VK_F:
                player.skills.tryPowerStrike(player);
                break;

            case KeyEvent.VK_T:
                // T — Whirlwind (unlock Lv 3)
                player.skills.tryWhirlwind(player.levelSys.level);
                break;

            case KeyEvent.VK_R:
                // R — เก็บของจากหีบ
                if (chest != null && chest.opened && !chest.collected
                        && chest.isNearPlayer(player)) {
                    chest.collect();
                    SoundManager.playSound("click.wav");
                    switch (chest.itemType) {
                        case SHIELD:       player.shield.activate();       break;
                        case HEAL:         player.heal(50);                 break;
                        case ATTACK_BUFF:  player.activateAttackBuff();    break;
                        case GOLD:         score += 500;                    break;
                    }
                }
                break;

            case KeyEvent.VK_E:
                // E — เปิดหีบ
                if (chest != null && !chest.opened && chest.isNearPlayer(player)) {
                    chest.open();
                    SoundManager.playSound("click.wav");
                }
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
        player.shield.active = false;
        player.attackBuffActive = false;
        player.attackDamage = player.baseAttackDamage;
        playedEndSound = false;
        showPortal = false;
        transitioning = false;
        transitionAlpha = 0;
        wave = 1;
        currentBg = 0;
        score = 0;
        shots = 0;
        totalSlimesKilled = 0;
        chestSpawned = false;
        chest = null;
        startTime = System.currentTimeMillis();

        // รีเซ็ต Level & Skill System
        player.levelSys = new LevelSystem();
        player.skills   = new SkillSystem();
        player.baseAttackDamage = 12;
        player.attackDamage     = 12;
        player.maxHealth = 150;
        player.health    = 150;
        player.speed     = 5;

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
            if (numTargets > 1)
                t.x = (int)(getWidth() * (0.3 + i * 0.35));
            targets.add(t);
        }

        player.x = 50;
        player.y = (int)(getHeight() * 0.75) - 15;
        requestFocusInWindow();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Image bg = backgrounds[currentBg];
        if (bg != null)
            g2.drawImage(bg, 0, 0, getWidth(), getHeight(), null);
        else {
            g2.setColor(Color.DARK_GRAY);
            g2.fillRect(0, 0, getWidth(), getHeight());
        }

        for (Target target : targets) target.draw(g2);
        if (chest != null) chest.draw(g2);

        // Whirlwind visual
        player.skills.drawWhirlEffect(g2, player, animTick);

        player.draw(g2);
        for (FloatingDamage fd : floatingDamages) fd.draw(g2);

        if (showPortal) drawPortal(g2);

        drawHUD(g2);

        // Skill Bar
        player.skills.drawSkillBar(g2, getWidth(), getHeight(), player.levelSys.level);

        if (stageClear && (endState.equals("WIN") || endState.equals("LOSE"))) {
            drawEndScreen(g2);
        }

        // Upgrade menu วาดทับทุกอย่าง
        player.levelSys.drawUpgradeMenu(g2, getWidth(), getHeight(), animTick);

        if (transitionAlpha > 0) {
            g2.setColor(new Color(0, 0, 0, Math.min(transitionAlpha, 255)));
            g2.fillRect(0, 0, getWidth(), getHeight());
            if (transitionAlpha > 200) {
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Arial", Font.BOLD, 28));
                String waveText = "Wave " + (wave + 1) + " / 3";
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(waveText,
                        getWidth() / 2 - fm.stringWidth(waveText) / 2,
                        getHeight() / 2);
            }
        }

        // crosshair
        g2.setColor(Color.RED);
        g2.drawLine(mouseX - 10, mouseY, mouseX + 10, mouseY);
        g2.drawLine(mouseX, mouseY - 10, mouseX, mouseY + 10);
    }

    private void drawHUD(Graphics2D g2) {
        g2.setColor(new Color(0, 0, 0, 120));
        g2.fillRoundRect(10, 10, 220, 115, 12, 12);

        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 18));
        g2.drawString("Score: " + score, 20, 35);
        g2.drawString("Wave:  " + wave + " / 3", 20, 58);

        if (!stageClear) {
            long elapsed = (System.currentTimeMillis() - startTime) / 1000;
            g2.drawString("Time:  " + elapsed + "s", 20, 81);
        }

        // Level + EXP ใน HUD
        player.levelSys.drawHUDLevel(g2, 20, 103);

        // Skill hints
        if (showPortal && wave < 3) {
            g2.setColor(new Color(255, 255, 100, 220));
            g2.setFont(new Font("Arial", Font.BOLD, 16));
            String hint = "► Walk right into the Portal!";
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(hint, getWidth() / 2 - fm.stringWidth(hint) / 2,
                    getHeight() - 90);
        }

        // controls hint ซ้ายล่าง
        g2.setColor(new Color(200, 200, 200, 160));
        g2.setFont(new Font("Arial", Font.PLAIN, 11));
        g2.drawString("SPACE=Attack  Q=Dash  F=PowerStrike  T=Whirl(Lv3)  E=Open  R=Collect", 10, getHeight() - 80);
    }

    private void drawPortal(Graphics2D g2) {
        int px = portalRect.x, py = portalRect.y;
        int pw = portalRect.width, ph = portalRect.height;

        for (int i = 6; i >= 0; i--) {
            g2.setColor(new Color(100, 50, 255, 25 * i));
            g2.fillOval(px - i * 8, py - i * 6, pw + i * 16, ph + i * 12);
        }

        g2.setColor(new Color(80, 0, 200, 200));
        g2.fillOval(px, py, pw, ph);

        g2.setColor(new Color(180, 100, 255));
        g2.setStroke(new BasicStroke(4));
        g2.drawOval(px, py, pw, ph);
        g2.setStroke(new BasicStroke(1));

        for (int i = 0; i < 5; i++) {
            double angle = Math.toRadians(i * 72 + animTick * 4);
            int sx = px + pw / 2 + (int)(Math.cos(angle) * pw / 2);
            int sy = py + ph / 2 + (int)(Math.sin(angle) * ph / 2);
            g2.setColor(new Color(220, 180, 255,
                    150 + (int)(Math.sin(animTick * 0.1 + i) * 80)));
            g2.fillOval(sx - 5, sy - 5, 10, 10);
        }

        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 13));
        g2.drawString("PORTAL", px + 2, py - 8);
    }

    private void drawEndScreen(Graphics2D g2) {
        g2.setColor(new Color(0, 0, 0, 210));
        g2.fillRect(0, 0, getWidth(), getHeight());

        int bounce = (int)(Math.sin(animTick * 0.1) * 8);
        g2.setFont(new Font("Arial", Font.BOLD, 64));
        String text = endState.equals("WIN") ? "YOU WIN!" : "YOU LOSE!";

        g2.setColor(Color.BLACK);
        g2.drawString(text, getWidth() / 2 - 170 + 4, 160 + bounce + 4);
        g2.setColor(endState.equals("WIN")
                ? new Color(0, 255, 150) : new Color(255, 80, 80));
        g2.drawString(text, getWidth() / 2 - 170, 160 + bounce);

        int boxX = getWidth() / 2 - 220;
        int boxY = 185;
        int boxW = 440;
        int boxH = 210;

        g2.setColor(new Color(20, 20, 40, 200));
        g2.fillRoundRect(boxX, boxY, boxW, boxH, 16, 16);
        g2.setColor(new Color(100, 150, 255));
        g2.setStroke(new BasicStroke(2));
        g2.drawRoundRect(boxX, boxY, boxW, boxH, 16, 16);
        g2.setStroke(new BasicStroke(1));

        g2.setFont(new Font("Arial", Font.BOLD, 17));
        g2.setColor(new Color(180, 220, 255));
        g2.drawString("── This Run ──", boxX + 155, boxY + 28);

        g2.setFont(new Font("Arial", Font.BOLD, 16));
        g2.setColor(Color.WHITE);
        g2.drawString("Time:    " + clearTime + " seconds", boxX + 20, boxY + 55);
        g2.drawString("Slimes:  " + totalSlimesKilled + " killed", boxX + 20, boxY + 78);
        g2.drawString("Score:   " + score, boxX + 20, boxY + 101);
        g2.drawString("Mode:    " + currentMode.name(), boxX + 20, boxY + 124);
        g2.drawString("Level:   " + player.levelSys.level, boxX + 240, boxY + 55);

        g2.setColor(new Color(100, 100, 150));
        g2.drawLine(boxX + 15, boxY + 135, boxX + boxW - 15, boxY + 135);

        g2.setFont(new Font("Arial", Font.BOLD, 14));
        g2.setColor(new Color(180, 220, 255));
        g2.drawString("── Recent Records ──", boxX + 135, boxY + 155);

        List<StatsManager.Record> records = StatsManager.loadRecords();
        int startIdx = Math.max(0, records.size() - 3);
        int yOff = boxY + 172;
        for (int i = startIdx; i < records.size(); i++) {
            StatsManager.Record r = records.get(i);
            g2.setColor(i == records.size() - 1
                    ? new Color(255, 220, 80) : new Color(190, 190, 190));
            g2.setFont(new Font("Arial", Font.PLAIN, 12));
            g2.drawString(r.date + "  [" + r.mode + "]  Slimes: "
                    + r.slimesKilled + "  Time: " + r.clearTimeSeconds + "s",
                    boxX + 15, yOff);
            yOff += 18;
        }

        drawButton(g2, playAgainBtn, "PLAY AGAIN", hoverPlay);
        drawButton(g2, menuBtn, "MENU", hoverMenu);
    }

    private void drawButton(Graphics2D g2, Rectangle r, String text, boolean hover) {
        g2.setColor(hover ? new Color(200, 200, 200) : Color.WHITE);
        g2.fillRoundRect(r.x, r.y, r.width, r.height, 30, 30);
        g2.setColor(Color.BLACK);
        g2.setFont(new Font("Arial", Font.BOLD, 18));
        FontMetrics fm = g2.getFontMetrics();
        int tx = r.x + (r.width - fm.stringWidth(text)) / 2;
        int ty = r.y + (r.height + fm.getAscent()) / 2 - 3;
        g2.drawString(text, tx, ty);
    }

    @Override public void run() {}
}