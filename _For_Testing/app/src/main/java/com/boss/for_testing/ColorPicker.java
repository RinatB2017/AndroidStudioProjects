package com.boss.for_testing;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;

public class ColorPicker extends AppCompatImageView
        implements View.OnTouchListener
{
    Paint mPaint;
    Canvas c_bitmap;
    Bitmap bitmap;

    int width  = 600;
    int height = 600;
    int sw = width / 4;
    int center_x = width / 2;
    int center_y = height / 2;
    int radius = width / 2;

    int color_R = 0;
    int color_B = 0;
    int color_G = 0;

    //---------------------------------------------------------------------------------------------
    public ColorPicker(Context context) {
        super(context);
    }

    //---------------------------------------------------------------------------------------------
    public ColorPicker(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
        init();
    }

    //---------------------------------------------------------------------------------------------
    public ColorPicker(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    //---------------------------------------------------------------------------------------------
    public void init() {
        bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        c_bitmap = new Canvas(bitmap);
        mPaint = new Paint();
        mPaint.setColor(Color.RED);
        mPaint.setStrokeWidth(sw);
        mPaint.setStyle(Paint.Style.STROKE);

        final int[] colors = new int[] {
                0xFFFF0000,
                0xFFFFFF00,
                0xFF00FF00,
                0xFF00FFFF,
                0xFF0000FF,
                0xFFFF00FF,
                0xFFFF0000
        };

        /*
        final int[] colors = new int[] {
                0xFFFF0000,
                0xFFFF00FF,
                0xFF0000FF,
                0xFF00FFFF,
                0xFF00FF00,
                0xFFFFFF00,
                0xFFFF0000
        };
        */

        Shader shader = new SweepGradient(center_x, center_x, colors, null);
        mPaint.setShader(shader);

        RectF rectf = new RectF(center_x, center_y, width-center_x, height-center_y);
        c_bitmap.drawCircle(center_x, center_y, radius - (sw / 2), mPaint);

        mPaint.reset();
        mPaint.setColor(Color.rgb(color_R, color_G, color_B));
        mPaint.setStyle(Paint.Style.FILL);
        c_bitmap.drawCircle(center_x, center_y, width / 2 - sw - 20, mPaint);

        setImageBitmap(bitmap);
        setOnTouchListener(this);
    }

    //---------------------------------------------------------------------------------------------
    @Override
    public boolean onTouch(View v, MotionEvent event) {

        float x = event.getX();
        float y = event.getY();

        //---
        float correct_x = (getWidth() - bitmap.getWidth()) / 2;
        x -= correct_x;
        //---

        if(x > bitmap.getWidth()) {
            return false;
        }
        if(y > bitmap.getHeight()) {
            return false;
        }

        mPaint.reset();
        mPaint.setColor(bitmap.getPixel((int)x, (int)y));
        mPaint.setStyle(Paint.Style.FILL);
        c_bitmap.drawCircle(center_x, center_y, width / 2 - sw - 20, mPaint);

        setImageBitmap(bitmap);
        return true;
    }

    //---------------------------------------------------------------------------------------------
    public void set_new_size(int new_width, int new_hight) {
        width = new_width;
        height = new_hight;

        center_x = width / 2;
        center_y = height / 2;
        sw = width / 4;
        radius = width / 2;

        init();
    }

    //---------------------------------------------------------------------------------------------
    public void set_color(int v_R, int v_G, int v_B) {
        color_R = v_R;
        color_G = v_G;
        color_B = v_B;

        mPaint.reset();
        mPaint.setColor(Color.rgb(color_R, color_G, color_B));
        mPaint.setStyle(Paint.Style.FILL);
        c_bitmap.drawCircle(center_x, center_y, width / 2 - sw - 20, mPaint);

        setImageBitmap(bitmap);
    }

    //---------------------------------------------------------------------------------------------
    public int get_color() {
        return bitmap.getPixel(center_x, center_y);
    }

    //---------------------------------------------------------------------------------------------
}
