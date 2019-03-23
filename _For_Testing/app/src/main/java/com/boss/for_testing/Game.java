package com.boss.for_testing;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class Game extends AppCompatImageView
        implements View.OnTouchListener
{
    Paint mPaint;
    Canvas c_bitmap;
    Bitmap bitmap;

    int width  = 15 * 64;
    int height = 24 * 64;

    //---------------------------------------------------------------------------------------------
    public Game(Context context) {
        super(context);
    }

    //---------------------------------------------------------------------------------------------
    public Game(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
        init();
    }

    //---------------------------------------------------------------------------------------------
    public Game(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    //---------------------------------------------------------------------------------------------
    public void init() {
        bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        c_bitmap = new Canvas(bitmap);
        mPaint = new Paint();
        mPaint.setColor(Color.BLACK);

        //c_bitmap.drawRect(0, 0, width, height, mPaint);

        int max_y = height / 32;
        int max_x = width / 32;

        int color = Color.BLACK;
        int old_color = Color.BLACK;
        for(int y=0; y<max_y; y++) {
            for(int x=0; x<max_x; x++) {
                do {
                    int index = (int) (Math.random() * 3);
                    switch (index) {
                        case 0:
                            color = Color.RED;
                            break;
                        case 1:
                            color = Color.GREEN;
                            break;
                        case 2:
                            color = Color.BLUE;
                            break;
                    }
                } while(color == old_color);
                old_color = color;
                mPaint.setColor(color);
                c_bitmap.drawCircle(x * 64 + 32, y * 64 +32, 32, mPaint);
            }
        }

        setImageBitmap(bitmap);
        setOnTouchListener(this);

    }

    //---------------------------------------------------------------------------------------------
    @Override
    public boolean onTouch(View v, MotionEvent event) {

        float x = event.getX();
        float y = event.getY();

        return false;
    }

    //---------------------------------------------------------------------------------------------
}
