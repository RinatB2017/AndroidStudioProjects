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
    int bytes = 0; // bytes returned from read()

    //----------------------------------------------------------------------------------------
    private void block_interface(boolean state) {

    }

    //----------------------------------------------------------------------------------------
    public Bluetooth(Context context, TextView log) throws BT_exception {
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
            //send_log(context.getString(R.string.bluetooth_not_found));
            throw new BT_exception(context.getString(R.string.bluetooth_not_found));
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
    public void device_connect() throws BT_exception {
        send_log("connect");
        Runnable runnable = new Runnable() {
            public void run() {
                if (bluetooth == null) {
                    send_log(context.getString(R.string.bluetooth_not_found));
                    return;
                }
                if (!bluetooth.isEnabled()) {
                    send_log(context.getString(R.string.bluetooth_off));
                    return;
                }

                try {
                    boolean ok = connect_remote_device(BluetoothName.get_mac(context.getApplicationContext()));
                    if (ok)
                        send_log(context.getString(R.string.connection_on));
                } catch (BT_exception r) {
                    send_log(context.getString(R.string.connection_fail));
                }
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
                    } catch (IOException e) {
                        send_log(e.getMessage());
                    }
                    send_log(context.getString(R.string.connection_breaks));
                }
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }

    //---------------------------------------------------------------------------------------------
    public boolean connect_remote_device(String MAC_address) throws BT_exception {
        if (MAC_address.isEmpty()) {
            throw new BT_exception("MAC_address is empty!");
        }

        r_device = bluetooth.getRemoteDevice(MAC_address);
        //---
        try {
            tmp = r_device.createRfcommSocketToServiceRecord(MY_UUID);
            Method m = r_device.getClass().getMethod("createRfcommSocket", new Class[]{int.class});
            tmp = (BluetoothSocket) m.invoke(r_device, 1);
        } catch (IOException e) {
            throw new BT_exception("create ERROR: " + e.getMessage());
        } catch (NoSuchMethodException e) {
            throw new BT_exception("create ERROR: " + e.getMessage());
        } catch (IllegalAccessException e) {
            throw new BT_exception("create ERROR: " + e.getMessage());
        } catch (InvocationTargetException e) {
            throw new BT_exception("create ERROR: " + e.getMessage());
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
                throw new BT_exception("Stream ERROR: Couldn't establish Bluetooth connection!");
            }
        }
        //---
        try {
            tmpIn = mmSocket.getInputStream();
            tmpOut = mmSocket.getOutputStream();
        } catch (IOException e) {
            throw new BT_exception("Stream ERROR: " + e.getMessage());
        }
        //---
        inputStream = tmpIn;
        outputStream = tmpOut;
        return true;
    }

    //---------------------------------------------------------------------------------------------
    public boolean send_data(String message) throws BT_exception {
        if (bluetooth == null) {
            throw new BT_exception(context.getString(R.string.bluetooth_not_found));
        }
        if (!bluetooth.isEnabled()) {
            throw new BT_exception(context.getString(R.string.bluetooth_off));
        }
        if (mmSocket == null) {
            throw new BT_exception("mmSocket == null");
        }
        if (!mmSocket.isConnected()) {
            throw new BT_exception("!mmSocket.isConnected()");
        }

        int bytesAvailableCount = 0;

        if (outputStream == null) {
            throw new BT_exception("outputStream not created!");
        }
        try {
            for(int n=0; n<128; n++) {
                buffer[n] = 0;
            }
            bytes = 0;
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
            throw new BT_exception("send_data ERROR: " + e.getMessage());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return true;
    }

    //---------------------------------------------------------------------------------------------
    public int get_cnt_result_bytes() {
        return bytes;
    }

    //---------------------------------------------------------------------------------------------
    public byte[] get_result() {
        return buffer;
    }

    //---------------------------------------------------------------------------------------------

}
