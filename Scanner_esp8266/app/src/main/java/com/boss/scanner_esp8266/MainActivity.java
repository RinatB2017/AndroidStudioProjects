package com.boss.scanner_esp8266;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.sqlite.SQLiteException;
import android.graphics.Color;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.concurrent.atomic.AtomicInteger;

// http://developer.alexanderklimov.ru/android/theory/lifecycle.php

public class MainActivity extends AppCompatActivity  implements MainAsyncResponse{
    final String LOG_TAG = "States";

    private final static int TIMER_INTERVAL = 1500;

    private Wireless wifi;
    private Database db;
    private String cachedWanIp;
    private Handler signalHandler = new Handler();
    private TextView internalIp;
    private TextView signalStrength;
    private TextView ssid;
    private TextView bssid;

    TextView tv_log;

    private BroadcastReceiver receiver = new BroadcastReceiver() {

        /**
         * Detect if a network connection has been lost or established
         * @param context
         * @param intent
         */
        @Override
        public void onReceive(Context context, Intent intent) {
            NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
            if (info == null) {
                return;
            }

            getNetworkInfo(info);
        }

    };

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
    public void click(View view)
    {

    }

    //---------------------------------------------------------------------------------------------
    /**
     * Sets up the device's MAC address and vendor
     */
    public void setupMac() {
        TextView macAddress = findViewById(R.id.deviceMacAddress);
        TextView macVendor = findViewById(R.id.deviceMacVendor);

        try {
            if (!wifi.isEnabled()) {
                macAddress.setText(R.string.wifiDisabled);
                macVendor.setText(R.string.wifiDisabled);

                return;
            }

            String mac = wifi.getMacAddress();
            macAddress.setText(mac);

            String vendor = Host.findMacVendor(mac, db);
            macVendor.setText(vendor);
        } catch (UnknownHostException | SocketException | Wireless.NoWifiManagerException e) {
            macAddress.setText(R.string.noWifiConnection);
            macVendor.setText(R.string.noWifiConnection);
        } catch (IOException | SQLiteException | UnsupportedOperationException e) {
            macVendor.setText(R.string.getMacVendorFailed);
        } catch (Wireless.NoWifiInterface e) {
            macAddress.setText(R.string.noWifiInterface);
        }
    }

    //---------------------------------------------------------------------------------------------
    /**
     * Gets network information about the device and updates various UI elements
     */
    private void getNetworkInfo(NetworkInfo info) {
        setupMac();
        getExternalIp();

        final Resources resources = getResources();
        final Context context = getApplicationContext();
        try {
            boolean enabled = wifi.isEnabled();
            if (!info.isConnected() || !enabled) {
                signalHandler.removeCallbacksAndMessages(null);
                internalIp.setText(Wireless.getInternalMobileIpAddress());
            }

            if (!enabled) {
                signalStrength.setText(R.string.wifiDisabled);
                ssid.setText(R.string.wifiDisabled);
                bssid.setText(R.string.wifiDisabled);

                return;
            }
        } catch (Wireless.NoWifiManagerException e) {
            Errors.showError(context, resources.getString(R.string.failedWifiManager));
        }

        if (!info.isConnected()) {
            signalStrength.setText(R.string.noWifiConnection);
            ssid.setText(R.string.noWifiConnection);
            bssid.setText(R.string.noWifiConnection);

            return;
        }

        signalHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                int signal;
                int speed;
                try {
                    speed = wifi.getLinkSpeed();
                } catch (Wireless.NoWifiManagerException e) {
                    Errors.showError(context, resources.getString(R.string.failedLinkSpeed));
                    return;
                }
                try {
                    signal = wifi.getSignalStrength();
                } catch (Wireless.NoWifiManagerException e) {
                    Errors.showError(context, resources.getString(R.string.failedSignal));
                    return;
                }

                signalStrength.setText(String.format(resources.getString(R.string.signalLink), signal, speed));
                signalHandler.postDelayed(this, TIMER_INTERVAL);
            }
        }, 0);

        getInternalIp();

        String wifiSsid;
        String wifiBssid;
        try {
            wifiSsid = wifi.getSSID();
        } catch (Wireless.NoWifiManagerException e) {
            Errors.showError(context, resources.getString(R.string.failedSsid));
            return;
        }
        try {
            wifiBssid = wifi.getBSSID();
        } catch (Wireless.NoWifiManagerException e) {
            Errors.showError(context, resources.getString(R.string.failedBssid));
            return;
        }

        ssid.setText(wifiSsid);
        bssid.setText(wifiBssid);
    }

    /**
     * Wrapper for getting the external IP address
     * We can control whether or not to do this based on the user's preference
     * If the user doesn't want this then hide the appropriate views
     */
    private void getExternalIp() {
        TextView label = findViewById(R.id.externalIpAddressLabel);
        TextView ip = findViewById(R.id.externalIpAddress);

        if (UserPreference.getFetchExternalIp(this)) {
            label.setVisibility(View.VISIBLE);
            ip.setVisibility(View.VISIBLE);

            if (cachedWanIp == null) {
                wifi.getExternalIpAddress(this);
            }
        } else {
            label.setVisibility(View.GONE);
            ip.setVisibility(View.GONE);
        }
    }

    /**
     * Wrapper method for getting the internal wireless IP address.
     * This gets the netmask, counts the bits set (subnet size),
     * then prints it along side the IP.
     */
    private void getInternalIp() {
        try {
            int netmask = wifi.getInternalWifiSubnet();
            String internalIpWithSubnet = wifi.getInternalWifiIpAddress(String.class) + "/" + Integer.toString(netmask);
            internalIp.setText(internalIpWithSubnet);
        } catch (UnknownHostException | Wireless.NoWifiManagerException e) {
            Errors.showError(getApplicationContext(), getResources().getString(R.string.notConnectedLan));
        }
    }

    @Override
    public void processFinish(Host h, AtomicInteger i) {

    }

    @Override
    public void processFinish(int output) {

    }

    @Override
    public void processFinish(String output) {

    }

    @Override
    public void processFinish(boolean output) {

    }

    @Override
    public <T extends Throwable> void processFinish(T output) {

    }
}
