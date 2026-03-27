package aimtrainer;

import java.awt.*;
import javax.swing.*;

public class Main extends JFrame {

    CardLayout layout = new CardLayout();
    JPanel mainPanel = new JPanel(layout);
    GamePanel game;

    public Main() {
        setTitle("Slime Slayer");
        setSize(900, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        game = new GamePanel(this);
        MenuUI menu = new MenuUI(this);

        mainPanel.add(menu, "menu");
        mainPanel.add(game, "game");

        add(mainPanel);
        setVisible(true);

        // 🎵 เล่นเพลงพื้นหลัง
        SoundManager.playMusic();
    }

    // 🎮 เริ่มเกม (มีเสียง click)
    public void startGame(Mode mode) {
        SoundManager.playSound("click.wav"); // 🔊 เสียงกดปุ่ม

        game.setMode(mode);

        layout.show(mainPanel, "game");
        game.requestFocusInWindow();
    }

    // 🔙 กลับเมนู (มีเสียง click)
    public void backToMenu() {
        SoundManager.playSound("click.wav"); // 🔊 เสียงกดปุ่ม

        layout.show(mainPanel, "menu");
    }

    public static void main(String[] args) {
        new Main();
    }
}