package aimtrainer;

public enum Mode {

    EASY(10),
    NORMAL(20),
    HARD(30);

    public int targetCount;

    Mode(int targetCount){
        this.targetCount = targetCount;
    }
}