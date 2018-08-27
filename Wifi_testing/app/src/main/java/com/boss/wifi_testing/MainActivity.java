package com.boss.wifi_testing;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

// https://ru.stackoverflow.com/questions/576309/%D0%9A%D0%B0%D0%BA-%D0%BF%D1%80%D0%BE%D0%B3%D1%80%D0%B0%D0%BC%D0%BC%D0%BD%D0%BE-%D0%BF%D0%BE%D0%B4%D0%BA%D0%BB%D1%8E%D1%87%D0%B8%D1%82%D1%8C%D1%81%D1%8F-%D0%BA-%D1%81%D0%B5%D1%82%D0%B8-wi-fi-%D0%BD%D0%B0-android

public class MainActivity extends AppCompatActivity {

    ListView lv;
    WifiManager wifi;
    String wifis[];
    WifiScanReceiver wifiReciever;

    private static final int RECORD_REQUEST_CODE = 101;
    private final static int REQUEST_ENABLE_BT = 1;

    String networkSSID = "SoftAP";
    String networkPass = "12345678";

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        requestPermission(Manifest.permission.ACCESS_WIFI_STATE, RECORD_REQUEST_CODE);
        requestPermission(Manifest.permission.CHANGE_WIFI_STATE, RECORD_REQUEST_CODE);

        lv=(ListView)findViewById(R.id.listView);
        wifi=(WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        //Проверяем включен ли WiFi, если нет то включаем
        enableWifi();

        wifiReciever = new WifiScanReceiver();
        wifi.startScan();

        myConnect();
    }

    //---------------------------------------------------------------------------------------------
    protected void onPause() {
        unregisterReceiver(wifiReciever);
        super.onPause();
    }

    //---------------------------------------------------------------------------------------------
    protected void onResume() {
        registerReceiver(wifiReciever, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        super.onResume();
    }

    //---------------------------------------------------------------------------------------------
    private class WifiScanReceiver extends BroadcastReceiver {

        public void onReceive(Context c, Intent intent) {

            List<ScanResult> wifiScanList = wifi.getScanResults();
            wifis = new String[wifiScanList.size()];

            for(int i = 0; i < wifiScanList.size(); i++) {
                wifis[i] = ((wifiScanList.get(i)).toString());
            }
            lv.setAdapter(new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_list_item_1,wifis));
        }
    }

    //---------------------------------------------------------------------------------------------
    public void enableWifi() {

        if (!wifi.isWifiEnabled()) {
            wifi.setWifiEnabled(true);

            Toast toast = Toast.makeText(getApplicationContext(), "Wifi Turned On", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    //---------------------------------------------------------------------------------------------
    void myConnect() {

        WifiConfiguration wifiConfig = new WifiConfiguration();
        wifiConfig.SSID = String.format("\"%s\"", networkSSID);
        wifiConfig.preSharedKey = String.format("\"%s\"", networkPass);

        WifiManager wifiManager = (WifiManager)getApplicationContext().getSystemService(WIFI_SERVICE);
//remember id
        int netId = wifiManager.addNetwork(wifiConfig);
        wifiManager.disconnect();
        wifiManager.enableNetwork(netId, true);
        wifiManager.reconnect();
    }

    //---------------------------------------------------------------------------------------------
}
