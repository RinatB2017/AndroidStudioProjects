package com.boss.for_testing;

import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    //private final int RECORD_REQUEST_CODE = 101;
    private String LOG_TAG = "States";

    private final String s_log = "s_log";
    private final String s_current_tab = "s_current_tab";

    TextView tv_log;

    TabHost tabHost;
    Handler h_print;

    //---
    Button btn_test;

    SoundPool soundPool;
    int explosionId = -1;
    //---

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
    public void send_log(int color, String text) {
        if (text == null) {
            return;
        }
        Message msg = new Message();
        msg.arg1 = color;
        msg.obj = text;
        h_print.sendMessage(msg);
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
    void init_log() {
        tv_log = (TextView) findViewById(R.id.logView);
        tv_log.setTextColor(Color.BLACK);

        h_print = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                String text = (String) msg.obj;
                int color = msg.arg1;
                Log.i(LOG_TAG, text);

                String c_text = "<font color=#" + Integer.toHexString(color).substring(2) + ">" + text + "</font><br>";
                tv_log.append(Html.fromHtml(c_text, Html.FROM_HTML_MODE_LEGACY));

                //---
                Toast toast = Toast.makeText(getBaseContext(),
                        text,
                        Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.BOTTOM, 0, 0);
                toast.show();
                //---
            }
        };
    }

    //---------------------------------------------------------------------------------------------
    void init_tabs()
    {
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

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.weight = 1;

        TabWidget tabWidget = tabHost.getTabWidget();
        for (int i = 0; i < tabHost.getTabWidget().getChildCount(); i++) {
            //tabHost.getTabWidget().getChildAt(i).setBackgroundColor(Color.parseColor("#CCCCCC"));
            tabHost.getTabWidget().getChildAt(i).setLayoutParams(layoutParams);
            TextView textView = (TextView) tabWidget.getChildAt(i).findViewById(android.R.id.title);
            textView.setTextColor(Color.BLACK);
        }
    }

    //---------------------------------------------------------------------------------------------
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putString(s_log, tv_log.getText().toString());
        savedInstanceState.putInt(s_current_tab, tabHost.getCurrentTab());
        super.onSaveInstanceState(savedInstanceState);
    }

    //---------------------------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init_log();

        btn_test = (Button) findViewById(R.id.btn_test);

        //---

        //TODO временный костыль
        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        //---

        init_tabs();

        if (savedInstanceState != null) {
            String temp = savedInstanceState.getString(s_log);
            if (temp != null) {
                if (!temp.isEmpty()) {
                    tv_log.setText(temp);
                }
            }

            int current_tab = savedInstanceState.getInt(s_current_tab);
            tabHost.setCurrentTab(current_tab);
        } else {
            Bundle bundle = new Bundle();
            getIntent().putExtras(bundle);
        }

        //---
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            soundPool = new SoundPool.Builder()
                    .setMaxStreams(10)
                    .build();
        } else {
            soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 1);
        }

        try {
            AssetManager assetManager = getAssets();
            AssetFileDescriptor descriptor = assetManager.openFd("health.wav");
            explosionId = soundPool.load(descriptor, 1);
        } catch (IOException e) {
            send_log(Color.RED, e.getMessage());
        }
        //---
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
        send_log(Color.RED, "test");

        if (explosionId != -1) {
            soundPool.play(explosionId, 1, 1, 0, 0, 1);
        }

        /*
        // Get running processes
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningProcesses = manager.getRunningAppProcesses();
        if (runningProcesses != null && runningProcesses.size() > 0) {
            // Set data to the list adapter
            setListAdapter(new ListAdapter(this, runningProcesses));
        } else {
            // In case there are no processes running (not a chance :))
            Toast.makeText(getApplicationContext(), "No application is running", Toast.LENGTH_LONG).show();
        }
        */
    }

    public void btn1_click(View view) {
        send_log(Color.RED, "1");
        //throw new RuntimeException("Усё пропало!");

        /*
        try {
            // получаем входной поток
            InputStream ims = getAssets().open("splash_screen.png");
            // загружаем как Drawable
            Drawable d = Drawable.createFromStream(ims, null);
            // выводим картинку в ImageView
            ImageView mImage = (ImageView)findViewById(R.id.asset_image);
            mImage.setImageDrawable(d);
        }
        catch(IOException ex) {
            send_log(Color.RED, ex.getMessage());
        }
        */
    }

    public void btn2_click(View view) {
        send_log(Color.RED, "2");
    }

    //---------------------------------------------------------------------------------------------
}
