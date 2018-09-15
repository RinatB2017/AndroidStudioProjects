package com.boss.for_testing;

import android.content.Intent;
import android.content.pm.PackageManager;
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
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;

public class MainActivity extends AppCompatActivity {
    static final String LOG_TAG = "States";

    TextView tv_log;
    ToggleButton toggleButton;
    ImageView imageView;
    Runnable runnable;

    int WIDTH;
    int HEIGHT;

    Handler handler;

    //---------------------------------------------------------------------------------------------
    public void send_log(String text) {
        if (text == null) {
            return;
        }
        Message msg = new Message();
        msg.obj = text;
        handler.sendMessage(msg);
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

        handler = new Handler() {
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
        Display display = getWindowManager().getDefaultDisplay();
        Point p = new Point();
        display.getSize(p);
        WIDTH  = p.x - 1;
        HEIGHT = p.y - 1;

        send_log("WIDTH  " + WIDTH);
        send_log("HEIGHT " + HEIGHT);

        LinearLayout lll;
        lll = (LinearLayout)findViewById(R.id.l_test);
        lll.measure(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        send_log("l_test " + lll.getMeasuredHeight());

        Button btn;
        btn = (Button)findViewById(R.id.button);
        btn.measure(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        send_log("btn " + btn.getMeasuredHeight());
        btn.setText(String.valueOf(btn.getHeight()));
        //---

        add_bitmap();
        add_seekBar();
        add_toggleButton();
    }
    //---------------------------------------------------------------------------------------------
    void add_bitmap() {
        Bitmap bitmap = Bitmap.createBitmap(WIDTH / 2, WIDTH / 2, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bitmap);
        Paint mPaint = new Paint();
        float cx = bitmap.getWidth() / 2;
        float cy = bitmap.getHeight() / 2;
        int color = 0;
        for(int radius = bitmap.getWidth() / 2; radius > 0; radius -= 10 ) {
            mPaint.setColor(Color.rgb(0, 0, color));
            c.drawCircle(cx, cx, radius, mPaint);
            color += 10;
        }

        imageView = (ImageView) findViewById(R.id.imageView);
        imageView.setImageBitmap(bitmap);

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
        send_log("onStart()");
    }

    //---------------------------------------------------------------------------------------------
    @Override
    protected void onRestart() {
        super.onRestart();
        send_log("onRestart()");
    }

    //---------------------------------------------------------------------------------------------
    @Override
    protected void onResume() {
        super.onResume();
        send_log("onResume()");

        //flag_is_running = false;
        Bundle bundle = getIntent().getExtras(); //new Bundle();
        bundle.putBoolean("flag_is_running", false);
        getIntent().putExtras(bundle);
    }

    //---------------------------------------------------------------------------------------------
    @Override
    protected void onPause() {
        super.onPause();
        send_log("onPause()");
    }

    //---------------------------------------------------------------------------------------------
    @Override
    protected void onStop() {
        super.onStop();
        send_log("onStop()");
    }

    //---------------------------------------------------------------------------------------------
    @Override
    protected void onDestroy() {
        super.onDestroy();
        send_log("onDestroy()");
    }

    //---------------------------------------------------------------------------------------------
    public void test(View view) {
        Intent intent = new Intent(this, PrefActivity.class);
        startActivity(intent);
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
