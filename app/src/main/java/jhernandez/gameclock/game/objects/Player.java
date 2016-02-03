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
    private boolean jump;
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
        if (jump) {
            direction++;
            if (direction == 80) {
                jump = false;
                direction = 0;
            }
            y = direction <= 40 ? y+2 : y-2;
            if (dy > 14)
                dy = 14;
            else if (dy < -5)
                dy = -5;
        }
    }
    public void jump() {
        this.jump = true;
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
