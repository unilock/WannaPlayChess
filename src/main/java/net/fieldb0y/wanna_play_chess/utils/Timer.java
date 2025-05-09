package net.fieldb0y.wanna_play_chess.utils;

public class Timer {
    public boolean ticking = false;
    public Runnable executableMethod;
    public int tickTimeInSec;
    public int progress = 0;

    public Timer(Runnable executableMethod, int tickTimeInSec){
        this.executableMethod = executableMethod;
        this.tickTimeInSec = tickTimeInSec;
    }

    public void start(){
        if (!ticking)
            this.ticking = true;
    }

    public void stop(){
        if (ticking){
            this.ticking = false;
            this.progress = 0;
        }
    }

    public void tick(){
        tick(ticking);
    }

    public void tick(boolean tickingCondition){
        if (tickingCondition){
            progress++;
            if (progress >= tickTimeInSec * 20){
                executableMethod.run();
                progress = 0;
            }
        } else {
            progress = 0;
        }
    }
}
