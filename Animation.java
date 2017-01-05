package com.example.vick.myfirstgame;

import android.graphics.Bitmap;
import android.provider.Settings;

/**
 * Created by vick on 12/16/16.
 */

public class Animation {
    private Bitmap[] frames;
    private int currentFrame;
    private long startTime, delay;
    private boolean playedOnce;

    public void setFrames(Bitmap[] frames){
        this.frames = frames;
        currentFrame = 0;
        startTime = System.nanoTime();
    }
    public void setDelay(long d){delay=d;}
    public void setFrame(int i){currentFrame = 1;}
    public void update(){
        long elasped = (System.nanoTime() - startTime)/1000000;
        if(elasped>delay){
            currentFrame++;
            startTime = System.nanoTime();
        }
        if(currentFrame == frames.length){
            currentFrame = 0;
            playedOnce = true;
        }
    }
    public Bitmap getImage(){
        return frames[currentFrame];
    }
    public int getFrame(){return currentFrame;}
    public boolean playedOnce(){return playedOnce;}
}
