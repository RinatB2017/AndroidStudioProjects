package com.test;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class Page_1 extends Test_class{

  TextView  logView;

  //---------------------------------------------------------------------------------------------
  void logging(String text) {
    Log.v(TAG, text + "\n");
    logView.append(text + "\n");
  }
  //---------------------------------------------------------------------------------------------
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.page_1);

    logView = (TextView)findViewById(R.id.logView);

    logging("Page_1: onCreate()");
    test("Page_1");
  }
  //---------------------------------------------------------------------------------------------
  @Override
  protected void onRestart() {
    super.onRestart();
    logging("Page_1: onRestart()");
  }
  //---------------------------------------------------------------------------------------------
  @Override
  protected void onStart() {
    super.onStart();
    logging("Page_1: onStart()");
  }
  //---------------------------------------------------------------------------------------------
  @Override
  protected void onResume() {
    super.onResume();
    logging("Page_1: onResume()");
  }
  //---------------------------------------------------------------------------------------------
  @Override
  protected void onPause() {
    super.onPause();
    logging("Page_1: onPause()");
  }
  //---------------------------------------------------------------------------------------------
  @Override
  protected void onStop() {
    super.onStop();
    logging("Page_1: onStop()");
  }
  //---------------------------------------------------------------------------------------------
  @Override
  protected void onDestroy() {
    super.onDestroy();
    logging("Page_1: onDestroy()");
  }
  //---------------------------------------------------------------------------------------------
  public void onClick(View view) {
    // переключение активити
    Intent intent = new Intent(this, Page_2.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    startActivity(intent);
  }
  //---------------------------------------------------------------------------------------------
}
