package com.boss.for_testing;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;
import android.widget.ToggleButton;

public class MainActivity extends AppCompatActivity implements View.OnTouchListener {
    static final String LOG_TAG = "States";

    TextView tv_log;
    ToggleButton toggleButton;
    ImageView imageView;
    Runnable runnable;

    TabHost tabHost;

    TextView coords;

    Bitmap bitmap;
    Canvas c_bitmap;
    Paint mPaint;

    Handler h_print;

    //---------------------------------------------------------------------------------------------
    public void send_log(String text) {
        if (text == null) {
            return;
        }
        Message msg = new Message();
        msg.obj = text;
        h_print.sendMessage(msg);
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
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    //---------------------------------------------------------------------------------------------
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_clear_log:
                tv_log.setText("");
                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //---------------------------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv_log = (TextView) findViewById(R.id.logView);
        tv_log.setTextColor(Color.BLACK);

        coords = (TextView) findViewById(R.id.coords);

        h_print = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                String text = (String) msg.obj;
                Log.i(LOG_TAG, text);
                tv_log.append(text + "\n");
            }
        };

        if(savedInstanceState == null) {
            Bundle bundle = new Bundle();
            getIntent().putExtras(bundle);
        }

        //---
        imageView = (ImageView) findViewById(R.id.imageView);
        //---

        //---
        tabHost = (TabHost) findViewById(R.id.tabHost);

        tabHost.setup();

        TabHost.TabSpec tabSpec;

        tabSpec = tabHost.newTabSpec("tab_main");
        tabSpec.setContent(R.id.tab_main);
        tabSpec.setIndicator(getString(R.string.main));
        tabHost.addTab(tabSpec);

        tabSpec = tabHost.newTabSpec("tab_log");
        tabSpec.setContent(R.id.tab_log);
        tabSpec.setIndicator(getString(R.string.log));
        tabHost.addTab(tabSpec);

        //tabHost.setCurrentTab(0);

        //---
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.weight = 1;

        TabWidget tabWidget = tabHost.getTabWidget();
        for(int i = 0; i < tabHost.getTabWidget().getChildCount(); i++) {
            //tabHost.getTabWidget().getChildAt(i).setBackgroundColor(Color.parseColor("#CCCCCC"));
            tabHost.getTabWidget().getChildAt(i).setLayoutParams(layoutParams);
            TextView textView = (TextView) tabWidget.getChildAt(i).findViewById(android.R.id.title);
            textView.setTextColor(Color.BLACK);
        }
        //---
    }

    //---------------------------------------------------------------------------------------------
    @Override
    public boolean onTouch(View view, MotionEvent event) {

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            float x = event.getX();
            float y = event.getY();

            send_log("x=" + x + " y=" + y);
            coords.setText("x=" + x + " y=" + y);

            //mPaint.setColor(Color.YELLOW);
            mPaint.setColor(Color.WHITE);
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setAntiAlias(true);
            c_bitmap.drawCircle(x, y, 10, mPaint);

            mPaint.setColor(Color.BLACK);
            c_bitmap.drawRect(0, 0, c_bitmap.getWidth(), c_bitmap.getHeight(), mPaint);

            imageView.setImageBitmap(bitmap);
        }
        return true;
    }

    //---------------------------------------------------------------------------------------------
    void add_bitmap() {

        Display display = getWindowManager().getDefaultDisplay();
        Point p = new Point();
        display.getSize(p);
        send_log("p.x " + p.x);
        send_log("p.y " + p.y);

        tabHost.measure(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        int s_tabHost = tabHost.getMeasuredHeight();
        send_log("measuredHeight " + s_tabHost);

        int hhh = tabHost.getTabWidget().getMeasuredHeight();
        send_log("hhh " + hhh);

        int size = 0;

        int color = 0;
        int rotate = getWindowManager().getDefaultDisplay().getRotation();
        switch (rotate) {
            case Surface.ROTATION_0:
            case Surface.ROTATION_180:
                // ORIENTATION_PORTRAIT
                color = Color.BLUE;
                size = p.x;
                break;

            case Surface.ROTATION_90:
            case Surface.ROTATION_270:
                // ORIENTATION_LANDSCAPE
                color = Color.RED;
                //size = p.y;
                size = p.y - (int)(hhh * 2.5);
                //size = p.y - s_tabHost;
                break;
        }

        send_log("size " + size);

        bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);

        c_bitmap = new Canvas(bitmap);
        mPaint = new Paint();

        float cx = bitmap.getWidth() / 2;
        float cy = bitmap.getHeight() / 2;
        for(int radius = bitmap.getWidth() / 2; radius > 0; radius -= 10 ) {
            //mPaint.setColor(Color.rgb(0, 0, color));
            mPaint.setColor(color);
            c_bitmap.drawCircle(cx, cx, radius, mPaint);
        }

        //---
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(size, size);
        imageView.setLayoutParams(layoutParams);
        imageView.setImageBitmap(bitmap);
        //---
    }

    //---------------------------------------------------------------------------------------------
    void add_seekBar() {
        SeekBar sb = (SeekBar)findViewById(R.id.seekBar);
        sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
                send_log("pos = " + seekBar.getProgress());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,boolean fromUser) {
                // TODO Auto-generated method stub
                //logging("pos = " + progress);
            }
        });
    }

    //---------------------------------------------------------------------------------------------
    void add_toggleButton() {
        toggleButton = (ToggleButton)findViewById(R.id.toggleButton);
        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    send_log("ON");
                }
                else {
                    send_log("OFF");
                }
            }
        });
    }

    //---------------------------------------------------------------------------------------------
    @Override
    protected void onStart() {
        super.onStart();
        //send_log("onStart()");
    }

    //---------------------------------------------------------------------------------------------
    @Override
    protected void onRestart() {
        super.onRestart();
        //send_log("onRestart()");
    }

    //---------------------------------------------------------------------------------------------
    @Override
    protected void onResume() {
        super.onResume();
        //send_log("onResume()");

        Bundle bundle = getIntent().getExtras(); //new Bundle();
        bundle.putBoolean("flag_is_running", false);
        getIntent().putExtras(bundle);
    }

    //---------------------------------------------------------------------------------------------
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        add_bitmap();
        add_seekBar();
        add_toggleButton();

        imageView.setOnTouchListener(this);

        imageView.measure(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        send_log("imageView: w " + imageView.getMeasuredWidth());
        send_log("imageView: h " + imageView.getMeasuredHeight());

        send_log("tabWidget: w " + tabHost.getTabWidget().getMeasuredWidth());
        send_log("tabWidget: h " + tabHost.getTabWidget().getMeasuredHeight());
    }

    //---------------------------------------------------------------------------------------------
    @Override
    protected void onPause() {
        super.onPause();
        //send_log("onPause()");
    }

    //---------------------------------------------------------------------------------------------
    @Override
    protected void onStop() {
        super.onStop();
        //send_log("onStop()");
    }

    //---------------------------------------------------------------------------------------------
    @Override
    protected void onDestroy() {
        super.onDestroy();
        //send_log("onDestroy()");
    }

    //---------------------------------------------------------------------------------------------
    public void test(View view) {
        //Intent intent = new Intent(this, PrefActivity.class);
        //startActivity(intent);

        send_log("TEST imageView: w " + imageView.getMeasuredWidth());
        send_log("TEST imageView: h " + imageView.getMeasuredHeight());
    }

    //---------------------------------------------------------------------------------------------
    public void start(View view) {
        Bundle bundle = getIntent().getExtras(); //new Bundle();
        bundle.putBoolean("flag_is_running", true);
        getIntent().putExtras(bundle);

        runnable = new Runnable() {
            public void run() {
                int n = 0;
                boolean flag_is_running = true;
				while(true) {
                    Bundle bundle = getIntent().getExtras();
                    if(bundle != null)
                    {
                        flag_is_running = bundle.getBoolean("flag_is_running");
                    }
				    if(!flag_is_running) {
                        send_log("thread is stoped!");
				        return;
                    }
                    send_log("n = " + n);
				    n++;

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }

    //---------------------------------------------------------------------------------------------
    public void stop(View view) {
        Bundle bundle = getIntent().getExtras();
        bundle.putBoolean("flag_is_running", false);
        getIntent().putExtras(bundle);
    }

    //---------------------------------------------------------------------------------------------
}
