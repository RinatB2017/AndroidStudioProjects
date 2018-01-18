package newmoonlight;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class OptionsActivity extends AppCompatActivity {

    final String LOG_TAG = "States";

    Button btn_apply;
    Button btn_cancel;

    ListView lv_devices;

    int position = -1;

    private BluetoothAdapter bluetoothAdapter;
    private BroadcastReceiver discoverDevicesReceiver;
    private BroadcastReceiver discoveryFinishedReceiver;

    private final List<BluetoothDevice> discoveredDevices = new ArrayList<BluetoothDevice>();

    private ProgressDialog progressDialog;
    private ArrayAdapter<BluetoothDevice> listAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);

        btn_apply = (Button)findViewById(R.id.btn_apply);
        btn_cancel = (Button)findViewById(R.id.btn_cancel);

        btn_apply.setEnabled(false);
        btn_cancel.setEnabled(false);

        View.OnClickListener apply = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(LOG_TAG, "apply");

                //TODO
                if(position >= 0) {
                    BluetoothDevice deviceSelected = discoveredDevices.get(position);
                    if (deviceSelected != null) {
                        BluetoothName.set_name(getApplicationContext(), deviceSelected.getName());
                        BluetoothName.set_mac(getApplicationContext(), deviceSelected.getAddress());
                    }
                    f_run_MainActivity();
                }
                else {
                    Toast.makeText(getBaseContext(), "Устройство не выбрано.", Toast.LENGTH_LONG).show();
                }
            }
        };
        View.OnClickListener cancel = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(LOG_TAG, "cancel");

                f_run_MainActivity();
            }
        };

        btn_apply.setOnClickListener(apply);
        btn_cancel.setOnClickListener(cancel);

        lv_devices = (ListView)findViewById(R.id.lv_devices);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        listAdapter = new ArrayAdapter<BluetoothDevice>(getBaseContext(), android.R.layout.simple_list_item_1, discoveredDevices) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                final BluetoothDevice device = getItem(position);
                ((TextView) view.findViewById(android.R.id.text1)).setText(device.getName());
                return view;
            }
        };
        lv_devices.setAdapter(listAdapter);
        lv_devices.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int pos, long id)
            {
                position = pos;
            }
        });
    }

    public void discoverDevices(View view) {

        discoveredDevices.clear();
        listAdapter.notifyDataSetChanged();

        if (discoverDevicesReceiver == null) {
            discoverDevicesReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    String action = intent.getAction();

                    if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                        BluetoothDevice e_device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                        if(e_device != null) {
                            Log.i(LOG_TAG, "e_device [" + e_device.getName() + "]");

                            if(e_device.getName() != null) {
                                if (!discoveredDevices.contains(e_device)) {
                                    discoveredDevices.add(e_device);
                                    listAdapter.notifyDataSetChanged();
                                }
                            }
                        }
                    }
                }
            };
        }
        else {
            Log.i(LOG_TAG, "what???");
        }

        if (discoveryFinishedReceiver == null) {
            discoveryFinishedReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    lv_devices.setEnabled(true);
                    if (progressDialog != null)
                        progressDialog.dismiss();
                    Toast.makeText(getBaseContext(), "Поиск закончен.", Toast.LENGTH_LONG).show();
                    if(!listAdapter.isEmpty()) {
                        btn_apply.setEnabled(true);
                    }
                    btn_cancel.setEnabled(true);
                    unregisterReceiver(discoveryFinishedReceiver);
                }
            };
        }

        registerReceiver(discoverDevicesReceiver,   new IntentFilter(BluetoothDevice.ACTION_FOUND));
        registerReceiver(discoveryFinishedReceiver, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED));

        lv_devices.setEnabled(false);

        progressDialog = ProgressDialog.show(this, "Поиск устройств", "Подождите...");

        bluetoothAdapter.startDiscovery();
    }

    void f_run_MainActivity() {
        Intent intent = new Intent(this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
