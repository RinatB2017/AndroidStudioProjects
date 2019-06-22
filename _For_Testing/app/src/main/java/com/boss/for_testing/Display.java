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

public class Display extends AppCompatImageView
        implements View.OnTouchListener {
    Paint mPaint;
    Canvas c_bitmap;
    Bitmap bitmap;

    int max_x = 420;
    int max_y = 32;

    int size = 24;  //16;

    int width  = max_x * size;
    int height = max_y * size;

    //---------------------------------------------------------------------------------------------
    public Display(Context context) {
        super(context);
    }

    //---------------------------------------------------------------------------------------------
    public Display(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
        init();
    }

    //---------------------------------------------------------------------------------------------
    public Display(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }
    //---------------------------------------------------------------------------------------------
    public void init() {
        bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        c_bitmap = new Canvas(bitmap);
        mPaint = new Paint();
        mPaint.setColor(Color.BLACK);
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);

        c_bitmap.drawRect(0, 0, width, height, mPaint);

        mPaint.setColor(Color.WHITE);
        for(int y=0; y<max_y; y++)
        {
            c_bitmap.drawLine(0, y * size, width, y * size, mPaint);
        }
        for(int x=0; x<max_x; x++)
        {
            c_bitmap.drawLine(x * size, 0, x * size, height, mPaint);
        }

        setImageBitmap(bitmap);
        setOnTouchListener(this);
    }
    //---------------------------------------------------------------------------------------------
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return true;
    }
    //---------------------------------------------------------------------------------------------

}
