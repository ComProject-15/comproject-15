package aimtrainer;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class MenuUI extends JPanel {

    Main main;

    CardLayout layout = new CardLayout();
    BackgroundPanel panel = new BackgroundPanel("menuUI.png");

    // ===== RoundButton (pill shape) =====
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
    // ===== จบ RoundButton =====


    public MenuUI(Main main) {
        this.main = main;

        setOpaque(true);
        setLayout(new BorderLayout());
        panel.setLayout(layout);

        // ================= START MENU =================
        BackgroundPanel startMenu = new BackgroundPanel("menuUI.png");
        startMenu.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(10, 0, 10, 0);

        RoundButton start = new RoundButton("START",       new Color(0xC0, 0x6A, 0x20));
        RoundButton howTo = new RoundButton("HOW TO PLAY", new Color(0x20, 0x80, 0xC0));
        RoundButton quit  = new RoundButton("QUIT",        new Color(0xC0, 0x6A, 0x20));

        gbc.gridy = 0; startMenu.add(start, gbc);
        gbc.gridy = 1; startMenu.add(howTo, gbc);
        gbc.gridy = 2; startMenu.add(quit,  gbc);

        // ================= HOW TO PLAY =================
        BackgroundPanel howToMenu = new BackgroundPanel("menuUI.png");
        howToMenu.setLayout(new GridBagLayout());

        GridBagConstraints hgbc = new GridBagConstraints();
        hgbc.gridx = 0;
        hgbc.anchor = GridBagConstraints.CENTER;
        hgbc.insets = new Insets(8, 0, 8, 0);

        // กล่องวิธีเล่น
        JPanel tutorialBox = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(30, 15, 5, 200));
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 20, 20));
                g2.setColor(new Color(0xC0, 0x6A, 0x20));
                g2.setStroke(new BasicStroke(2));
                g2.draw(new RoundRectangle2D.Float(1, 1, getWidth() - 2, getHeight() - 2, 20, 20));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        tutorialBox.setOpaque(false);
        tutorialBox.setLayout(new GridBagLayout());
        tutorialBox.setPreferredSize(new Dimension(420, 300));

        // หัวข้อ
        JLabel title = new JLabel("HOW TO PLAY", SwingConstants.CENTER);
        title.setFont(new Font("Courier New", Font.BOLD, 26));
        title.setForeground(new Color(0xFF, 0xD7, 0x00));

        // control list: [key, คำอธิบาย]
        String[][] controls = {
            { "A",     "Move Left" },
            { "D",     "Move Right" },
            { "W",     "Jump" },
            { "SPACE", "Attack" },
        };

        GridBagConstraints tgbc = new GridBagConstraints();
        tgbc.gridx = 0; tgbc.gridy = 0;
        tgbc.gridwidth = 2;
        tgbc.insets = new Insets(10, 20, 16, 20);
        tutorialBox.add(title, tgbc);

        // เส้นแบ่ง
        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(0xC0, 0x6A, 0x20));
        sep.setPreferredSize(new Dimension(360, 2));
        tgbc.gridy = 1;
        tgbc.insets = new Insets(0, 20, 12, 20);
        tutorialBox.add(sep, tgbc);

        // แถว control แต่ละอัน
        tgbc.gridwidth = 1;
        for (int i = 0; i < controls.length; i++) {
            tgbc.gridy = i + 2;

            // กล่องปุ่ม keyboard
            JLabel keyLabel = new JLabel(controls[i][0], SwingConstants.CENTER) {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(new Color(0xC0, 0x6A, 0x20));
                    g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));
                    g2.setColor(new Color(0x6B, 0x38, 0x0A));
                    g2.setStroke(new BasicStroke(1.5f));
                    g2.draw(new RoundRectangle2D.Float(1, 1, getWidth() - 2, getHeight() - 2, 8, 8));
                    g2.dispose();
                    super.paintComponent(g);
                }
            };
            keyLabel.setFont(new Font("Courier New", Font.BOLD, 16));
            keyLabel.setForeground(Color.WHITE);
            keyLabel.setOpaque(false);
            keyLabel.setPreferredSize(new Dimension(80, 34));

            tgbc.gridx = 0;
            tgbc.anchor = GridBagConstraints.EAST;
            tgbc.insets = new Insets(8, 30, 8, 10);
            tutorialBox.add(keyLabel, tgbc);

            // คำอธิบาย
            JLabel descLabel = new JLabel(controls[i][1]);
            descLabel.setFont(new Font("SansSerif", Font.PLAIN, 18));
            descLabel.setForeground(Color.WHITE);

            tgbc.gridx = 1;
            tgbc.anchor = GridBagConstraints.WEST;
            tgbc.insets = new Insets(8, 14, 8, 30);
            tutorialBox.add(descLabel, tgbc);
        }

        RoundButton backFromHow = new RoundButton("BACK", new Color(0x60, 0x60, 0x60));

        hgbc.gridy = 0; howToMenu.add(tutorialBox, hgbc);
        hgbc.gridy = 1; hgbc.insets = new Insets(16, 0, 8, 0);
        howToMenu.add(backFromHow, hgbc);

        // ================= MODE MENU =================
        BackgroundPanel modeMenu = new BackgroundPanel("background.jpg");
        modeMenu.setLayout(new GridBagLayout());

        GridBagConstraints gbc2 = new GridBagConstraints();
        gbc2.gridx = 0;
        gbc2.fill = GridBagConstraints.NONE;
        gbc2.anchor = GridBagConstraints.CENTER;
        gbc2.insets = new Insets(10, 0, 10, 0);

        JLabel select = new JLabel("SELECT MODE", SwingConstants.CENTER);
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

        // ================= ADD PANEL =================
        panel.add(startMenu, "start");
        panel.add(howToMenu, "howto");
        panel.add(modeMenu,  "mode");
        add(panel);

        // ================= ACTION =================
        start.addActionListener(e -> layout.show(panel, "mode"));
        howTo.addActionListener(e -> layout.show(panel, "howto"));
        backFromHow.addActionListener(e -> layout.show(panel, "start"));
        back.addActionListener(e -> layout.show(panel, "start"));

        quit.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to quit?",
                "Quit Game",
                JOptionPane.YES_NO_OPTION
            );
            if (confirm == JOptionPane.YES_OPTION) System.exit(0);
        });

        easy.addActionListener(e   -> main.startGame(Mode.EASY));
        normal.addActionListener(e -> main.startGame(Mode.NORMAL));
        hard.addActionListener(e   -> main.startGame(Mode.HARD));
    }
}