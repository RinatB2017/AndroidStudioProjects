package com.example.tux.myapplication;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class MainActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener {
    private TextView log_tv;

    private SeekBar sb;
    private TextView sb_tv;

    //--------------------------------------------------------------------------------------------
    //--------------------------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        log_tv = (TextView)findViewById(R.id.log_editText);

        sb = (SeekBar)findViewById(R.id.seekBar);
        sb.setOnSeekBarChangeListener(this);

        sb_tv = (TextView)findViewById(R.id.sb_editText);
        sb_tv.setText("0");

        test();

        log_tv.append(getInfo());

        Log.i("INFO", "onCreate");
    }
    //--------------------------------------------------------------------------------------------
    @Override
    protected void onStart() {
        super.onStart();
        // The activity is about to become visible.
        Log.i("INFO", "onStart");
    }
    //--------------------------------------------------------------------------------------------
    @Override
    protected void onResume() {
        super.onResume();
        // The activity has become visible (it is now "resumed").
        Log.i("INFO", "onResume");
    }
    //--------------------------------------------------------------------------------------------
    @Override
    protected void onPause() {
        super.onPause();
        // Another activity is taking focus (this activity is about to be "paused").
        Log.i("INFO", "onPause");
    }
    //--------------------------------------------------------------------------------------------
    @Override
    protected void onStop() {
        super.onStop();
        // The activity is no longer visible (it is now "stopped")
        Log.i("INFO", "onStop");
    }
    //--------------------------------------------------------------------------------------------
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // The activity is about to be destroyed.
        Log.i("INFO", "onDestroy");
    }
    //--------------------------------------------------------------------------------------------
    //--------------------------------------------------------------------------------------------
    private String getInfo() {
        StringBuffer sb = new StringBuffer();
        sb.append("abi: ").append(Build.CPU_ABI).append("n");
        if (new File("/proc/cpuinfo").exists()) {
            try {
                BufferedReader br = new BufferedReader(new FileReader(new File("/proc/cpuinfo")));
                String aLine;
                while ((aLine = br.readLine()) != null) {
                    sb.append(aLine + "\n");
                }

                if (br != null) {
                    br.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
    //--------------------------------------------------------------------------------------------
    public void test()
    {
        Button btn_test = (Button)findViewById(R.id.btn_test);
        btn_test.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                log_tv.append("btn_test click()\n");
            }
        });
    }
    //--------------------------------------------------------------------------------------------
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress,
                                  boolean fromUser) {
        // TODO Auto-generated method stub
        sb_tv.setText(String.valueOf(seekBar.getProgress()));
    }
    //--------------------------------------------------------------------------------------------
    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        // TODO Auto-generated method stub
    }
    //--------------------------------------------------------------------------------------------
    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        // TODO Auto-generated method stub
        sb_tv.setText(String.valueOf(seekBar.getProgress()));
    }
    //--------------------------------------------------------------------------------------------
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    //--------------------------------------------------------------------------------------------
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }
    //--------------------------------------------------------------------------------------------
    public void click_btn1(View view) {
        log_tv.append("1");
    }
    //--------------------------------------------------------------------------------------------
    public void click_btn2(View view) {
        log_tv.append("2");
    }
    //--------------------------------------------------------------------------------------------
    public void click_btn3(View view) {
        log_tv.append("3");
    }
    //--------------------------------------------------------------------------------------------
    public void click_btn4(View view) {
        log_tv.append("4");
    }
    //--------------------------------------------------------------------------------------------
    public void f0(MenuItem item) {
        Intent intent = new Intent(MainActivity.this, MainActivity.class);
        startActivity(intent);
    }
    //--------------------------------------------------------------------------------------------
    public void f1(MenuItem item) {
        Intent intent = new Intent(MainActivity.this, Page_1.class);
        startActivity(intent);
    }
    //--------------------------------------------------------------------------------------------
    public void f2(MenuItem item) {
        Intent intent = new Intent(MainActivity.this, Page_2.class);
        startActivity(intent);
    }
    //--------------------------------------------------------------------------------------------
    public void close_app(MenuItem item) {
        this.finish();
    }
    //--------------------------------------------------------------------------------------------
    /*
    AlertDialog.Builder dlg = new AlertDialog.Builder(this);
    dlg.setTitle("My Dialog");
    dlg.setMessage("1");
    dlg.setNegativeButton("ОК", null);
    dlg.show();
    */
}
