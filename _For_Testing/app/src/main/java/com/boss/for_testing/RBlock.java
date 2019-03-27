package com.boss.for_testing;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;

public class RBlock {
    int width = 100;
    int height = 100;

    //---------------------------------------------------------------------------------------------
    public Bitmap get_bitmap(int color) {
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        mPaint.setColor(color);
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);

        /*
        Path path = new Path();
        path.reset();
        path.moveTo(0, height / 2);
        path.lineTo(width / 2, 0);
        path.lineTo(width, height / 2);
        path.close();

        canvas.drawPath(path, mPaint);
        */

        canvas.drawCircle(width / 2, height / 2, width / 2, mPaint);

        return  bitmap;
    }
    //---------------------------------------------------------------------------------------------
}
