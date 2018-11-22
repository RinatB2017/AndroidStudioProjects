package com.boss.for_testing;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;

import java.util.Random;

class World {
    Handler h_view;
    Bitmap bitmap;
    Canvas c_bitmap;
    Paint mPaint;

    int width;
    int height;

    public World(Handler h, int width, int height) {
        h_view = h;

        this.width = width;
        this.height = height;

        //---
        bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        c_bitmap = new Canvas(bitmap);

        mPaint = new Paint();
        mPaint.setStrokeWidth(5);
        mPaint.setColor(Color.BLACK);
        mPaint.setStyle(Paint.Style.STROKE);
        //---

        run();
    }

    void run() {
        while(true) {
            Random randNumber = new Random();

            int x1 = randNumber.nextInt() % width;
            int y1 = randNumber.nextInt() % height;
            int x2 = randNumber.nextInt() % width;
            int y2 = randNumber.nextInt() % height;

            c_bitmap.drawLine(x1, y1, x2, y2, mPaint);
            //c_bitmap.drawRect(x1, y1, x2, y2, mPaint);

            Message msg = new Message();
            msg.obj = bitmap;
            h_view.sendMessage(msg);

            sleep();
        }
    }

    // делает паузу
    public void sleep() {
        try {
            int delay = 20;
            Thread.sleep(delay);
        } catch (InterruptedException e) {
        }
    }
}
