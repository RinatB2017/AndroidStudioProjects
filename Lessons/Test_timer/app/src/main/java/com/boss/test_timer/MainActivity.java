package com.boss.test_timer;

// Если этот код работает, его написал Александр Климов,
// а если нет, то не знаю, кто его писал.

// http://developer.alexanderklimov.ru/android/java/timer.php

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

public class MainActivity extends Activity {

    private CheckBox mSingleShotCheckBox;
    private Button mStartButton, mCancelButton;
    private TextView mCounterTextView;

    private Timer mTimer;
    private MyTimerTask mMyTimerTask;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSingleShotCheckBox = (CheckBox) findViewById(R.id.checkBoxSingleShot);
        mStartButton = (Button) findViewById(R.id.buttonStart);
        mCancelButton = (Button) findViewById(R.id.buttonCancel);
        mCounterTextView = (TextView) findViewById(R.id.textViewCounter);

        mStartButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {

                if (mTimer != null) {
                    mTimer.cancel();
                }

                // re-schedule timer here
                // otherwise, IllegalStateException of
                // "TimerTask is scheduled already"
                // will be thrown
                mTimer = new Timer();
                mMyTimerTask = new MyTimerTask();

                if (mSingleShotCheckBox.isChecked()) {
                    // singleshot delay 1000 ms
                    mTimer.schedule(mMyTimerTask, 1000);
                } else {
                    // delay 1000ms, repeat in 5000ms
                    mTimer.schedule(mMyTimerTask, 1000, 5000);
                }
            }
        });

        mCancelButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mTimer != null) {
                    mTimer.cancel();
                    mTimer = null;
                }
            }
        });
    }

    class MyTimerTask extends TimerTask {

        @Override
        public void run() {
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
                    "dd:MMMM:yyyy HH:mm:ss a", Locale.getDefault());
            final String strDate = simpleDateFormat.format(calendar.getTime());

            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    mCounterTextView.setText(strDate);
                }
            });
        }
    }
}
