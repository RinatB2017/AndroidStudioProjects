package com.boss.cyberbiology;

import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class MainActivity extends AppCompatActivity {

    //final String LOG_TAG = "Log";

    int width = 0;
    int height = 0;

    Bitmap bitmap;
    Canvas c_bitmap;
    Paint mPaint;
    LinearLayout linLayout;
    LinearLayout.LayoutParams layoutParams;
    ImageView main_view;

    Handler h_view;

    //---------------------------------------------------------------------------------------------
    void max_screen() {
        // займем весь экран
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION           // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN                // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE);
    }

    //---------------------------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);

        //max_screen();

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        linLayout = new LinearLayout(this);
        layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        //layoutParams.setMargins(0, 0, 0, 0);

        main_view = new ImageView(this);
        linLayout.addView(main_view, layoutParams);

        h_view = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                Bitmap bitmap = (Bitmap) msg.obj;
                main_view.setImageBitmap(bitmap);
            }
        };

        linLayout.setOrientation(LinearLayout.VERTICAL);
        setContentView(linLayout);
    }

    //---------------------------------------------------------------------------------------------
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        main_view.measure(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);

        width = main_view.getWidth();
        height = main_view.getHeight();

        bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        c_bitmap = new Canvas(bitmap);

        mPaint = new Paint();
        mPaint.setColor(Color.RED);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setAntiAlias(true);

        main_view.setImageBitmap(bitmap);

        Runnable runnable = new Runnable() {
            public void run() {
				World world = new World(h_view, 400, 200);
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }
}
