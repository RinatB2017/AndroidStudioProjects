package com.test_wifi;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/*
    <uses-permission android:name="android.permission.ACCESS_WIFI_STATE"/>
    <uses-permission android:name="android.permission.CHANGE_WIFI_STATE"/>
    <uses-permission android:name="android.permission.CHANGE_WIFI_MULTICAST_STATE"/>
 */

public class MainActivity extends Activity {
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
    private boolean hasPermission_ACCESS_FINE_LOCATION() {
        int res = 0;
        //string array of permissions,
        String[] permissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION};

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
        log.append("check_all_permissions()\n");
        if(!hasPermission_ACCESS_WIFI_STATE())
        {
            log.append("hasPermission_ACCESS_WIFI_STATE is FALSE\n");
            return false;
        }
        if(!hasPermission_CHANGE_WIFI_STATE())
        {
            log.append("hasPermission_CHANGE_WIFI_STATE is FALSE\n");
            return false;
        }
        if(!hasPermission_CHANGE_WIFI_MULTICAST_STATE())
        {
            log.append("hasPermission_CHANGE_WIFI_MULTICAST_STATE is FALSE\n");
            return false;
        }
        if(!hasPermission_ACCESS_FINE_LOCATION())
        {
            log.append("hasPermission_ACCESS_FINE_LOCATION is FALSE\n");
            return false;
        }
        return true;
    }
    //---------------------------------------------------------------------------------------------
    public void connect(View view) {
        log.append("connect()\n");
        wifi=(WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if(wifi == null)
        {
            log.append("wifi модуль не найден\n");
            return;
        }
        if(!wifi.isWifiEnabled())
        {
            log.append("Включаем wifi модуль!\n");
            boolean ok = wifi.setWifiEnabled(true);
            if(!ok)
            {
                log.append("Неудача!\n");
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
        log.append("MAC " + wifiInf.getMacAddress() + "\n");
        log.append("OK\n");
    }
    //---------------------------------------------------------------------------------------------
    public void disconnect(View view) {
        log.append("disconnect()\n");
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
            log.append("wifi модуль не найден\n");
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

        log.append("scan()\n");
        if(wifi != null) {
            boolean ok = wifi.startScan();
            if (ok) {
                //scanResults = wifi.getScanResults();
                //log.append("size = " + String.valueOf(scanResults.size()) +"\n");
            }
            if (!ok) log.append("startScan return false\n");
        }
        else
        {
            log.append("wifi модуль не найден\n");
        }
    }
    //---------------------------------------------------------------------------------------------
    private class WifiScanReceiver extends BroadcastReceiver{
        public void onReceive(Context c, Intent intent) {
            log.append("onReceive()\n");

            List<ScanResult> wifiScanList = wifi.getScanResults();
            wifis = new String[wifiScanList.size()];

            int size = wifiScanList.size();
            log.append("wifiScanList.size() = " + String.valueOf(size) + "\n");
            if(size > 0) {
                log.append("-------------------\n");
                for (int i = 0; i < wifiScanList.size(); i++) {
                    //log.append((wifiScanList.get(i).toString()) + "\n");
                    log.append((wifiScanList.get(i)).SSID + "\n");
                }
                log.append("-------------------\n");
            }
            log.append("OK" + "\n");
        }
    }
    //---------------------------------------------------------------------------------------------
    public void send_cmd_1(View view) {
        if(wifi == null) {
            log.append("wifi модуль не найден\n");
            return;
        }
        if(!wifi.isWifiEnabled()) {
            log.append("wifi модуль не включен\n");
            return;
        }

        log.append("send_cmd_1()\n");
    }
    //---------------------------------------------------------------------------------------------
    public void send_cmd_2(View view) {
        if(wifi == null) {
            log.append("wifi модуль не найден\n");
            return;
        }
        if(!wifi.isWifiEnabled()) {
            log.append("wifi модуль не включен\n");
            return;
        }
        log.append("send_cmd_2()\n");
    }
    //---------------------------------------------------------------------------------------------
    public void send_cmd_3(View view) {
        if(wifi == null) {
            log.append("wifi модуль не найден\n");
            return;
        }
        if(!wifi.isWifiEnabled()) {
            log.append("wifi модуль не включен\n");
            return;
        }
        log.append("send_cmd_3()\n");
    }
    //---------------------------------------------------------------------------------------------
    public void send_cmd_4(View view) {
        if(wifi == null) {
            log.append("wifi модуль не найден\n");
            return;
        }
        if(!wifi.isWifiEnabled()) {
            log.append("wifi модуль не включен\n");
            return;
        }
        log.append("send_cmd_4()\n");
    }
    //---------------------------------------------------------------------------------------------
}
