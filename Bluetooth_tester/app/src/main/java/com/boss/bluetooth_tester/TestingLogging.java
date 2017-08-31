package com.boss.bluetooth_tester;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

public class TestingLogging extends AppCompatActivity {

    final String TAG = "States";
    final String log_name = "LOG";
    TextView logView;
    //---------------------------------------------------------------------------------------------
    void logging(String text) {
        Log.v(TAG, text + "\n");
        logView.append(text + "\n");
    }
    //---------------------------------------------------------------------------------------------
    void clean_log() {
        logView.setText("");
    }
    //---------------------------------------------------------------------------------------------
    void load_log() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        logView.append(sp.getString(log_name, ""));

        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();
        editor.putString(log_name, "");
        editor.apply();
    }
    //---------------------------------------------------------------------------------------------
    void save_log() {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();
        editor.putString(log_name, logView.getText().toString());
        editor.apply();
    }
    //---------------------------------------------------------------------------------------------
    @Override
    protected void onRestart() {
        super.onRestart();
    }
    //---------------------------------------------------------------------------------------------
    @Override
    protected void onStart() {
        super.onStart();
    }
    //---------------------------------------------------------------------------------------------
    @Override
    protected void onResume() {
        super.onResume();
    }
    //---------------------------------------------------------------------------------------------
    @Override
    protected void onPause() {
        super.onPause();
    }
    //---------------------------------------------------------------------------------------------
    @Override
    protected void onStop() {
        super.onStop();

        save_log();
    }
    //---------------------------------------------------------------------------------------------
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
    //---------------------------------------------------------------------------------------------

}
