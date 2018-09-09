package com.javadevblog.messengerexampleapp;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import java.util.concurrent.TimeUnit;

public class SimpleServiceIPC extends Service {

    public static final int TASK_1 = 1;
    public static final int TASK_RESPONSE_1 = 2;

    public static final int TASK_2 = 3;
    public static final int TASK_RESPONSE_2 = 4;

    final String LOG_TAG = "States";

    Messenger messenger = new Messenger(new IncomingHandler());

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return messenger.getBinder();
    }

    class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {

            Message message;
            Bundle bundle = new Bundle();
            String messageText;
            int n = 0;
            Messenger activityMessenger;

            switch (msg.what) {
                case TASK_1:
                    messageText = msg.getData().getString("message");

                    message = Message.obtain(null, TASK_RESPONSE_1);
                    Toast.makeText(getApplicationContext(), "Пришло с Activity: " + messageText, Toast.LENGTH_SHORT).show();

                    n = 1;
                    //for(int n=0; n<10; n++) {
                        bundle.putString("message_res", messageText + "+" + String.valueOf(n));
                        message.setData(bundle);
                        activityMessenger = msg.replyTo;
                        try {
                            activityMessenger.send(message);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                        try {
                            TimeUnit.SECONDS.sleep(1);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    //}
                    break;

                case TASK_2:
                    messageText = msg.getData().getString("message");

                    message = Message.obtain(null, TASK_RESPONSE_2);
                    Toast.makeText(getApplicationContext(), "Пришло с Activity: " + messageText, Toast.LENGTH_SHORT).show();

                    n = 2;
                    //for(int n=0; n<10; n++) {
                        bundle.putString("message_res", messageText + "-" + String.valueOf(n));
                        message.setData(bundle);
                        activityMessenger = msg.replyTo;
                        try {
                            activityMessenger.send(message);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                        try {
                            TimeUnit.SECONDS.sleep(1);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    //}
                    break;

                default:
                    super.handleMessage(msg);
            }
        }
    }
}