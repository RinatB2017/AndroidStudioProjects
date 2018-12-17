package com.boss.myapplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;

public class Flower extends View {

    Context f_context;

    private Paint mPaint;
    Canvas c_bitmap;

    Bitmap bitmap;

    float temp_x;
    float temp_y;

    float center_x;
    float center_y;

    float center_r;

    float led_r;
    float min_r;
    float max_r;
    float min_angle;
    float max_angle;
    int inc_r;

    ImageView main_view;

    LED_points points;

    int background = Color.BLACK;
    int color_border_on = Color.GREEN;
    int color_border_off = Color.GRAY;
    int text_color = Color.WHITE;

    int DEFAULT_HOT_COLOR = 10;
    int DEFAULT_COLD_COLOR = 10;

    byte[][] leds = new byte[6][6];
    int[][] leds_arr = {
            {0x2112, 0x2255, 0x4223, 0x4314, 0x5415, 0x2001},
            {0x4151, 0x3245, 0x5213, 0x5334, 0x0510, 0x4011},
            {0x3102, 0x2535, 0x0333, 0x0424, 0x4400, 0x3050}};

    public Flower(Context context) {
        super(context);

        f_context = context;
    }

//    public Flower(Context context, AttributeSet attrs) {
//        super(context, attrs);
//    }

    public void set_color_on(int color) {
        color_border_on = color;
    }

    public void set_color_off(int color) {
        color_border_off = color;
    }

    public void set_background_color(int color) {
        background = color;
    }

    public View get_view(int w, int h) {
//        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
//        Canvas c_bitmap = new Canvas(bitmap);
//        ImageView main_view = new ImageView(f_context);
//
//        Paint mPaint = new Paint();
//        mPaint.setColor(Color.RED);
//
//        mPaint.setStrokeWidth(3);
//
//        int b = 10;
//        mPaint.setStyle(Paint.Style.STROKE);
//        c_bitmap.drawRect(b, b, w-b, h-b, mPaint);
//        c_bitmap.drawLine(0, 0, w, h, mPaint);
//        c_bitmap.drawLine(0, h, w, 0, mPaint);
//
//        main_view.setImageBitmap(bitmap);
        //main_view.setAdjustViewBounds(true);

        //---
        int size = 0;
        if(w > h)
            size = h;
        else
            size = w;
        bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        mPaint = new Paint();
        c_bitmap = new Canvas(bitmap);

        points = new LED_points();

        main_view = new ImageView(f_context);
        main_view.setImageBitmap(bitmap);

        new_draw_field();
        //---

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

    public void redraw() {
        if(main_view == null) {
            return;
        }
        if(bitmap != null) {
            for (int n = 0; n < points.size(); n++) {
                draw_led(n);
            }
            main_view.setImageBitmap(bitmap);
        }
    }

    private void new_draw_field() {

        points.get(0).number = 0;
        points.get(0).center_x = center_x;
        points.get(0).center_y = center_y;
        points.get(0).radius = center_r;
        points.get(0).hot_color = DEFAULT_HOT_COLOR;
        points.get(0).cold_color = DEFAULT_COLD_COLOR;
        points.get(0).address = 0;

        min_angle = -30.0f;
        max_angle = 330.0f;

        float angle = min_angle;
        int x = 5;
        /*
        рисуем по часовой стрелке
        */
        int number = 1; //центр уже нарисовали ранее
        while (angle < max_angle) {
            for (int n = 0; n < 3; n++) {
                calc_line(center_x,
                        center_y,
                        angle,
                        inc_r * (n + 1));
                c_bitmap.drawCircle(temp_x,
                        temp_y,
                        led_r,
                        mPaint);

                LED s_led = new LED();
                s_led.number = number;
                s_led.center_x = temp_x;
                s_led.center_y = temp_y;
                s_led.radius = led_r;
                s_led.address = leds_arr[n][x];

                //FIXME исправить позже
                if (n == 2) {
                    if (angle == 270) s_led.text = "1";
                    if (angle == -30) s_led.text = "2";
                    if (angle == 30) s_led.text = "3";
                    if (angle == 90) s_led.text = "4";
                    if (angle == 150) s_led.text = "5";
                    if (angle == 210) s_led.text = "6";
                    s_led.draw_text = true;
                } else {
                    s_led.draw_text = false;
                }
                //---

                points.get(number).number = s_led.number;
                points.get(number).address = s_led.address;
                points.get(number).center_x = s_led.center_x;
                points.get(number).center_y = s_led.center_y;
                points.get(number).radius = s_led.radius;
                points.get(number).color_border_on = s_led.color_border_on;
                points.get(number).color_border_off = s_led.color_border_off;
                points.get(number).draw_text = s_led.draw_text;
                points.get(number).color_text = s_led.color_text;
                points.get(number).text = s_led.text;

                number++;
            }
            angle += 60.0f;
            x--;
        }
        redraw();
    }

    public void draw_led(int num) {
        if (bitmap == null) {
            //send_log("X1");
            return;
        }

        if (c_bitmap == null) {
            //send_log("X2");
            return;
        }

        LED led = points.get(num);
        if (led.is_active)
            mPaint.setColor(led.color_border_on);
        else
            mPaint.setColor(led.color_border_off);

        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setAntiAlias(true);

        c_bitmap.drawCircle(led.center_x,
                led.center_y,
                led.radius,
                mPaint);


        //---
        final RectF circle = new RectF();
        circle.set(led.center_x - led.radius,
                led.center_y - led.radius,
                led.center_x + led.radius,
                led.center_y + led.radius);

        // hot
        mPaint.setColor(Color.rgb(led.hot_color, 0, 0));
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setAntiAlias(true);
        c_bitmap.drawArc(circle,
                90, 180,
                true,
                mPaint);
        //---

        // cold
        mPaint.setColor(Color.rgb(0, 0, led.cold_color));
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setAntiAlias(true);
        c_bitmap.drawArc(circle,
                270, 180,
                true,
                mPaint);
        //sweepAngle - на сколько градусов рисуем от startAngle
        //---

        //FIXME исправить позже
        if (points.get(num).draw_text) {
            int fontSize = (int) (led.radius * 1.37 + 0.5); //58; //80;

            String text = points.get(num).text;
            mPaint.setColor(points.get(num).color_text);

            mPaint.setStyle(Paint.Style.FILL);
            mPaint.setAntiAlias(true);
            mPaint.setTextSize(fontSize);

            Paint fontPaint;
            fontPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            fontPaint.setTextSize(fontSize);
            float width = fontPaint.measureText(text);

            Rect bounds = new Rect();
            fontPaint.getTextBounds(text, 0, 1, bounds);

            float height = bounds.height();

            c_bitmap.drawText(text,
                    points.get(num).center_x - width / 2,
                    points.get(num).center_y + height / 2,
                    mPaint);
        }
    }

    private void calc_line(float x,
                           float y,
                           float angle,
                           float radius) {
        float A = radius;
        float B = (float) Math.cos(Math.toRadians(angle)) * A;
        float C = (float) Math.sin(Math.toRadians(angle)) * A;

        temp_x = x + B;
        temp_y = y + C;
    }

}
