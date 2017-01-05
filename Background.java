package com.example.vick.myfirstgame;

import android.graphics.Bitmap;
import android.graphics.Canvas;

/**
 * Created by vick on 12/16/16.
 */

public class Background {
    private Bitmap image;
    private int x, y, dx;

    public Background(Bitmap res){
        this.image = res;
        this.dx = GamePanel.MOVINGSPEED;
    }

    public void update(){
        x+=dx;
        if(x<-GamePanel.WIDTH){
            x=0;
        }
    }

    public void draw(Canvas canvas)
    {
        canvas.drawBitmap(image, x, y, null);
        if(x<0){
            canvas.drawBitmap(image,x+GamePanel.WIDTH,y,null);
        }
    }

//    public void setVector(int dx){
//        this.dx = dx;
//    }
}
