package jhernandez.gameclock.game.objects;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import jhernandez.gameclock.game.GamePanel;

/**
 * Created by Jason on 10/12/2015.
 */
public class Background {


    private Bitmap image;
    private int x, y, dx;


    public Background(Bitmap res) {

        image = res;
        dx = GamePanel.MOVESPEED;
    }

    public void update() {
        x+=dx;
        if (x < -GamePanel.WIDTH) {
            x=0;
        }
    }

    public void draw(Canvas canvas) {
        canvas.drawBitmap(image, x, y, null);
        if (x < 0) {
            canvas.drawBitmap(image, x+GamePanel.WIDTH, y, null);
        }
    }
}
