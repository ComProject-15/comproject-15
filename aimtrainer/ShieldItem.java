package aimtrainer;

public class ShieldItem {
    public boolean active = false;
    private int timer = 0;
    private final int DURATION = 300;

    public void activate() {
        active = true;
        timer = DURATION;
    }

    public void update() {
        if (active) {
            timer--;
            if (timer <= 0) active = false;
        }
    }

    public int getTimer() { return timer; }
    public int getDuration() { return DURATION; }
    public float getProgress() { return timer / (float) DURATION; }
}