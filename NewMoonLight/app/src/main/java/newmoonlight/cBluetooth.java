package newmoonlight;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class cBluetooth extends Activity {

    BluetoothAdapter bluetooth;
    BluetoothSocket btSocket;
    private BroadcastReceiver discoveryDevicesReceiver;
    private BroadcastReceiver discoveryFinishedReceiver;
    private final List<BluetoothDevice> discoveredDevices = new ArrayList<BluetoothDevice>();

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

    private final static int REQUEST_ENABLE_BT = 1;

    final String LOG_TAG = "States";

    //---------------------------------------------------------------------------------------------
    public void logging(String text) {
        Log.i(LOG_TAG, text);
    }
    //---------------------------------------------------------------------------------------------
    cBluetooth() {
        init();
    }
    //---------------------------------------------------------------------------------------------
    private void init() {
        bluetooth = BluetoothAdapter.getDefaultAdapter();
    }
    //---------------------------------------------------------------------------------------------
    public boolean scan() {
        logging("Scan begin!");
        if(!check_bluetooth()) {
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
                                        discoveredDevices.add(device);
                                        bluetooth.cancelDiscovery();
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
                }
            };
        }

        registerReceiver(discoveryDevicesReceiver,  new IntentFilter(BluetoothDevice.ACTION_FOUND));
        registerReceiver(discoveryFinishedReceiver, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED));

        bluetooth.startDiscovery();
        logging("Scan begin...");
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
};
