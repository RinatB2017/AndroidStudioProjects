package com.boss.net_location;

import java.util.Date;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class MainActivity extends AppCompatActivity {

    private TextView tvEnabledNet;
    private TextView tvStatusNet;
    private TextView tvLocationNet;

    private TextView logView;
    private final String TAG = "States";

    private final int CODE_PERMISSIONS = 0;

    private LocationManager locationManager;

    Location begin_loc;
    double diff_dist = 0;
    boolean first_loc = false;

    void logging(String text) {
        logView.append(text + "\n");
        Log.i(TAG, text);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvEnabledNet = (TextView) findViewById(R.id.tvEnabledNet);
        tvStatusNet = (TextView) findViewById(R.id.tvStatusNet);
        tvLocationNet = (TextView) findViewById(R.id.tvLocationNet);

        logView = (TextView)findViewById(R.id.logView);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        //---
        String[] neededPermissions = {
                Manifest.permission.CHANGE_WIFI_STATE,
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.ACCESS_FINE_LOCATION
        };
        ActivityCompat.requestPermissions( this, neededPermissions, CODE_PERMISSIONS );
        //---
        //locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
    }

    //---
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId())
        {
            case R.id.action_geolocation:
                onClickLocationSettings(MainActivity.this);
                break;

            case R.id.clear_log:
                logView.setText("");
                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    //---

    @Override
    protected void onResume() {
        super.onResume();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CHANGE_WIFI_STATE) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_WIFI_STATE) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            logging("onResume: error permission");
            return;
        }
        logging("requestLocationUpdates");
        locationManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER,
                0, //10 * 1000,
                0, //10,
                locationListener);
        checkEnabled();
    }

    /*
    public void update(View view) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CHANGE_WIFI_STATE) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_WIFI_STATE) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            logging("onResume: error permission");
            return;
        }
        logging("update");
        if(!locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            logging("isProviderEnabled return FALSE");
        }
        locationManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER,
                0, //10 * 1000,
                0, //10,
                locationListener);
        checkEnabled();
        logging("OK");
    }
    */


    @Override
    protected void onPause() {
        super.onPause();
        locationManager.removeUpdates(locationListener);
    }

    private LocationListener locationListener = new LocationListener() {

        @Override
        public void onLocationChanged(Location location) {
            showLocation(location);
        }

        @Override
        public void onProviderDisabled(String provider) {
            checkEnabled();
        }

        @Override
        public void onProviderEnabled(String provider) {
            if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CHANGE_WIFI_STATE) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_WIFI_STATE) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(getApplicationContext(), ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                logging("onProviderEnabled: error permission");
                return;
            }
            //checkEnabled();
            showLocation(locationManager.getLastKnownLocation(provider));
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            if (provider.equals(LocationManager.NETWORK_PROVIDER)) {
                tvStatusNet.setText("Status: " + String.valueOf(status));
                logging("Status: " + String.valueOf(status));
            }
        }
    };

    private void showLocation(Location location) {
        if (location == null) {
            logging("showLocation: location is NULL");
            return;
        }
        if (location.getProvider().equals(LocationManager.NETWORK_PROVIDER)) {
            tvLocationNet.setText(formatLocation(location));
            logging(formatLocation(location));
        }
    }

    private String formatLocation(Location location) {
        if (location == null) {
            logging("formatLocation: location is NULL");
            return "";
        }
        //TODO
        if(!first_loc) {
            begin_loc = location;
            first_loc = true;
        }
        else {
            double temp_diff_lat = begin_loc.distanceTo(location);
            if(temp_diff_lat != diff_dist)
            {
                diff_dist = temp_diff_lat;
            }
        }
        //---
        StringBuilder temp = new StringBuilder();
        temp.append("Coordinates: \n");
        temp.append(String.format("lat = %1$.4f\n", location.getLatitude()));
        temp.append(String.format("lon = %1$.4f\n", location.getLongitude()));
        temp.append(String.format("accuracy = %1$.4f\n", location.getAccuracy()));
        temp.append(String.format("speed = %1$.4f\n", location.getSpeed()));
        temp.append(String.format("altitude = %1$.4f\n", location.getAltitude()));
        temp.append(String.format("bearing = %1$.4f\n", location.getBearing()));
        temp.append(String.format("diff = %1$.4f\n", diff_dist));
        temp.append(String.format("time = %1$tF %1$tT", new Date(location.getTime())));
        return temp.toString();
    }

    private void checkEnabled() {
        tvEnabledNet.setText("Enabled: "
                + locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER));
        logging("Enabled: "
                + locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER));
    }

    public void onClickLocationSettings(MainActivity view) {
        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
    };
}
