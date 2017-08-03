package com.test;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class Page_2 extends Test_class {

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

    logView = (TextView)findViewById(R.id.logView);

    logging("Page_2: onCreate()");
    test("Page_2");
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
