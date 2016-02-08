package jhernandez.gameclock.game.objects;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import jhernandez.gameclock.game.GameAnimator;
import jhernandez.gameclock.game.GamePanel;

/**
 * Created by Jason on 1/11/2016.
 */
public class Player extends GameObject {
    private Bitmap spritesheet;
    private int score;
    private int direction;
    private boolean playing;
    private GameAnimator animation = new GameAnimator();
    private long startTime;
    private int jump;
    public Player (Bitmap res, int w, int h, int numFrames) {
        x = 100;
        y = GamePanel.HEIGHT/2;
        score = 0;
        height = h;
        width = w;

        Bitmap[] image = new Bitmap[numFrames];
        spritesheet = res;
        for (int i = 0; i < image.length; i++) {
            image[i] = Bitmap.createBitmap(spritesheet, i*width, 0, width, height);
        }
        animation.setFrames(image);
        animation.setDelay(10);
        startTime = System.nanoTime();
    }
    public void setUP(boolean up) {}//this.down = up;}
    public void update() {
        long elasped = (System.nanoTime() - startTime)/100000;
        if (elasped > 100) {
            score++;
            startTime = System.nanoTime();
        }
        animation.update();
        if (jump > 0) {
            direction++;
            if (direction == 40) {
                jump = 0;
                direction = 0;
            }
            y = direction <= 20 ? y+2 : y-2;
        }
    }
    public void jump() {
        if (jump < 2) {
            this.jump++;
        }
    }
    public void draw(Canvas canvas) {
        canvas.drawBitmap(animation.getImage(), x, y, null);
    }
    public boolean isPlaying() {return this.playing;}
    public int getScore() {return score;}
    public void setPlaying(boolean playing) {this.playing = playing;}
    public void resetDY() {this.dy = 0;}
    public void resetScore() {score = 0;}
}
