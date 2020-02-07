package com.test;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class Test_Activity extends AppCompatActivity {

    final String TAG = "States";

    void logging(String text) {
        Log.v(TAG, text + "\n");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void test(View view) {
        logging("i'm test!");

        Intent intent2 = new Intent(this, MainActivity.class);
        startActivity(intent2);
    }
}
