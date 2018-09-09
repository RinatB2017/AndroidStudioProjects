package com.boss.activity_service;

import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

// http://www.pvsm.ru/razrabotka-pod-android/18682

public class MainActivity extends Activity {
    public static final String TAG = "TestService";

    TestServiceConnection testServConn;
    TextView testTxt;

    final Messenger messenger = new Messenger(new IncomingHandler());
    Messenger toServiceMessenger;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        testTxt = (TextView)findViewById(R.id.test_txt);

        bindService(new Intent(this, TestService.class),
                (testServConn = new TestServiceConnection()),
                Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();

        unbindService(testServConn);
    }

    //---
    public void countIncrClick(View button){
        Message msg = Message.obtain(null, TestService.COUNT_PLUS);
        msg.replyTo = messenger;
        try {
            toServiceMessenger.send(msg);
        }
        catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void countDecrClick(View button){
        Message msg = Message.obtain(null, TestService.COUNT_MINUS);
        msg.replyTo = messenger;
        try {
            toServiceMessenger.send(msg);
        }
        catch (RemoteException e) {
            e.printStackTrace();
        }
    }
    //---

    private class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg){
            switch (msg.what) {
                case TestService.GET_COUNT:
                    Log.d(TAG, "(activity)...get count");
                    testTxt.setText(""+msg.arg1);

                    break;
            }
        }
    }

    private class TestServiceConnection implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            toServiceMessenger = new Messenger(service);
            //отправляем начальное значение счетчика
            Message msg = Message.obtain(null, TestService.SET_COUNT);
            msg.replyTo = messenger;
            msg.arg1 = 0; //наш счетчик
            try {
                toServiceMessenger.send(msg);
            }
            catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {	}
    }
}
