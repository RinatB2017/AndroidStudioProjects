package com.boss.for_testing;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AlmasReceiver extends BroadcastReceiver
{
    /**
     * Triggered by the Alarm periodically (starts the service to run task)
     * @param context
     * @param intent
     */

    @Override
    public void onReceive(Context context, Intent intent)
    {
        Intent i = new Intent(context, AlmasService.class);
        i.putExtra("foo", "AlarmReceiver");
        context.startService(i);
    }
}
