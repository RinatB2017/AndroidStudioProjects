package com.javadevblog.messengerexampleapp;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

// https://javadevblog.com/primer-raboty-c-messenger-sluzhby-v-android.html

public class MainActivity extends AppCompatActivity {

    private final Messenger mActivityMessenger = new Messenger(new ResponseHandler(this));
    private Button mButtonSend1;
    private Button mButtonSend2;
    private EditText mEditTextMessage1;
    private EditText mEditTextMessage2;
    private Messenger mMessenger;
    private boolean isBound;

    ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mMessenger = new Messenger(service);
            isBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isBound = false;
            mMessenger = null;
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mEditTextMessage1 = (EditText) findViewById(R.id.et_message1);
        mEditTextMessage2 = (EditText) findViewById(R.id.et_message2);

        mButtonSend1 = (Button) findViewById(R.id.btn_send1);
        mButtonSend1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String messageText = mEditTextMessage1.getText().toString();
                if (messageText.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Введите сообщение!", Toast.LENGTH_LONG).show();
                } else {
                    Message message = Message.obtain(null, SimpleServiceIPC.TASK_1);
                    Bundle bundle = new Bundle();
                    bundle.putString("message", messageText);
                    message.setData(bundle);
                    message.replyTo = mActivityMessenger;
                    try {
                        mMessenger.send(message);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        mButtonSend2 = (Button) findViewById(R.id.btn_send2);
        mButtonSend2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String messageText = mEditTextMessage2.getText().toString();
                if (messageText.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Введите сообщение!", Toast.LENGTH_LONG).show();
                } else {
                    Message message = Message.obtain(null, SimpleServiceIPC.TASK_2);
                    Bundle bundle = new Bundle();
                    bundle.putString("message", messageText);
                    message.setData(bundle);
                    message.replyTo = mActivityMessenger;
                    try {
                        mMessenger.send(message);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!isBound) {
            // start service here
            Intent intent = new Intent(MainActivity.this, SimpleServiceIPC.class);
            bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        isBound = false;
        mMessenger = null;
    }

    private class ResponseHandler extends Handler {
        private Context mContext;

        ResponseHandler(Context context) {
            mContext = context;
        }

        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case SimpleServiceIPC.TASK_RESPONSE_1:
                    String result1 = msg.getData().getString("message_res");
                    Toast.makeText(mContext, "Пришло из IPC службы: " + result1, Toast.LENGTH_LONG).show();

                    //TODO
                    mEditTextMessage1.setText(result1);
                    break;

                case SimpleServiceIPC.TASK_RESPONSE_2:
                    String result2 = msg.getData().getString("message_res");
                    Toast.makeText(mContext, "Пришло из IPC службы: " + result2, Toast.LENGTH_LONG).show();

                    //TODO
                    mEditTextMessage2.setText(result2);
                    break;

                default:
                    super.handleMessage(msg);
            }
        }
    }
}