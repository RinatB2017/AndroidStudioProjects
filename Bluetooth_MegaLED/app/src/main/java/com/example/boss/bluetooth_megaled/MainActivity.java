package com.example.boss.bluetooth_megaled;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    private TextView tv;
    private ListView lv;
    private final static int REQUEST_ENABLE_BT = 1;

    final String TAG_LOG = "INFO";

    Button btn_scan;
    Button btn_1;
    Button btn_2;
    Button btn_3;
    Button btn_4;

    BluetoothAdapter bluetooth;
    BluetoothSocket btSocket;
    private BroadcastReceiver discoveryDevicesReceiver;
    private BroadcastReceiver discoveryFinishedReceiver;
    private final List<BluetoothDevice> discoveredDevices = new ArrayList<BluetoothDevice>();
    private ProgressDialog progressDialog;

    //private static final UUID MY_UUID = UUID.fromString("50001101-0000-1000-8000-00809F9B34FB");
    private static final UUID MY_UUID = UUID.fromString("00000001-0001-0001-0001-000000000001");
    private static final String DEVICE_NAME = "MEGALED";
    private static InputStream  inputStream;
    private static OutputStream outputStream;

    BluetoothDevice r_device;
    BluetoothSocket tmp = null;
    BluetoothSocket mmSocket = null;
    InputStream tmpIn = null;
    OutputStream tmpOut = null;

    ModBus modbus;

    //---------------------------------------------------------------------------------------------
    //--------------------------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_scan = (Button)findViewById(R.id.btn_scan);

        btn_1 = (Button)findViewById(R.id.btn_1);
        btn_2 = (Button)findViewById(R.id.btn_2);
        btn_3 = (Button)findViewById(R.id.btn_3);
        btn_4 = (Button)findViewById(R.id.btn_4);

        tv = (TextView) findViewById(R.id.editText);
        bluetooth = BluetoothAdapter.getDefaultAdapter();

        block_interface(true);

        modbus = new ModBus();

        if(bluetooth == null)
        {
            tv.append("Bluetooth модуль не найден\n");
            return;
        }
        if(!bluetooth.isEnabled())
        {
            // Bluetooth выключен. Предложим пользователю включить его.
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        boolean ok = scan();
        if(ok) btn_scan.setEnabled(true);
        Log.d(TAG_LOG, "onCreate");
    }
    //--------------------------------------------------------------------------------------------
    @Override
    protected void onStart() {
        super.onStart();
        // The activity is about to become visible.
        Log.d(TAG_LOG, "onStart");
    }
    //--------------------------------------------------------------------------------------------
    @Override
    protected void onResume() {
        super.onResume();
        // The activity has become visible (it is now "resumed").
        Log.d(TAG_LOG, "onResume");
    }
    //--------------------------------------------------------------------------------------------
    @Override
    protected void onPause() {
        super.onPause();
        // Another activity is taking focus (this activity is about to be "paused").
        Log.d(TAG_LOG, "onPause");
    }
    //--------------------------------------------------------------------------------------------
    @Override
    protected void onStop() {
        super.onStop();
        // The activity is no longer visible (it is now "stopped")
        Log.d(TAG_LOG, "onStop");
    }
    //--------------------------------------------------------------------------------------------
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // The activity is about to be destroyed.
        if(bluetooth != null) {
            if (bluetooth.isEnabled()) {
                tv.append("disconnect...\n");
                //bluetooth.disable();
            }
        }
        Log.d(TAG_LOG, "onDestroy");
    }
    //--------------------------------------------------------------------------------------------
    //---------------------------------------------------------------------------------------------
    private void block_interface(boolean state)
    {
        btn_scan.setEnabled(!state);

        btn_1.setEnabled(!state);
        btn_2.setEnabled(!state);
        btn_3.setEnabled(!state);
        btn_4.setEnabled(!state);
    }
    //---------------------------------------------------------------------------------------------
    public boolean scan()
    {
        if(bluetooth == null)
        {
            tv.append("Bluetooth модуль не найден\n");
            return false;
        }
        if(!bluetooth.isEnabled())
        {
            tv.append("Bluetooth выключен\n");
            btn_scan.setEnabled(true);
            return false;
        }

        discoveredDevices.clear();

        if (discoveryDevicesReceiver == null) {
            discoveryDevicesReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    String action = intent.getAction();

                    if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                        BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                        if (!discoveredDevices.contains(device))
                        {
                            if(device.getName().equals(DEVICE_NAME))
                            {
                                tv.append("[" + DEVICE_NAME + "] FOUND\n");
                                boolean ok = connect_remote_device(device.getAddress());
                                block_interface(!ok);
                            }
                            discoveredDevices.add(device);
                        }
                    }
                }
            };
        }

        if (discoveryFinishedReceiver == null) {
            discoveryFinishedReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    tv.append("Scan end!\n");
                    if (progressDialog != null) progressDialog.dismiss();
                    unregisterReceiver(discoveryFinishedReceiver);
                    unregisterReceiver(discoveryDevicesReceiver);

                    btn_scan.setEnabled(false);
                    btn_1.setEnabled(true);
                    btn_2.setEnabled(true);
                    btn_3.setEnabled(true);
                    btn_4.setEnabled(true);
                }
            };
        }

        registerReceiver(discoveryDevicesReceiver,  new IntentFilter(BluetoothDevice.ACTION_FOUND));
        registerReceiver(discoveryFinishedReceiver, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED));

        progressDialog = ProgressDialog.show(this, "Поиск устройств", "Подождите...");

        bluetooth.startDiscovery();
        tv.append("Scan begin...\n");
        return true;
    }
    //---------------------------------------------------------------------------------------------
    public void scan(View view) {
        scan();
    }
    //---------------------------------------------------------------------------------------------
    public boolean connect_remote_device(String MAC_address)
    {
        if(MAC_address.isEmpty())
        {
            tv.append("MAC_address is empty!\n");
            return false;
        }

        r_device = bluetooth.getRemoteDevice(MAC_address);
        //---
        try {
            tmp = r_device.createRfcommSocketToServiceRecord(MY_UUID);
            Method m = r_device.getClass().getMethod("createRfcommSocket", new Class[]{int.class});
            tmp = (BluetoothSocket) m.invoke(r_device, 1);
        } catch (IOException e) {
            tv.append("create ERROR: " +e.getMessage() + "\n");
            return false;
        } catch (NoSuchMethodException e)
        {
            tv.append("create ERROR: " +e.getMessage() + "\n");
            return false;
        } catch (IllegalAccessException e)
        {
            tv.append("create ERROR: " +e.getMessage() + "\n");
            return false;
        } catch (InvocationTargetException e)
        {
            tv.append("create ERROR: " +e.getMessage() + "\n");
            return false;
        }
        //---
        mmSocket = tmp;
        try {
            mmSocket.connect();
        } catch (IOException e) {
            try {
                mmSocket = (BluetoothSocket) r_device.getClass().getMethod("createRfcommSocket", new Class[] {int.class}).invoke(r_device,1);
                mmSocket.connect();
            }
            catch (Exception e2) {
                tv.append("Stream ERROR: Couldn't establish Bluetooth connection!\n");
                return false;
            }
        }
        //---
        try {
            tmpIn  = mmSocket.getInputStream();
            tmpOut = mmSocket.getOutputStream();
        } catch (IOException e) {
            tv.append("Stream ERROR: " +e.getMessage() + "\n");
            return false;
        }
        //---
        inputStream = tmpIn;
        outputStream = tmpOut;
        tv.append("OK\n");
        return true;
    }
    //---------------------------------------------------------------------------------------------
    public ByteArrayOutputStream get_modbus_data_cmd_1()
    {
        ByteArrayOutputStream ba = new ByteArrayOutputStream();
        String[] buf = {
                "RRRRRRRR",
                "RRRRRRRR",
                "RRRRRRRR",
                "RRRRRRRR",
                "RRRRRRRR",
                "RRRRRRRR" };
        for(int y=0; y<buf.length; y++)
        {
            for(int x=0; x<buf[0].length(); x++)
            {
                ba.write(buf[y].getBytes()[x]);
            }
        }
        return ba;
    }
    //---------------------------------------------------------------------------------------------
    public ByteArrayOutputStream get_modbus_data_cmd_2()
    {
        ByteArrayOutputStream ba = new ByteArrayOutputStream();
        String[] buf = {
                "GGGGGGGG",
                "GGGGGGGG",
                "GGGGGGGG",
                "GGGGGGGG",
                "GGGGGGGG",
                "GGGGGGGG" };
        for(int y=0; y<buf.length; y++)
        {
            for(int x=0; x<buf[0].length(); x++)
            {
                ba.write(buf[y].getBytes()[x]);
            }
        }
        return ba;
    }
    //---------------------------------------------------------------------------------------------
    public ByteArrayOutputStream get_modbus_data_cmd_3()
    {
        ByteArrayOutputStream ba = new ByteArrayOutputStream();
        String[] buf = {
                "BBBBBBBB",
                "BBBBBBBB",
                "BBBBBBBB",
                "BBBBBBBB",
                "BBBBBBBB",
                "BBBBBBBB" };
        for(int y=0; y<buf.length; y++)
        {
            for(int x=0; x<buf[0].length(); x++)
            {
                ba.write(buf[y].getBytes()[x]);
            }
        }
        return ba;
    }
    //---------------------------------------------------------------------------------------------
    public ByteArrayOutputStream get_modbus_data_cmd_4()
    {
        ByteArrayOutputStream ba = new ByteArrayOutputStream();
        String[] buf = {
                "00000000",
                "00000000",
                "00000000",
                "00000000",
                "00000000",
                "00000000" };
        for(int y=0; y<buf.length; y++)
        {
            for(int x=0; x<buf[0].length(); x++)
            {
                ba.write(buf[y].getBytes()[x]);
            }
        }
        return ba;
    }
    //---------------------------------------------------------------------------------------------
    public void send_cmd_1(View view)
    {
        ModBus modbus = new ModBus();
        modbus.set_address(1);
        modbus.set_command(1);
        modbus.set_data(get_modbus_data_cmd_1());
        send_modbus_data(modbus.get_string());
    }
    //---------------------------------------------------------------------------------------------
    public void send_cmd_2(View view)
    {
        ModBus modbus = new ModBus();
        modbus.set_address(1);
        modbus.set_command(1);
        modbus.set_data(get_modbus_data_cmd_2());
        send_modbus_data(modbus.get_string());
    }
    //---------------------------------------------------------------------------------------------
    public void send_cmd_3(View view)
    {
        ModBus modbus = new ModBus();
        modbus.set_address(1);
        modbus.set_command(1);
        modbus.set_data(get_modbus_data_cmd_3());
        send_modbus_data(modbus.get_string());
    }
    //---------------------------------------------------------------------------------------------
    public void send_cmd_4(View view)
    {
        ModBus modbus = new ModBus();
        modbus.set_address(1);
        modbus.set_command(1);
        modbus.set_data(get_modbus_data_cmd_4());
        send_modbus_data(modbus.get_string());
    }
    //---------------------------------------------------------------------------------------------
    public boolean send_modbus_data(String message)
    {
        byte[] buffer = new byte[128];  // buffer store for the stream
        int bytes = 0; // bytes returned from read()
        int bytesAvailableCount = 0;

        if(outputStream == null)
        {
            tv.append("outputStream not created!\n");
            return false;
        }
        try {
            outputStream.write(message.getBytes());
            do {
                bytesAvailableCount = inputStream.available();
                if(bytesAvailableCount > 0) {
                    bytes = inputStream.read(buffer);
                    tv.append("read " + bytes + " bytes\n");
                    String temp = new String(buffer);
                    tv.append(temp);
                }
            } while(bytesAvailableCount > 0);
        } catch (IOException e) {
            tv.append("send_data ERROR: " + e.getMessage() + "\n");
            return false;
        }
        return true;
    }
    //---------------------------------------------------------------------------------------------
}
