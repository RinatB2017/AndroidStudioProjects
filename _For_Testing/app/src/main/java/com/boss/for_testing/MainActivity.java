package com.boss.for_testing;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
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

    //---------------------------------------------------------------------------------------------
    public void logging(String text) {
        Log.i(LOG_TAG, text);
        tv_log.append(text + "\n");
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

        if(savedInstanceState == null) {
            Bundle bundle = new Bundle();
            getIntent().putExtras(bundle);
            logging("NULL");
        }

        //---
        Display display = getWindowManager().getDefaultDisplay();
        Point p = new Point();
        display.getSize(p);
        int WIDTH  = p.x - 1;
        int HEIGHT = p.y - 1;

        logging("WIDTH  " + WIDTH);
        logging("HEIGHT " + HEIGHT);

        LinearLayout lll;
        lll = (LinearLayout)findViewById(R.id.l_test);
        lll.measure(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        logging("l_test " + lll.getMeasuredHeight());

        Button btn;
        btn = (Button)findViewById(R.id.button);
        btn.measure(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        logging("btn " + btn.getMeasuredHeight());
        btn.setText(String.valueOf(btn.getHeight()));
        //---

        Bitmap bitmap = Bitmap.createBitmap(WIDTH, WIDTH, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bitmap);
        Paint mPaint = new Paint();
        float cx = WIDTH / 2;
        float cy = WIDTH / 2;
        int color = 0;
        for(int radius = WIDTH / 2; radius > 0; radius -= 10 ) {
            mPaint.setColor(Color.rgb(0, 0, color));
            c.drawCircle(cx, cx, radius, mPaint);
            color += 10;
        }

        imageView = (ImageView) findViewById(R.id.imageView);
        imageView.setImageBitmap(bitmap);
        //---

        SeekBar sb = (SeekBar)findViewById(R.id.seekBar);
        sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
                logging("pos = " + seekBar.getProgress());
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

        toggleButton = (ToggleButton)findViewById(R.id.toggleButton);
        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            if (isChecked) {
                    logging("ON");
                }
                else {
                    logging("OFF");
                }
            }
        });
    }

    //---------------------------------------------------------------------------------------------
    @Override
    protected void onStart() {
        super.onStart();
        logging("onStart()");
    }

    //---------------------------------------------------------------------------------------------
    @Override
    protected void onRestart() {
        super.onRestart();
        logging("onRestart()");
    }

    //---------------------------------------------------------------------------------------------
    @Override
    protected void onResume() {
        super.onResume();
        logging("onResume()");

        //flag_is_running = false;
        Bundle bundle = getIntent().getExtras(); //new Bundle();
        bundle.putBoolean("flag_is_running", false);
        getIntent().putExtras(bundle);
    }

    //---------------------------------------------------------------------------------------------
    @Override
    protected void onPause() {
        super.onPause();
        logging("onPause()");
    }

    //---------------------------------------------------------------------------------------------
    @Override
    protected void onStop() {
        super.onStop();
        logging("onStop()");
    }

    //---------------------------------------------------------------------------------------------
    @Override
    protected void onDestroy() {
        super.onDestroy();
        logging("onDestroy()");
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
				        Log.i(LOG_TAG, "thread is stoped!");
				        return;
                    }
				    Log.i(LOG_TAG, "n = " + n);
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
        //flag_is_running = false;
        Bundle bundle = getIntent().getExtras();    //new Bundle();
        bundle.putBoolean("flag_is_running", false);
        getIntent().putExtras(bundle);
    }

    //---------------------------------------------------------------------------------------------
}
