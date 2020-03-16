package com.boss.for_testing;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;

public class TestClass extends Activity {
    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

//        dialog = new Dialog(this);
//        dialog.setTitle("Ура!");
//        dialog.show();
    }
}
