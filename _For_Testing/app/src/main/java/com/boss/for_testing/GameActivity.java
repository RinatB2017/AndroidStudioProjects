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
    import android.view.View;
    import android.view.Window;
    import android.view.WindowManager;
    import android.widget.ImageView;
    import android.widget.LinearLayout;

// если на смартфоне стоит Android 6.0, то надо поставить в свойствах app
// Flawors Target SDK Version API22
// иначе bluetooth не будет находить устройства

public class GameActivity extends AppCompatActivity {

    final String LOG_TAG = "States";

    private Paint mPaint;
    Canvas canvas;
    LinearLayout linLayout;
    LinearLayout.LayoutParams layoutParams;
    Bitmap bitmap;

    int width  = 0;
    int height = 0;

    RBlock rb;
    Bitmap b_red;
    Bitmap b_green;
    Bitmap b_blue;

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

        linLayout = new LinearLayout(this);

        linLayout.setOrientation(LinearLayout.VERTICAL);
        setContentView(linLayout);
        linLayout.addView(main_view);
    }

    //---------------------------------------------------------------------------------------------
    private void test_0() {
        byte[][] map = {
                {0, 8, 8, 8, 0},
                {8, 1, 8, 1, 8},
                {8, 8, 2, 8, 8},
                {8, 1, 8, 1, 8},
                {0, 8, 8, 8, 0}
        };
        for(int y=0; y<5; y++) {
            for(int x=0; x<5; x++) {
                switch (map[x][y]) {
                    case 0:
                        canvas.drawBitmap(b_red, x*100 + 100, y*100 + 199, null);
                        break;

                    case 1:
                        canvas.drawBitmap(b_green, x*100 + 100, y*100 + 199, null);
                        break;

                    case 2:
                        canvas.drawBitmap(b_blue, x*100 + 100, y*100 + 199, null);
                        break;

                    default:
                        break;
                }
            }
        }
    }

    //---------------------------------------------------------------------------------------------
    private void test_1() {
        int color = 0;
        for(int y=0; y<5; y++) {
            for(int x=0; x<5; x++) {
                Bitmap bitmap;
                switch (color) {
                    case 0:
                        canvas.drawBitmap(b_red, x*100 + 100, y*100 + 199, null);
                        color++;
                        break;

                    case 1:
                        canvas.drawBitmap(b_green, x*100 + 100, y*100 + 199, null);
                        color++;
                        break;

                    case 2:
                        canvas.drawBitmap(b_blue, x*100 + 100, y*100 + 199, null);
                        color++;
                        break;

                    default:
                        canvas.drawBitmap(b_red, x*100 + 100, y*100 + 199, null);
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
//        mPaint.setColor(Color.RED);
//        mPaint.setStyle(Paint.Style.STROKE);
//        mPaint.setAntiAlias(true);

//        canvas.drawLine(0,  0,      width,  height, mPaint);
//        canvas.drawLine(0,  height, width,  0,      mPaint);

        //---
        rb = new RBlock();
        b_red   = rb.get_bitmap(Color.RED);
        b_green = rb.get_bitmap(Color.GREEN);
        b_blue  = rb.get_bitmap(Color.BLUE);
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
}
