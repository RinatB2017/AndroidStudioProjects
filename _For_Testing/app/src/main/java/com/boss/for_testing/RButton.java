package com.boss.for_testing;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
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

    int border = 50;
    int t_border = border;
    int l_border = border;
    int r_border = border;
    int b_border = border;

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
        setHeight(200);

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
    public void onDraw(Canvas c_bitmap) {
        width = getWidth();
        height = getHeight();

        //---
        int begin_color = Color.DKGRAY;
        int end_color   = Color.LTGRAY;
        //---
        int begin_border_color = begin_color;
        int end_border_color   = end_color;
        //---

        //draw the button background
        //m_paint.setColor(m_color);
        //c_bitmap.drawRoundRect(new RectF(0, 0, width, height), 30, 30, m_paint);

        m_paint.setColor(Color.TRANSPARENT);
        m_paint.setStyle(Paint.Style.FILL_AND_STROKE);
        c_bitmap.drawRect(0, 0, width, height, m_paint);

        m_paint.setColor(end_color);
        c_bitmap.drawRect(border, border, width - border, height - border, m_paint);

        Path path = new Path();
        //---
        //left
        Shader shader_left = new LinearGradient(
                0, height / 2.0f,
                border, height / 2.0f,
                begin_border_color, end_border_color,
                Shader.TileMode.CLAMP);
        Shader shader_up = new LinearGradient(
                width / 2.0f, 0,
                width / 2.0f, border,
                begin_border_color, end_border_color,
                Shader.TileMode.CLAMP);
        Shader shader_right = new LinearGradient(
                width, height / 2.0f,
                width - border, height / 2.0f,
                begin_border_color, end_border_color,
                Shader.TileMode.CLAMP);
        Shader shader_down = new LinearGradient(
                width / 2.0f, height,
                width / 2.0f, height - border,
                begin_border_color, end_border_color,
                Shader.TileMode.CLAMP);

        Shader shader_ul = new LinearGradient(
                l_border / 2.0f, t_border / 2.0f,
                l_border, t_border,
                begin_border_color, end_border_color,
                Shader.TileMode.CLAMP);
        Shader shader_ur = new LinearGradient(
                width - l_border / 2.0f, t_border / 2.0f,
                width - l_border, t_border,
                begin_border_color, end_border_color,
                Shader.TileMode.CLAMP);
        Shader shader_dl = new LinearGradient(
                l_border / 2.0f, height - t_border / 2.0f,
                l_border, height - t_border,
                begin_border_color, end_border_color,
                Shader.TileMode.CLAMP);
        Shader shader_dr = new LinearGradient(
                width - l_border / 2.0f, height - t_border / 2.0f,
                width - l_border, height - t_border,
                begin_border_color, end_border_color,
                Shader.TileMode.CLAMP);

        //---
        //left
        m_paint.setShader(shader_left);
        path.reset();
        path.moveTo(0, t_border);
        path.lineTo(border, border);
        path.lineTo(border, height - border);
        path.lineTo(0, height - b_border);
        path.close();

        c_bitmap.drawPath(path, m_paint);
        //---
        //up
        m_paint.setShader(shader_up);
        path.reset();
        path.moveTo(l_border, 0);
        path.lineTo(l_border, t_border);
        path.lineTo(width - r_border, t_border);
        path.lineTo(width - r_border, 0);
        path.close();

        c_bitmap.drawPath(path, m_paint);
        //---
        //right
        m_paint.setShader(shader_right);
        path.reset();
        path.moveTo(width, t_border);
        path.lineTo(width - border, border);
        path.lineTo(width - border, height - border);
        path.lineTo(width, height - b_border);
        path.close();

        c_bitmap.drawPath(path, m_paint);
        //---
        //down
        m_paint.setShader(shader_down);
        path.reset();
        path.moveTo(l_border, height);
        path.lineTo(border, height - border);
        path.lineTo(width - border, height - border);
        path.lineTo(width - r_border, height);
        path.close();

        c_bitmap.drawPath(path, m_paint);
        //---

        //---
        //up-left
        m_paint.setShader(shader_ul);
        path.reset();
        path.moveTo(0, t_border);
        path.lineTo(l_border, 0);
        path.lineTo(l_border, t_border);
        path.close();

        c_bitmap.drawPath(path, m_paint);
        //---
        //up-right
        m_paint.setShader(shader_ur);
        path.reset();
        path.moveTo(width - r_border, 0);
        path.lineTo(width, t_border);
        path.lineTo(width - r_border, t_border);
        path.close();

        c_bitmap.drawPath(path, m_paint);
        //---
        //down-left
        m_paint.setShader(shader_dl);
        path.reset();
        path.moveTo(0, height - b_border);
        path.lineTo(l_border, height);
        path.lineTo(l_border, height - b_border);
        path.close();

        c_bitmap.drawPath(path, m_paint);
        //---
        //down-right
        m_paint.setShader(shader_dr);
        path.reset();
        path.moveTo(width - r_border, height);
        path.lineTo(width, height - b_border);
        path.lineTo(width - r_border, height - b_border);
        path.close();

        c_bitmap.drawPath(path, m_paint);
        //---

        //draw the text
        m_paint.reset();
        m_paint.setColor(Color.BLACK);
        m_paint.setAntiAlias(true);
        m_paint.setTextSize(fontsize);
        c_bitmap.drawText( text,
                width / 2.0f - text_width / 2.0f,
                height / 2.0f + text_height / 2.0f,
                m_paint);
    }

}
