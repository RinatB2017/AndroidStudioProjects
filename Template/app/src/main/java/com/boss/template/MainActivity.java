package com.boss.template;

import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.graphics.Color;
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
import android.widget.TextView;

import java.util.Random;

public class MainActivity extends AppCompatActivity {
    static final String LOG_TAG = "States";

    private ProgressDialog scanProgressDialog;
    Handler handler;

    final Random random = new Random();

    TextView tv_log;

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
    public void click(View view) {
        logging("test");

        // https://javadevblog.com/dialogi-v-android-primer-raboty-s-progressdialog.html
        // http://developer.alexanderklimov.ru/android/java/random.php

        int minValue = 0;
        int maxValue = 100;

        scanProgressDialog = new ProgressDialog(MainActivity.this, R.style.DialogTheme);
        scanProgressDialog.setCancelable(false);
        scanProgressDialog.setTitle("Scanning: " + minValue + " to " + maxValue);
        scanProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        scanProgressDialog.incrementProgressBy(1);
        scanProgressDialog.setProgress(minValue);
        scanProgressDialog.setMax(maxValue);
        scanProgressDialog.show();

        handler = new Handler() {
            public void handleMessage(Message msg) {
                // и обновляем идикатор, пока шкала не заполнится
                if (scanProgressDialog.getProgress() < scanProgressDialog.getMax()) {
                    //scanProgressDialog.setProgress(msg.what);
                    // обновляем индикаторы на 1 пункт за 100 милисекунд
                    scanProgressDialog.incrementProgressBy(1);
                    handler.sendEmptyMessageDelayed(0, 100);
                } else {
                    // когда шкала заполнилась, диалог пропадает
                    scanProgressDialog.dismiss();
                }
            }
        };
        // имитируем подключение к удаленному серверу
        // (ожидаем 10 секунд перед стартом обновления индикатора)
        handler.sendEmptyMessageDelayed(0, 10000);

        /*
        Thread t = new Thread(new Runnable() {
            public void run() {
                for (int i = 1; i <= 10; i++) {
                    // долгий процесс
                    try {
                        int n = random.nextInt(10) * 1000;
                        Thread.sleep(1000 + n);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    handler.sendEmptyMessage(i);
                    // пишем лог
                    Log.d(LOG_TAG, "i = " + i);
                }
            }
        });
        t.start();
        */

        logging("the end!");
    }

    //---------------------------------------------------------------------------------------------
}
