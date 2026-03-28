package aimtrainer;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import javax.swing.*;

public class MenuUI extends JPanel {

    Main main;

    CardLayout layout = new CardLayout();
    BackgroundPanel panel = new BackgroundPanel("menuUI.png");

    // ===== RoundButton =====
    static class RoundButton extends JButton {
        private final Color baseColor;
        private final Color hoverColor;
        private final Color pressColor;
        private final Color borderColor = new Color(0x6B, 0x38, 0x0A);
        private final Color shineColor  = new Color(0xFF, 0xFF, 0xFF, 70);
        private boolean isHovered = false;
        private boolean isPressed = false;

        public RoundButton(String text, Color base) {
            super(text);
            this.baseColor  = base;
            this.hoverColor = base.brighter();
            this.pressColor = base.darker();
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setOpaque(false);
            setForeground(Color.WHITE);
            setFont(new Font("SansSerif", Font.BOLD, 20));
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setPreferredSize(new Dimension(240, 52));

            addMouseListener(new java.awt.event.MouseAdapter() {
                @Override public void mouseEntered(java.awt.event.MouseEvent e)  { isHovered = true;  repaint(); }
                @Override public void mouseExited (java.awt.event.MouseEvent e)  { isHovered = false; repaint(); }
                @Override public void mousePressed(java.awt.event.MouseEvent e)  { isPressed = true;  repaint(); }
                @Override public void mouseReleased(java.awt.event.MouseEvent e) { isPressed = false; repaint(); }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth(), h = getHeight(), arc = h;
            Color fill = isPressed ? pressColor : (isHovered ? hoverColor : baseColor);

            if (!isPressed) {
                g2.setColor(new Color(0, 0, 0, 60));
                g2.fill(new RoundRectangle2D.Float(3, 5, w - 3, h - 2, arc, arc));
            }

            g2.setColor(borderColor);
            g2.fill(new RoundRectangle2D.Float(0, 0, w, h, arc, arc));

            g2.setColor(fill);
            g2.fill(new RoundRectangle2D.Float(2, 2, w - 4, h - 4, arc - 2, arc - 2));

            if (!isPressed) {
                g2.setColor(shineColor);
                g2.fill(new RoundRectangle2D.Float(4, 4, w - 8, h / 2 - 4, arc - 4, arc - 4));
            }

            FontMetrics fm = g2.getFontMetrics(getFont());
            int tx = (w - fm.stringWidth(getText())) / 2;
            int ty = (h - fm.getHeight()) / 2 + fm.getAscent();

            g2.setFont(getFont());
            g2.setColor(new Color(0, 0, 0, 100));
            g2.drawString(getText(), tx + 1, ty + 1);
            g2.setColor(getForeground());
            g2.drawString(getText(), tx, ty);
            g2.dispose();
        }

        @Override
        public boolean contains(int x, int y) {
            int arc = getHeight();
            return new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), arc, arc).contains(x, y);
        }
    }

    // ===== VolumeButton =====
    static class VolumeButton extends JButton {
        private boolean muted = false;
        private float volume  = 1.0f;
        private boolean isHovered = false;
        private boolean isPressed = false;

        public VolumeButton() {
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setOpaque(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setPreferredSize(new Dimension(52, 52));

            addMouseListener(new java.awt.event.MouseAdapter() {
                @Override public void mouseEntered(java.awt.event.MouseEvent e)  { isHovered = true;  repaint(); }
                @Override public void mouseExited (java.awt.event.MouseEvent e)  { isHovered = false; repaint(); }
                @Override public void mousePressed(java.awt.event.MouseEvent e)  { isPressed = true;  repaint(); }
                @Override public void mouseReleased(java.awt.event.MouseEvent e) { isPressed = false; repaint(); }
            });
        }

        public boolean isMuted()          { return muted; }
        public void    toggleMute()        { muted = !muted; repaint(); }
        public void    setMute(boolean m)  { muted = m; repaint(); }
        public float   getVolume()         { return volume; }
        public void    setVolume(float v)  { volume = Math.max(0f, Math.min(1f, v)); repaint(); }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth(), h = getHeight();

            // ---- พื้นวงกลม (สไตล์เดียวกับ RoundButton) ----
            Color base  = new Color(0x60, 0x60, 0x60);
            Color fill  = isPressed ? base.darker() : (isHovered ? base.brighter() : base);
            Color border = new Color(0x6B, 0x38, 0x0A);

            if (!isPressed) {
                g2.setColor(new Color(0, 0, 0, 60));
                g2.fillOval(3, 5, w - 3, h - 3);
            }

            g2.setColor(border);
            g2.fillOval(0, 0, w, h);

            g2.setColor(fill);
            g2.fillOval(2, 2, w - 4, h - 4);

            if (!isPressed) {
                g2.setColor(new Color(255, 255, 255, 70));
                g2.fillArc(4, 4, w - 8, (h - 8) / 2 + 2, 0, 180);
            }

            // ---- วาด icon speaker ----
            int cx = w / 2 - 2, cy = h / 2;
            g2.setColor(muted ? new Color(255, 100, 100) : Color.WHITE);
            g2.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            // ลำโพงสี่เหลี่ยม
            g2.fillRect(cx - 9, cy - 4, 5, 8);
            // กรวย
            int[] px = { cx - 4, cx + 3, cx + 3, cx - 4 };
            int[] py = { cy - 4, cy - 8, cy + 8, cy + 4 };
            g2.fillPolygon(px, py, 4);

            if (!muted) {
                // คลื่นเสียง
                if (volume > 0.05f) g2.drawArc(cx + 4, cy - 4, 6, 8,  -30, 60);
                if (volume > 0.35f) g2.drawArc(cx + 6, cy - 7, 9, 14, -40, 80);
                if (volume > 0.65f) g2.drawArc(cx + 8, cy - 10, 12, 20, -50, 100);
            } else {
                // X ขีดฆ่า
                g2.setColor(new Color(255, 80, 80));
                g2.setStroke(new BasicStroke(2.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.drawLine(cx + 5, cy - 6, cx + 13, cy + 6);
                g2.drawLine(cx + 13, cy - 6, cx + 5, cy + 6);
            }

            g2.dispose();
        }
    }

    // ===== KeyBadge (how to play แต่ละแถว) =====
    static class KeyBadge extends JPanel {
        private final String keyText;
        private final String descText;
        private final Color  badgeColor;

        public KeyBadge(String key, String desc, Color color) {
            this.keyText    = key;
            this.descText   = desc;
            this.badgeColor = color;
            setOpaque(false);
            setPreferredSize(new Dimension(340, 54));
            setMaximumSize(new Dimension(340, 54));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth(), h = getHeight(), arc = h;
            int badgeW = 120;

            // shadow
            g2.setColor(new Color(0, 0, 0, 60));
            g2.fill(new RoundRectangle2D.Float(3, 5, w - 3, h - 3, arc, arc));

            // outer border (สีเดียวกับ RoundButton)
            g2.setColor(new Color(0x6B, 0x38, 0x0A));
            g2.fill(new RoundRectangle2D.Float(0, 0, w, h, arc, arc));

            // dark body
            g2.setColor(new Color(0x1A, 0x1A, 0x2E));
            g2.fill(new RoundRectangle2D.Float(2, 2, w - 4, h - 4, arc - 2, arc - 2));

            // colored badge section
            g2.setColor(badgeColor);
            g2.fill(new RoundRectangle2D.Float(2, 2, badgeW, h - 4, arc - 2, arc - 2));
            // ปิดขอบขวาของ badge ให้ตรง
            g2.fillRect(badgeW - arc / 2, 2, arc / 2, h - 4);

            // shine บน badge
            g2.setColor(new Color(255, 255, 255, 55));
            g2.fill(new RoundRectangle2D.Float(4, 4, badgeW - 4, (h - 8) / 2, arc - 4, arc - 4));

            // key text
            Font keyFont = new Font("SansSerif", Font.BOLD, 16);
            g2.setFont(keyFont);
            FontMetrics fm = g2.getFontMetrics();
            int kx = (badgeW - fm.stringWidth(keyText)) / 2 + 2;
            int ky = (h + fm.getAscent() - fm.getDescent()) / 2;

            g2.setColor(new Color(0, 0, 0, 90));
            g2.drawString(keyText, kx + 1, ky + 1);
            g2.setColor(Color.WHITE);
            g2.drawString(keyText, kx, ky);

            // desc text
            g2.setFont(new Font("SansSerif", Font.BOLD, 19));
            fm = g2.getFontMetrics();
            ky = (h + fm.getAscent() - fm.getDescent()) / 2;
            int dx = badgeW + 18;

            g2.setColor(new Color(0, 0, 0, 90));
            g2.drawString(descText, dx + 1, ky + 1);
            g2.setColor(Color.WHITE);
            g2.drawString(descText, dx, ky);

            g2.dispose();
        }
    }

    public MenuUI(Main main) {
        this.main = main;

        setLayout(new BorderLayout());
        panel.setLayout(layout);

        // ===== START MENU =====
        BackgroundPanel startMenu = new BackgroundPanel("menuUI.png");
        startMenu.setLayout(new GridBagLayout());

        RoundButton start = new RoundButton("START",       new Color(0xC0, 0x6A, 0x20));
        RoundButton howTo = new RoundButton("HOW TO PLAY", new Color(0x20, 0x80, 0xC0));
        RoundButton quit  = new RoundButton("QUIT",        new Color(0xC0, 0x6A, 0x20));
        VolumeButton volBtn = new VolumeButton();

        // ปุ่มกลาง — ใช้ panel แยกต่างหากไม่ให้ volBtn ดัน layout
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false);
        GridBagConstraints gc = new GridBagConstraints();
        gc.gridx = 0; gc.insets = new Insets(10, 0, 10, 0);
        gc.gridy = 0; centerPanel.add(start, gc);
        gc.gridy = 1; centerPanel.add(howTo, gc);
        gc.gridy = 2; centerPanel.add(quit,  gc);

        // volBtn มุมขวาบน แยกออกมาใส่ NORTH
        JPanel topRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 12));
        topRight.setOpaque(false);
        topRight.add(volBtn);

        startMenu.setLayout(new BorderLayout());
        startMenu.add(centerPanel, BorderLayout.CENTER);
        startMenu.add(topRight,    BorderLayout.NORTH);

        // ===== MODE MENU =====
        BackgroundPanel modeMenu = new BackgroundPanel("background.jpg");
        modeMenu.setLayout(new GridBagLayout());

        GridBagConstraints gbc2 = new GridBagConstraints();
        gbc2.gridx = 0;
        gbc2.insets = new Insets(10, 0, 10, 0);

        JLabel select = new JLabel("SELECT MODE");
        select.setFont(new Font("SansSerif", Font.BOLD, 30));
        select.setForeground(Color.WHITE);

        RoundButton easy   = new RoundButton("EASY",   new Color(0x30, 0x9A, 0x30));
        RoundButton normal = new RoundButton("NORMAL", new Color(0xC0, 0x96, 0x20));
        RoundButton hard   = new RoundButton("HARD",   new Color(0xC0, 0x30, 0x30));
        RoundButton back   = new RoundButton("BACK",   new Color(0x60, 0x60, 0x60));

        gbc2.gridy = 0; modeMenu.add(select, gbc2);
        gbc2.gridy = 1; modeMenu.add(easy,   gbc2);
        gbc2.gridy = 2; modeMenu.add(normal, gbc2);
        gbc2.gridy = 3; modeMenu.add(hard,   gbc2);
        gbc2.gridy = 4; modeMenu.add(back,   gbc2);

        // ===== HOW TO PLAY MENU =====
        BackgroundPanel howToMenu = new BackgroundPanel("background.jpg");
        howToMenu.setLayout(new GridBagLayout());

        GridBagConstraints gbc3 = new GridBagConstraints();
        gbc3.gridx = 0;
        gbc3.insets = new Insets(12, 0, 12, 0);

        JLabel howToTitle = new JLabel("HOW TO PLAY");
        howToTitle.setFont(new Font("SansSerif", Font.BOLD, 36));
        howToTitle.setForeground(Color.WHITE);

        String[] keys  = { "W",    "A",          "D",           "SPACEBAR" };
        String[] descs = { "JUMP", "MOVE LEFT",  "MOVE RIGHT",  "ATTACK"   };
        Color[]  cols  = {
            new Color(0x30, 0x9A, 0x30),
            new Color(0x20, 0x80, 0xC0),
            new Color(0x20, 0x80, 0xC0),
            new Color(0xC0, 0x30, 0x30)
        };

        JPanel badgesPanel = new JPanel();
        badgesPanel.setOpaque(false);
        badgesPanel.setLayout(new BoxLayout(badgesPanel, BoxLayout.Y_AXIS));
        for (int i = 0; i < keys.length; i++) {
            KeyBadge kb = new KeyBadge(keys[i], descs[i], cols[i]);
            kb.setAlignmentX(Component.CENTER_ALIGNMENT);
            badgesPanel.add(kb);
            if (i < keys.length - 1)
                badgesPanel.add(Box.createVerticalStrut(10));
        }

        RoundButton backFromHowTo = new RoundButton("BACK", new Color(0x60, 0x60, 0x60));

        gbc3.gridy = 0; howToMenu.add(howToTitle,    gbc3);
        gbc3.gridy = 1; howToMenu.add(badgesPanel,   gbc3);
        gbc3.gridy = 2; howToMenu.add(backFromHowTo, gbc3);

        panel.add(startMenu, "start");
        panel.add(modeMenu,  "mode");
        panel.add(howToMenu, "howto");
        add(panel);

        // ===== VOLUME POPUP =====
        JPopupMenu volumePopup = new JPopupMenu();
        volumePopup.setBackground(new Color(0x1A, 0x1A, 0x30));
        volumePopup.setBorder(BorderFactory.createLineBorder(new Color(0x6B, 0x38, 0x0A), 2));

        JPanel popPanel = new JPanel();
        popPanel.setOpaque(false);
        popPanel.setLayout(new BoxLayout(popPanel, BoxLayout.Y_AXIS));
        popPanel.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));

        JLabel volLabel = new JLabel("🔊  VOLUME", SwingConstants.CENTER);
        volLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        volLabel.setForeground(Color.WHITE);
        volLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JSlider slider = new JSlider(0, 100, 100);
        slider.setOpaque(false);
        slider.setForeground(new Color(0xC0, 0x6A, 0x20));
        slider.setPreferredSize(new Dimension(150, 30));
        slider.setMaximumSize(new Dimension(150, 30));
        slider.setAlignmentX(Component.CENTER_ALIGNMENT);

        RoundButton muteBtn = new RoundButton("🔇 MUTE", new Color(0x60, 0x60, 0x60));
        muteBtn.setPreferredSize(new Dimension(150, 42));
        muteBtn.setMaximumSize(new Dimension(150, 42));
        muteBtn.setFont(new Font("SansSerif", Font.BOLD, 16));
        muteBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        popPanel.add(volLabel);
        popPanel.add(Box.createVerticalStrut(8));
        popPanel.add(slider);
        popPanel.add(Box.createVerticalStrut(8));
        popPanel.add(muteBtn);
        volumePopup.add(popPanel);

        // ===== ACTIONS =====
        start.addActionListener(e -> {
            SoundManager.playSound("click.wav");
            layout.show(panel, "mode");
        });

        howTo.addActionListener(e -> {
            SoundManager.playSound("click.wav");
            layout.show(panel, "howto");
        });

        backFromHowTo.addActionListener(e -> {
            SoundManager.playSound("click.wav");
            layout.show(panel, "start");
        });

        back.addActionListener(e -> {
            SoundManager.playSound("click.wav");
            layout.show(panel, "start");
        });

        quit.addActionListener(e -> {
            SoundManager.playSound("click.wav");
            int confirm = JOptionPane.showConfirmDialog(
                this, "Quit?", "Exit", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) System.exit(0);
        });

        easy.addActionListener(e -> {
            SoundManager.playSound("click.wav");
            main.startGame(Mode.EASY);
        });

        normal.addActionListener(e -> {
            SoundManager.playSound("click.wav");
            main.startGame(Mode.NORMAL);
        });

        hard.addActionListener(e -> {
            SoundManager.playSound("click.wav");
            main.startGame(Mode.HARD);
        });

        // Volume popup
        volBtn.addActionListener(e -> {
            SoundManager.playSound("click.wav");
            volumePopup.show(volBtn, volBtn.getWidth() / 2 - 90, volBtn.getHeight() + 6);
        });

        // Slider ปรับเสียง
        slider.addChangeListener(e -> {
            float v = slider.getValue() / 100.0f;
            volBtn.setVolume(v);
            SoundManager.setVolume(v);
            boolean nowMuted = (slider.getValue() == 0);
            volBtn.setMute(nowMuted);
            muteBtn.setText(nowMuted ? "🔊 UNMUTE" : "🔇 MUTE");
        });

        // Mute toggle
        muteBtn.addActionListener(e -> {
            SoundManager.playSound("click.wav");
            volBtn.toggleMute();
            if (volBtn.isMuted()) {
                SoundManager.setVolume(0f);
                slider.setValue(0);
                muteBtn.setText("🔊 UNMUTE");
            } else {
                float v = Math.max(volBtn.getVolume(), 0.5f);
                volBtn.setVolume(v);
                SoundManager.setVolume(v);
                slider.setValue((int)(v * 100));
                muteBtn.setText("🔇 MUTE");
            }
        });
    }
}