package com.example.vick.myfirstgame;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by vick on 12/16/16.
 */

public class GamePanel extends SurfaceView implements SurfaceHolder.Callback {

    public static final int WIDTH = 856;
    public static final int HEIGHT = 480;
    public static final int MOVINGSPEED = -5;
    private long smokeStartTimer;
    private long missileStart;
    private MainThread thread;
    private Background bg;
    private Player player;
    private ArrayList<Smokepuff> smoke;
    private ArrayList<Missile> missiles;
    private ArrayList<TopBorder> topBorder;
    private ArrayList<BottomBorder> bottomBorder;
    private Random rand = new Random();
    private int maxBorderHeight;
    private int minBorderHeight;
    private boolean topDown = true;
    private boolean bottomDown = true;
    private int progressDenom = 20;
    private boolean newGameCreated;
    private Paint paint;

    public GamePanel (Context context){
        super(context);

        //add the callback
        getHolder().addCallback(this);

        paint = new Paint();
        paint.setColor(Color.RED);
        setFocusable(true);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height){}

    @Override
    public void surfaceDestroyed(SurfaceHolder holder){
        boolean retry = true;
        int counter = 0;
        while (retry && counter < 1000){
            counter++;
            try{
                thread.setRunning(false);
                thread.join();
                retry = false;
                thread = null;
            }catch(InterruptedException e){e.printStackTrace();}

        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder){
        bg = new Background(BitmapFactory.decodeResource(getResources(), R.drawable.grassbg1));
        player = new Player((BitmapFactory.decodeResource(getResources(), R.drawable.helicopter)),
                            65, 25, 3);
        smoke = new ArrayList<Smokepuff>();
        missiles = new ArrayList<Missile>();

        topBorder = new ArrayList<TopBorder>();
        bottomBorder = new ArrayList<BottomBorder>();
        thread = new MainThread(getHolder(), this);


        missileStart = System.nanoTime();
        smokeStartTimer = System.nanoTime();


//        bg.setVector(-5);
        //startgameloop
        thread.setRunning(true);
        thread.start();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        if(event.getAction()==MotionEvent.ACTION_DOWN) {
            if (!player.getPlaying()) {
                player.setPlaying(true);
            } else {
                player.setUp(true);
            }
            return true;
        }
        if(event.getAction()==MotionEvent.ACTION_UP){
            player.setUp(false);
            return true;
        }

        return super.onTouchEvent(event);
    }


    public void update (){
        if (player.getPlaying()) {
            bg.update();
            player.update();


            maxBorderHeight = (30+player.getScore())/progressDenom;
            if (maxBorderHeight > HEIGHT/4){maxBorderHeight = HEIGHT/4;}

            minBorderHeight = 5+player.getScore()/progressDenom;


            for(int i = 0; i < topBorder.size();i++)
            {
                if(collision(topBorder.get(i), player)){
                    player.setPlaying(false);
                }
            }

            for(int i = 0; i<bottomBorder.size();i++){
                if(collision(bottomBorder.get(i), player)){
                    player.setPlaying(false);
                }
            }


            //top
            this.updateTopBorder();

            //bottom
            this.updateBottomBorder();

            long missileElaspedTime = (System.nanoTime() -missileStart)/1000000;
            long elaspedTime = (System.nanoTime() -smokeStartTimer)/100000;

            if(missileElaspedTime > (2000-player.getScore()/4)){
                //first missile
                if(missiles.size() == 0){
                    missiles.add(new Missile((BitmapFactory.decodeResource(getResources(), R.drawable.missile)),
                                    WIDTH+10, HEIGHT/2, 45, 15, player.getScore(), 13));
                }else{
                    missiles.add(new Missile((BitmapFactory.decodeResource(getResources(), R.drawable.missile)),
                            WIDTH+10, (int)(rand.nextDouble()*HEIGHT) , 45, 15, player.getScore(), 13));
                }
                missileStart = System.nanoTime();
            }

            if(elaspedTime >120){
                smoke.add(new Smokepuff(player.getX(), player.getY()+10));
                smokeStartTimer = System.nanoTime();
            }

            for(int i = 0;i < missiles.size();i++){
                missiles.get(i).update();
                if(collision(missiles.get(i), player)){
                    missiles.remove(i);
                    player.setPlaying(false);
                    break;
                }
                if(missiles.get(i).getX()<-100){
                    missiles.remove(i);
                    break;
                }
            }

            for(int i = 0; smoke.size() > i; i++){
                smoke.get(i).update();
                if(smoke.get(i).getX() < -10){
                    smoke.remove(i);
                }
            }

        }else{
            newGameCreated = false;
            if(!newGameCreated){
            newGame();}
        }
    }

    public boolean collision(GameObject x, GameObject y){
        if(Rect.intersects(x.getRectangle(), y.getRectangle())){
            return true;
        }
        return false;
    }

    @Override
    public void draw(Canvas canvas)
    {
        final float scaleFactorX = (float)getWidth()/WIDTH;
        final float scaleFactorY = (float)getHeight()/HEIGHT;
        if (canvas!=null) {
            final int savedState = canvas.save();
            canvas.scale(scaleFactorX, scaleFactorY);
            bg.draw(canvas);
            player.draw(canvas);

            for(Smokepuff i: smoke){
                i.draw(canvas);
            }

            for(Missile i: missiles){
                i.draw(canvas);
            }

            for(TopBorder i:topBorder){i.draw(canvas);}
            for(BottomBorder b: bottomBorder){b.draw(canvas);}



            canvas.drawText("Hello", 0,5, 100, 100,paint);


            canvas.restoreToCount(savedState);
        }
    }

    public void updateTopBorder(){
        //every 50 points, insert randomly placed top block that
        if(player.getScore()%50 == 0){
            topBorder.add(new TopBorder(BitmapFactory.decodeResource(getResources(), R.drawable.brick),
                    topBorder.get(topBorder.size()-1).getX()+20,0, (int)(rand.nextDouble()*(maxBorderHeight))+1));
        }
        for(int i = 0; i< topBorder.size();i++){
            topBorder.get(i).update();

            if(topBorder.get(i).getX()<-20){
                topBorder.remove(i);

                if(topBorder.get(topBorder.size()-1).getHeight() >= maxBorderHeight){
                    topDown = false;
                }

                if(topBorder.get(topBorder.size()-1).getHeight() <= minBorderHeight){
                    topDown = true;
                }

                if(topDown){
                    topBorder.add(new TopBorder(BitmapFactory.decodeResource(getResources(), R.drawable.brick),
                            topBorder.get(topBorder.size()-1).getX()+20, 0, topBorder.get(topBorder.size()-1).getHeight()+1));
                }else{
                    topBorder.add(new TopBorder(BitmapFactory.decodeResource(getResources(), R.drawable.brick),
                            topBorder.get(topBorder.size()-1).getX()+20, 0, topBorder.get(topBorder.size()-1).getHeight()-1));
                }

                }

        }
    }

    public void updateBottomBorder(){
//        if(player.getScore()%40 == 0){
//            bottomBorder.add(new BottomBorder(BitmapFactory.decodeResource(getResources(), R.drawable.brick), bottomBorder.get(bottomBorder.size()-1).getX()+20,
//                    (int)((rand.nextDouble()*maxBorderHeight)+(HEIGHT-maxBorderHeight))));
//        }

        //update botoom border
        for(int i = 0; i<bottomBorder.size();i++){
            bottomBorder.get(i).update();

            if(bottomBorder.get(i).getX() < -20) {
                bottomBorder.remove(i);
                bottomBorder.add(new BottomBorder(BitmapFactory.decodeResource(getResources(), R.drawable.brick),
                        i*20, HEIGHT-16));

//                if (bottomBorder.get(bottomBorder.size() - 1).getHeight() >= maxBorderHeight) {
//                    bottomDown = false;
//                }
//
//                if (bottomBorder.get(bottomBorder.size() - 1).getHeight() <= minBorderHeight) {
//                    bottomDown = true;
//                }
//
//                if (bottomDown) {
//                    bottomBorder.add(new BottomBorder(BitmapFactory.decodeResource(getResources(), R.drawable.brick),
//                            bottomBorder.get(bottomBorder.size() - 1).getX() + 20, bottomBorder.get(bottomBorder.size() - 1).getHeight() + 1));
//                } else {
//                    bottomBorder.add(new BottomBorder(BitmapFactory.decodeResource(getResources(), R.drawable.brick),
//                            bottomBorder.get(bottomBorder.size() - 1).getX() + 20, bottomBorder.get(bottomBorder.size() - 1).getHeight() - 1));
//                }
            }

        }

        }

    public void newGame(){
        bottomBorder.clear();
        topBorder.clear();
        missiles.clear();
        smoke.clear();



        minBorderHeight = 5;
        maxBorderHeight = 30;

        player.resetScore();
        player.setY(HEIGHT/2);

        for(int i = 0; i*20<WIDTH+40; i++){
            if(i==0){
                topBorder.add(new TopBorder(BitmapFactory.decodeResource(getResources(),R.drawable.brick),
                        i*20,0,10));
            }else{
                topBorder.add(new TopBorder(BitmapFactory.decodeResource(getResources(),R.drawable.brick),
                        i*20,0, topBorder.get(topBorder.size()-1).getHeight()+1));
            }
        }

        for(int i = 0; i*20<WIDTH+50; i++){

            bottomBorder.add(new BottomBorder(BitmapFactory.decodeResource(getResources(), R.drawable.brick),
                        i*20, HEIGHT-16));


//            if(i==0){
//                bottomBorder.add(new BottomBorder(BitmapFactory.decodeResource(getResources(), R.drawable.brick),
//                        i*20, HEIGHT));
//            }else{
//                bottomBorder.add(new BottomBorder(BitmapFactory.decodeResource(getResources(), R.drawable.brick),
//                        i*20, bottomBorder.get(bottomBorder.size()-1).getHeight()-1));
//            }
        }

        newGameCreated = true;

    }
}
