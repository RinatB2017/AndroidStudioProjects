package com.boss.for_testing;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.nfc.NfcAdapter;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.io.File;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    static final String LOG_TAG = "States";

    private static final int RECORD_REQUEST_CODE = 101;

    TextView tv_log;
    ToggleButton toggleButton;

    TabHost tabHost;

    Runnable runnable;

    Handler h_print;

    private NfcAdapter nfc;

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

        h_print = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                String text = (String) msg.obj;
                Log.i(LOG_TAG, text);
                tv_log.append(text + "\n");
            }
        };

        if (savedInstanceState == null) {
            Bundle bundle = new Bundle();
            getIntent().putExtras(bundle);
        }

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
        for (int i = 0; i < tabHost.getTabWidget().getChildCount(); i++) {
            //tabHost.getTabWidget().getChildAt(i).setBackgroundColor(Color.parseColor("#CCCCCC"));
            tabHost.getTabWidget().getChildAt(i).setLayoutParams(layoutParams);
            TextView textView = (TextView) tabWidget.getChildAt(i).findViewById(android.R.id.title);
            textView.setTextColor(Color.BLACK);
        }
        //---


        requestPermission(Manifest.permission.READ_EXTERNAL_STORAGE, RECORD_REQUEST_CODE);
    }

    //---------------------------------------------------------------------------------------------
    void add_seekBar() {
        SeekBar sb = (SeekBar) findViewById(R.id.seekBar);
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
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // TODO Auto-generated method stub
                //logging("pos = " + progress);
            }
        });
    }

    //---------------------------------------------------------------------------------------------
    void add_toggleButton() {
        toggleButton = (ToggleButton) findViewById(R.id.toggleButton);
        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    send_log("ON");
                } else {
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

        add_seekBar();
        add_toggleButton();
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
    public void start(View view) {
        Bundle bundle = getIntent().getExtras(); //new Bundle();
        bundle.putBoolean("flag_is_running", true);
        getIntent().putExtras(bundle);

        runnable = new Runnable() {
            public void run() {
                int n = 0;
                boolean flag_is_running = true;
                while (true) {
                    Bundle bundle = getIntent().getExtras();
                    if (bundle != null) {
                        flag_is_running = bundle.getBoolean("flag_is_running");
                    }
                    if (!flag_is_running) {
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
    /* Проверяет, доступно ли external storage как минимум для чтения */
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    private void list_files(File path) {
        if (path == null) {
            return;
        }

        send_log("DIR: " + path);

        File[] l_files = path.listFiles();
        if (l_files == null) {
            return;
        }
        for (int n = 0; n < l_files.length; n++) {
            if (l_files[n].isDirectory()) {
                list_files(l_files[n]);
            } else {
                send_log("   file: " + l_files[n].getName());
            }
        }
    }

    public void test(View view) {
        send_log("test");

        /*
        tv_log.setText("");

        if(!isExternalStorageReadable()) {
            send_log("ERROR: external storage no readable");
            return;
        }

        String basePath = Environment.getExternalStorageDirectory().getAbsolutePath();
        //basePath = "/storage/emulated/0/Android/data/com.mendhak.gpslogger/files";
        send_log("basePath " + basePath);
        list_files(new File(basePath));

        //File file = new File("/storage/emulated/0/Android/data/com.mendhak.gpslogger/files");
        */

        String name = "name";
        String company = "company";
        int price = 100500;

        Product product = new Product(name, company, price);

        Intent intent = new Intent(this, SecondActivity.class);
        intent.putExtra(Product.class.getSimpleName(), product);
        startActivity(intent);
    }

    //---------------------------------------------------------------------------------------------
}
