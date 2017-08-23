package com.test;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

public class TestingLogging extends AppCompatActivity {

    final String TAG = "States";
    //---------------------------------------------------------------------------------------------
    void logging(String text) {
        Log.v(TAG, text + "\n");
    }
    //---------------------------------------------------------------------------------------------
    @Override
    protected void onRestart() {
        super.onRestart();
        logging(getLocalClassName() +": onRestart()");
    }
    //---------------------------------------------------------------------------------------------
    @Override
    protected void onStart() {
        super.onStart();
        logging(getLocalClassName() +": onStart()");
    }
    //---------------------------------------------------------------------------------------------
    @Override
    protected void onResume() {
        super.onResume();
        logging(getLocalClassName() +": onResume()");
    }
    //---------------------------------------------------------------------------------------------
    @Override
    protected void onPause() {
        super.onPause();
        logging(getLocalClassName() +": onPause()");
    }
    //---------------------------------------------------------------------------------------------
    @Override
    protected void onStop() {
        super.onStop();
        logging(getLocalClassName() +": onStop()");
    }
    //---------------------------------------------------------------------------------------------
    @Override
    protected void onDestroy() {
        super.onDestroy();
        logging(getLocalClassName() +": onDestroy()");
    }
    //---------------------------------------------------------------------------------------------

}
