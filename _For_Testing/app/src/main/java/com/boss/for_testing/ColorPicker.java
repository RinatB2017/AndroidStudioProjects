package com.boss.for_testing;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class ColorPicker extends AppCompatImageView
        implements View.OnTouchListener
{
    Paint mPaint;
    Canvas c_bitmap;
    Bitmap bitmap;

    int width = 600;
    int height = 600;
    int sw = 150;
    int center_x = width / 2;
    int center_y = height / 2;
    int radius = width / 2;

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

        //final int[] colors = new int[] { Color.CYAN, Color.MAGENTA, Color.YELLOW };
        //final int[] colors = new int[] { Color.RED, Color.GREEN, Color.BLUE };

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
        //c_bitmap.rotate(90, 300, 300);
        c_bitmap.drawCircle(center_x, center_y, radius - (sw / 2), mPaint);
        //c_bitmap.drawArc(rectf, 20, 320, false, mPaint);

        //mPaint.setStrokeWidth(2);
        //c_bitmap.drawRect(0, 0, s, s, mPaint);

        mPaint.reset();
        mPaint.setColor(Color.BLUE);
        mPaint.setStyle(Paint.Style.FILL);
        c_bitmap.drawCircle(center_x, center_y, width / 5, mPaint);

        setImageBitmap(bitmap);
        setOnTouchListener(this);
    }

    //---------------------------------------------------------------------------------------------
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        ((MainActivity) getContext()).send_log(Color.BLACK, "x: " + String.valueOf(event.getX()));
        ((MainActivity) getContext()).send_log(Color.BLACK, "y: " + String.valueOf(event.getY()));

        float x = event.getX();
        float y = event.getY();

        mPaint.reset();
        mPaint.setColor(bitmap.getPixel((int)x, (int)y));
        mPaint.setStyle(Paint.Style.FILL);
        c_bitmap.drawCircle(center_x, center_y, width / 5, mPaint);

        setImageBitmap(bitmap);


        return false;
    }

    //---------------------------------------------------------------------------------------------
    public int get_color() {
        return bitmap.getPixel(center_x, center_y);
    }

    //---------------------------------------------------------------------------------------------
}
