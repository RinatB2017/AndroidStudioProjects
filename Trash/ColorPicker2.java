package com.boss.for_testing;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class ColorPicker2 extends AppCompatImageView
        implements View.OnTouchListener
{
    Paint mPaint;
    Canvas c_bitmap;
    Bitmap bitmap;

    int width  = 800;
    int height = 150;

    int spacer = 20;

    int s_width  = height;
    int s_height = height;

    int center_x = (width - s_width - spacer) / 2;
    int center_y = height / 2;

    int color_R = 0;
    int color_B = 0;
    int color_G = 0;

    //---------------------------------------------------------------------------------------------
    public ColorPicker2(Context context) {
        super(context);
    }

    //---------------------------------------------------------------------------------------------
    public ColorPicker2(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
        init();
    }

    //---------------------------------------------------------------------------------------------
    public ColorPicker2(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    //---------------------------------------------------------------------------------------------
    public void init() {
        bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        c_bitmap = new Canvas(bitmap);
        mPaint = new Paint();
        mPaint.setColor(Color.RED);
        //mPaint.setStrokeWidth(sw);
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);

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

        Shader shader = new LinearGradient(0, 0, width - s_width, height, colors, null, Shader.TileMode.MIRROR);
        mPaint.setShader(shader);

        c_bitmap.drawRect(0, 0, width - s_width - spacer, height, mPaint);

        mPaint.reset();
        mPaint.setColor(Color.RED);
        mPaint.setStyle(Paint.Style.FILL);
        c_bitmap.drawRect(width - s_width, 0, width, height, mPaint);

        setImageBitmap(bitmap);
        setOnTouchListener(this);
    }

    //---------------------------------------------------------------------------------------------
    @Override
    public boolean onTouch(View v, MotionEvent event) {

        float x = event.getX();
        float y = event.getY();

        //---
        int correct_x = (getWidth() - bitmap.getWidth()) / 2;
        int correct_y = (getHeight() - bitmap.getHeight()) / 2;
        x -= correct_x;
        y -= correct_y;
        //---

        if(x <= 0) {
            return false;
        }
        if(y <= 0) {
            return false;
        }
        if(x > bitmap.getWidth()) {
            return false;
        }
        if(y > bitmap.getHeight()) {
            return false;
        }

        mPaint.reset();
        mPaint.setColor(bitmap.getPixel((int)x, (int)y));
        mPaint.setStyle(Paint.Style.FILL);
        c_bitmap.drawRect(width - s_width, 0, width, height, mPaint);

        setImageBitmap(bitmap);
        return true;
    }

    //---------------------------------------------------------------------------------------------
    public void set_new_size(int new_width, int new_hight) {
        width = new_width;
        height = new_hight;

        width  = new_width;
        height = new_hight;

        s_width  = height;
        s_height = height;

        center_x = (width - s_width - spacer) / 2;
        center_y = height / 2;

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
        c_bitmap.drawRect(width - s_width, 0, width, height, mPaint);

        setImageBitmap(bitmap);

    }

    //---------------------------------------------------------------------------------------------
    public int get_color() {
        return bitmap.getPixel(center_x, center_y);
    }

    //---------------------------------------------------------------------------------------------
}
