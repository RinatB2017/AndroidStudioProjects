package com.boss.template;

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
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

// http://developer.alexanderklimov.ru/android/theory/lifecycle.php

public class MainActivity extends AppCompatActivity{
    final String LOG_TAG = "States";

    //---
    String[] args = {"cat", "/proc/net/arp"};
    ArrayList<Node> listNote;
    String queryString = "https://www.macvendorlookup.com/api/v2/";
    //---

    TextView tv_log;

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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    //---------------------------------------------------------------------------------------------
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_clear_log:
                tv_log.setText("");
                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //---------------------------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv_log = (TextView) findViewById(R.id.logView);
        tv_log.setTextColor(Color.BLACK);

        //---
        listNote = new ArrayList<>();
        //---

        logging("onCreate()");
    }

    //---------------------------------------------------------------------------------------------
    @Override
    protected void onStart()
    {
        super.onStart();
        logging("onStart()");
    }

    //---------------------------------------------------------------------------------------------
    @Override
    protected void onRestart()
    {
        super.onRestart();
        logging("onRestart()");
    }

    //---------------------------------------------------------------------------------------------
    @Override
    protected void onResume()
    {
        super.onResume();
        logging("onResume()");
    }

    //---------------------------------------------------------------------------------------------
    @Override
    protected void onPause()
    {
        super.onPause();
        logging("onPause()");
    }

    //---------------------------------------------------------------------------------------------
    @Override
    protected void onStop()
    {
        super.onStop();
        logging("onStop()");
    }

    //---------------------------------------------------------------------------------------------
    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        logging("onDestroy()");
    }

    //---------------------------------------------------------------------------------------------
    private String toRead()
    {
        ProcessBuilder cmd;
        String result="";

        try{
            cmd = new ProcessBuilder(args);

            Process process = cmd.start();
            InputStream in = process.getInputStream();
            byte[] re = new byte[1024];
            while(in.read(re) != -1){
                System.out.println(new String(re));
                result = result + new String(re);
            }
            in.close();
        } catch(IOException ex){
            ex.printStackTrace();
        }
        return result;
    }

    //---------------------------------------------------------------------------------------------
    public void click(View view) {
        //tv_log.setText(toRead());

        readAddresses();
        tv_log.setText("");
        for(int i=0; i<listNote.size(); i++){
            tv_log.append(i + " ");
            tv_log.append(listNote.get(i).toString());
            tv_log.append("\n");
        }
    }

    private void readAddresses() {
        listNote.clear();
        BufferedReader bufferedReader = null;

        try {
            bufferedReader = new BufferedReader(new FileReader("/proc/net/arp"));

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                String[] splitted = line.split(" +");
                if (splitted != null && splitted.length >= 4) {
                    String ip = splitted[0];
                    String mac = splitted[3];
                    if (mac.matches("..:..:..:..:..:..")) {
                        if(!mac.equals("00:00:00:00:00:00"))
                        {
                            Node thisNode = new Node(ip, mac);
                            listNote.add(thisNode);
                            //logging(sendQuery(mac));
                            /*
                            try {
                                String jsonBody = sendQuery(mac);
                                logging(jsonBody);
                            } catch (IOException e) {
                            }
                            */
                        }
                    }
                }
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally{
            try {
                bufferedReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String sendQuery(String qMac) throws IOException{
        String result = "";

        URL searchURL = new URL(queryString + qMac);

        HttpsURLConnection httpsURLConnection = (HttpsURLConnection) searchURL.openConnection();

        int code = httpsURLConnection.getResponseCode();
        logging("code " + String.valueOf(code));

        if(httpsURLConnection.getResponseCode() == HttpsURLConnection.HTTP_OK) {
            InputStreamReader inputStreamReader = new InputStreamReader(httpsURLConnection.getInputStream());
            BufferedReader bufferedReader = new BufferedReader(
                    inputStreamReader,
                    8192);

            String line = null;
            while((line = bufferedReader.readLine()) != null){
                result += line;
            }

            bufferedReader.close();
        }

        return result;
    }

    class Node {
        String ip;
        String mac;

        Node(String ip, String mac){
            this.ip = ip;
            this.mac = mac;
        }

        @Override
        public String toString() {
            return ip + " " + mac;
        }

    }

    //---------------------------------------------------------------------------------------------
}
