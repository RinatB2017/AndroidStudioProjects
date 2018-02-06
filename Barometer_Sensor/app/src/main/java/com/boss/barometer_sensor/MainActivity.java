package com.boss.barometer_sensor;

    import android.Manifest;
    import android.app.ProgressDialog;
    import android.bluetooth.BluetoothAdapter;
    import android.bluetooth.BluetoothDevice;
    import android.bluetooth.BluetoothSocket;
    import android.content.BroadcastReceiver;
    import android.content.Intent;
    import android.content.pm.ActivityInfo;
    import android.content.pm.PackageManager;
    import android.os.Bundle;
    import android.os.Handler;
    import android.support.v4.app.ActivityCompat;
    import android.support.v4.content.ContextCompat;
    import android.support.v7.app.AppCompatActivity;
    import android.util.Log;
    import android.view.Menu;
    import android.view.MenuItem;
    import android.view.View;
    import android.view.Window;
    import android.view.WindowManager;
    import android.widget.TextView;

    import java.io.IOException;
    import java.io.InputStream;
    import java.io.OutputStream;
    import java.lang.reflect.InvocationTargetException;
    import java.lang.reflect.Method;
    import java.util.ArrayList;
    import java.util.List;
    import java.util.UUID;

// если на смартфоне стоит Android 6.0, то надо поставить в свойствах app
// Flawors Target SDK Version API22
// иначе bluetooth не будет находить устройства

public class MainActivity extends AppCompatActivity {

    private static final int RECORD_REQUEST_CODE = 101;
    private final static int REQUEST_ENABLE_BT = 1;
    final String LOG_TAG = "States";

    //---
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

    private Handler handler = new Handler();

    TextView logView;
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
                scan();
                break;

            case R.id.action_settings_disconnect:
                if(mmSocket.isConnected()) {
                    try {
                        mmSocket.close();
                        logging("Соединение разорвано");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;

            case R.id.action_settings_options:
                Intent intent = new Intent(this, OptionsActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                if(intent != null) {
                    startActivity(intent);
                }
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
    void max_screen() {
        // займем весь экран
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION           // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN                // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE);
    }

    //---------------------------------------------------------------------------------------------
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        logView = (TextView)findViewById(R.id.logView);

        //TODO временный костыль
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //max_screen();
        create_bluetooth();

        requestPermission(Manifest.permission.ACCESS_FINE_LOCATION, RECORD_REQUEST_CODE);

        //TODO
        scan();

        //load_states();
    }
    //---------------------------------------------------------------------------------------------
    public void logging(String text) {
        logView.append(text + "\n");
    }
    //---------------------------------------------------------------------------------------------
    public void create_bluetooth()
    {
        bluetooth = BluetoothAdapter.getDefaultAdapter();

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
    public boolean scan()
    {
        if(bluetooth == null)
        {
            logging("Bluetooth модуль не найден");
            return false;
        }
        if(!bluetooth.isEnabled())
        {
            logging("Bluetooth выключен");
            return false;
        }

        boolean ok = connect_remote_device(BluetoothName.get_mac(getApplicationContext()));
        if(ok)
            logging("Соединение установлено");
        else
            logging("Соединение не удалось");

        return ok;
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
            logging("create ERROR: " + e.getMessage());
            return false;
        } catch (NoSuchMethodException e)
        {
            logging("create ERROR: " + e.getMessage());
            return false;
        } catch (IllegalAccessException e)
        {
            logging("create ERROR: " + e.getMessage());
            return false;
        } catch (InvocationTargetException e)
        {
            logging("create ERROR: " + e.getMessage());
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
            logging("Stream ERROR: " + e.getMessage());
            return false;
        }
        //---
        inputStream = tmpIn;
        outputStream = tmpOut;

        BluetoothSocketListener bsl = new BluetoothSocketListener(mmSocket, handler, logView);
        Thread messageListener = new Thread(bsl);
        messageListener.start();

        return true;
    }
    //---
    private class BluetoothSocketListener implements Runnable {
        private BluetoothSocket socket; private TextView textView; private Handler handler;
        public BluetoothSocketListener(BluetoothSocket socket,
                                       Handler handler, TextView textView) {
            this.socket = socket; this.textView = textView; this.handler = handler;
        }

        public void run() {
            int bufferSize = 1024;
            byte[] buffer = new byte[bufferSize];

            try {
                InputStream instream = socket.getInputStream();
                int bytesRead = -1;
                String message = "";
                while (true) {
                    message = "";
                    bytesRead = instream.read(buffer);
                    if (bytesRead != -1) {
                        while ((bytesRead==bufferSize) && (buffer[bufferSize-1] != 0)) {
                            message = message + new String(buffer, 0, bytesRead);
                            bytesRead = instream.read(buffer);
                        }
                        message = message + new String(buffer, 0, bytesRead - 1);
                        handler.post(new MessagePoster(textView, message));
                        socket.getInputStream();
                    }
                }
            } catch (IOException e) {
                Log.d(LOG_TAG, e.getMessage());
            }
        }
    }
    //---------------------------------------------------------------------------------------------
    @Override
    protected void onStop() {
        super.onStop();

        //save_states();
    }
    //---------------------------------------------------------------------------------------------
}
