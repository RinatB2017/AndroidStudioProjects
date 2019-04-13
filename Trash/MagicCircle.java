package com.boss.for_testing;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class MagicCircle extends AppCompatImageView
        implements View.OnTouchListener
{
    Paint mPaint;
    Canvas c_bitmap;
    Bitmap bitmap;

    int width  = 200;
    int height = 200;
    int center_x = width / 2;
    int center_y = height / 2;

    int radius_1 = width / 2;
    int radius_2 = width / 6;

    float begin_angle = 20;
    float end_angle   = 340;

    float temp_x = 0;
    float temp_y = 0;

    int fontsize = 60;

    //---------------------------------------------------------------------------------------------
    public MagicCircle(Context context) {
        super(context);
    }

    //---------------------------------------------------------------------------------------------
    public MagicCircle(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
        init();
    }

    //---------------------------------------------------------------------------------------------
    public MagicCircle(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    //---------------------------------------------------------------------------------------------
    public void init() {
        bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        c_bitmap = new Canvas(bitmap);
        mPaint = new Paint();
        //---
        String text = "Test";

        mPaint.setColor(Color.BLACK);
        mPaint.setAntiAlias(true);
        mPaint.setTextSize(fontsize);

        Paint fontPaint;
        fontPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        fontPaint.setTextSize(fontsize);
        float text_width = fontPaint.measureText(text);

        Rect bounds = new Rect();
        fontPaint.getTextBounds(text, 0, 1, bounds);

        float text_height = bounds.height();

        c_bitmap.drawText(text,
                center_x - text_width / 2,
                center_y + text_height / 2,
                mPaint);
        //---

        mPaint.setColor(Color.RED);
        mPaint.setStrokeWidth(2);
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);

        //c_bitmap.drawCircle(s / 2, s / 2, s / 2 - 1, mPaint);
        //c_bitmap.drawCircle(s / 2, s / 2, s / 2 - 20, mPaint);

        RectF rectf1 = new RectF(0,  0,  width,     height);
        RectF rectf2 = new RectF(20, 20, width-20,  height-20);
        c_bitmap.rotate(90, center_x, center_y);
        c_bitmap.drawArc(rectf1, 20, 320, false, mPaint);
        c_bitmap.drawArc(rectf2, 20, 320, false, mPaint);

        //---
        calc_line(center_x, center_y, 20, width / 2);
        float begin_x1 = temp_x;
        float begin_y1 = temp_y;

        calc_line(center_x, center_y, 20, width / 2 - 20);
        float end_x1 = temp_x;
        float end_y1 = temp_y;

        calc_line(center_x, center_y, 340, width / 2);
        float begin_x2 = temp_x;
        float begin_y2 = temp_y;

        calc_line(center_x, center_y, 340, width / 2 - 20);
        float end_x2 = temp_x;
        float end_y2 = temp_y;

        //mPaint.setColor(Color.GREEN);
        c_bitmap.drawLine(begin_x1, begin_y1, end_x1, end_y1, mPaint);
        //mPaint.setColor(Color.BLUE);
        c_bitmap.drawLine(begin_x2, begin_y2, end_x2, end_y2, mPaint);
        //---

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
        float correct_y = (getHeight() - bitmap.getHeight()) / 2;
        x -= correct_x;
        y -= correct_y;
        //---

        double dist = calc_dist(x, y);
        if(dist > radius_1) {
            return false;
        }
        if(dist < radius_2) {
            return false;
        }

        float angle = calc_angle(x, y) - 90;
        if(angle < 0) {
            angle += 360.0f;
        }

        RectF rectf1 = new RectF(10,
                10,
                width - 10,
                height - 10);

        mPaint.setStrokeWidth(18);
        mPaint.setColor(Color.WHITE);
        c_bitmap.drawArc(rectf1,
                begin_angle,
                end_angle,
                false,
                mPaint);

        mPaint.setStrokeWidth(20);
        mPaint.setColor(Color.RED);
        c_bitmap.drawArc(rectf1,
                begin_angle,
                angle - begin_angle,
                false,
                mPaint);

        setImageBitmap(bitmap);
        return true;
    }

    //---------------------------------------------------------------------------------------------
    public int get_color() {
        return bitmap.getPixel(center_x, center_y);
    }

    //---------------------------------------------------------------------------------------------
    private void calc_line(float x,
                           float y,
                           float angle,
                           float radius) {
        float A = radius;
        float B = (float) Math.cos(Math.toRadians(angle)) * A;
        float C = (float) Math.sin(Math.toRadians(angle)) * A;

        temp_x = x + B;
        temp_y = y + C;
    }

    //---------------------------------------------------------------------------------------------
    private double calc_dist(float x,
                             float y) {
        double dist_x = Math.abs(x - center_x);
        double dist_y = Math.abs(y - center_y);

        double dist = Math.sqrt(dist_x * dist_x + dist_y * dist_y);
        return dist;
    }

    //---------------------------------------------------------------------------------------------
    private float calc_angle(float x,
                              float y) {
        double radians = Math.atan2(y - center_y, x - center_x);
        float angle = (float)radians * 180.0f / (float)Math.PI;
        return angle;
    }

    //---------------------------------------------------------------------------------------------
    public void set_new_size(int new_width, int new_hight) {
        width = new_width;
        height = new_hight;

        center_x = width / 2;
        center_y = height / 2;

        init();
    }

    //---------------------------------------------------------------------------------------------
    public void set_fontsize(int value) {
        fontsize = value;

        init();
    }

    //---------------------------------------------------------------------------------------------
}
