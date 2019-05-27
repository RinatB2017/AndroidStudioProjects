package com.boss.sender_acceleration_data;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static java.lang.Thread.sleep;

public class Bluetooth {

    private final static int REQUEST_ENABLE_BT = 1;

    final String LOG_TAG = "States";

    BluetoothAdapter bluetooth;
    //private BroadcastReceiver discoveryDevicesReceiver;
    //private BroadcastReceiver discoveryFinishedReceiver;
    //private final List<BluetoothDevice> discoveredDevices = new ArrayList<BluetoothDevice>();
    //private ProgressDialog progressDialog;

    private static final UUID MY_UUID = UUID.fromString("00000001-0001-0001-0001-000000000001");
    private static InputStream inputStream;
    private static OutputStream outputStream;

    BluetoothDevice r_device;
    BluetoothSocket tmp = null;
    BluetoothSocket mmSocket = null;
    InputStream tmpIn = null;
    OutputStream tmpOut = null;

    private Context context;
    private TextView tv_log;
    Handler handler;

    ModBus modbus;

    byte[] buffer = new byte[128];  // buffer store for the stream

    //----------------------------------------------------------------------------------------
    private void block_interface(boolean state) {

    }

    //----------------------------------------------------------------------------------------
    public Bluetooth(Context context, TextView log) {
        this.context = context;
        this.tv_log = log;
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                String text = (String) msg.obj;
                Log.i(LOG_TAG, text);
                tv_log.append(text + "\n");
            }
        };

        bluetooth = BluetoothAdapter.getDefaultAdapter();

        block_interface(true);

        modbus = new ModBus();

        if (bluetooth == null) {
            send_log(context.getString(R.string.bluetooth_not_found));
            return;
        }
        if (!bluetooth.isEnabled()) {
            // Bluetooth выключен. Предложим пользователю включить его.
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            ((MainActivity) context).startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
        send_log("OK");
    }

    //----------------------------------------------------------------------------------------
    private void send_log(String text) {
        if (text == null) {
            return;
        }
        Message msg = new Message();
        msg.obj = text;
        handler.sendMessage(msg);
    }

    //---------------------------------------------------------------------------------------------
    public  boolean is_enabled() {
        if(bluetooth == null) {
            return false;
        }
        return bluetooth.isEnabled();
    }

    //---------------------------------------------------------------------------------------------
    public boolean is_connected() {
        if (mmSocket == null) {
            return false;
        }
        if (!mmSocket.isConnected()) {
            return false;
        }
        return true;
    }

    //---------------------------------------------------------------------------------------------
    public void device_connect() {
        send_log("connect");
        Runnable runnable = new Runnable() {
            public void run() {
                if (bluetooth == null) {
                    send_log(context.getString(R.string.bluetooth_not_found));
                    return;
                }
                if (!bluetooth.isEnabled()) {
                    send_log(context.getString(R.string.bluetooth_off));
                    block_interface(true);
                    return;
                }

                boolean ok = connect_remote_device(BluetoothName.get_mac(context.getApplicationContext()));
                if (ok)
                    send_log(context.getString(R.string.connection_on));
                else
                    send_log(context.getString(R.string.connection_fail));
                block_interface(!ok);
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }

    //---------------------------------------------------------------------------------------------
    public void device_disconnect() {
        send_log("disconnect");
        Runnable runnable = new Runnable() {
            public void run() {
                if (mmSocket == null) {
                    return;
                }

                if (mmSocket.isConnected()) {
                    try {
                        mmSocket.close();
                        send_log(context.getString(R.string.connection_breaks));
                        block_interface(true);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }

    //---------------------------------------------------------------------------------------------
    public boolean connect_remote_device(String MAC_address) {
        if (MAC_address.isEmpty()) {
            send_log("MAC_address is empty!");
            return false;
        }

        r_device = bluetooth.getRemoteDevice(MAC_address);
        //---
        try {
            tmp = r_device.createRfcommSocketToServiceRecord(MY_UUID);
            Method m = r_device.getClass().getMethod("createRfcommSocket", new Class[]{int.class});
            tmp = (BluetoothSocket) m.invoke(r_device, 1);
        } catch (IOException e) {
            send_log("create ERROR: " + e.getMessage());
            return false;
        } catch (NoSuchMethodException e) {
            send_log("create ERROR: " + e.getMessage());
            return false;
        } catch (IllegalAccessException e) {
            send_log("create ERROR: " + e.getMessage());
            return false;
        } catch (InvocationTargetException e) {
            send_log("create ERROR: " + e.getMessage());
            return false;
        }
        //---
        mmSocket = tmp;
        try {
            mmSocket.connect();
        } catch (IOException e) {
            try {
                mmSocket = (BluetoothSocket) r_device.getClass().getMethod("createRfcommSocket", new Class[]{int.class}).invoke(r_device, 1);
                mmSocket.connect();
            } catch (Exception e2) {
                send_log("Stream ERROR: Couldn't establish Bluetooth connection!");
                return false;
            }
        }
        //---
        try {
            tmpIn = mmSocket.getInputStream();
            tmpOut = mmSocket.getOutputStream();
        } catch (IOException e) {
            send_log("Stream ERROR: " + e.getMessage());
            return false;
        }
        //---
        inputStream = tmpIn;
        outputStream = tmpOut;
        return true;
    }

    //---------------------------------------------------------------------------------------------
    public boolean send_data(String message) {
        if (bluetooth == null) {
            send_log(context.getString(R.string.bluetooth_not_found));
            return false;
        }
        if (!bluetooth.isEnabled()) {
            send_log(context.getString(R.string.bluetooth_off));
            block_interface(true);
            return false;
        }
        if (mmSocket == null) {
            return false;
        }
        if (!mmSocket.isConnected()) {
            return false;
        }

        int bytes = 0; // bytes returned from read()
        int bytesAvailableCount = 0;

        if (outputStream == null) {
            send_log("outputStream not created!");
            return false;
        }
        try {
            outputStream.write(message.getBytes());
            sleep(1000); //FIXME костыль
            do {
                bytesAvailableCount = inputStream.available();
                if (bytesAvailableCount > 0) {
                    bytes = inputStream.read(buffer);
                    send_log("Получено " + bytes + " байтов");
                }
            } while (bytesAvailableCount > 0);
        } catch (IOException e) {
            send_log("send_data ERROR: " + e.getMessage());
            block_interface(true);
            return false;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return true;
    }

    //---------------------------------------------------------------------------------------------
    public byte[] get_result() {
        return buffer;
    }

    //---------------------------------------------------------------------------------------------

}
