package aimtrainer;

import javax.swing.*;
import java.awt.*;

public class Main extends JFrame {

    CardLayout layout = new CardLayout();
    JPanel mainPanel = new JPanel(layout);

    GamePanel game;

    public Main(){

        setTitle("Aim Trainer");
        setSize(900,600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        game = new GamePanel();
        MenuUI menu = new MenuUI(this);

        mainPanel.add(menu,"menu");
        mainPanel.add(game,"game");

        add(mainPanel);
        setVisible(true);
    }

    public void startGame(Mode mode){
        game.setMode(mode);
        layout.show(mainPanel,"game");
    }

    public static void main(String[] args){
        new Main();
    }
}