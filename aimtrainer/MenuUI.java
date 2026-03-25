package aimtrainer;

import javax.swing.*;
import java.awt.*;

public class MenuUI extends JPanel {

    Main main;

    CardLayout layout = new CardLayout();
    BackgroundPanel panel = new BackgroundPanel("menuUI.png");

    public MenuUI(Main main){

        this.main = main;

        // Set background for main MenuUI
        setOpaque(true);
        setLayout(new BorderLayout());
        panel.setLayout(layout);

        // ================= START MENU =================
        BackgroundPanel startMenu = new BackgroundPanel("menuUI.png");
        startMenu.setLayout(new GridLayout(3,1,20,20));
        startMenu.setBorder(BorderFactory.createEmptyBorder(100,100,100,100));

        JLabel title = new JLabel("SLIME SLAYER",SwingConstants.CENTER);
        title.setFont(new Font("SansSerif",Font.BOLD,40));
        title.setForeground(Color.WHITE);

        JButton start = new JButton("START");
        JButton quit = new JButton("QUIT");

        start.setFont(new Font("SansSerif",Font.BOLD,25));
        quit.setFont(new Font("SansSerif",Font.BOLD,25));
        start.setForeground(Color.WHITE);
        quit.setForeground(Color.WHITE);
        start.setBackground(new Color(200, 100, 50));
        quit.setBackground(new Color(200, 50, 50));
        start.setFocusPainted(false);
        quit.setFocusPainted(false);
        start.setOpaque(true);
        quit.setOpaque(true);

        startMenu.add(title);
        startMenu.add(start);
        startMenu.add(quit);

        // ================= MODE MENU =================
        BackgroundPanel modeMenu = new BackgroundPanel("background.jpg");
        modeMenu.setLayout(new GridLayout(5,1,20,20));
        modeMenu.setBorder(BorderFactory.createEmptyBorder(100,100,100,100));

        JLabel select = new JLabel("SELECT MODE",SwingConstants.CENTER);
        select.setFont(new Font("SansSerif",Font.BOLD,30));
        select.setForeground(Color.WHITE);

        JButton easy = new JButton("EASY");
        JButton normal = new JButton("NORMAL");
        JButton hard = new JButton("HARD");
        JButton back = new JButton("BACK");

        easy.setFont(new Font("SansSerif",Font.BOLD,20));
        normal.setFont(new Font("SansSerif",Font.BOLD,20));
        hard.setFont(new Font("SansSerif",Font.BOLD,20));
        back.setFont(new Font("SansSerif",Font.BOLD,20));
        
        // Style mode buttons
        easy.setForeground(Color.WHITE);
        normal.setForeground(Color.WHITE);
        hard.setForeground(Color.WHITE);
        back.setForeground(Color.WHITE);
        easy.setBackground(new Color(50, 150, 50));
        normal.setBackground(new Color(200, 150, 50));
        hard.setBackground(new Color(200, 50, 50));
        back.setBackground(new Color(100, 100, 100));
        easy.setFocusPainted(false);
        normal.setFocusPainted(false);
        hard.setFocusPainted(false);
        back.setFocusPainted(false);
        easy.setOpaque(true);
        normal.setOpaque(true);
        hard.setOpaque(true);
        back.setOpaque(true);

        modeMenu.add(select);
        modeMenu.add(easy);
        modeMenu.add(normal);
        modeMenu.add(hard);
        modeMenu.add(back);

        // ================= ADD PANEL =================
        panel.add(startMenu,"start");
        panel.add(modeMenu,"mode");

        add(panel);

        // ================= ACTION =================

        // ไปหน้าเลือกโหมด
        start.addActionListener(e -> layout.show(panel,"mode"));

        // กลับหน้าแรก
        back.addActionListener(e -> layout.show(panel,"start"));

        // ออกจากเกม (มี confirm)
        quit.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to quit?",
                "Quit Game",
                JOptionPane.YES_NO_OPTION
            );
            if(confirm == JOptionPane.YES_OPTION){
                System.exit(0);
            }
        });

        // เลือกโหมด
        easy.addActionListener(e -> main.startGame(Mode.EASY));
        normal.addActionListener(e -> main.startGame(Mode.NORMAL));
        hard.addActionListener(e -> main.startGame(Mode.HARD));
    }
}