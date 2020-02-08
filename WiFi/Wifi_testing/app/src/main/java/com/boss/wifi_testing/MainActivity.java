package com.boss.wifi_testing;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

// https://ru.stackoverflow.com/questions/576309/%D0%9A%D0%B0%D0%BA-%D0%BF%D1%80%D0%BE%D0%B3%D1%80%D0%B0%D0%BC%D0%BC%D0%BD%D0%BE-%D0%BF%D0%BE%D0%B4%D0%BA%D0%BB%D1%8E%D1%87%D0%B8%D1%82%D1%8C%D1%81%D1%8F-%D0%BA-%D1%81%D0%B5%D1%82%D0%B8-wi-fi-%D0%BD%D0%B0-android
// необходимо включить GPS

public class MainActivity extends AppCompatActivity {

    static final String LOG_TAG = "States";
    ListView lv;
    WifiManager wifiManager;
    String wifis[];
    WifiScanReceiver wifiReciever;

    TextView tv_log;
    Handler h_print;

    //private static final int RECORD_REQUEST_CODE = 101;

    //String networkSSID = "SoftAP";
    //String networkPass = "12345678";

    //---------------------------------------------------------------------------------------------
    void init_log() {
        tv_log = (TextView) findViewById(R.id.logView);
        tv_log.setTextColor(Color.BLACK);

        h_print = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                String text = (String) msg.obj;
                int color = msg.arg1;
                Log.i(LOG_TAG, text);

                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    String c_text = "<font color=#" + Integer.toHexString(color).substring(2) + ">" + text + "</font><br>";
                    tv_log.append(Html.fromHtml(c_text, Html.FROM_HTML_MODE_LEGACY));
                } else {
                    tv_log.append(text + "\n");
                }

                //---
                Toast toast = Toast.makeText(getBaseContext(),
                        text,
                        Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.BOTTOM, 0, 0);
                toast.show();
                //---
            }
        };
    }

    //---------------------------------------------------------------------------------------------
    public void send_log(int color, String text) {
        if (text == null) {
            return;
        }
        Message msg = new Message();
        msg.arg1 = color;
        msg.obj = text;
        h_print.sendMessage(msg);
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init_log();

        lv=(ListView)findViewById(R.id.listView);
        wifiManager=(WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        // Проверяем включен ли WiFi, если нет то включаем
        enableWifi();

        //wifiReciever = new WifiScanReceiver();
        //registerReceiver(wifiReciever, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

        //requestPermission(Manifest.permission.ACCESS_WIFI_STATE, RECORD_REQUEST_CODE);
        //requestPermission(Manifest.permission.CHANGE_WIFI_STATE, RECORD_REQUEST_CODE);

        /*
        wifiManager.startScan();
        List<ScanResult> lv = wifiManager.getScanResults();

        ArrayList<String> l_pass = new ArrayList<>();
        l_pass.add("0000000");
        l_pass.add("1111111");
        l_pass.add("2222222");
        l_pass.add("3333333");
        l_pass.add("4444444");
        l_pass.add("5555555");
        l_pass.add("6666666");
        l_pass.add("7777777");
        l_pass.add("8888888");
        l_pass.add("9999999");

        if(!lv.isEmpty()) {
            logging("Found " + lv.size());
            for(int i = 0; i < lv.size(); i++) {
                logging(lv.get(i).SSID);
                boolean ok = false;
                String ssid = lv.get(i).SSID;
                for (int n = 0; n < l_pass.size(); n++) {
                    ok = myConnect(ssid, l_pass.get(n));
                    if (ok) {
                        logging("FOUND: SSID = " + lv.get(i).SSID + " pass " + l_pass.get(n));
                    }
                }
            }
        }
        */

        //myConnect();
    }

    //---------------------------------------------------------------------------------------------
    protected void onPause() {
        //unregisterReceiver(wifiReciever);
        super.onPause();
    }

    //---------------------------------------------------------------------------------------------
    protected void onResume() {
        //registerReceiver(wifiReciever, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        super.onResume();
    }

    //---------------------------------------------------------------------------------------------
    public void scan(View view) {
        requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},1);

        //wifiManager.startScan();
        //registerReceiver(wifiReciever, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

        Runnable runnable = new Runnable() {
            public void run() {
                wifiManager.startScan();
                List<ScanResult> lv = wifiManager.getScanResults();

                ArrayList<String> l_pass = new ArrayList<>();
                l_pass.add("0000000");
                l_pass.add("1111111");
                l_pass.add("2222222");
                l_pass.add("3333333");
                l_pass.add("4444444");
                l_pass.add("5555555");
                l_pass.add("6666666");
                l_pass.add("7777777");
                l_pass.add("8888888");
                l_pass.add("9999999");

                if(!lv.isEmpty()) {
                    send_log(Color.BLACK, "Found " + lv.size());
                    for(int i = 0; i < lv.size(); i++) {
                        send_log(Color.BLACK, lv.get(i).SSID);
                        boolean ok = false;
                        String ssid = lv.get(i).SSID;
                        for (int n = 0; n < l_pass.size(); n++) {
                            ok = myConnect(ssid, l_pass.get(n));
                            if (ok) {
                                send_log(Color.BLACK, "FOUND: SSID = " + lv.get(i).SSID + " pass " + l_pass.get(n));
                            }
                        }
                    }
                }
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();

    }

    //---------------------------------------------------------------------------------------------
    private class WifiScanReceiver extends BroadcastReceiver {

        public void onReceive(Context c, Intent intent) {
            List<ScanResult> wifiScanList = wifiManager.getScanResults();
            wifis = new String[wifiScanList.size()];

            if(wifiScanList.size() > 0)
                send_log(Color.BLACK, "Found " + wifiScanList.size() + " networks");
            else
                send_log(Color.RED, "NOT FOUND");

            for(int i = 0; i < wifiScanList.size(); i++) {
                send_log(Color.BLACK, wifiScanList.get(i).SSID);
                //wifis[i] = ((wifiScanList.get(i)).toString());
            }
            //lv.setAdapter(new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_list_item_1,wifis));
        }
    }

    //---------------------------------------------------------------------------------------------
    public void enableWifi() {

        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);

            Toast toast = Toast.makeText(getApplicationContext(), "Wifi Turned On", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    //---------------------------------------------------------------------------------------------
    boolean myConnect(String networkSSID, String networkPass) {
        WifiConfiguration wifiConfig = new WifiConfiguration();
        wifiConfig.SSID = String.format("\"%s\"", networkSSID);
        wifiConfig.preSharedKey = String.format("\"%s\"", networkPass);

        WifiManager wifiManager = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        //remember id
        int netId = wifiManager.addNetwork(wifiConfig);
        boolean result = false;
        if(netId > 0) {
            send_log(Color.BLACK, "netId = " + netId);
            wifiManager.disconnect();
            result = wifiManager.enableNetwork(netId, true);
            wifiManager.reconnect();
        }

        if(!result) {
            wifiManager.disableNetwork(netId);
            wifiManager.removeNetwork(netId);
        }

        return result;
    }

    //---------------------------------------------------------------------------------------------
}
