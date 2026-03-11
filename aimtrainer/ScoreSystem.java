package aimtrainer;

import java.awt.*;

public class ScoreSystem {

    int score = 0;
    int shots = 0;
    int hits = 0;
    int maxTargets = 20;

    public void setMode(Mode mode){
        maxTargets = mode.targetCount;
    }

    public void addShot(){
        shots++;
    }

    public void addHit(){
        hits++;
        score++;
    }

    public boolean isGameOver(){
        return hits >= maxTargets;
    }

    public double getAccuracy(){

        if(shots == 0) return 0;

        return (hits * 100.0) / shots;
    }

    public void draw(Graphics2D g){

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial",Font.BOLD,20));

        g.drawString("Score: " + score,20,30);
        g.drawString("Shots: " + shots,20,60);
        g.drawString("Accuracy: " + String.format("%.1f",getAccuracy()) + "%",20,90);
    }
}