package aimtrainer;

import java.awt.*;
import javax.swing.*;

public class Main extends JFrame {

    CardLayout layout = new CardLayout();
    JPanel mainPanel = new JPanel(layout);
    GamePanel game;

    public Main() {
        // ตั้งค่า JFrame
        setTitle("Slime Slayer");
        setSize(900, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // สร้าง panel เกมและเมนู
        game = new GamePanel(this);
        MenuUI menu = new MenuUI(this);

        // ใส่ panel ลง mainPanel
        mainPanel.add(menu, "menu");
        mainPanel.add(game, "game");

        add(mainPanel);
        setVisible(true);
        
        SoundManager.playMusic();
        
        
    }
    public void startGame(Mode mode) {
        game.setMode(mode);
        layout.show(mainPanel, "game");
    }

    public void backToMenu() {
        layout.show(mainPanel, "menu");
    }

    public static void main(String[] args) {
        new Main();
    }
}