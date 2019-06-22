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

import java.util.ArrayList;

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

    ArrayList<Display_color> colors;

    //---------------------------------------------------------------------------------------------
    public class Display_color {
        int x;
        int y;
        int color;
    }
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

        colors = new ArrayList<Display_color>();
        for(int y=0; y<max_y; y++) {
            for(int x=0; x<max_x; x++) {
                Display_color dc = new Display_color();
                dc.x = x;
                dc.y = y;
                dc.color = Color.BLACK;

                colors.add(dc);
            }
        }

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
    public boolean set_color(int x, int y, int color) {
        if(x < 0 || x > max_x) return false;
        if(y < 0 || y > max_y) return false;

        for (Display_color dc: colors) {
            if(x == dc.x && y == dc.y) {
                dc.color = color;
                return true;
            }
        }
        return false;
    }
    //---------------------------------------------------------------------------------------------
    public void redraw() {
        for (Display_color dc: colors) {
            int x = dc.x;
            int y = dc.y;
            int color = dc.color;

            if(color == Color.RED) {
                color = Color.GREEN;
            }

            mPaint.setColor(color);
            mPaint.setStyle(Paint.Style.FILL);
            c_bitmap.drawRect(x*size, y*size, x*size+size, y*size+size, mPaint);
        }
        setImageBitmap(bitmap);
    }
    //---------------------------------------------------------------------------------------------
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return true;
    }
    //---------------------------------------------------------------------------------------------

}
