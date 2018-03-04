package boss.armor;

import android.Manifest;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity
        implements SeekBar.OnSeekBarChangeListener
{
    private static final int RECORD_REQUEST_CODE = 101;
    private final static int REQUEST_ENABLE_BT = 1;
    final String LOG_TAG = "States";

    TextView tv_log;

    Button btn_1;
    Button btn_2;
    Button btn_3;
    Button btn_4;
    Button btn_5;

    BluetoothAdapter bluetooth;
    private BroadcastReceiver discoveryDevicesReceiver;
    private BroadcastReceiver discoveryFinishedReceiver;
    private final List<BluetoothDevice> discoveredDevices = new ArrayList<BluetoothDevice>();
    private ProgressDialog progressDialog;

    private static final UUID MY_UUID = UUID.fromString("00000001-0001-0001-0001-000000000001");
    //private static final String DEVICE_NAME = "20:15:10:19:62:52";
    //private static final String DEVICE_NAME = "HC-05";
    private static InputStream inputStream;
    private static OutputStream outputStream;

    BluetoothDevice r_device;
    BluetoothSocket tmp = null;
    BluetoothSocket mmSocket = null;
    InputStream tmpIn = null;
    OutputStream tmpOut = null;

    ModBus modbus;

    SeekBar sb_delay_ms;
    SeekBar sb_brightness;
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
    public void onStartTrackingTouch(SeekBar seekBar) {
    }
    //---------------------------------------------------------------------------------------------
    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }
    //---------------------------------------------------------------------------------------------
    @Override
    public void onProgressChanged(SeekBar seekBar,
                                  int progress,
                                  boolean fromUser) {
    }
    //---------------------------------------------------------------------------------------------
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
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
        return true;
    }
    //---------------------------------------------------------------------------------------------
    public boolean device_connect()
    {
        logging("connect");
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

        boolean ok = connect_remote_device(BluetoothName.get_mac(getApplicationContext()));
        if(ok)
            logging("Соединение установлено");
        else
            logging("Соединение не удалось");
        block_interface(!ok);

        return ok;
    }
    //---------------------------------------------------------------------------------------------
    public void device_disconnect()
    {
        logging("disconnect");

        if(mmSocket.isConnected()) {
            try {
                mmSocket.close();
                logging("Соединение разорвано");
                block_interface(true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    //---------------------------------------------------------------------------------------------
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId())
        {
            case R.id.action_settings_scan:
                if(device_connect())
                {
                    block_interface(false);
                }
                break;

            case R.id.action_settings_disconnect:
                device_disconnect();
                block_interface(true);
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
    public void create_bluetooth()
    {
        bluetooth = BluetoothAdapter.getDefaultAdapter();

        block_interface(true);

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
        logging("OK");
    }
    //---------------------------------------------------------------------------------------------
    private void block_interface(boolean state)
    {
        sb_delay_ms.setEnabled(!state);
        sb_brightness.setEnabled(!state);

        btn_1.setEnabled(!state);
        btn_2.setEnabled(!state);
        btn_3.setEnabled(!state);
        btn_4.setEnabled(!state);
        btn_5.setEnabled(!state);
    }
    //---------------------------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv_log = (TextView)findViewById(R.id.logView);
        //tv_log.setTextColor(Color.WHITE);

        sb_delay_ms = (SeekBar)findViewById(R.id.sb_delay_ms);
        sb_brightness = (SeekBar)findViewById(R.id.sb_brightness);

        //sb_delay_ms.setMin(0);
        sb_delay_ms.setMax(1000);

        //sb_brightness.setMin(0);
        sb_brightness.setMax(150);

        btn_1 = (Button)findViewById(R.id.btn_cmd_1);
        btn_2 = (Button)findViewById(R.id.btn_cmd_2);
        btn_3 = (Button)findViewById(R.id.btn_cmd_3);
        btn_4 = (Button)findViewById(R.id.btn_cmd_4);
        btn_5 = (Button)findViewById(R.id.btn_cmd_5);

        requestPermission(Manifest.permission.ACCESS_FINE_LOCATION, RECORD_REQUEST_CODE);
        create_bluetooth();

        block_interface(true);
    }

    //---------------------------------------------------------------------------------------------
    public void command_1(View view) {
        logging("CMD 1");
    }
    //---------------------------------------------------------------------------------------------
    public void command_2(View view) {
        logging("CMD 2");
    }
    //---------------------------------------------------------------------------------------------
    public void command_3(View view) {
        logging("CMD 3");
    }
    //---------------------------------------------------------------------------------------------
    public void command_4(View view) {
        logging("CMD 4");
    }
    //---------------------------------------------------------------------------------------------
    public void command_5(View view) {
        logging("CMD 5");
    }
    //---------------------------------------------------------------------------------------------
}
