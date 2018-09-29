package com.test;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;

/**
 * Created by boss on 10.07.17.
 */

public class Test_class extends AppCompatActivity {

    final String TAG = "States";
    int cnt = 0;

    void logging(String text) {
        Log.v(TAG, text + "\n");
    }

    public void test(String text) {
        logging(text);
    }

    public int get_count() {
        return cnt;
    }

    public float calc(float a, float b) {
        return a / b;
    }
}
