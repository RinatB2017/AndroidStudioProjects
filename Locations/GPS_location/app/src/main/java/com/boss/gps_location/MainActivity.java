package com.boss.gps_location;

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
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private TextView tvEnabledGPS;
    private TextView tvStatusGPS;
    private TextView tvLocationGPS;

    TabHost tabHost;

    Location begin_loc;
    double diff_dist = 0;
    boolean first_loc = false;

    private TextView logView;
    private final String TAG = "States";

    private static final int RECORD_REQUEST_CODE = 101;

    private LocationManager locationManager;

    //---------------------------------------------------------------------------------------------
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
    //private static final int RECORD_REQUEST_CODE = 101;
    //requestPermission(Manifest.permission.ACCESS_FINE_LOCATION, RECORD_REQUEST_CODE);
    //---

    //---------------------------------------------------------------------------------------------
    void logging(String text) {
        logView.append(text + "\n");
        Log.v(TAG, text);
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

    //---------------------------------------------------------------------------------------------
    void init_tabs() {
        tabHost = (TabHost) findViewById(R.id.tabHost);

        tabHost.setup();

        TabHost.TabSpec tabSpec;

        tabSpec = tabHost.newTabSpec("tab_main");
        tabSpec.setContent(R.id.tab_main);
        tabSpec.setIndicator(getString(R.string.main));
        tabHost.addTab(tabSpec);

        tabSpec = tabHost.newTabSpec("tab_log");
        tabSpec.setContent(R.id.tab_log);
        tabSpec.setIndicator(getString(R.string.log));
        tabHost.addTab(tabSpec);

        tabHost.setCurrentTab(0);
        //---

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.weight = 1;

        TabWidget tabWidget = tabHost.getTabWidget();
        for (int i = 0; i < tabHost.getTabWidget().getChildCount(); i++) {
            tabHost.getTabWidget().getChildAt(i).setLayoutParams(layoutParams);
            TextView textView = (TextView) tabWidget.getChildAt(i).findViewById(android.R.id.title);
        }
    }

    //---------------------------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvEnabledGPS = (TextView) findViewById(R.id.tvEnabledGPS);
        tvStatusGPS = (TextView) findViewById(R.id.tvStatusGPS);
        tvLocationGPS = (TextView) findViewById(R.id.tvLocationGPS);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        logView = (TextView)findViewById(R.id.logView);

        init_tabs();

        logging("Init");
    }

    //---------------------------------------------------------------------------------------------
    @Override
    protected void onResume() {
        super.onResume();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            logging("onResume: error permission");
            return;
        }
        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                0, //1000,
                0, //1,
                locationListener);
        checkEnabled();
    }

    //---------------------------------------------------------------------------------------------
    public void update(View view) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            logging("onResume: error permission");
            return;
        }
        logging("update");
        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                0, //1000,
                0, //1,
                locationListener);
        checkEnabled();
        logging("OK");
    }


    //---------------------------------------------------------------------------------------------
    @Override
    protected void onPause() {
        super.onPause();
        locationManager.removeUpdates(locationListener);
    }

    //---------------------------------------------------------------------------------------------
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
            checkEnabled();
            if (ActivityCompat.checkSelfPermission(getParent(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(getParent(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                logging("onProviderEnabled: error permission");
                return;
            }
            showLocation(locationManager.getLastKnownLocation(provider));
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            if (provider.equals(LocationManager.GPS_PROVIDER)) {
                tvStatusGPS.setText("Status: " + String.valueOf(status));
            }
        }
    };

    //---------------------------------------------------------------------------------------------
    private void showLocation(Location location) {
        if (location == null) {
            logging("showLocation: location is NULL");
            return;
        }
        if (location.getProvider().equals(LocationManager.GPS_PROVIDER)) {
            tvLocationGPS.setText(formatLocation(location));
        }
    }

    //---------------------------------------------------------------------------------------------
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

    //---------------------------------------------------------------------------------------------
    private void checkEnabled() {
        tvEnabledGPS.setText("Enabled: "
                + locationManager
                .isProviderEnabled(LocationManager.GPS_PROVIDER));
    }

    //---------------------------------------------------------------------------------------------
    public void onClickLocationSettings(MainActivity view) {
        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
    };

    //---------------------------------------------------------------------------------------------
    public void test(View view) {

        logging("Test");
    }

    //---------------------------------------------------------------------------------------------

}
