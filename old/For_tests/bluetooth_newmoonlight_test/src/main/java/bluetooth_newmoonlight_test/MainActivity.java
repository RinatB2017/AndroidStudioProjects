package com.example.boss.bluetooth_newmoonlight_test;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.SeekBar;
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

public class MainActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener {
    private final static int REQUEST_ENABLE_BT = 1;

    final String TAG_LOG = "myLogs";

    TextView log;

    SeekBar sb_hot;
    SeekBar sb_cold;

    Button btn_scan;

    TextView tv_hot;
    TextView tv_cold;

    //---
    byte[][] leds = new byte[6][6];
    {
        for (int y=0; y<6; y++)
        {
            for(int x=0; x<6; x++)
            {
                leds[x][y] = 0;
            }
        }
    }
    int[][] leds_arr = {
            {0x2112, 0x2255, 0x4223, 0x4314, 0x5415, 0x2001},
            {0x4151, 0x3245, 0x5213, 0x5334, 0x0510, 0x4011},
            {0x3102, 0x2535, 0x0333, 0x0424, 0x4400, 0x3050}};
    //---

    CheckBox btn_0_0;
    CheckBox btn_0_1;
    CheckBox btn_0_2;
    CheckBox btn_0_3;
    CheckBox btn_0_4;
    CheckBox btn_0_5;

    CheckBox btn_1_0;
    CheckBox btn_1_1;
    CheckBox btn_1_2;
    CheckBox btn_1_3;
    CheckBox btn_1_4;
    CheckBox btn_1_5;

    CheckBox btn_2_0;
    CheckBox btn_2_1;
    CheckBox btn_2_2;
    CheckBox btn_2_3;
    CheckBox btn_2_4;
    CheckBox btn_2_5;

    BluetoothAdapter bluetooth;
    BluetoothSocket btSocket;
    private BroadcastReceiver discoveryDevicesReceiver;
    private BroadcastReceiver discoveryFinishedReceiver;
    private final List<BluetoothDevice> discoveredDevices = new ArrayList<BluetoothDevice>();
    private ProgressDialog progressDialog;

    //private static final UUID MY_UUID = UUID.fromString("50001101-0000-1000-8000-00809F9B34FB");
    private static final UUID MY_UUID = UUID.fromString("00000001-0001-0001-0001-000000000001");
    private static final String DEVICE_NAME = "HC-05";
    private static InputStream inputStream;
    private static OutputStream outputStream;

    BluetoothDevice r_device;
    BluetoothSocket tmp = null;
    BluetoothSocket mmSocket = null;
    InputStream tmpIn = null;
    OutputStream tmpOut = null;

    ModBus modbus;
    //---------------------------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        create_widgets();
        create_bluetooth();

        Log.d(TAG_LOG, "onCreate");
    }
    //---------------------------------------------------------------------------------------------
    public void create_widgets()
    {
        log = (TextView) findViewById(R.id.log);

        sb_hot = (SeekBar) findViewById(R.id.sb_hot);
        sb_cold = (SeekBar) findViewById(R.id.sb_cold);

        btn_scan = (Button)findViewById(R.id.btn_scan);

        btn_0_0 = (CheckBox) findViewById(R.id.btn_0_0);
        btn_0_1 = (CheckBox) findViewById(R.id.btn_0_1);
        btn_0_2 = (CheckBox) findViewById(R.id.btn_0_2);
        btn_0_3 = (CheckBox) findViewById(R.id.btn_0_3);
        btn_0_4 = (CheckBox) findViewById(R.id.btn_0_4);
        btn_0_5 = (CheckBox) findViewById(R.id.btn_0_5);

        btn_1_0 = (CheckBox) findViewById(R.id.btn_1_0);
        btn_1_1 = (CheckBox) findViewById(R.id.btn_1_1);
        btn_1_2 = (CheckBox) findViewById(R.id.btn_1_2);
        btn_1_3 = (CheckBox) findViewById(R.id.btn_1_3);
        btn_1_4 = (CheckBox) findViewById(R.id.btn_1_4);
        btn_1_5 = (CheckBox) findViewById(R.id.btn_1_5);

        btn_2_0 = (CheckBox) findViewById(R.id.btn_2_0);
        btn_2_1 = (CheckBox) findViewById(R.id.btn_2_1);
        btn_2_2 = (CheckBox) findViewById(R.id.btn_2_2);
        btn_2_3 = (CheckBox) findViewById(R.id.btn_2_3);
        btn_2_4 = (CheckBox) findViewById(R.id.btn_2_4);
        btn_2_5 = (CheckBox) findViewById(R.id.btn_2_5);

        tv_hot = (TextView) findViewById(R.id.tv_hot);
        tv_cold = (TextView) findViewById(R.id.tv_cold);

        sb_hot.setOnSeekBarChangeListener(this);
        sb_cold.setOnSeekBarChangeListener(this);

        tv_hot.setText("" + sb_hot.getProgress());
        tv_cold.setText("" + sb_cold.getProgress());

        /*
        for(int i = 0; i<arr.length; i++) {
            for (int j = 0; j < arr[i].length; j++) {
                log.append(" 0x" + Integer.toHexString(arr[i][j]));
            }
            log.append("\n");
        }
        */
    }
    //---------------------------------------------------------------------------------------------
    public void create_bluetooth()
    {
        bluetooth = BluetoothAdapter.getDefaultAdapter();

        block_interface(true);

        modbus = new ModBus();

        if(bluetooth == null)
        {
            log.append("Bluetooth модуль не найден\n");
            return;
        }
        if(bluetooth.isEnabled() == false)
        {
            // Bluetooth выключен. Предложим пользователю включить его.
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        /*
        boolean ok = scan();
        if(ok)
        {
            block_interface(true);
        }
        */
    }
    //---------------------------------------------------------------------------------------------
    public void set_led_data(int value)
    {
        byte[] bytes = new byte[2];
        bytes[0] = (byte) ((value >> 8) & 0xFF);
        bytes[1] = (byte) (value & 0xFF);

        byte a = (byte) ((bytes[0] >> 4) & 0x0F);
        byte b = (byte) (bytes[0] & 0x0F);
        byte c = (byte) ((bytes[1] >> 4) & 0x0F);
        byte d = (byte) (bytes[1] & 0x0F);

        log.append("0x" + Integer.toHexString(value).toUpperCase() + " = ");
        log.append("0x" + Integer.toHexString(a) + " ");
        log.append("0x" + Integer.toHexString(b) + " ");
        log.append("0x" + Integer.toHexString(c) + " ");
        log.append("0x" + Integer.toHexString(d) + "\n");

        int value_hot = sb_hot.getProgress();
        int value_cold = sb_cold.getProgress();

        leds[a][b] = (byte)value_cold;
        leds[c][d] = (byte)value_hot;
    }
    //---------------------------------------------------------------------------------------------
    public void check_button() {
        if(btn_0_0.isChecked()) {   set_led_data(leds_arr[0][0]);   log.append("btn_0_0\n");    }
        if(btn_0_1.isChecked()) {   set_led_data(leds_arr[0][1]);   log.append("btn_0_1\n");    }
        if(btn_0_2.isChecked()) {   set_led_data(leds_arr[0][2]);   log.append("btn_0_2\n");    }
        if(btn_0_3.isChecked()) {   set_led_data(leds_arr[0][3]);   log.append("btn_0_3\n");    }
        if(btn_0_4.isChecked()) {   set_led_data(leds_arr[0][4]);   log.append("btn_0_4\n");    }
        if(btn_0_5.isChecked()) {   set_led_data(leds_arr[0][5]);   log.append("btn_0_5\n");    }

        if(btn_1_0.isChecked()) {   set_led_data(leds_arr[1][0]);   log.append("btn_1_0\n");    }
        if(btn_1_1.isChecked()) {   set_led_data(leds_arr[1][1]);   log.append("btn_1_1\n");    }
        if(btn_1_2.isChecked()) {   set_led_data(leds_arr[1][2]);   log.append("btn_1_2\n");    }
        if(btn_1_3.isChecked()) {   set_led_data(leds_arr[1][3]);   log.append("btn_1_3\n");    }
        if(btn_1_4.isChecked()) {   set_led_data(leds_arr[1][4]);   log.append("btn_1_4\n");    }
        if(btn_1_5.isChecked()) {   set_led_data(leds_arr[1][5]);   log.append("btn_1_5\n");    }

        if(btn_2_0.isChecked()) {   set_led_data(leds_arr[2][0]);   log.append("btn_2_0\n");    }
        if(btn_2_1.isChecked()) {   set_led_data(leds_arr[2][1]);   log.append("btn_2_1\n");    }
        if(btn_2_2.isChecked()) {   set_led_data(leds_arr[2][2]);   log.append("btn_2_2\n");    }
        if(btn_2_3.isChecked()) {   set_led_data(leds_arr[2][3]);   log.append("btn_2_3\n");    }
        if(btn_2_4.isChecked()) {   set_led_data(leds_arr[2][4]);   log.append("btn_2_4\n");    }
        if(btn_2_5.isChecked()) {   set_led_data(leds_arr[2][5]);   log.append("btn_2_5\n");    }
    }
    //---------------------------------------------------------------------------------------------
    public void send_command()
    {
        check_button();

        ModBus modbus = new ModBus();
        modbus.set_address(0);
        modbus.set_command(1);
        modbus.set_data(get_data());

        send_modbus_data(modbus.get_string());
    }
    //---------------------------------------------------------------------------------------------
    @Override
    public void onProgressChanged(SeekBar seekBar,
                                  int progress,
                                  boolean fromUser) {
        // TODO Auto-generated method stub
        //log.append("onProgressChanged\n");
        send_command();
    }
    //---------------------------------------------------------------------------------------------
    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        // TODO Auto-generated method stub
        //log.append("onStartTrackingTouch\n");
    }
    //---------------------------------------------------------------------------------------------
    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        // TODO Auto-generated method stub
        //log.append("onStopTrackingTouch\n");
        if(seekBar == sb_hot)
        {
            tv_hot.setText("" + seekBar.getProgress());
        }
        if(seekBar == sb_cold)
        {
            tv_cold.setText("" + seekBar.getProgress());
        }
        send_command();
    }
    //---------------------------------------------------------------------------------------------
    public boolean connect_remote_device(String MAC_address)
    {
        if(MAC_address.isEmpty())
        {
            log.append("MAC_address is empty!\n");
            return false;
        }

        r_device = bluetooth.getRemoteDevice(MAC_address);
        //---
        try {
            tmp = r_device.createRfcommSocketToServiceRecord(MY_UUID);
            Method m = r_device.getClass().getMethod("createRfcommSocket", new Class[]{int.class});
            tmp = (BluetoothSocket) m.invoke(r_device, 1);
        } catch (IOException e) {
            log.append("create ERROR: " +e.getMessage() + "\n");
            return false;
        } catch (NoSuchMethodException e)
        {
            log.append("create ERROR: " +e.getMessage() + "\n");
            return false;
        } catch (IllegalAccessException e)
        {
            log.append("create ERROR: " +e.getMessage() + "\n");
            return false;
        } catch (InvocationTargetException e)
        {
            log.append("create ERROR: " +e.getMessage() + "\n");
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
                log.append("Stream ERROR: Couldn't establish Bluetooth connection!\n");
                return false;
            }
        }
        //---
        try {
            tmpIn  = mmSocket.getInputStream();
            tmpOut = mmSocket.getOutputStream();
        } catch (IOException e) {
            log.append("Stream ERROR: " +e.getMessage() + "\n");
            return false;
        }
        //---
        inputStream = tmpIn;
        outputStream = tmpOut;
        log.append("OK\n");
        return true;
    }
    //---------------------------------------------------------------------------------------------
    private void block_interface(boolean state)
    {
        //btn_scan.setEnabled(!state);
    }
    //---------------------------------------------------------------------------------------------
    public void scan(View view) {
        scan();
    }
    //---------------------------------------------------------------------------------------------
    public boolean scan()
    {
        if(bluetooth == null)
        {
            log.append("Bluetooth модуль не найден\n");
            return false;
        }
        if(bluetooth.isEnabled() == false)
        {
            log.append("Bluetooth выключен\n");
            block_interface(false);
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
                                log.append("[" + DEVICE_NAME + "] FOUND\n");
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
                    log.append("Scan end!\n");
                    if (progressDialog != null) progressDialog.dismiss();
                    unregisterReceiver(discoveryFinishedReceiver);
                    unregisterReceiver(discoveryDevicesReceiver);

                    block_interface(true);
                }
            };
        }

        registerReceiver(discoveryDevicesReceiver,  new IntentFilter(BluetoothDevice.ACTION_FOUND));
        registerReceiver(discoveryFinishedReceiver, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED));

        progressDialog = ProgressDialog.show(this, "Поиск устройств", "Подождите...");

        bluetooth.startDiscovery();
        log.append("Scan begin...\n");
        return true;
    }
    //---------------------------------------------------------------------------------------------
    public boolean send_modbus_data(String message)
    {
        byte[] buffer = new byte[128];  // buffer store for the stream
        int bytes = 0; // bytes returned from read()
        int bytesAvailableCount = 0;

        if(outputStream == null)
        {
            log.append("outputStream not created!\n");
            return false;
        }
        try {
            outputStream.write(message.getBytes());
            do {
                bytesAvailableCount = inputStream.available();
                if(bytesAvailableCount > 0) {
                    bytes = inputStream.read(buffer);
                    log.append("read " + bytes + " bytes\n");
                    String temp = new String(buffer);
                    log.append(temp);
                }
            } while(bytesAvailableCount > 0);
        } catch (IOException e) {
            log.append("send_data ERROR: " + e.getMessage() + "\n");
            return false;
        }
        return true;
    }
    //---------------------------------------------------------------------------------------------
    public ByteArrayOutputStream get_data()
    {
        ByteArrayOutputStream data;
        data = new ByteArrayOutputStream();
        for (int y=0; y<6; y++)
        {
            for(int x=0; x<6; x++)
            {
                data.write(leds[x][y]);
            }
        }
        return data;
    }
    //---------------------------------------------------------------------------------------------
    public void test(View view) {
        send_command();
    }
    //---------------------------------------------------------------------------------------------
    public void set_all(View view) {
        boolean state = true;

        btn_0_0.setChecked(state);
        btn_0_1.setChecked(state);
        btn_0_2.setChecked(state);
        btn_0_3.setChecked(state);
        btn_0_4.setChecked(state);
        btn_0_5.setChecked(state);

        btn_1_0.setChecked(state);
        btn_1_1.setChecked(state);
        btn_1_2.setChecked(state);
        btn_1_3.setChecked(state);
        btn_1_4.setChecked(state);
        btn_1_5.setChecked(state);

        btn_2_0.setChecked(state);
        btn_2_1.setChecked(state);
        btn_2_2.setChecked(state);
        btn_2_3.setChecked(state);
        btn_2_4.setChecked(state);
        btn_2_5.setChecked(state);
    }
    //---------------------------------------------------------------------------------------------
    public void clr_all(View view) {
        boolean state = false;

        btn_0_0.setChecked(state);
        btn_0_1.setChecked(state);
        btn_0_2.setChecked(state);
        btn_0_3.setChecked(state);
        btn_0_4.setChecked(state);
        btn_0_5.setChecked(state);

        btn_1_0.setChecked(state);
        btn_1_1.setChecked(state);
        btn_1_2.setChecked(state);
        btn_1_3.setChecked(state);
        btn_1_4.setChecked(state);
        btn_1_5.setChecked(state);

        btn_2_0.setChecked(state);
        btn_2_1.setChecked(state);
        btn_2_2.setChecked(state);
        btn_2_3.setChecked(state);
        btn_2_4.setChecked(state);
        btn_2_5.setChecked(state);
    }
    //---------------------------------------------------------------------------------------------
}
