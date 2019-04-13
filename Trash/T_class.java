package com.boss.for_testing;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;

public class T_class extends AppCompatImageView
        implements View.OnTouchListener
{
    //---------------------------------------------------------------------------------------------
    public T_class(Context context) {
        super(context);
    }

    //---------------------------------------------------------------------------------------------
    public T_class(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
        init();
    }

    //---------------------------------------------------------------------------------------------
    public T_class(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    //---------------------------------------------------------------------------------------------
    public void init() {

    }

    //---------------------------------------------------------------------------------------------
    @Override
    public boolean onTouch(View v, MotionEvent event) {

        float x = event.getX();
        float y = event.getY();

        return false;
    }

    //---------------------------------------------------------------------------------------------
}
