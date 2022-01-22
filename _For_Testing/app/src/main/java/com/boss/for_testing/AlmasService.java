package com.boss.for_testing;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AlmasService extends IntentService
{
    public Context context=null;

    // Must create a default constructor
    public AlmasService() {
        // Used to name the worker thread, important only for debugging.
        super("test-service");
    }

    @Override
    public void onCreate() {
        super.onCreate(); // if you override onCreate(), make sure to call super().
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        context=this;
        try
        {
            Thread.sleep(5000);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }

        String val = intent.getStringExtra("foo");

        // Do the task here
        Log.i("States", val);
    }
}