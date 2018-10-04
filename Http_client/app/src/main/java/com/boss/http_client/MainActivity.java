package com.boss.http_client;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URL;

// https://metanit.com/java/android/15.1.php
// https://stackoverflow.com/questions/9573196/how-to-get-the-ip-of-the-wifi-hotspot-in-android

public class MainActivity extends AppCompatActivity {
    static final String LOG_TAG = "States";
    String contentText = null;
    WifiManager wifiManager;

    TextView tv_log;
    LinearLayout lv_container;
    ToggleButton btn_wifi;
    Button btn_scan;

    int port = 15000;

    Hosts hosts;

    private ProgressDialog scanProgressDialog;

    Handler h_print;
    Handler h_progress;
    Handler h_scan;

    //----------------------------------------------------------------------------------------
    public void send_log(String text) {
        if (text == null) {
            return;
        }
        Message msg = new Message();
        msg.obj = text;
        h_print.sendMessage(msg);
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

    //----------------------------------------------------------------------------------------
    private void init_log() {
        tv_log = (TextView) findViewById(R.id.logView);

        h_print = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                String text = (String) msg.obj;
                Log.i(LOG_TAG, text);
                tv_log.append(text + "\n");
            }
        };
    }

    //----------------------------------------------------------------------------------------
    private void init_tabs() {
        TabHost tabHost = (TabHost) findViewById(R.id.tabHost);

        tabHost.setup();

        TabHost.TabSpec tabSpec;

        tabSpec = tabHost.newTabSpec("main");
        tabSpec.setContent(R.id.tab_main);
        tabSpec.setIndicator("Main");
        tabHost.addTab(tabSpec);

        tabSpec = tabHost.newTabSpec("log");
        tabSpec.setContent(R.id.tab_log);
        tabSpec.setIndicator("Log");
        tabHost.addTab(tabSpec);

        tabHost.setCurrentTab(0);
    }

    //---------------------------------------------------------------------------------------------
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        savedInstanceState.putSerializable("hosts", hosts);
    }

    //----------------------------------------------------------------------------------------
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //TODO временный костыль
        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        init_log();

        if (savedInstanceState != null) {
            hosts = (Hosts) savedInstanceState.getSerializable("hosts");
        } else {
            hosts = new Hosts();

            Bundle bundle = new Bundle();
            getIntent().putExtras(bundle);
        }

        lv_container = (LinearLayout) findViewById(R.id.lv_container);

        btn_scan = (Button)findViewById(R.id.btn_scan);

        h_progress = new Handler() {
            public void handleMessage(Message msg) {
                if(scanProgressDialog == null) {
                    return;
                }
                if (scanProgressDialog.getProgress() < scanProgressDialog.getMax()) {
                    scanProgressDialog.setProgress(msg.what);
                }
            };
        };

        h_scan = new Handler() {
            public void handleMessage(Message msg) {
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                Fragment fo = new FragmentOne();

                Data data = (Data) msg.obj;

                Bundle bundle = new Bundle();

                //String host = "http://" + data.host + ":" + port;
                bundle.putString("caption", data.ip);
                bundle.putString("ip",      data.ip);
                bundle.putInt("port",       data.port);
                bundle.putInt("pwm_value",  data.pwm_value);

                fo.setArguments(bundle);

                fragmentTransaction.add(R.id.lv_container, fo);
                fragmentTransaction.commit();
            };
        };

        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        //Инициализируем элемент Toggle Button:
        btn_wifi = (ToggleButton) findViewById(R.id.wifi_switcher);

        //Настраиваем слушателя изменения состояния переключателя:
        btn_wifi.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    toggleWiFi(true);
                    send_log("Wi-Fi Включен!");
                    btn_scan.setEnabled(true);
                }
                //Если Wi-FI выключен - Toast сообщение об этом:
                else {
                    toggleWiFi(false);
                    send_log("Wi-Fi Выключен!");
                    btn_scan.setEnabled(false);
                }
            }
        });
        btn_wifi.setChecked(wifiManager.isWifiEnabled());

        btn_scan.setEnabled(wifiManager.isWifiEnabled());

        init_tabs();

        send_log("Init...");
    }
    //---------------------------------------------------------------------------------------------
    @Override
    protected void onStart() {
        super.onStart();
        send_log("onStart()");
    }

    //---------------------------------------------------------------------------------------------
    @Override
    protected void onRestart() {
        super.onRestart();
        send_log("onRestart()");
    }

    //---------------------------------------------------------------------------------------------
    void remove_all_fragments() {
        if(getFragmentManager().findFragmentById(R.id.lv_container) != null) {
            getFragmentManager()
                    .beginTransaction().
                    remove(getFragmentManager().findFragmentById(R.id.lv_container)).commit();

        }
    }

    //---------------------------------------------------------------------------------------------
    @Override
    protected void onResume() {
        super.onResume();
        send_log("onResume()");

        Bundle bundle = getIntent().getExtras();
        if(bundle == null)
        {
            bundle = new Bundle();
        }

        bundle.putBoolean("flag_is_running", false);

        remove_all_fragments();

        if(hosts.get_count() <= 0) {
            send_log("Error: hosts is empty");
            return;
        }
        send_log("==========> array.size = " + hosts.get_count());
        for(int n=0; n<hosts.get_count(); n++) {
            Data data = hosts.get_data(n);
            if(data != null) {
                send_log("N    ===> " + n);
                send_log("IP   ===> " + data.ip);
                send_log("PORT ===> " + data.port);
                send_log("PWM  ===> " + data.pwm_value);
                final Message message = new Message();
                message.obj = data;

                Runnable runnable = new Runnable() {
                    public void run() {
                        if (h_scan != null) {
                            h_scan.sendMessage(message);
                        }
                    }
                };
                Thread thread = new Thread(runnable);
                thread.start();
            }
        }
        getIntent().putExtras(bundle);
    }

    //---------------------------------------------------------------------------------------------
    @Override
    protected void onPause() {
        super.onPause();
        send_log("onPause()");
    }

    //---------------------------------------------------------------------------------------------
    @Override
    protected void onStop() {
        super.onStop();
        send_log("onStop()");
    }

    //---------------------------------------------------------------------------------------------
    @Override
    protected void onDestroy() {
        super.onDestroy();
        send_log("onDestroy()");
    }

    //----------------------------------------------------------------------------------------
    public void scan(final View view) {
        send_log("Scan");

        remove_all_fragments();

        //---
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int ip = wifiInfo.getIpAddress();
        int speed = wifiInfo.getLinkSpeed();

        @SuppressLint("DefaultLocale") String result = String.format("%d.%d.%d.%d",
                (ip & 0xff),
                (ip >> 8 & 0xff),
                (ip >> 16 & 0xff),
                (ip >> 24 & 0xff));

        send_log("addr: " + String.valueOf(result));
        send_log("speed: " + String.valueOf(speed));
        //---

        final int minValue = 2;
        final int maxValue = 254;

        scanProgressDialog = new ProgressDialog(MainActivity.this);
        scanProgressDialog.setCancelable(false);
        scanProgressDialog.setTitle("Scanning address " + minValue + " to " + maxValue);
        scanProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        scanProgressDialog.incrementProgressBy(1);
        scanProgressDialog.setProgress(minValue);
        scanProgressDialog.setMax(maxValue);
        scanProgressDialog.show();

        final byte[] bytes = new byte[4];

        bytes[0] = (byte)(ip & 0xff);
        bytes[1] = (byte)(ip >> 8 & 0xff);
        bytes[2] = (byte)(ip >> 16 & 0xff);

        Runnable runnable_scan = new Runnable() {
            public void run() {
                Bundle bundle = getIntent().getExtras();
                if(bundle != null) {
                    bundle.putBoolean("flag_is_running", true);
                    getIntent().putExtras(bundle);
                }

                hosts.clean_all();
                boolean flag_scan_is_running = true;
                for (int i = minValue; i < maxValue; i++) {
                    bundle = getIntent().getExtras();
                    if(bundle != null)
                    {
                        flag_scan_is_running = bundle.getBoolean("flag_is_running");
                    }
                    if(!flag_scan_is_running)
                    {
                        return;
                    }
                    if(h_progress != null) {
                        h_progress.sendEmptyMessage(i);
                    }
                    bytes[3] = (byte)i;

                    boolean ok = test(bytes);
                    if (ok) {
                        @SuppressLint("DefaultLocale") String ip = String.format("%d.%d.%d.%d",
                            (bytes[0] & 0xff),
                            (bytes[1] & 0xff),
                            (bytes[2] & 0xff),
                            (bytes[3] & 0xff));

                        //---
                        hosts.add_data(ip, port, 0);
                        //---

                        Data data = new Data();
                        data.ip = ip;
                        data.port = port;
                        data.pwm_value = 0;

                        Message message = new Message();
                        message.obj = data;
                        if(h_scan != null) {
                            h_scan.sendMessage(message);
                        }
                    }
                }

                if(scanProgressDialog != null) {
                        scanProgressDialog.dismiss();
                }
            }
        };
        Thread thread = new Thread(runnable_scan);
        thread.start();
    }

    public void exec_task(Data data) {
        send_log(data.ip);
        String temp = "http://" + data.ip + ":" + data.port + "/pwm/" + String.valueOf(data.pwm_value);

        for(int n=0; n<hosts.get_count(); n++) {
            Data host = hosts.get_data(n);
            if(host.ip == data.ip) {
                host.pwm_value = data.pwm_value;
                hosts.set_data(n, host);
            }
        }

        new ProgressTask().execute(temp);
    }

    //----------------------------------------------------------------------------------------
    //Описываем сам метод включения Wi-Fi:
    public void toggleWiFi(boolean status) {
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        //Статус true соответствует включенному состоянию Wi-Fi, мы включаем
        //его с помощью команды wifiManager.setWifiEnabled(true):
        if (status == true && !wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
            btn_scan.setEnabled(true);
        }
        //А статус false соответствует выключенному состоянию Wi-Fi мы выключаем
        // его с помощью команды wifiManager.setWifiEnabled(false):
        else if (status == false && wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(false);
            btn_scan.setEnabled(false);
        }
    }

    //----------------------------------------------------------------------------------------
    private class ProgressTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... path) {

            String content;
            try {
                content = getContent(path[0]);
            } catch (IOException ex) {
                content = ex.getMessage();
            }

            return content;
        }

        @Override
        protected void onPostExecute(String content) {
            contentText = content;
            send_log("Данные загружены");
            send_log(contentText);
        }

        private String getContent(String path) throws IOException {
            BufferedReader reader = null;
            try {
                URL url = new URL(path);
                HttpURLConnection c = (HttpURLConnection) url.openConnection();
                c.setRequestMethod("GET");
                c.setReadTimeout(10000);
                c.connect();
                reader = new BufferedReader(new InputStreamReader(c.getInputStream()));
                StringBuilder buf = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null) {
                    buf.append(line + "\n");
                }
                return (buf.toString());
            } finally {
                if (reader != null) {
                    reader.close();
                }
            }
        }
    }

    //----------------------------------------------------------------------------------------
    private boolean test(byte[] bytes) {
        Socket socket = new Socket();
        int timeout = 150;
        boolean ok = false;

        try {
            socket.setTcpNoDelay(true);
            InetSocketAddress addr = new InetSocketAddress(InetAddress.getByAddress(bytes), port);
            socket.connect(addr, timeout);
            ok = true;
        } catch (IOException ignored) {
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
            }
        }
        return ok;
    }

    //----------------------------------------------------------------------------------------
}
