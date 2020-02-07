package com.test;

import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;

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
