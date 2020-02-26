package com.example.boss.test_location;

import android.content.Intent;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;

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
