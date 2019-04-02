package com.boss.for_testing;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;

public class RButton extends android.support.v7.widget.AppCompatButton {

    private Paint m_paint = new Paint();
    private int m_color = 0XFF92C84D; //LIKE AN OLIVE GREEN..
    String text = "TEST";
    int fontsize = 60;
    int width = 0;
    int height = 0;
    int text_width = 0;
    int text_height = 0;

    public RButton(Context context) {
        super(context);
        init();
    }

    public RButton(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
        init();
    }

    public RButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        setHeight(100);

        Paint fontPaint;
        fontPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        fontPaint.setTextSize(fontsize);

        float x = getTextSize();

        Rect bounds = new Rect();
        fontPaint.getTextBounds(text, 0, 1, bounds);

        text_width = (int)fontPaint.measureText(text);
        text_height = bounds.height();
    }

    @SuppressLint("DrawAllocation")
    public void onDraw(Canvas iCanvas) {
        width = getWidth();
        height = getHeight();

        //draw the button background
        m_paint.setColor(m_color);
        iCanvas.drawRoundRect(new RectF(0, 0, width, height), 30, 30, m_paint);

        //draw the text
        m_paint.setColor(Color.BLACK);
        m_paint.setAntiAlias(true);
        m_paint.setTextSize(fontsize);
        iCanvas.drawText( text,
                width / 2.0f - text_width / 2.0f,
                height / 2.0f + text_height / 2.0f,
                m_paint);
    }

}
