package com.boss.test_timer;

import java.util.Timer;
import java.util.TimerTask;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private TextView mCounterTextView;

    int cnt = 0;

    private Timer mTimer;
    private MyTimerTask mMyTimerTask;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mCounterTextView = (TextView) findViewById(R.id.textViewCounter);

        mTimer = new Timer();
        mMyTimerTask = new MyTimerTask();
        //mTimer.schedule(mMyTimerTask, 500, 500);
        //mTimer.schedule(mMyTimerTask, 30 * 1000, 30 * 1000);
        //mTimer.schedule(mMyTimerTask, 0, 60*1000);
        mTimer.schedule(mMyTimerTask, 0, 1000);
        }

    class MyTimerTask extends TimerTask {
        @Override
        public void run() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    cnt ++;
                    mCounterTextView.setText(String.valueOf(cnt));
                }
            });
        }
    }
}
