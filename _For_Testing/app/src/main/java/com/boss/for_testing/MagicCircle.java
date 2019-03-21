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

    public MagicCircle(Context context) {
        super(context);
    }

    public MagicCircle(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
        init();
    }

    public MagicCircle(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public void init() {
        bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        c_bitmap = new Canvas(bitmap);
        mPaint = new Paint();
        //---
        int fontSize = 60;
        String text = "Test";

        mPaint.setColor(Color.BLACK);
        mPaint.setAntiAlias(true);
        mPaint.setTextSize(fontSize);

        Paint fontPaint;
        fontPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        fontPaint.setTextSize(fontSize);
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
        c_bitmap.drawCircle(center_x, center_y, width / 5, mPaint);

        setImageBitmap(bitmap);


        return false;
    }

    public int get_color() {
        return bitmap.getPixel(center_x, center_y);
    }
}
