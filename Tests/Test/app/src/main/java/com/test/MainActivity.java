package com.test;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends TestingLogging {

    final String TAG = "States";
    TextView logView;

    CheckBox cb1;
    CheckBox cb2;
    CheckBox cb3;

    // https://developer.android.com/guide/topics/ui/controls/spinner.html
    Spinner spinner;
    String[] data = {"one", "two", "three", "four", "five"};

    Memory mem;

    public static final String BOOL_CHECKBOX1 = "checkboxset1";
    public static final String BOOL_CHECKBOX2 = "checkboxset2";
    public static final String BOOL_CHECKBOX3 = "checkboxset3";
    public static final String PASSWORD = "password";

    //---------------------------------------------------------------------------------------------
    void state_save() {
        mem.set_boolean_value(BOOL_CHECKBOX1, cb1.isChecked());
        mem.set_boolean_value(BOOL_CHECKBOX2, cb2.isChecked());
        mem.set_boolean_value(BOOL_CHECKBOX3, cb3.isChecked());
    }
    //---------------------------------------------------------------------------------------------
    void state_restore() {
        cb1.setChecked(mem.get_boolean_value(BOOL_CHECKBOX1));
        cb2.setChecked(mem.get_boolean_value(BOOL_CHECKBOX2));
        cb3.setChecked(mem.get_boolean_value(BOOL_CHECKBOX3));
    }
    //---------------------------------------------------------------------------------------------
    boolean check_data() {
        String temp = mem.get_string(PASSWORD);
        return !temp.isEmpty();
    }
    //---------------------------------------------------------------------------------------------
    void dialog() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle("Title");
        alert.setMessage("Message");

        // Set an EditText view to get user input
        final EditText input = new EditText(this);
        input.setText(mem.get_string_value(PASSWORD));
        alert.setView(input);

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                Editable value = input.getText();
                if(!value.toString().isEmpty()) {
                    mem.set_string(PASSWORD, value.toString());
                }
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
            }
        });

        alert.show();
    }
    //---------------------------------------------------------------------------------------------
    void test_first_create() {
        //SharedPreferences sp = getSharedPreferences(MY_SETTINGS, Context.MODE_PRIVATE);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        // проверяем, первый ли раз открывается программа
        boolean hasVisited = sp.getBoolean("hasVisited", false);

        if (!hasVisited) {
            // выводим нужную активность
            SharedPreferences.Editor e = sp.edit();
            e.putBoolean("hasVisited", true);
            e.commit(); // не забудьте подтвердить изменения

            logging("Is FIRST !!!");
        }
    }
    //---------------------------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cb1 = (CheckBox) findViewById(R.id.checkBox1);
        cb2 = (CheckBox) findViewById(R.id.checkBox2);
        cb3 = (CheckBox) findViewById(R.id.checkBox3);

        //---
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, data);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setAdapter(adapter);
        //---

        mem = new Memory(getApplicationContext());

        state_restore();

        logView = (TextView)findViewById(R.id.logView);

        logging(getLocalClassName() + ": onCreate()");

        if(!check_data()) {
            dialog();
        }
        else {
            logging("password = [" + mem.get_string(PASSWORD) + "]");
        }
        test_first_create();
    }
    //---------------------------------------------------------------------------------------------
    void logging(String text) {
        Log.v(TAG, text + "\n");
        logView.append(text + "\n");
    }
    //---------------------------------------------------------------------------------------------
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    //---------------------------------------------------------------------------------------------
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId())
        {
            case R.id.action_settings_test:
                dialog();
                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    //---------------------------------------------------------------------------------------------
    @Override
    protected void onStop() {
        super.onStop();

        state_save();
    }
    //---------------------------------------------------------------------------------------------
    public void onClick(View view) {
        Intent intent = new Intent(this, Page_1.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

        //Intent intent2 = new Intent(this, Test_Activity.class);
        //startActivity(intent2);
    }
    //---------------------------------------------------------------------------------------------
    public void test(View view) {
        Test my_test = new Test();

        try {
            my_test.test();
            my_test.test2();
        }
        catch (RuntimeException e) {
            logging(e.getMessage());
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    //---------------------------------------------------------------------------------------------
    public void test1(View view) {
        Test_class t_class = new Test_class();
        t_class.test("test");
        logging(String.valueOf(t_class.get_count()));
    }
    //---------------------------------------------------------------------------------------------
    public void test2(View view) {
        for(int n=0; n<100; n++) {
            logging("n="+String.valueOf(n));
        }
    }
    //---------------------------------------------------------------------------------------------
    public void run(View view) {
        logging(spinner.getSelectedItem().toString());
    }
    //---------------------------------------------------------------------------------------------
}
