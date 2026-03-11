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

        // หน้า START
        JPanel startMenu = new JPanel();
        startMenu.setBackground(Color.DARK_GRAY);
        startMenu.setLayout(new GridLayout(2,1,20,20));

        JLabel title = new JLabel("AIM TRAINER",SwingConstants.CENTER);
        title.setFont(new Font("Arial",Font.BOLD,40));
        title.setForeground(Color.WHITE);

        JButton start = new JButton("START");
        start.setFont(new Font("Arial",Font.BOLD,25));

        startMenu.add(title);
        startMenu.add(start);

        // หน้าเลือกโหมด
        JPanel modeMenu = new JPanel();
        modeMenu.setBackground(Color.DARK_GRAY);
        modeMenu.setLayout(new GridLayout(4,1,20,20));

        JLabel select = new JLabel("SELECT MODE",SwingConstants.CENTER);
        select.setFont(new Font("Arial",Font.BOLD,30));
        select.setForeground(Color.WHITE);

        JButton easy = new JButton("EASY");
        JButton normal = new JButton("NORMAL");
        JButton hard = new JButton("HARD");

        easy.setFont(new Font("Arial",Font.BOLD,20));
        normal.setFont(new Font("Arial",Font.BOLD,20));
        hard.setFont(new Font("Arial",Font.BOLD,20));

        modeMenu.add(select);
        modeMenu.add(easy);
        modeMenu.add(normal);
        modeMenu.add(hard);

        panel.add(startMenu,"start");
        panel.add(modeMenu,"mode");

        add(panel);

        // กด START → ไปเลือกโหมด
        start.addActionListener(e -> layout.show(panel,"mode"));

        // เลือกโหมด → เข้าเกม
        easy.addActionListener(e -> main.startGame(Mode.EASY));
        normal.addActionListener(e -> main.startGame(Mode.NORMAL));
        hard.addActionListener(e -> main.startGame(Mode.HARD));
    }
}