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

  //---------------------------------------------------------------------------------------------
  void logging(String text) {
    Log.v(TAG, text + "\n");
    logView.append(text + "\n");
  }
  //---------------------------------------------------------------------------------------------
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.page_2);
    Log.d(TAG, "Page_2: onCreate()");

    logView = (TextView)findViewById(R.id.logView);

    logging("Page_2");
  }
  //---------------------------------------------------------------------------------------------
  @Override
  protected void onRestart() {
    super.onRestart();
    logging("Page_2: onRestart()");
  }
  //---------------------------------------------------------------------------------------------
  @Override
  protected void onStart() {
    super.onStart();
    logging("Page_2: onStart()");
  }
  //---------------------------------------------------------------------------------------------
  @Override
  protected void onResume() {
    super.onResume();
    logging("Page_2: onResume()");
  }
  //---------------------------------------------------------------------------------------------
  @Override
  protected void onPause() {
    super.onPause();
    logging("Page_2: onPause()");
  }
  //---------------------------------------------------------------------------------------------
  @Override
  protected void onStop() {
    super.onStop();
    logging("Page_2: onStop()");
  }
  //---------------------------------------------------------------------------------------------
  @Override
  protected void onDestroy() {
    super.onDestroy();
    logging("Page_2: onDestroy()");
  }
  //---------------------------------------------------------------------------------------------
  public void onClick(View view) {
    // переключение активити
    Intent intent = new Intent(this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    startActivity(intent);
  }
  //---------------------------------------------------------------------------------------------
}
