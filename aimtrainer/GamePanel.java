package aimtrainer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class GamePanel extends JPanel implements MouseMotionListener, MouseListener {

    Target target = new Target();
    Gun gun = new Gun();
    ScoreSystem score = new ScoreSystem();

    int mouseX;
    int mouseY;

    int targetSize = 60;
    int spawnDelay = 1000;

    public GamePanel(){

        setBackground(Color.DARK_GRAY);
 
        addMouseMotionListener(this);
        addMouseListener(this);
        target.spawnCenter(800,600);

        Timer timer = new Timer(16,e -> repaint());
        timer.start();
    }
    public void setMode(Mode mode){

    score.setMode(mode);

    if(mode == Mode.EASY){
        targetSize = 80;
    }

    if(mode == Mode.NORMAL){
        targetSize = 60;
    }

    if(mode == Mode.HARD){
        targetSize = 40;
    }

    target.setSize(targetSize);
    target.spawnCenter(800,600);
}

    
    protected void paintComponent(Graphics g){

        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;

        target.draw(g2);

        gun.draw(g2,mouseX,mouseY,getWidth(),getHeight());

        score.draw(g2);
    }

    public void mouseMoved(MouseEvent e){

        mouseX = e.getX();
        mouseY = e.getY();
    }

    public void mouseClicked(MouseEvent e){
            score.addShot();
        if(target.isHit(e.getX(),e.getY())){

            score.addHit();
            target.spawn(getWidth(),getHeight());
        }
    }

    public void mouseDragged(MouseEvent e){}
    public void mousePressed(MouseEvent e){}
    public void mouseReleased(MouseEvent e){}
    public void mouseEntered(MouseEvent e){}
    public void mouseExited(MouseEvent e){}
}