package com.test_wifi;

import android.Manifest;
import android.content.pm.PackageManager;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

    /*
    <uses-permission android:name="android.permission.ACCESS_WIFI_STATE"/>
    <uses-permission android:name="android.permission.CHANGE_WIFI_STATE"/>
    <uses-permission android:name="android.permission.CHANGE_WIFI_MULTICAST_STATE"/>
    */

public class MainActivity extends TestingLogging {
    TextView log;

    EditText et_address;

    WifiManager wifi;
    String wifis[];

    //---------------------------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        et_address = (EditText)findViewById(R.id.et_address);
        log = (TextView)findViewById(R.id.editText);
    }
    //---------------------------------------------------------------------------------------------
    void logging(String text) {
        if(!text.isEmpty()) {
            log.append(text + "\n");
        }
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
                send_cmd_1(item.getActionView());
                break;

            case R.id.action_2:
                send_cmd_2(item.getActionView());
                break;

            case R.id.action_3:
                send_cmd_3(item.getActionView());
                break;

            case R.id.action_4:
                send_cmd_4(item.getActionView());
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
    class MyDownloadTask extends AsyncTask<String, Void, String> {

        String responseMsg = "not response";

        protected String doInBackground(String... params) {
            try{
                URL url = new URL(params[0]);

                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                //con.setRequestMethod("POST");
                con.setRequestMethod("GET");
                con.setRequestProperty("User-Agent", "Mozilla/5.0");
                con.setRequestProperty("Accept-Charset", "UTF-8");
                con.setReadTimeout(10000);
                con.connect();

                BufferedReader reader= new BufferedReader(new InputStreamReader(con.getInputStream()));
                StringBuilder buf=new StringBuilder();
                String line = null;
                while ((line=reader.readLine()) != null) {
                    buf.append(line);
                    Log.e("States", "readLine [" + line + "]");
                }

                responseMsg = buf.toString();
                return buf.toString();
            }
            catch (MalformedURLException e) {
                Log.e("States", e.toString());
            }
            catch (IOException e) {
                Log.e("States", e.toString());
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            //display progress dialog.
        }

        @Override
        protected void onPostExecute(String content) {
            if(!responseMsg.isEmpty()) {
                logging(responseMsg);
            }
            //Toast.makeText(getApplication(), "Данные загружены", Toast.LENGTH_SHORT).show();
        }
    }
    //---------------------------------------------------------------------------------------------
    public void send_cmd_1(View view) {
        logging("send_cmd_1()");
        if(wifi == null) {
            logging("wifi модуль не найден");
            return;
        }
        if(!wifi.isWifiEnabled()) {
            logging("wifi модуль не включен");
            return;
        }
        if(!check_all_permissions()) {
            return;
        }

        String addr = et_address.getText().toString();
        if(!addr.isEmpty()) {
            logging(addr);
            MyDownloadTask x = new MyDownloadTask();
            x.execute(addr);
            //x.execute("http://192.168.0.1");
            //x.execute("http://www.google.com");
        }
    }
    //---------------------------------------------------------------------------------------------
    public void send_cmd_2(View view) {
        logging("send_cmd_2()");
        if(wifi == null) {
            logging("wifi модуль не найден");
            return;
        }
        if(!wifi.isWifiEnabled()) {
            logging("wifi модуль не включен");
            return;
        }
        if(!check_all_permissions()) {
            return;
        }
    }
    //---------------------------------------------------------------------------------------------
    public void send_cmd_3(View view) {
        logging("send_cmd_3()");
        if(wifi == null) {
            logging("wifi модуль не найден");
            return;
        }
        if(!wifi.isWifiEnabled()) {
            logging("wifi модуль не включен");
            return;
        }
        if(!check_all_permissions()) {
            return;
        }
    }
    //---------------------------------------------------------------------------------------------
    public void send_cmd_4(View view) {
        logging("send_cmd_4()");
        if(wifi == null) {
            logging("wifi модуль не найден");
            return;
        }
        if(!wifi.isWifiEnabled()) {
            logging("wifi модуль не включен");
            return;
        }
        if(!check_all_permissions()) {
            return;
        }
    }
    //---------------------------------------------------------------------------------------------
}
