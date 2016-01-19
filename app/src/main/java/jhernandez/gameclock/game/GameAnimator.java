package jhernandez.gameclock.game;

import android.graphics.Bitmap;

/**
 * Created by Jason on 1/11/2016.
 */
public class GameAnimator {

    private Bitmap[] frames;
    private int currentFrame;
    private long startTime;
    private long delay;
    private boolean repeating;

    public void setFrames(Bitmap[] frames) {
        this.frames = frames;
        currentFrame = 0;
        startTime = System.nanoTime();
    }

    public void setDelay(long delay) {
        this.delay = delay;
    }
    public void setCurrentFrame(int currentFrame) {
        this.currentFrame = currentFrame;
    }

    public void update() {
        long elapsed = (System.nanoTime() - startTime)/1000000;

        if (elapsed > delay) {
            currentFrame++;
        }
        if (currentFrame == frames.length) {
            currentFrame = 0;
        }
    }

    public Bitmap getImage() {return frames[currentFrame];}
    public int getCurrentFrame() {return currentFrame;}
    public boolean repeating() {return repeating;}
}
