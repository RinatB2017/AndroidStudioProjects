package com.boss.tab_template;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TabHost;
import android.widget.TextView;

// http://developer.alexanderklimov.ru/android/views/tabhost-tabwidget.php

public class MainActivity extends AppCompatActivity {
    static final String LOG_TAG = "States";
    TextView tv_log;

    //---------------------------------------------------------------------------------------------
    public void logging(String text) {
        Log.i(LOG_TAG, text);
        tv_log.append(text + "\n");
    }

    //---------------------------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv_log = (TextView) findViewById(R.id.logView);
        tv_log.setTextColor(Color.BLACK);

        TabHost tabHost = (TabHost) findViewById(R.id.tabHost);

        tabHost.setup();

        TabHost.TabSpec tabSpec = tabHost.newTabSpec("tag_log");

        tabSpec.setContent(R.id.tab_test);
        tabSpec.setIndicator("Test");
        tabHost.addTab(tabSpec);

        tabSpec = tabHost.newTabSpec("log");
        tabSpec.setContent(R.id.tab_log);
        tabSpec.setIndicator("Log");
        tabHost.addTab(tabSpec);

        tabHost.setCurrentTab(0);
    }

    //---------------------------------------------------------------------------------------------
    @Override
    protected void onStart()
    {
        super.onStart();
        logging("onStart()");
    }

    //---------------------------------------------------------------------------------------------
    @Override
    protected void onRestart()
    {
        super.onRestart();
        logging("onRestart()");
    }

    //---------------------------------------------------------------------------------------------
    @Override
    protected void onResume()
    {
        super.onResume();
        logging("onResume()");
    }

    //---------------------------------------------------------------------------------------------
    @Override
    protected void onPause()
    {
        super.onPause();
        logging("onPause()");
    }

    //---------------------------------------------------------------------------------------------
    @Override
    protected void onStop()
    {
        super.onStop();
        logging("onStop()");
    }

    //---------------------------------------------------------------------------------------------
    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        logging("onDestroy()");
    }

    //---------------------------------------------------------------------------------------------
    public void click1(View view) {
        logging("1");
    }

    //---------------------------------------------------------------------------------------------
    public void click2(View view) {
        logging("2");
    }

    //---------------------------------------------------------------------------------------------
    public void click3(View view) {
        logging("3");
    }

    //---------------------------------------------------------------------------------------------
}
