package com.boss.for_testing;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

public class ServiceReceiver extends BroadcastReceiver {
    static final String LOG_TAG = "States";

    @Override
    public void onReceive(final Context context, Intent intent) {
        TelephonyManager telephony = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        telephony.listen(new PhoneStateListener(){
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                super.onCallStateChanged(state, incomingNumber);

                //((MainActivity) getActivity()).send_log("incomingNumber : " + incomingNumber);
                Log.i(LOG_TAG, "incomingNumber : " + incomingNumber);
            }
        }, PhoneStateListener.LISTEN_CALL_STATE);
    }
}
