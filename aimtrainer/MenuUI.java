package aimtrainer;

import javax.swing.*;
import java.awt.*;

public class MenuUI extends JPanel {

    Main main;

    CardLayout layout = new CardLayout();
    JPanel panel = new JPanel(layout);

    public MenuUI(Main main){

        this.main = main;

        setLayout(new BorderLayout());

        // ================= START MENU =================
        JPanel startMenu = new JPanel();
        startMenu.setBackground(Color.DARK_GRAY);
        startMenu.setLayout(new GridLayout(3,1,20,20));
        startMenu.setBorder(BorderFactory.createEmptyBorder(100,100,100,100));

        JLabel title = new JLabel("AIM TRAINER",SwingConstants.CENTER);
        title.setFont(new Font("SansSerif",Font.BOLD,40));
        title.setForeground(Color.WHITE);

        JButton start = new JButton("START");
        JButton quit = new JButton("QUIT");

        start.setFont(new Font("SansSerif",Font.BOLD,25));
        quit.setFont(new Font("SansSerif",Font.BOLD,25));

        startMenu.add(title);
        startMenu.add(start);
        startMenu.add(quit);

        // ================= MODE MENU =================
        JPanel modeMenu = new JPanel();
        modeMenu.setBackground(Color.DARK_GRAY);
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