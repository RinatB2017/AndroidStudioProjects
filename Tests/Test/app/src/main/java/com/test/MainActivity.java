package com.test;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    final String TAG = "States";
    TextView logView;

    CheckBox cb1;
    CheckBox cb2;
    CheckBox cb3;

    private static final int MY_PERMISSIONS_REQUEST_READ_PHONE_STATE = 0;

    public SharedPreferences mSet;
    public static final String NAME_PREFERENCES = "mysetting";
    public static final String BOOL_CHECKBOX1 = "checkboxset1";
    public static final String BOOL_CHECKBOX2 = "checkboxset2";
    public static final String BOOL_CHECKBOX3 = "checkboxset3";

    void state_save() {
        mSet = getSharedPreferences(NAME_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mSet.edit();
        editor.putBoolean(BOOL_CHECKBOX1, cb1.isChecked());
        editor.putBoolean(BOOL_CHECKBOX2, cb2.isChecked());
        editor.putBoolean(BOOL_CHECKBOX3, cb3.isChecked());
        editor.commit();
    }

    void state_restore() {
        mSet = getSharedPreferences(NAME_PREFERENCES, Context.MODE_PRIVATE);
        if(mSet.contains(BOOL_CHECKBOX1)) {cb1.setChecked(mSet.getBoolean(BOOL_CHECKBOX1, false));}
        if(mSet.contains(BOOL_CHECKBOX2)) {cb2.setChecked(mSet.getBoolean(BOOL_CHECKBOX2, false));}
        if(mSet.contains(BOOL_CHECKBOX3)) {cb3.setChecked(mSet.getBoolean(BOOL_CHECKBOX3, false));}
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cb1 = (CheckBox) findViewById(R.id.checkBox1);
        cb2 = (CheckBox) findViewById(R.id.checkBox2);
        cb3 = (CheckBox) findViewById(R.id.checkBox3);

        state_restore();
        Log.v(TAG, "MainActivity: onCreate()");

        logView = (TextView)findViewById(R.id.logView);

        logView.append("MainActivity\n");
    }

    void logging(String text) {
        Log.v(TAG, text);
        logView.append(text);
    }

    @Override
    protected void onStart() {
        super.onStart();
        logging("MainActivity: onStart()");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        logging("MainActivity: onRestart()");
    }

    @Override
    protected void onResume() {
        super.onResume();
        logging("MainActivity: onResume()");
    }

    @Override
    protected void onPause() {
        super.onPause();
        logging("MainActivity: onPause()");
    }

    @Override
    protected void onStop() {
        super.onStop();
        logging("MainActivity: onStop()");

        state_save();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        logging("MainActivity: onDestroy()");
    }

    public void onClick(View view) {
        //Intent intent = new Intent(this, Page_1.class);
        //startActivity(intent);

        Intent intent = new Intent(this, Test_Activity.class);
        startActivity(intent);
    }

    public void test(View view) {
        Test_class t_class = new Test_class();
        t_class.test();
        logging(String.valueOf(t_class.get_count()));
    }
}
