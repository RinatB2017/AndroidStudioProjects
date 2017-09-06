package com.example.boss.test_location;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void f_gps(View view) {
        startActivity(new Intent(this, GPS_activity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
    }

    public void f_net(View view) {
        startActivity(new Intent(this, NET_activity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
    }

    public void f_both(View view) {
        startActivity(new Intent(this, BOTH_activity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
    }
}
