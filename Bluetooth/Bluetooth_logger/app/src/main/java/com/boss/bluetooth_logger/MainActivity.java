package com.boss.bluetooth_logger;

import android.Manifest;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    //public final static String UUID = "e91521df-92b9-47bf-96d5-c52ee838f6f6";
    public final static String UUID = "00001101-0000-1000-8000-00805F9B34FB";

    private static final int RECORD_REQUEST_CODE = 101;
    final String LOG_TAG = "States";

    TextView tv_log;

    public boolean is_hex = false;

    private final List<BluetoothDevice> discoveredDevices = new ArrayList<BluetoothDevice>();

    private ServerThread serverThread;
    Bluetooth bt;

    Handler h_print;

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
            case R.id.action_settings_connect:
                bt.device_connect();
                break;

            case R.id.action_settings_disconnect:
                bt.device_disconnect();
                break;

            case R.id.action_settings_options:
                Intent intent = new Intent(this, OptionsActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                if (intent != null) {
                    startActivity(intent);
                }
                break;

            case R.id.clear_log:
                tv_log.setText("");
                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
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
    public void block_interface(boolean state) {

    }

    //----------------------------------------------------------------------------------------
    public void send_log(String text) {
        if (text == null) {
            return;
        }
        Message msg = new Message();
        msg.obj = text;
        h_print.sendMessage(msg);
    }

    //---------------------------------------------------------------------------------------------
    void init_log() {
        tv_log = (TextView) findViewById(R.id.logView);

        h_print = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                String text = (String) msg.obj;
                Log.i(LOG_TAG, text);
                tv_log.append(text + "\n");
            }
        };
    }

    //---------------------------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init_log();

        bt = new Bluetooth(MainActivity.this, tv_log);
        requestPermission(Manifest.permission.ACCESS_FINE_LOCATION, RECORD_REQUEST_CODE);

    }

    //---------------------------------------------------------------------------------------------
    @Override
    protected void onStart() {
        super.onStart();
        bt.device_connect();
    }

    //---------------------------------------------------------------------------------------------
    @Override
    protected void onRestart() {
        super.onRestart();
        bt.device_connect();
    }

    //---------------------------------------------------------------------------------------------
    @Override
    protected void onResume() {
        super.onResume();

        send_log("serverThread.start();");
        serverThread = new ServerThread(communicatorService);
        serverThread.start();

        discoveredDevices.clear();
    }

    //---------------------------------------------------------------------------------------------
    @Override
    protected void onPause() {
        super.onPause();
        bt.device_disconnect();
    }

    //---------------------------------------------------------------------------------------------
    @Override
    protected void onStop() {
        super.onStop();
    }

    //---------------------------------------------------------------------------------------------
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    //----------------------------------------------------------------------------------------
    private final CommunicatorService communicatorService = new CommunicatorService() {
        @Override
        public Communicator createCommunicatorThread(final BluetoothSocket socket) {
            return new CommunicatorImpl(socket, new CommunicatorImpl.CommunicationListener() {
                @Override
                public void onMessage(final String message) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //send_log(tv_log.getText().toString());
                            if(is_hex) {
                                byte[] array = new byte[0];
                                try {
                                    array = message.getBytes("UTF-8");
                                } catch (UnsupportedEncodingException e) {
                                    e.printStackTrace();
                                };
                                StringBuffer hexString = new StringBuffer();
                                for (byte b : array) {
                                    int intVal = b & 0xff;
                                    if (intVal < 0x10)
                                        hexString.append("0");
                                    hexString.append(Integer.toHexString(intVal));
                                }
                                send_log(hexString.toString());
                            }
                            else {
                                send_log(message);
                            }
                            //send_log(socket.getRemoteDevice().getName());

                            // отвечаем эхом
                            try {
                                OutputStream o_stream = socket.getOutputStream();
                                o_stream.write(message.getBytes());
                            } catch (IOException e) {
                                send_log("Stream ERROR: " + e.getMessage());
                            }
                        }
                    });
                }
            });
        }
    };

    //---------------------------------------------------------------------------------------------
}
