package com.boss.myapplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class Flower extends View {

    public Flower(Context context) {
        super(context);
    }

//    public Flower(Context context, AttributeSet attrs) {
//        super(context, attrs);
//    }

    public void set_color(Color color) {

    }

    public void set_background_color(Color color) {

    }

    public View get_view(int w, int h) {
        return null;
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

    public View get_bitmap(int w, int h, int color) {
        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas c_bitmap = new Canvas(bitmap);
        ImageView main_view = new ImageView(getContext());

        Paint mPaint = new Paint();
        mPaint.setColor(color);

        float r = 0;
        if(w > h) {
            r = h / 4;
        }
        else {
            r = w / 4;
        }

        mPaint.setStyle(Paint.Style.FILL);
        c_bitmap.drawCircle(w / 2,
                h / 2,
                r,
                mPaint);

        main_view.setImageBitmap(bitmap);

        return main_view;
    }

    public View get_text() {
        TextView tv = new TextView(getContext());
        tv.setText("It's TEXT");

        return tv;
    }
}
