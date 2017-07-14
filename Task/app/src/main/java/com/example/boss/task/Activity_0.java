package com.example.boss.task;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class Activity_0 extends AppCompatActivity {
    //---------------------------------------------------------------------------------------------
    final String LOG_TAG = "myLogs";
    //---------------------------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_0);
    }
    //---------------------------------------------------------------------------------------------
    public void page_0(View view) {
        Intent intent = new Intent(this, Activity_1.class);
        startActivity(intent);
    }
    //---------------------------------------------------------------------------------------------
    protected void onDestroy() {
        super.onDestroy();
        Log.d(LOG_TAG, "Activity_0: onDestroy");
    }
    //---------------------------------------------------------------------------------------------
    protected void onPause() {
        super.onPause();
        Log.d(LOG_TAG, "Activity_0: onPause");
    }
    //---------------------------------------------------------------------------------------------
    protected void onRestart() {
        super.onRestart();
        Log.d(LOG_TAG, "Activity_0: onRestart");
    }
    //---------------------------------------------------------------------------------------------
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.d(LOG_TAG, "Activity_0: onRestoreInstanceState");
    }
    //---------------------------------------------------------------------------------------------
    protected void onResume() {
        super.onResume();
        Log.d(LOG_TAG, "Activity_0: onResume ");
    }
    //---------------------------------------------------------------------------------------------
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(LOG_TAG, "Activity_0: onSaveInstanceState");
    }
    //---------------------------------------------------------------------------------------------
    protected void onStart() {
        super.onStart();
        Log.d(LOG_TAG, "Activity_0: onStart");
    }
    //---------------------------------------------------------------------------------------------
    protected void onStop() {
        super.onStop();
        Log.d(LOG_TAG, "Activity_0: onStop");
    }
    //---------------------------------------------------------------------------------------------
}