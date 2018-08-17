package com.boss.bluetooth_test;

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
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Spinner;
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

import static java.lang.Thread.*;

public class MainActivity extends AppCompatActivity {
    private static final int RECORD_REQUEST_CODE = 101;
    private final static int REQUEST_ENABLE_BT = 1;
    final String LOG_TAG = "States";

    TextView tv_log;

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

    public MainActivity() {
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                Bundle bundle = msg.getData();
                String log_text = bundle.getString("Log");

                tv_log.append(log_text);
                //TextView infoTextView = (TextView) findViewById(R.id.logView);
                //infoTextView.append(date);
            }
        };
    }

    //---------------------------------------------------------------------------------------------
    public void logging(String text) {
        Log.i(LOG_TAG, text);
        tv_log.append(text + "\n");
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
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    //---------------------------------------------------------------------------------------------
    public boolean connect_remote_device(String MAC_address) {
        if (MAC_address.isEmpty()) {
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
            logging("create ERROR: " + e.getMessage());
            return false;
        } catch (NoSuchMethodException e) {
            logging("create ERROR: " + e.getMessage());
            return false;
        } catch (IllegalAccessException e) {
            logging("create ERROR: " + e.getMessage());
            return false;
        } catch (InvocationTargetException e) {
            logging("create ERROR: " + e.getMessage());
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
                logging("Stream ERROR: Couldn't establish Bluetooth connection!");
                return false;
            }
        }
        //---
        try {
            tmpIn = mmSocket.getInputStream();
            tmpOut = mmSocket.getOutputStream();
        } catch (IOException e) {
            logging("Stream ERROR: " + e.getMessage());
            return false;
        }
        //---
        inputStream = tmpIn;
        outputStream = tmpOut;
        return true;
    }

    //---------------------------------------------------------------------------------------------
    public boolean device_connect() {
        logging("connect");
        if (bluetooth == null) {
            logging("Bluetooth модуль не найден");
            return false;
        }
        if (!bluetooth.isEnabled()) {
            logging("Bluetooth выключен");
            return false;
        }

        boolean ok = connect_remote_device(BluetoothName.get_mac(getApplicationContext()));
        if (ok)
            logging("Соединение установлено");
        else
            logging("Соединение не удалось");

        return ok;
    }

    //---------------------------------------------------------------------------------------------
    public void device_disconnect() {
        logging("disconnect");

        if (mmSocket.isConnected()) {
            try {
                mmSocket.close();
                logging("Соединение разорвано");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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
                tv_log.setText("");
                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //---------------------------------------------------------------------------------------------
    Handler handler;

    //---------------------------------------------------------------------------------------------
    public void create_bluetooth() {
        bluetooth = BluetoothAdapter.getDefaultAdapter();

        if (bluetooth == null) {
            logging("Bluetooth модуль не найден");
            return;
        }
        if (!bluetooth.isEnabled()) {
            // Bluetooth выключен. Предложим пользователю включить его.
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        Runnable runnable = new Runnable() {
            public void run() {
                byte[] buffer = new byte[128];  // buffer store for the stream

                while (true) {
                    synchronized (this) {
                        try {
                            int bytesAvailableCount = inputStream.available();
                            if (bytesAvailableCount > 0) {
                                int bytes = inputStream.read(buffer);

                                Message msg = handler.obtainMessage();
                                Bundle bundle = new Bundle();
                                bundle.putString("Log", "Получено " + bytes + " байтов" + "\n");
                                msg.setData(bundle);
                                handler.sendMessage(msg);
                            }

                        } catch (Exception e) {
                        }
                    }
                }
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();

        logging("OK");
    }

    //---------------------------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv_log = (TextView) findViewById(R.id.logView);
        tv_log.setTextColor(Color.BLACK);

        //TODO временный костыль
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        requestPermission(Manifest.permission.ACCESS_FINE_LOCATION, RECORD_REQUEST_CODE);
        create_bluetooth();
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

    //---------------------------------------------------------------------------------------------
}
