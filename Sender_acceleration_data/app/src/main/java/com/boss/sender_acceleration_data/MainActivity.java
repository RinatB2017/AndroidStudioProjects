package com.boss.sender_acceleration_data;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import static java.lang.Thread.*;

public class MainActivity extends AppCompatActivity {

    private static final int RECORD_REQUEST_CODE = 101;
    private final static int REQUEST_ENABLE_BT = 1;

    final String LOG_TAG = "States";

    TextView tv_log;

    TextView tvText;
    SensorManager sensorManager;
    Sensor sensorAccel;
    Sensor sensorLinAccel;
    Sensor sensorGravity;
    Sensor sensorMagnet;

    BluetoothAdapter bluetooth;
    private BroadcastReceiver discoveryDevicesReceiver;
    private BroadcastReceiver discoveryFinishedReceiver;
    private final List<BluetoothDevice> discoveredDevices = new ArrayList<BluetoothDevice>();
    private ProgressDialog progressDialog;

    private static final UUID MY_UUID = UUID.fromString("00000001-0001-0001-0001-000000000001");
    private static InputStream inputStream;
    private static OutputStream outputStream;

    BluetoothDevice r_device;
    BluetoothSocket tmp = null;
    BluetoothSocket mmSocket = null;
    InputStream tmpIn = null;
    OutputStream tmpOut = null;

    ModBus modbus;

    StringBuilder sb = new StringBuilder();

    Handler handler;

    Timer timer;

    //----------------------------------------------------------------------------------------
    public void send_log(String text) {
        if (text == null) {
            return;
        }
        Message msg = new Message();
        msg.obj = text;
        handler.sendMessage(msg);
    }

    //---------------------------------------------------------------------------------------------
    private void block_interface(boolean state) {

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
        switch (item.getItemId()) {
            case R.id.action_settings_scan:
                device_connect();
                break;

            case R.id.action_settings_disconnect:
                device_disconnect();
                break;

            case R.id.action_settings_options:
                Intent intent = new Intent(this, OptionsActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                if (intent != null) {
                    startActivity(intent);
                }
                break;

            case R.id.action_clear_log:
                //tv_log.setText("");
                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //---------------------------------------------------------------------------------------------
    public void device_connect() {
        send_log("connect");
        Runnable runnable = new Runnable() {
            public void run() {
                if (bluetooth == null) {
                    send_log(getString(R.string.bluetooth_not_found));
                    return;
                }
                if (!bluetooth.isEnabled()) {
                    send_log(getString(R.string.bluetooth_off));
                    block_interface(true);
                    return;
                }

                boolean ok = connect_remote_device(BluetoothName.get_mac(getApplicationContext()));
                if (ok)
                    send_log(getString(R.string.connection_on));
                else
                    send_log(getString(R.string.connection_fail));
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
                        send_log(getString(R.string.connection_breaks));
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
    public void create_bluetooth() {
        bluetooth = BluetoothAdapter.getDefaultAdapter();

        block_interface(true);

        modbus = new ModBus();

        if (bluetooth == null) {
            send_log(getString(R.string.bluetooth_not_found));
            return;
        }
        if (!bluetooth.isEnabled()) {
            // Bluetooth выключен. Предложим пользователю включить его.
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
        send_log("OK");
    }

    //---------------------------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv_log = (TextView) findViewById(R.id.logView);
        tv_log.setTextColor(Color.BLACK);
        //tv_log.setTextColor(Color.WHITE);

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                String text = (String) msg.obj;
                Log.i(LOG_TAG, text);
                tv_log.append(text + "\n");
            }
        };

        //Bluetooth bt = new Bluetooth(MainActivity.this);
        //bt.device_connect();
        //bt.device_disconnect();

        tvText = (TextView) findViewById(R.id.tvText);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensorAccel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorLinAccel = sensorManager
                .getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        sensorGravity = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        sensorMagnet = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        //TODO временный костыль
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        requestPermission(Manifest.permission.ACCESS_FINE_LOCATION, RECORD_REQUEST_CODE);
        create_bluetooth();
    }

    //---------------------------------------------------------------------------------------------
    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(listener, sensorAccel,
                SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(listener, sensorLinAccel,
                SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(listener, sensorGravity,
                SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(listener, sensorMagnet,
                SensorManager.SENSOR_DELAY_NORMAL);

        timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        getDeviceOrientation();
                        showInfo();
                    }
                });
            }
        };
        timer.schedule(task, 0, 400);
    }

    //---------------------------------------------------------------------------------------------
    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(listener);
        timer.cancel();
    }

    //---------------------------------------------------------------------------------------------
    String format(float values[]) {
        return String.format("%1$.1f\t\t%2$.1f\t\t%3$.1f",
                values[0],
                values[1],
                values[2]);
    }

    //---------------------------------------------------------------------------------------------
    String format2(float values[]) {
        return String.format("%1$.1f;%2$.1f;%3$.1f",
                values[0],
                values[1],
                values[2]);
    }

    //---------------------------------------------------------------------------------------------
    public void show_answer(byte[] buffer) {
        send_log("show_answer");
    }

    //---------------------------------------------------------------------------------------------
    public boolean send_data(String message) {
        byte[] buffer = new byte[128];  // buffer store for the stream
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
                    show_answer(buffer);
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
    void show_messagebox_info(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message);
        builder.setTitle("Информация");
        builder.setIcon(android.R.drawable.ic_dialog_info);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // You don't have to do anything here if you just
                // want it dismissed when clicked
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    //---------------------------------------------------------------------------------------------
    void show_messagebox_alert(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message);
        builder.setTitle("Ошибка");
        builder.setIcon(android.R.drawable.ic_dialog_alert);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // You don't have to do anything here if you just
                // want it dismissed when clicked
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    void getDeviceOrientation() {
        float[] r = new float[9];

        SensorManager.getRotationMatrix(r, null, valuesAccel, valuesMagnet);
        SensorManager.getOrientation(r, valuesResult);

        valuesResult[0] = (float) Math.toDegrees(valuesResult[0]);
        valuesResult[1] = (float) Math.toDegrees(valuesResult[1]);
        valuesResult[2] = (float) Math.toDegrees(valuesResult[2]);

        return;
    }

    //---------------------------------------------------------------------------------------------
    void showInfo() {
        sb.setLength(0);
        sb.append("Accelerometer: " + format(valuesAccel) + "\n");
        sb.append("Orientation: " + format(valuesResult) + "\n");
        sb.append("Accel motion: " + format(valuesAccelMotion) + "\n");
        sb.append("Accel gravity : " + format(valuesAccelGravity) + "\n");
        sb.append("Lin accel : " + format(valuesLinAccel) + "\n");
        sb.append("Gravity : " + format(valuesGravity) + "\n");
        sb.append("Magnetic : " + format(valuesMagnet) + "\n");
        tvText.setText(sb);

        if (bluetooth == null) {
            send_log(getString(R.string.bluetooth_not_found));
            return;
        }
        if (!bluetooth.isEnabled()) {
            send_log(getString(R.string.bluetooth_off));
            block_interface(true);
            return;
        }
        if (mmSocket == null) {
            return;
        }
        if (!mmSocket.isConnected()) {
            return;
        }

        //---
        Runnable runnable = new Runnable() {
            public void run() {
                String temp_str = "";

                temp_str += format2(valuesAccel);
                temp_str += ";";
                temp_str += format2(valuesResult);
                temp_str += ";";
                temp_str += format2(valuesAccelMotion);
                temp_str += ";";
                temp_str += format2(valuesAccelGravity);
                temp_str += ";";
                temp_str += format2(valuesLinAccel);
                temp_str += ";";
                temp_str += format2(valuesGravity);
                temp_str += ";";
                temp_str += format2(valuesMagnet);
                temp_str += "\n";

                boolean ok = send_data(temp_str);
                if (ok) {
                    //send_log("Данные переданы.");
                    //show_messagebox_info("Данные переданы.");
                } else {
                    send_log("Ошибка соединения.");
                    //show_messagebox_alert("Ошибка соединения.");
                }
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
        //---
    }

    float[] valuesAccel = new float[3];
    float[] valuesAccelMotion = new float[3];
    float[] valuesAccelGravity = new float[3];
    float[] valuesLinAccel = new float[3];
    float[] valuesGravity = new float[3];
    float[] valuesMagnet = new float[3];
    float[] valuesResult = new float[3];

    SensorEventListener listener = new SensorEventListener() {

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }

        @Override
        public void onSensorChanged(SensorEvent event) {
            switch (event.sensor.getType()) {
                case Sensor.TYPE_ACCELEROMETER:
                    for (int i = 0; i < 3; i++) {
                        valuesAccel[i] = event.values[i];
                        valuesAccelGravity[i] = (float) (0.1 * event.values[i] + 0.9 * valuesAccelGravity[i]);
                        valuesAccelMotion[i] = event.values[i]
                                - valuesAccelGravity[i];
                    }
                    break;
                case Sensor.TYPE_LINEAR_ACCELERATION:
                    for (int i = 0; i < 3; i++) {
                        valuesLinAccel[i] = event.values[i];
                    }
                    break;
                case Sensor.TYPE_GRAVITY:
                    for (int i = 0; i < 3; i++) {
                        valuesGravity[i] = event.values[i];
                    }
                    break;
                case Sensor.TYPE_MAGNETIC_FIELD:
                    for (int i = 0; i < 3; i++) {
                        valuesMagnet[i] = event.values[i];
                    }
                    break;
            }
        }
    };

    //---------------------------------------------------------------------------------------------

}
