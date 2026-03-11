package aimtrainer;

import javax.swing.*;
import java.awt.*;

public class MenuUI extends JPanel {

    public MenuUI(Main main){

        setLayout(new GridLayout(3,1,20,20));
        setBackground(Color.BLACK);

        JButton easy = new JButton("EASY");
        JButton normal = new JButton("NORMAL");
        JButton hard = new JButton("HARD");

        easy.setFont(new Font("Arial",Font.BOLD,25));
        normal.setFont(new Font("Arial",Font.BOLD,25));
        hard.setFont(new Font("Arial",Font.BOLD,25));

        easy.addActionListener(e -> main.startGame(Mode.EASY));
        normal.addActionListener(e -> main.startGame(Mode.NORMAL));
        hard.addActionListener(e -> main.startGame(Mode.HARD));

        add(easy);
        add(normal);
        add(hard);
    }
}