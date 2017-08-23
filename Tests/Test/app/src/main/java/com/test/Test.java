package com.test;

import android.util.Log;

public class Test {

    final String TAG = "States";

    void logging(String text) {
        Log.v(TAG, text + "\n");
    }

    public void test() {
        logging("Test:test()");
        Test2 test = new Test2();
        test.test();
    }

    public void test2() {
        logging("Test:test2()");
        Test2 test = new Test2();
        test.test2();
    }
}
