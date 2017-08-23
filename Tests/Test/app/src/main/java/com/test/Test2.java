package com.test;

import android.util.Log;

public class Test2 {

    final String TAG = "States";

    void logging(String text) {
        Log.v(TAG, text + "\n");
    }

    public void test() {
        logging("Test2:test()");
        int n = 0;
        for(n = 0; n<10; n++) {
            logging(String.valueOf(n));
            if(n == 5)
                throw new RuntimeException("Test: test()");
        }
        n = 0;
    }

    public void test2() {
        logging("Test2:test2()");
        throw new RuntimeException("Test: test2()");
    }
}
