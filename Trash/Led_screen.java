package com.boss.for_testing;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;

public class Led_screen extends android.support.v7.widget.AppCompatImageView {
    Bitmap bitmap;
    Canvas c_bitmap;
    private Paint mPaint;

    public Led_screen(Context context) {
        super(context);

        bitmap = Bitmap.createBitmap(200, 200, Bitmap.Config.ARGB_8888);
        c_bitmap = new Canvas(bitmap);

        mPaint.setColor(Color.RED);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setAntiAlias(true);
        c_bitmap.drawLine(0, 0, bitmap.getWidth(), bitmap.getHeight(), mPaint);
    }

    public Led_screen(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public Led_screen(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
}
