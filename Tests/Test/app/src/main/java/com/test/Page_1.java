package com.test;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class Page_1 extends TestingLogging{

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
    setContentView(R.layout.page_1);

    logView = (TextView)findViewById(R.id.logView);

    logging(getLocalClassName() + ": onCreate()");
  }
  //---------------------------------------------------------------------------------------------
  public void onClick(View view) {
    // переключение активити
    Intent intent = new Intent(this, Page_2.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    startActivity(intent);
  }
  //---------------------------------------------------------------------------------------------
}
