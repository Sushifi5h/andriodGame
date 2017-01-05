package com.example.vick.myfirstgame;

import android.graphics.Bitmap;
import android.graphics.Canvas;

/**
 * Created by vick on 12/18/16.
 */

public class TopBorder extends GameObject {
    private Bitmap image;

    public TopBorder(Bitmap res, int x , int y, int h){
        this.height = h;
        this.width = 20;

        this.x = x;
        this.y = y;

        dx = GamePanel.MOVINGSPEED;
        image = Bitmap.createBitmap(res, 0, 0, width, height);
    }

    public void update(){
        x+=dx;
    }

    public void draw(Canvas canvas) {
        try {
            canvas.drawBitmap(image, x, y, null);
        } catch (Exception e) {
        }
    }
}
