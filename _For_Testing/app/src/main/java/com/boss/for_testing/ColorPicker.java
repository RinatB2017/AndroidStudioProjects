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

public class ColorPicker extends AppCompatImageView
        implements View.OnTouchListener
{
    Paint mPaint;
    Canvas c_bitmap;
    Bitmap bitmap;

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
        int s = 400;

        bitmap = Bitmap.createBitmap(s, s, Bitmap.Config.ARGB_8888);

        c_bitmap = new Canvas(bitmap);
        mPaint = new Paint();
        mPaint.setColor(Color.RED);
        mPaint.setStrokeWidth(10);
        mPaint.setStyle(Paint.Style.STROKE);
        c_bitmap.drawCircle(s / 2, s / 2, s / 2 - 10, mPaint);

        mPaint.setStrokeWidth(2);
        c_bitmap.drawRect(0, 0, s, s, mPaint);

        setImageBitmap(bitmap);
        setOnTouchListener(this);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        ((MainActivity) getContext()).send_log(Color.BLACK, "x: " + String.valueOf(event.getX()));
        ((MainActivity) getContext()).send_log(Color.BLACK, "y: " + String.valueOf(event.getY()));
        return false;
    }
}
