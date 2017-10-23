package com.test_wifi;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.SystemClock;
import android.util.Log;
import android.widget.TextView;

import java.util.List;

public class WiFiConnector {
    public static final String MAIN_LOG_TAG = "States";

    private boolean wifiEnabled;
    private boolean isConnected = false;
    private String mSSID;
    private String mPassword;
    private Context mContext;
    private WifiManager wifiManager;
    private WifiConfiguration wifiConfig;
    private WifiReceiver wifiResiver;

    TextView tv_view;

    public WiFiConnector(Context context, TextView log_view)
    {
        if(context != null)
        {
            mContext = context;
        }
        //
        tv_view = log_view;

        // Создаём новый объект для подключения к конкретной точке
        wifiConfig = new WifiConfiguration();
        // Сканер Wi-Fi, который нам будет помогать подключаться к нужной точке
        wifiManager = (WifiManager) mContext.getSystemService(mContext.WIFI_SERVICE);
        // Узнаём, включен ли Wi-Fi
        wifiEnabled = wifiManager.isWifiEnabled();
        // Наш Resiver, который будет подключать нас столько, сколько нам понадобится, пока не будет подключена нужная точка
        wifiResiver = new WifiReceiver();
        //
        if( ! wifiEnabled) {
            wifiManager.setWifiEnabled(true);
        }
    }

    public void connect(String SSID, String Password)
    {
        mSSID = SSID;
        mPassword = Password;
        mContext.registerReceiver(wifiResiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        wifiManager.startScan();
    }

    public boolean isConnect()
    {
       return isConnected;
    }

    /*
    * Receiver, который каждый раз запускает сканер сети
    */
    public class WifiReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context c, Intent intent) {
            // Сканируем Wi-Fi точки и узнаём, какие из них доступны
            List<ScanResult> results = wifiManager.getScanResults();
            // Проходимся по всем возможным точкам
            for (final ScanResult ap : results) {
                Log.e(MAIN_LOG_TAG, "wifi scan: " + ap.SSID.toString().trim());
                // Ищем нужную нам точку с помощью if'а (будет находить ту, которую вы ввели)
                if(ap.SSID.toString().trim().equals(mSSID))
                {
                    Log.e(MAIN_LOG_TAG, "TRUE:" + ap.SSID.toString().trim());

                    wifiConfig.SSID = "\"" + mSSID + "\"";
                    wifiConfig.preSharedKey = "\"" + mPassword + "\"";

                    // Получаем ID сети и пытаемся к ней подключиться
                    wifiManager.disconnect();
                    int netId = wifiManager.addNetwork(wifiConfig);
                    // Если Wi-Fi выключен, то включаем его
                    wifiManager.enableNetwork(netId, true);
                    // Если же он включен, но подключен к другой сети, то перегружаем Wi-Fi
                    wifiManager.reconnect();
                    //
                    mContext.unregisterReceiver(wifiResiver);
                    //
                    SystemClock.sleep(2000);
                    //
                    WifiManager wifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
                    final WifiInfo connectionInfo = wifiManager.getConnectionInfo();
                    if(connectionInfo != null)
                    {
                        tv_view.append("Connecting \n");
                        Log.d(MAIN_LOG_TAG,"NetworkMonitor wifi ssid: " + connectionInfo.getSSID());
                        //
                        if(connectionInfo.getSSID().replace("\"", "").equals(mSSID))
                            isConnected = true;
                        else
                            isConnected = false;
                    }
                    else
                        isConnected = false;

                    break;
                }
            }
        }
    }
}
