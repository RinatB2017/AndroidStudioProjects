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

    int s = 600;
    int sw = 150;

    public ColorPicker(Context context) {
        super(context);
    }

    public ColorPicker(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
        init();
    }

    public ColorPicker(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public void init() {
        bitmap = Bitmap.createBitmap(s, s, Bitmap.Config.ARGB_8888);

        c_bitmap = new Canvas(bitmap);
        mPaint = new Paint();
        mPaint.setColor(Color.RED);
        mPaint.setStrokeWidth(sw);
        mPaint.setStyle(Paint.Style.STROKE);

        final int[] colors = new int[] { Color.CYAN, Color.MAGENTA, Color.YELLOW };
        Shader shader = new SweepGradient(s / 2, s / 2, colors, null);
        mPaint.setShader(shader);

        RectF rectf = new RectF((sw / 2), (sw / 2), s-(sw / 2), s-(sw / 2));
        c_bitmap.rotate(90, 300, 300);
        c_bitmap.drawCircle(s / 2, s / 2, s / 2 - (sw / 2), mPaint);
        //c_bitmap.drawArc(rectf, 20, 320, false, mPaint);

        //mPaint.setStrokeWidth(2);
        //c_bitmap.drawRect(0, 0, s, s, mPaint);

        mPaint.reset();
        mPaint.setColor(Color.BLUE);
        mPaint.setStyle(Paint.Style.FILL);
        c_bitmap.drawCircle(s / 2, s / 2, s / 5, mPaint);

        setImageBitmap(bitmap);
        setOnTouchListener(this);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        ((MainActivity) getContext()).send_log(Color.BLACK, "x: " + String.valueOf(event.getX()));
        ((MainActivity) getContext()).send_log(Color.BLACK, "y: " + String.valueOf(event.getY()));

        float x = event.getX();
        float y = event.getY();

        mPaint.reset();
        mPaint.setColor(bitmap.getPixel((int)x, (int)y));
        mPaint.setStyle(Paint.Style.FILL);
        c_bitmap.drawCircle(s / 2, s / 2, s / 5, mPaint);

        setImageBitmap(bitmap);


        return false;
    }

    public int get_color() {
        return bitmap.getPixel(s / 2, s / 2);
    }
}
