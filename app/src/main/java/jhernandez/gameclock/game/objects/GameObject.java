package jhernandez.gameclock.game.objects;

import android.graphics.Rect;

/**
 * Created by Jason on 1/11/2016.
 */
public abstract class GameObject {
    protected int x;
    protected int y;
    protected int dy;
    protected int dx;
    protected int width;
    protected int height;


    public void setX(int x) {
        this.x = x;
    }
    public void setY(int y) {
        this.y = y;
    }
    public void setDy(int dy) {
        this.dy = dy;
    }
    public void setDx(int dx) {
        this.dx = dx;
    }
    public void setWidth (int width) {
        this.width = width;
    }
    public void setHeight(int height) {
        this.height = height;
    }
    public int getX() {return this.x;}
    public int getY() {return this.y;}
    public int getDy() {return this.dy;}
    public int getDx() {return this.dx;}
    public int getWidth() {return this.width;}
    public int getHeight() {return this.height;}

    public Rect getRectange() {
        return new Rect(x, y, x+width, y+height);
    }
}
