package newmoonlight;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class cBluetooth {

    BluetoothAdapter bluetooth;
    BluetoothSocket btSocket;
    private BroadcastReceiver discoveryDevicesReceiver;
    private BroadcastReceiver discoveryFinishedReceiver;
    private final List<BluetoothDevice> discoveredDevices = new ArrayList<BluetoothDevice>();

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

    }
    //---------------------------------------------------------------------------------------------
    public void scan() {

    }
    //---------------------------------------------------------------------------------------------
};
