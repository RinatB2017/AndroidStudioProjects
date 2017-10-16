package com.test_wifi;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collections;
import java.util.List;

    /*
    <uses-permission android:name="android.permission.ACCESS_WIFI_STATE"/>
    <uses-permission android:name="android.permission.CHANGE_WIFI_STATE"/>
    <uses-permission android:name="android.permission.CHANGE_WIFI_MULTICAST_STATE"/>
    */

public class MainActivity extends TestingLogging {
    TextView log;

    WifiManager wifi;
    String wifis[];
    WifiScanReceiver wifiReciever;

    //---------------------------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        log = (TextView)findViewById(R.id.editText);
    }
    //---------------------------------------------------------------------------------------------
    void logging(String text) {
        log.append(text + "\n");
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
            case R.id.action_1:
                send_cmd_1();
                break;

            case R.id.action_2:
                send_cmd_2();
                break;

            case R.id.action_3:
                send_cmd_3();
                break;

            case R.id.action_4:
                send_cmd_4();
                break;

            case R.id.action_clean:
                log.setText("");
                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    //---------------------------------------------------------------------------------------------
    @Override
    protected void onDestroy() {
        try {
            unregisterReceiver(wifiReciever);
        } catch (IllegalArgumentException e) {

        }

        try {
            wifi.setWifiEnabled(false);
        } catch (NullPointerException e) {

        }
        super.onDestroy();
    }
    //---------------------------------------------------------------------------------------------
    private boolean hasPermission_ACCESS_WIFI_STATE() {
        int res = 0;
        //string array of permissions,
        String[] permissions = new String[]{Manifest.permission.ACCESS_WIFI_STATE};

        for (String perms : permissions){
            res = checkCallingOrSelfPermission(perms);
            if (!(res == PackageManager.PERMISSION_GRANTED)){
                return false;
            }
        }
        return true;
    }
    //---------------------------------------------------------------------------------------------
    private boolean hasPermission_CHANGE_WIFI_STATE() {
        int res = 0;
        //string array of permissions,
        String[] permissions = new String[]{Manifest.permission.CHANGE_WIFI_STATE};

        for (String perms : permissions){
            res = checkCallingOrSelfPermission(perms);
            if (!(res == PackageManager.PERMISSION_GRANTED)){
                return false;
            }
        }
        return true;
    }
    //---------------------------------------------------------------------------------------------
    private boolean hasPermission_CHANGE_WIFI_MULTICAST_STATE() {
        int res = 0;
        //string array of permissions,
        String[] permissions = new String[]{Manifest.permission.CHANGE_WIFI_MULTICAST_STATE};

        for (String perms : permissions){
            res = checkCallingOrSelfPermission(perms);
            if (!(res == PackageManager.PERMISSION_GRANTED)){
                return false;
            }
        }
        return true;
    }
    //---------------------------------------------------------------------------------------------
    private boolean hasPermission_INTERNET() {
        int res = 0;
        //string array of permissions,
        String[] permissions = new String[]{Manifest.permission.INTERNET};

        for (String perms : permissions){
            res = checkCallingOrSelfPermission(perms);
            if (!(res == PackageManager.PERMISSION_GRANTED)){
                return false;
            }
        }
        return true;
    }
    //---------------------------------------------------------------------------------------------
    private boolean check_all_permissions() {
        //logging("check_all_permissions()");
        if(!hasPermission_ACCESS_WIFI_STATE())
        {
            logging("hasPermission_ACCESS_WIFI_STATE is FALSE");
            return false;
        }
        if(!hasPermission_CHANGE_WIFI_STATE())
        {
            logging("hasPermission_CHANGE_WIFI_STATE is FALSE");
            return false;
        }
        if(!hasPermission_CHANGE_WIFI_MULTICAST_STATE())
        {
            logging("hasPermission_CHANGE_WIFI_MULTICAST_STATE is FALSE");
            return false;
        }
        if(!hasPermission_INTERNET())
        {
            logging("hasPermission_INTERNET is FALSE");
            return false;
        }
        return true;
    }
    //---------------------------------------------------------------------------------------------
    public void connect(View view) {
        logging("connect()");
        wifi=(WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if(wifi == null)
        {
            logging("wifi модуль не найден");
            return;
        }
        if(!wifi.isWifiEnabled())
        {
            logging("Включаем wifi модуль!");
            boolean ok = wifi.setWifiEnabled(true);
            if(!ok)
            {
                logging("Неудача!");
                return;
            }
        }

        //---
        if(!check_all_permissions()) {
            return;
        }
        //---

        wifi = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifiReciever = new WifiScanReceiver();
        registerReceiver(wifiReciever, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

        WifiInfo wifiInf = wifi.getConnectionInfo();
        logging("MAC " + wifiInf.getMacAddress());
        logging("OK");
    }
    //---------------------------------------------------------------------------------------------
    public void disconnect(View view) {
        logging("disconnect()");
        //---
        if(!check_all_permissions()) {
            return;
        }
        //---
        if(wifi != null) {
            wifi.setWifiEnabled(false);
        }
        else
        {
            logging("wifi модуль не найден");
        }
    }
    //---------------------------------------------------------------------------------------------
    //int hasSMSPermission = checkSelfPermission( Manifest.permission.ACCESS_WIFI_STATE );
    //List<String> permissions = new ArrayList<String>();
    public void scan(View view) {

        List<ScanResult> scanResults = Collections.emptyList();

        //---
        if(!check_all_permissions()) {
            return;
        }
        //---

        logging("scan()");
        if(wifi != null) {
            boolean ok = wifi.startScan();
            if (ok) {
                //scanResults = wifi.getScanResults();
                //logging("size = " + String.valueOf(scanResults.size()));
                logging("OK");
            }
            else {
                logging("startScan return false");
            }
        }
        else
        {
            logging("wifi модуль не найден");
        }
    }
    //---------------------------------------------------------------------------------------------
    private class WifiScanReceiver extends BroadcastReceiver{
        public void onReceive(Context c, Intent intent) {
            logging("onReceive()");

            List<ScanResult> wifiScanList = wifi.getScanResults();
            wifis = new String[wifiScanList.size()];

            int size = wifiScanList.size();
            logging("wifiScanList.size() = " + String.valueOf(size));
            if(size > 0) {
                logging("-------------------");
                for (int i = 0; i < wifiScanList.size(); i++) {
                    //logging((wifiScanList.get(i).toString()));
                    logging((wifiScanList.get(i)).SSID);
                }
                logging("-------------------");
            }
            logging("OK");
        }
    }
    //---------------------------------------------------------------------------------------------
    public void send_cmd_1() {
        if(wifi == null) {
            logging("wifi модуль не найден");
            return;
        }
        if(!wifi.isWifiEnabled()) {
            logging("wifi модуль не включен");
            return;
        }

        /*
        URL pageURL = null;
        try {
            pageURL = new URL("http://192.168.0.1");
            URLConnection uc = pageURL.openConnection();
            BufferedReader buff = new BufferedReader(new InputStreamReader(uc.getInputStream()));
//            InputStreamReader ir = new InputStreamReader(uc.getInputStream());
//            BufferedReader buff = new BufferedReader(ir);
            logging(buff.toString());
        } catch (MalformedURLException e) {
            logging("MalformedURLException: " + e.getMessage());
        } catch (IOException e) {
            logging("Exception: " + e.getMessage());
        }
        */

        try{
            URL url = null;
            url = new URL("http://192.168.0.1");
            URLConnection con1 = url.openConnection();
            BufferedReader reader = new BufferedReader(new InputStreamReader(con1.getInputStream()));
            String line ="";
            while ((line=reader.readLine())!=null){
                logging(line);
            }
        } catch (Exception e){
            logging("Exception: " + e.getMessage());
        }

        logging("send_cmd_1()");
    }
    //---------------------------------------------------------------------------------------------
    public void send_cmd_2() {
        if(wifi == null) {
            logging("wifi модуль не найден");
            return;
        }
        if(!wifi.isWifiEnabled()) {
            logging("wifi модуль не включен");
            return;
        }
        logging("send_cmd_2()");
    }
    //---------------------------------------------------------------------------------------------
    public void send_cmd_3() {
        if(wifi == null) {
            logging("wifi модуль не найден");
            return;
        }
        if(!wifi.isWifiEnabled()) {
            logging("wifi модуль не включен");
            return;
        }
        logging("send_cmd_3()");
    }
    //---------------------------------------------------------------------------------------------
    public void send_cmd_4() {
        if(wifi == null) {
            logging("wifi модуль не найден");
            return;
        }
        if(!wifi.isWifiEnabled()) {
            logging("wifi модуль не включен");
            return;
        }
        logging("send_cmd_4()");
    }
    //---------------------------------------------------------------------------------------------
}
