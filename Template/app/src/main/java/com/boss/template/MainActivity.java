package com.boss.template;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity{
    static final String LOG_TAG = "States";
    //final ProgressDialog scanProgressDialog;

    TextView tv_log;
    static Context context;
    ProgressDialog scanProgressDialog;

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

        context = this;

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
    public void click_test(View view) {
        logging("test");

        int minValue = 1;
        int maxValue = 10;

        //final ProgressDialog scanProgressDialog;
        scanProgressDialog = new ProgressDialog(context);
        scanProgressDialog.setCancelable(false);
        //scanProgressDialog.setIndeterminate(true);
        scanProgressDialog.setTitle("Scanning: " + minValue + " to " + maxValue);
        scanProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        scanProgressDialog.setProgress(minValue);
        scanProgressDialog.setMax(maxValue);
        scanProgressDialog.show();

        for(int n=minValue; n<maxValue; n++) {
            if (scanProgressDialog != null) {
                scanProgressDialog.setProgress(n);
                logging(String.valueOf(n));
            }

            Runnable runnable = new Runnable() {
                public void run() {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            };
            Thread thread = new Thread(runnable);
            thread.start();
//            try {
//                Thread.sleep(1000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
        }
        scanProgressDialog.dismiss();

        logging("the end!");
    }

    //---------------------------------------------------------------------------------------------
}
