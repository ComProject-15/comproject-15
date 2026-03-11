package aimtrainer;

public enum Mode {

    EASY(20),
    NORMAL(30),
    HARD(40);

    public int targetCount;

    Mode(int count){
        this.targetCount = count;
    }
}