package com.test;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Page_2 extends AppCompatActivity {

  final String TAG = "States";
  TextView logView;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.page_2);
    Log.d(TAG, "Page_2: onCreate()");

    logView = (TextView)findViewById(R.id.logView);

    logView.append("Page_2\n");
  }

  @Override
  protected void onRestart() {
    super.onRestart();
    Log.d(TAG, "Page_2: onRestart()");
  }

  @Override
  protected void onStart() {
    super.onStart();
    Log.d(TAG, "Page_2: onStart()");
  }

  @Override
  protected void onResume() {
    super.onResume();
    Log.d(TAG, "Page_2: onResume()");
  }

  @Override
  protected void onPause() {
    super.onPause();
    Log.d(TAG, "Page_2: onPause()");
  }

  @Override
  protected void onStop() {
    super.onStop();
    Log.d(TAG, "Page_2: onStop()");
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    Log.d(TAG, "Page_2: onDestroy()");
  }

  public void onClick(View view) {
    // переключение активити
    Intent intent = new Intent(this, MainActivity.class);
    startActivity(intent);
  }
}
