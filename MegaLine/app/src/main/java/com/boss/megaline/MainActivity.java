package com.boss.megaline;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity
        implements SeekBar.OnSeekBarChangeListener {

    private final static int REQUEST_ENABLE_BT = 1;
    final String LOG_TAG = "States";

    SeekBar sb_len_line;
    SeekBar sb_len_pause;
    SeekBar sb_delay_ms;

    TextView tv_value_len_line;
    TextView tv_value_len_pause;
    TextView tv_value_delay_ms;

    SeekBar sb_color_R;
    SeekBar sb_color_G;
    SeekBar sb_color_B;

    TextView tv_value_color_R;
    TextView tv_value_color_G;
    TextView tv_value_color_B;

    TextView logView;

    //---------------------------------------------------------------------------------------------
    BluetoothAdapter bluetooth;
    BluetoothSocket btSocket;
    private BroadcastReceiver discoveryDevicesReceiver;
    private BroadcastReceiver discoveryFinishedReceiver;
    private final List<BluetoothDevice> discoveredDevices = new ArrayList<BluetoothDevice>();
    private ProgressDialog progressDialog;

    private static final UUID MY_UUID = UUID.fromString("00000001-0001-0001-0001-000000000001");
    //private static final String DEVICE_NAME = "20:15:10:19:62:52";
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
    public SharedPreferences mSet;
    public static final String NAME_PREFERENCES = "mysetting";
    public static final String INT_SB_LEN_LINE = "sb_len_line";
    public static final String INT_SB_LEN_PAUSE = "sb_len_pause";
    public static final String INT_SB_DELAY_MS = "sb_delay_ms";
    public static final String INT_SB_COLOR_R = "sb_color_R";
    public static final String INT_SB_COLOR_G = "sb_color_G";
    public static final String INT_SB_COLOR_B = "sb_color_B";
    //---------------------------------------------------------------------------------------------
    void state_save() {
        mSet = getSharedPreferences(NAME_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mSet.edit();
        editor.putInt(INT_SB_LEN_LINE, sb_len_line.getProgress());
        editor.putInt(INT_SB_LEN_PAUSE, sb_len_pause.getProgress());
        editor.putInt(INT_SB_DELAY_MS, sb_delay_ms.getProgress());
        editor.putInt(INT_SB_COLOR_R, sb_color_R.getProgress());
        editor.putInt(INT_SB_COLOR_G, sb_color_G.getProgress());
        editor.putInt(INT_SB_COLOR_B, sb_color_B.getProgress());
        editor.commit();
    }
    //---------------------------------------------------------------------------------------------
    void state_restore() {
        mSet = getSharedPreferences(NAME_PREFERENCES, Context.MODE_PRIVATE);
        if(mSet.contains(INT_SB_LEN_LINE)) {
            sb_len_line.setProgress(mSet.getInt(INT_SB_LEN_LINE, 0));
            tv_value_len_line.setText(String.valueOf(sb_len_line.getProgress()));
        }
        if(mSet.contains(INT_SB_LEN_PAUSE)) {
            sb_len_pause.setProgress(mSet.getInt(INT_SB_LEN_PAUSE, 0));
            tv_value_len_pause.setText(String.valueOf(sb_len_pause.getProgress()));
        }
        if(mSet.contains(INT_SB_DELAY_MS)) {
            sb_delay_ms.setProgress(mSet.getInt(INT_SB_DELAY_MS, 0));
            tv_value_delay_ms.setText(String.valueOf(sb_delay_ms.getProgress()));
        }
        if(mSet.contains(INT_SB_COLOR_R)) {
            sb_color_R.setProgress(mSet.getInt(INT_SB_COLOR_R, 0));
            tv_value_color_R.setText(String.valueOf(sb_color_R.getProgress()));
        }
        if(mSet.contains(INT_SB_COLOR_G)) {
            sb_color_G.setProgress(mSet.getInt(INT_SB_COLOR_G, 0));
            tv_value_color_G.setText(String.valueOf(sb_color_G.getProgress()));
        }
        if(mSet.contains(INT_SB_COLOR_B)) {
            sb_color_B.setProgress(mSet.getInt(INT_SB_COLOR_B, 0));
            tv_value_color_B.setText(String.valueOf(sb_color_B.getProgress()));
        }
    }
    //---------------------------------------------------------------------------------------------
    @Override
    protected void onStop() {
        super.onStop();
        state_save();
    }
    //---------------------------------------------------------------------------------------------
    private void logging(String text) {
        Log.i(LOG_TAG, text);
        logView.append(text + "\n");
    }
    //---------------------------------------------------------------------------------------------
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    //---------------------------------------------------------------------------------------------
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId())
        {
            case R.id.action_settings_scan:
                logging("===> SCAN <===");
                scan();
                break;

            case R.id.action_settings_exit:
                this.finish();
                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }    //---------------------------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        logView = (TextView)findViewById(R.id.logView);

        sb_len_line = (SeekBar) findViewById(R.id.sb_len_line);
        sb_len_pause = (SeekBar) findViewById(R.id.sb_len_pause);
        sb_delay_ms = (SeekBar) findViewById(R.id.sb_delay_ms);

        tv_value_len_line = (TextView) findViewById(R.id.tv_value_len_line);
        tv_value_len_pause = (TextView) findViewById(R.id.tv_value_len_pause);
        tv_value_delay_ms = (TextView) findViewById(R.id.tv_value_delay_ms);

        sb_color_R = (SeekBar) findViewById(R.id.sb_color_R);
        sb_color_G = (SeekBar) findViewById(R.id.sb_color_G);
        sb_color_B = (SeekBar) findViewById(R.id.sb_color_B);

        tv_value_color_R = (TextView) findViewById(R.id.tv_value_color_R);
        tv_value_color_G = (TextView) findViewById(R.id.tv_value_color_G);
        tv_value_color_B = (TextView) findViewById(R.id.tv_value_color_B);

        sb_len_line.setOnSeekBarChangeListener(this);
        sb_len_pause.setOnSeekBarChangeListener(this);
        sb_delay_ms.setOnSeekBarChangeListener(this);

        sb_color_R.setOnSeekBarChangeListener(this);
        sb_color_G.setOnSeekBarChangeListener(this);
        sb_color_B.setOnSeekBarChangeListener(this);

        sb_len_line.setMax(5);
        sb_len_pause.setMax(5);
        sb_delay_ms.setMax(1000);

        sb_color_R.setMax(0xFF);
        sb_color_G.setMax(0xFF);
        sb_color_B.setMax(0xFF);

        //TODO временный костыль
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        create_bluetooth();
        state_restore();
    }
    //---------------------------------------------------------------------------------------------
    public void create_bluetooth()
    {
        bluetooth = BluetoothAdapter.getDefaultAdapter();

        //block_interface(true);

        modbus = new ModBus();

        if(bluetooth == null)
        {
            logging("Bluetooth модуль не найден");
            return;
        }
        if(!bluetooth.isEnabled())
        {
            // Bluetooth выключен. Предложим пользователю включить его.
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }
    //---------------------------------------------------------------------------------------------
    public void send_command()
    {
        logging("send_command");

        ModBus modbus = new ModBus();
        modbus.set_address(0);
        modbus.set_command(1);

        modbus.set_len_line(sb_len_line.getProgress());
        modbus.set_len_pause(sb_len_pause.getProgress());
        modbus.set_delay_ms(sb_delay_ms.getProgress());

        modbus.set_color_R(sb_color_R.getProgress());
        modbus.set_color_G(sb_color_G.getProgress());
        modbus.set_color_B(sb_color_B.getProgress());

        send_modbus_data(modbus.get_string());
    }
    //---------------------------------------------------------------------------------------------
    public boolean scan()
    {
        logging("Scan begin!");
        if(bluetooth == null)
        {
            logging("Bluetooth модуль не найден");
            return false;
        }
        if(!bluetooth.isEnabled())
        {
            logging("Bluetooth выключен");
            block_interface(true);
            return false;
        }

        block_interface(true);
        discoveredDevices.clear();

        if (discoveryDevicesReceiver == null) {
            discoveryDevicesReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    String action = intent.getAction();

                    if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                        BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                        //BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_NAME);

                        if (!discoveredDevices.contains(device))
                        {
                            String d_name = device.getName();
                            if(d_name != null) {
                                d_name = d_name.replaceAll("\n", "");
                                logging("name = [" + d_name + "]");
                                if (d_name.contains(DEVICE_NAME)) {
                                    logging("[" + DEVICE_NAME + "] FOUND");
                                    boolean ok = connect_remote_device(device.getAddress());
                                    if(ok) {
                                        block_interface(false);
                                        discoveredDevices.add(device);
                                        bluetooth.cancelDiscovery();

                                        Toast toast = Toast.makeText(getApplicationContext(),
                                                "Найден " + DEVICE_NAME,
                                                Toast.LENGTH_SHORT);
                                        toast.show();
                                    }
                                }
                            }
                        }
                    }
                }
            };
        }

        if (discoveryFinishedReceiver == null) {
            discoveryFinishedReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    logging("Scan end!");
                    if (progressDialog != null) progressDialog.dismiss();
                    unregisterReceiver(discoveryFinishedReceiver);
                    unregisterReceiver(discoveryDevicesReceiver);
                }
            };
        }

        registerReceiver(discoveryDevicesReceiver,  new IntentFilter(BluetoothDevice.ACTION_FOUND));
        registerReceiver(discoveryFinishedReceiver, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED));

        progressDialog = ProgressDialog.show(this, "Поиск устройств", "Подождите...");

        bluetooth.startDiscovery();
        logging("Scan begin...");
        return true;
    }
    //---------------------------------------------------------------------------------------------
    private void block_interface(boolean state)
    {

    }
    //---------------------------------------------------------------------------------------------
    public boolean connect_remote_device(String MAC_address)
    {
        if(MAC_address.isEmpty())
        {
            logging("MAC_address is empty!");
            return false;
        }

        r_device = bluetooth.getRemoteDevice(MAC_address);
        //---
        try {
            tmp = r_device.createRfcommSocketToServiceRecord(MY_UUID);
            Method m = r_device.getClass().getMethod("createRfcommSocket", new Class[]{int.class});
            tmp = (BluetoothSocket) m.invoke(r_device, 1);
        } catch (IOException e) {
            logging("create ERROR: " +e.getMessage());
            return false;
        } catch (NoSuchMethodException e)
        {
            logging("create ERROR: " +e.getMessage());
            return false;
        } catch (IllegalAccessException e)
        {
            logging("create ERROR: " +e.getMessage());
            return false;
        } catch (InvocationTargetException e)
        {
            logging("create ERROR: " +e.getMessage());
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
                logging("Stream ERROR: Couldn't establish Bluetooth connection!");
                return false;
            }
        }
        //---
        try {
            tmpIn  = mmSocket.getInputStream();
            tmpOut = mmSocket.getOutputStream();
        } catch (IOException e) {
            logging("Stream ERROR: " +e.getMessage());
            return false;
        }
        //---
        inputStream = tmpIn;
        outputStream = tmpOut;
        logging("OK");
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
            logging("outputStream not created!");
            return false;
        }

        logging("send_modbus_data " + message);

        try {
            outputStream.write(message.getBytes());
            do {
                bytesAvailableCount = inputStream.available();
                if(bytesAvailableCount > 0) {
                    bytes = inputStream.read(buffer);
                }
            } while(bytesAvailableCount > 0);
        } catch (IOException e) {
            logging("send_data ERROR: " + e.getMessage());
            Toast toast = Toast.makeText(getApplicationContext(),
                    "Ошибка связи",
                    Toast.LENGTH_SHORT);
            toast.show();
            return false;
        }
        return true;
    }
    //---------------------------------------------------------------------------------------------
    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        //log.append("onStartTrackingTouch");
    }
    //---------------------------------------------------------------------------------------------
    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        if(seekBar == sb_len_line)
        {
            tv_value_len_line.setText(String.valueOf(seekBar.getProgress()));
            return;
        }
        if(seekBar == sb_len_pause)
        {
            tv_value_len_pause.setText(String.valueOf(seekBar.getProgress()));
            return;
        }
        if(seekBar == sb_delay_ms)
        {
            tv_value_delay_ms.setText(String.valueOf(seekBar.getProgress()));
            return;
        }
        if(seekBar == sb_color_R)
        {
            tv_value_color_R.setText(String.valueOf(seekBar.getProgress()));
            return;
        }
        if(seekBar == sb_color_G)
        {
            tv_value_color_G.setText(String.valueOf(seekBar.getProgress()));
            return;
        }
        if(seekBar == sb_color_B)
        {
            tv_value_color_B.setText(String.valueOf(seekBar.getProgress()));
            return;
        }
        logging("not found");
    }
    //---------------------------------------------------------------------------------------------
    @Override
    public void onProgressChanged(SeekBar seekBar,
                                  int progress,
                                  boolean fromUser) {
    }
    //---------------------------------------------------------------------------------------------
    public void onClick(View view) {
        send_command();
    }
    //---------------------------------------------------------------------------------------------
}
