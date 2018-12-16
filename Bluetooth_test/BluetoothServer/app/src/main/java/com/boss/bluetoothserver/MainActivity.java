package com.boss.bluetoothserver;

import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends ListActivity {
    static final String LOG_TAG = "States";

    //public final static String UUID = "e91521df-92b9-47bf-96d5-c52ee838f6f6";
    public final static String UUID = "00001101-0000-1000-8000-00805F9B34FB";

    private BluetoothAdapter bluetoothAdapter;

    private final List<BluetoothDevice> discoveredDevices = new ArrayList<BluetoothDevice>();
    private ArrayAdapter<BluetoothDevice> listAdapter;

    private ServerThread serverThread;

    TextView tv_log;

    Handler h_print;

    //----------------------------------------------------------------------------------------
    public void send_log(String text) {
        if (text == null) {
            return;
        }
        Message msg = new Message();
        msg.obj = text;
        h_print.sendMessage(msg);
    }

    //----------------------------------------------------------------------------------------
    private void init_log() {
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
                            send_log(tv_log.getText().toString());
                            send_log(message + "\n");
                            send_log(socket.getRemoteDevice().getName() + "\n");
                            
                            // отвечаем эхом
                            try {
                                OutputStream o_stream = socket.getOutputStream();
                                o_stream.write(message.getBytes());
                            } catch (IOException e) {
                                send_log("Stream ERROR: " +e.getMessage() + " \n");
                            }
                        }
                    });
                }
            });
        }
    };

    //----------------------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init_log();

        create_bluetooth();
    }

    //----------------------------------------------------------------------------------------
    @Override
    public void onResume() {
        super.onResume();
        serverThread = new ServerThread(communicatorService);
        serverThread.start();

        discoveredDevices.clear();
        listAdapter.notifyDataSetChanged();
    }

    //----------------------------------------------------------------------------------------
    public void makeDiscoverable(View view) {
        Intent i = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        i.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
        startActivity(i);
    }

    //----------------------------------------------------------------------------------------
    public void create_bluetooth() {
        send_log("create_bluetooth");
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        listAdapter = new ArrayAdapter<BluetoothDevice>(getBaseContext(), android.R.layout.simple_list_item_1, discoveredDevices) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                final BluetoothDevice device = getItem(position);
                ((TextView) view.findViewById(android.R.id.text1)).setText(device.getName());
                return view;
            }
        };
        setListAdapter(listAdapter);
    }
}
