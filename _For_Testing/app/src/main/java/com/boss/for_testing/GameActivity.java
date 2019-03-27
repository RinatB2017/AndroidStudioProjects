package com.boss.for_testing;

import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class GameActivity extends AppCompatActivity
        implements View.OnTouchListener
{
    final String LOG_TAG = "States";

    private Paint mPaint;
    Canvas canvas;
    LinearLayout linLayout;
    LinearLayout.LayoutParams layoutParams;
    Bitmap bitmap;

    int width  = 0;
    int height = 0;
    int b_size = 0;

    int b_width  = 0;
    int b_height = 0;

    int max_x = 0;
    int max_y = 0;

    byte[][] map;

    RBlock rb;
    Bitmap b_red;
    Bitmap b_green;
    Bitmap b_blue;
    Bitmap b_black;

    ImageView main_view;

    //---------------------------------------------------------------------------------------------
    public void redraw() {
        main_view.setImageBitmap(bitmap);
    }
    //---------------------------------------------------------------------------------------------
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    //---------------------------------------------------------------------------------------------
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId())
        {
//            case R.id.action_settings_scan:
//                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    //---------------------------------------------------------------------------------------------
    protected void requestPermission(String permissionType, int requestCode) {
        int permission = ContextCompat.checkSelfPermission(this,
                permissionType);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{permissionType}, requestCode
            );
        }
    }
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
    private void prepare_screen() {
        Display display = getWindowManager().getDefaultDisplay();
        Point p = new Point();
        display.getRealSize(p);

        width = p.x;
        height = p.y;

        // 1080 1920
        Log.i(LOG_TAG, "width " + String.valueOf(width));
        Log.i(LOG_TAG, "height " + String.valueOf(height));

        bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        //---
        mPaint = new Paint();
        mPaint.setStrokeWidth(5);
        //---
        canvas = new Canvas(bitmap);

        main_view = new ImageView(this);
        main_view.setImageBitmap(bitmap);
        main_view.setOnTouchListener(this);

        linLayout = new LinearLayout(this);

        linLayout.setOrientation(LinearLayout.VERTICAL);
        setContentView(linLayout);
        linLayout.addView(main_view);
    }

    //---------------------------------------------------------------------------------------------
    private void test_0() {
        byte value = 0;
        byte old_value = 0;
        for(int y=0; y<max_y; y++) {
            for(int x=0; x<max_x; x++) {
                do {
                    value = (byte) (Math.random() * 3);
                } while(value == old_value);
                old_value = value;
                map[x][y] = value;
            }
        }

        for(int y=0; y<max_y; y++) {
            for(int x=0; x<max_x; x++) {
                switch (map[x][y]) {
                    case 0:
                        canvas.drawBitmap(b_red, x*b_width, y*b_height, null);
                        break;

                    case 1:
                        canvas.drawBitmap(b_green, x*b_width, y*b_height, null);
                        break;

                    case 2:
                        canvas.drawBitmap(b_blue, x*b_width, y*b_height, null);
                        break;

                    default:
                        break;
                }
            }
        }
    }

    //---------------------------------------------------------------------------------------------
    private void test_1() {
        int b_width  = b_size;
        int b_height = b_size;
        int color = 0;

        for(int y=0; y<(height / b_height); y++) {
            for(int x=0; x<(width / b_width); x++) {
                Bitmap bitmap;
                switch (color) {
                    case 0:
                        canvas.drawBitmap(b_red, x*b_width, y*b_height, null);
                        color++;
                        break;

                    case 1:
                        canvas.drawBitmap(b_green, x*b_width, y*b_height, null);
                        color++;
                        break;

                    case 2:
                        canvas.drawBitmap(b_blue, x*b_width, y*b_height, null);
                        color++;
                        break;

                    default:
                        canvas.drawBitmap(b_red, x*b_width, y*b_height, null);
                        color = 1;
                        break;
                }
            }
        }
    }

    //---------------------------------------------------------------------------------------------
    private void test_2() {
        canvas.drawBitmap(b_red,    200,    100,    null);
        canvas.drawBitmap(b_green,  200,    200,    null);
        canvas.drawBitmap(b_blue,   200,    300,    null);
        canvas.drawBitmap(b_red,    200,    400,    null);
        canvas.drawBitmap(b_red,    200,    500,    null);
        canvas.drawBitmap(b_red,    200,    600,    null);

        Resources resources = getResources();
        canvas.drawBitmap(BitmapFactory.decodeResource(resources, R.drawable.icon), 500, 500, null);
    }

    //---------------------------------------------------------------------------------------------
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        max_screen();

        //TODO временный костыль
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        prepare_screen();

        //---
        b_size = width / 8;
        //---
        rb = new RBlock();
        rb.set_new_size(b_size, b_size);
        b_red   = rb.get_circle(Color.RED);
        b_green = rb.get_romb(Color.GREEN);
        b_blue  = rb.get_romb(Color.BLUE);
        b_black = rb.get_rect(Color.BLACK);
        //---
        b_width  = b_size;
        b_height = b_size;

        max_x = width / b_width;
        max_y = height / b_height;

        map = new byte[max_x][max_y];
        //---
        test_0();
        //test_1();
        //test_2();
    }
    //---------------------------------------------------------------------------------------------
    @Override
    protected void onStart()
    {
        super.onStart();
        //logging("onStart()");
    }
    //---------------------------------------------------------------------------------------------
    @Override
    protected void onRestart()
    {
        super.onRestart();
        //logging("onRestart()");
    }
    //---------------------------------------------------------------------------------------------
    @Override
    protected void onResume()
    {
        super.onResume();
        //logging("onResume()");
    }
    //---------------------------------------------------------------------------------------------
    @Override
    protected void onPause()
    {
        super.onPause();
        //logging("onPause()");
    }
    //---------------------------------------------------------------------------------------------
    @Override
    protected void onStop()
    {
        super.onStop();
        //logging("onStop()");
    }
    //---------------------------------------------------------------------------------------------
    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        //logging("onDestroy()");
    }

    //---------------------------------------------------------------------------------------------
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        int pos_x = (int)x / b_width;
        int pos_y = (int)y / b_height;

        if(map[pos_x][pos_y] == 1) {
            //Log.i(LOG_TAG, "find!");
            canvas.drawBitmap(b_black, pos_x*b_width, pos_y*b_height, null);
            main_view.setImageBitmap(bitmap);
            return true;
        }

        return false;
    }

    //---------------------------------------------------------------------------------------------
}
