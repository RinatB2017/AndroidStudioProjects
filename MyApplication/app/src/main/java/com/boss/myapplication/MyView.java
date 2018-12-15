package com.boss.myapplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class MyView extends View {

    public MyView(Context context) {
        super(context);
    }

    public View getView(int w, int h)
    {
        int r = w / 4;

        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas c_bitmap = new Canvas(bitmap);
        ImageView main_view = new ImageView(getContext());

        Paint mPaint = new Paint();
        mPaint.setColor(Color.RED);

        mPaint.setStyle(Paint.Style.FILL);
        c_bitmap.drawCircle(w / 2,
                h / 2,
                r,
                mPaint);

        main_view.setImageBitmap(bitmap);

        return main_view;
    }

    View get_bitmap(int w, int h, int color) {
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

    View get_text() {
        TextView tv = new TextView(getContext());
        tv.setText("It's TEXT");

        return tv;
    }
}
