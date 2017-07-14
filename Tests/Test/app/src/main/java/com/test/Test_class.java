package com.test;

import android.util.Log;

/**
 * Created by boss on 10.07.17.
 */

public class Test_class {

    final String TAG = "States";
    int count = 666;

    public void test() {
        Log.v(TAG, "Ура!");
    }

    public int get_count() {
        return count;
    }

}
