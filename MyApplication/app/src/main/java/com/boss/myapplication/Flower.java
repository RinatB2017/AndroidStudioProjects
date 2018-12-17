package com.boss.myapplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;

public class Flower extends View {

    Context f_context;

    public Flower(Context context) {
        super(context);

        f_context = context;
    }

//    public Flower(Context context, AttributeSet attrs) {
//        super(context, attrs);
//    }

    public void set_color(Color color) {

    }

    public void set_background_color(Color color) {

    }

    public View get_view(int w, int h) {
        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas c_bitmap = new Canvas(bitmap);
        ImageView main_view = new ImageView(f_context);

        Paint mPaint = new Paint();
        mPaint.setColor(Color.RED);

        mPaint.setStrokeWidth(3);

        int b = 10;
        mPaint.setStyle(Paint.Style.STROKE);
        c_bitmap.drawRect(b, b, w-b, h-b, mPaint);
        c_bitmap.drawLine(0, 0, w, h, mPaint);
        c_bitmap.drawLine(0, h, w, 0, mPaint);

        main_view.setImageBitmap(bitmap);
        //main_view.setAdjustViewBounds(true);

        return main_view;
    }

    public boolean set_led_color(int num_led, int hot_color, int cold_color) {
        return true;
    }

    public int get_hot_color(int num_led) {
        return 0;
    }

    public int get_cold_color(int num_led) {
        return 0;
    }

}
