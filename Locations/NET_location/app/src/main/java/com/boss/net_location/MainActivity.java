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

public class MainActivity extends AppCompatActivity {

    private TextView tvEnabledNet;
    private TextView tvStatusNet;
    private TextView tvLocationNet;

    private TextView logView;
    private final String TAG = "States";

    private static final int RECORD_REQUEST_CODE = 101;

    private LocationManager locationManager;

    //---
    protected void requestPermission(String permissionType, int requestCode) {
        int permission = ContextCompat.checkSelfPermission(this,
                permissionType);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{permissionType},
                    requestCode );
        }
    }
    //---

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

        requestPermission(Manifest.permission.ACCESS_COARSE_LOCATION, RECORD_REQUEST_CODE);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        /*
        List<String> matchingProviders = locationManager.getAllProviders();
        for (String provider: matchingProviders) {
            logging("Provider: " + provider);
            }
        */
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
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            logging("onResume: error permission");
            return;
        }
        locationManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER,
                60 * 1000,
                10,
                locationListener);
        checkEnabled();
    }

    public void update(View view) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            logging("onResume: error permission");
            return;
        }
        logging("update");
        if(!locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            logging("isProviderEnabled return FALSE");
        }
        locationManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER,
                60 * 1000,
                10,
                locationListener);
        checkEnabled();
        logging("OK");
    }


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
            if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                logging("onProviderEnabled: error permission");
                return;
            }
            checkEnabled();
            showLocation(locationManager.getLastKnownLocation(provider));
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            if (provider.equals(LocationManager.NETWORK_PROVIDER)) {
                tvStatusNet.setText("Status: " + String.valueOf(status));
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
        }
    }

    private String formatLocation(Location location) {
        if (location == null) {
            logging("formatLocation: location is NULL");
            return "";
        }
        return String.format(
                "Coordinates: \nlat = %1$.4f\nlon = %2$.4f\ntime = %3$tF %3$tT",
                location.getLatitude(),
                location.getLongitude(),
                new Date(location.getTime()));
    }

    private void checkEnabled() {
        tvEnabledNet.setText("Enabled: "
                + locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER));
    }

    public void onClickLocationSettings(MainActivity view) {
        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
    };
}
