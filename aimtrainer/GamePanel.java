package aimtrainer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class GamePanel extends JPanel {

    Target target = new Target();

    int score = 0;
    int shots = 0;

    Mode currentMode;

    public GamePanel(){

        setBackground(Color.DARK_GRAY);

        // Timer ให้ slime เคลื่อนที่
        new Timer(16, e -> {
            target.update(getWidth(), getHeight());
            repaint();
        }).start();

        // ยิง
        addMouseListener(new MouseAdapter(){
            public void mousePressed(MouseEvent e){

                shots++;

                if(target.isHit(e.getX(), e.getY())){
                    score++;
                    target.spawn(getWidth(), getHeight());
                }

                repaint();
            }
        });
    }

    public void setMode(Mode mode){
        this.currentMode = mode;

        switch(mode){
            case EASY:
                target.setSize(100);
                break;
            case NORMAL:
                target.setSize(70);
                break;
            case HARD:
                target.setSize(50);
                break;
        }

        score = 0;
        shots = 0;

        target.spawn(800,600); // กันบัค width=0
    }

    @Override
    protected void paintComponent(Graphics g){
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;

        target.draw(g2);

        g2.setColor(Color.WHITE);
        g2.setFont(new Font("SansSerif", Font.BOLD, 20));

        g2.drawString("Score: " + score, 20, 30);
        g2.drawString("Shots: " + shots, 20, 60);

        double acc = (shots == 0) ? 0 : (score * 100.0 / shots);
        g2.drawString("Accuracy: " + String.format("%.1f", acc) + "%", 20, 90);
    }
}