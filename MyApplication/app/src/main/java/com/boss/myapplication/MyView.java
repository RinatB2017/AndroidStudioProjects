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

    public class MyException extends Exception {
        MyException(String message) {
            super(message);
        }
    }

    public int test(int value) throws MyException {
        if(value > 5) {
            throw new MyException("");
        }
        return value;
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

        mPaint.setStrokeWidth(3);

        int b = 10;
        mPaint.setStyle(Paint.Style.STROKE);
        c_bitmap.drawRect(b, b, w-b, h-b, mPaint);

        mPaint.setStyle(Paint.Style.FILL);
        c_bitmap.drawCircle(w / 2,
                h / 2,
                r,
                mPaint);

        mPaint.setColor(Color.BLACK);
        c_bitmap.drawLine(0, 0, w, h, mPaint);
        c_bitmap.drawLine(0, h, w, 0, mPaint);

        main_view.setImageBitmap(bitmap);

        return main_view;
    }

    public View get_text() {
        TextView tv = new TextView(getContext());
        tv.setText("It's TEXT");

        return tv;
    }
}
