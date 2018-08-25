package com.boss.bluetooth_tester;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MainActivity extends TestingLogging {

    private BluetoothAdapter bluetooth;
    private BluetoothSocket btSocket;
    private BroadcastReceiver discoveryDevicesReceiver;
    private BroadcastReceiver discoveryFinishedReceiver;
    private final List<BluetoothDevice> discoveredDevices = new ArrayList<BluetoothDevice>();
    private ArrayAdapter<BluetoothDevice> listAdapter;
    private ProgressDialog progressDialog;

    private static final UUID MY_UUID = UUID.fromString("00000001-0001-0001-0001-000000000001");
    //private static final String DEVICE_NAME = "HC-05";
    private static InputStream inputStream;
    private static OutputStream outputStream;

    private BluetoothDevice r_device = null;
    private BluetoothSocket tmp = null;
    private BluetoothSocket mmSocket = null;
    private InputStream tmpIn = null;
    private OutputStream tmpOut = null;

    //private final static int REQUEST_ENABLE_BT = 1;

    int position = -1;

    ListView lv_devices;

    Button btn_find;
    Button btn_connect;
    Button btn_disconnect;
    Button btn_send;

    //---------------------------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        logView = (TextView)findViewById(R.id.logView);
        load_log();

        btn_find = (Button)findViewById(R.id.btn_find);
        btn_connect = (Button)findViewById(R.id.btn_connect);
        btn_disconnect = (Button)findViewById(R.id.btn_disconnect);
        btn_send = (Button)findViewById(R.id.btn_send);

        lv_devices = (ListView)findViewById(R.id.lv_devices);

        // создаем адаптер
        listAdapter = new ArrayAdapter<BluetoothDevice>(getBaseContext(),
                android.R.layout.simple_list_item_1, discoveredDevices) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                final BluetoothDevice device = getItem(position);
                ((TextView) view.findViewById(android.R.id.text1)).setText(device.getName());
                return view;
            }
        };

        // присваиваем адаптер списку
        lv_devices.setAdapter(listAdapter);
        lv_devices.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int pos, long id)
            {
                position = pos;
            }
        });

        bluetooth = BluetoothAdapter.getDefaultAdapter();
    }
    //---------------------------------------------------------------------------------------------
    public boolean check_bluetooth() {
        if(bluetooth == null)
        {
            logging("Bluetooth модуль не найден");
            return false;
        }
        if(!bluetooth.isEnabled()) {
            logging("Bluetooth модуль не активен");
            return false;
        }
        return true;
    }
    //---------------------------------------------------------------------------------------------
    public boolean scan() {
        if(!check_bluetooth()) {
            return false;
        }

        discoveredDevices.clear();

        logging("Scan begin!");
        lock_btn(btn_find);

        discoveredDevices.clear();
        listAdapter.notifyDataSetChanged();

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
                            String d_address = device.getAddress();
                            d_name = d_name.replaceAll("\n", "");
                            if(d_name != null) {
                                logging(d_name + " " + d_address);
                                //listAdapter.add(device);
                                discoveredDevices.add(device);
                                listAdapter.notifyDataSetChanged();
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

                    unlock_btn(btn_find);
                }
            };
        }

        registerReceiver(discoveryDevicesReceiver,  new IntentFilter(BluetoothDevice.ACTION_FOUND));
        registerReceiver(discoveryFinishedReceiver, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED));

        progressDialog = ProgressDialog.show(this, "Поиск устройств", "Подождите...");

        bluetooth.startDiscovery();

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
        if(r_device == null) {
            logging("getRemoteDevice return FALSE");
            return false;
        }
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
    void lock_btn(Button btn) {
        if(btn != null)
            btn.setEnabled(false);
    }
    //---------------------------------------------------------------------------------------------
    void unlock_btn(Button btn) {
        if(btn != null)
            btn.setEnabled(true);
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
            case R.id.action_settings_test:
                logging("test");
                break;

            case R.id.action_clean_log:
                clean_log();
                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    //---------------------------------------------------------------------------------------------
    public void cmd_find(View view) {
        logging("cmd_find");
        scan();
    }
    //---------------------------------------------------------------------------------------------
    public void cmd_connect(View view) throws IOException {
        logging("cmd_connect");

        if(position < 0) {
            logging("position < 0");
            dialog("Устройство не выбрано!");
            return;
        }

        lock_btn(btn_connect);

        BluetoothDevice deviceSelected = discoveredDevices.get(position);
        if(deviceSelected != null) {
            boolean res = connect_remote_device(deviceSelected.getAddress());
            if(res) {
                dialog("Соединение удалось");
            }
            else {
                dialog("Соединение НЕ удалось");
            }
            //connect_remote_device("00:14:02:10:09:04");
        }
        else {
            dialog("Устройство не выбрано!");
        }
        unlock_btn(btn_connect);
    }
    //---------------------------------------------------------------------------------------------
    public void cmd_disconnect(View view) {
        logging("cmd_disconnect");

        lock_btn(btn_disconnect);
        if (mmSocket != null) {
            try {
                mmSocket.close();
            } catch (IOException e) {
                logging("Diconnect ERROR: "+ e.getMessage());
            }
            mmSocket = null;
        }
        unlock_btn(btn_disconnect);
    }
    //---------------------------------------------------------------------------------------------
    public boolean send_data(String message)
    {
        byte[] buffer = new byte[128];  // buffer store for the stream
        int bytes = 0; // bytes returned from read()
        int bytesAvailableCount = 0;

        if(outputStream == null)
        {
            logging("outputStream not created!");
            return false;
        }
        try {
            outputStream.write(message.getBytes());
            do {
                bytesAvailableCount = inputStream.available();
                if(bytesAvailableCount > 0) {
                    bytes = inputStream.read(buffer);
                    logging("Получено " + String.valueOf(bytes) + " байтов");
                    logging(new String(buffer, "ISO-8859-1"));
                }
            } while(bytesAvailableCount > 0);
        } catch (IOException e) {
            logging("send_data ERROR: " + e.getMessage());
            return false;
        }
        return true;
    }
    //---------------------------------------------------------------------------------------------
    public void cmd_send(View view) {
        logging("cmd_send");

        lock_btn(btn_send);
        send_data("test" + "\n");
        unlock_btn(btn_send);
//        dialog();
    }
    //---------------------------------------------------------------------------------------------
    void dialog(String message) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle("Сообщение");
        alert.setMessage(message);

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

            }
        });

        alert.show();
    }
    //---------------------------------------------------------------------------------------------
}
