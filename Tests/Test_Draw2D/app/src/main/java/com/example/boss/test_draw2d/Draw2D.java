package com.example.boss.test_draw2d;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

/**
 * Created by boss on 07.04.16.
 */
public class Draw2D extends View {
    private Paint mPaint = new Paint();

    public Draw2D(Context context) {
        super(context);
    }

    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);

        // стиль Заливка
        mPaint.setStyle(Paint.Style.FILL);

        // закрашиваем холст
        mPaint.setColor(Color.BLUE);
        canvas.drawPaint(mPaint);

        // Рисуем желтый круг
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.YELLOW);
        canvas.drawCircle(50, 50, 25, mPaint);

        // Рисуем зелёный прямоугольник
        mPaint.setColor(Color.GREEN);
        canvas.drawRect(20, 150, 150, 250, mPaint);
    }
}
