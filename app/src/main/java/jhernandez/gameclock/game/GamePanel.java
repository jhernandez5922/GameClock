package jhernandez.gameclock.game;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import jhernandez.gameclock.R;
import jhernandez.gameclock.game.objects.Background;
import jhernandez.gameclock.game.objects.Player;

/**
 * Created by Jason on 10/12/2015.
 */
public class GamePanel extends SurfaceView implements SurfaceHolder.Callback {

    public static final int WIDTH= 856;
    public static final int HEIGHT = 480;
    public static final int MOVESPEED = -5;
    private MainThread thread;
    private Background bg;
    private Player currentPlayer;

    public GamePanel(Context context) {
        super(context);

        getHolder().addCallback(this);

        thread = new MainThread(getHolder(), this);

        setFocusable(true);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

        bg = new Background(BitmapFactory.decodeResource(getResources(), R.drawable.grassbg1));
        currentPlayer = new Player(BitmapFactory.decodeResource(getResources(), R.drawable.helicopter), 65, 25, 3);
        thread.setRunning(true);
        thread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry = true;
        int counter = 0;
        while(retry && counter < 1000) {
            counter++;
            try {
                thread.setRunning(false);
                thread.join();
                retry = false;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if(!currentPlayer.isPlaying()) {
                currentPlayer.setPlaying(true);
            }
            else {
                currentPlayer.setUP(true);
            }
            return true;
        }
        if (event.getAction() == MotionEvent.ACTION_UP) {
            currentPlayer.setUP(false);
            return true;
        }
        return super.onTouchEvent(event);
    }

    public void update() {
        bg.update();
        if (currentPlayer.isPlaying()) {
            currentPlayer.update();
        }
    }

    @Override
    public void draw(Canvas canvas) {
        final float scaleFactorX = getWidth()/(WIDTH*1.f);
        final float scaleFactorY = getHeight()/(HEIGHT*1.f);

        if(canvas != null) {
            final int savedState = canvas.save();
            canvas.scale(scaleFactorX, scaleFactorY);
            bg.draw(canvas);
            currentPlayer.draw(canvas);
            canvas.restoreToCount(savedState);
        }
    }


    /**
     * Created by Jason on 10/12/2015.
     */
    public static class MainThread extends Thread {

        private int FPS = 30;
        private double avgFPS;
        private SurfaceHolder surfaceHolder;
        private GamePanel gamePanel;
        private boolean running;

        public static Canvas canvas;

        public MainThread(SurfaceHolder surfaceHolder, GamePanel gamePanel) {
            super();
            this.surfaceHolder = surfaceHolder;
            this.gamePanel = gamePanel;
        }

        @Override
        public void run() {
            long startTime;
            long timeMillis;
            long waitTime;
            long totalTime = 0;
            long frameCount = 0;
            long targetTime = 1000/FPS;


            while(running) {
                startTime = System.nanoTime();
                canvas = null;

                try {
                    canvas = this.surfaceHolder.lockCanvas();
                    synchronized (surfaceHolder) {
                        this.gamePanel.update();
                        this.gamePanel.draw(canvas);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                finally {
                    if(canvas != null) {
                        try{
                            surfaceHolder.unlockCanvasAndPost(canvas);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                timeMillis = (System.nanoTime() - startTime)/1000000;
                waitTime = Math.abs(targetTime - timeMillis);


                try {
                    sleep(waitTime);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                totalTime += System.nanoTime() - startTime;
                frameCount++;
                if (frameCount == FPS) {
                    avgFPS = 1000/((totalTime/frameCount)/1000000);
                    frameCount = 0;
                    totalTime = 0;
                    Log.v(MainThread.class.getSimpleName(), "CURRENT FPS: " + avgFPS);
                }
            }
        }

        public void setRunning(boolean run) {
            this.running = run;
        }
    }
}
