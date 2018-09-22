package com.boss.template;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.view.View;

import java.io.File;
import java.io.IOException;

public class MainActivity extends LogActivity {

    private static final int RECORD_REQUEST_CODE = 101;

    //---------------------------------------------------------------------------------------------
    protected void requestPermission(String permissionType, int requestCode) {
        int permission = ContextCompat.checkSelfPermission(this,
                permissionType);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{permissionType}, requestCode
            );
        }
    }

    //---------------------------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);

        send_log("MainActivity: onCreate()");

        requestPermission(Manifest.permission.READ_EXTERNAL_STORAGE, RECORD_REQUEST_CODE);
    }

    //---------------------------------------------------------------------------------------------
    @Override
    protected void onStart() {
        super.onStart();
        send_log("MainActivity: onStart()");
    }

    /* Проверяет, доступно ли external storage как минимум для чтения */
    public boolean isExternalStorageReadable()
    {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state))
        {
            return true;
        }
        return false;
    }

    private void list_files(File path) {
        if(path == null) {
            return;
        }

        send_log("DIR: " + path);

        File[] l_files = path.listFiles();
        if(l_files == null) {
            return;
        }
        for(int n=0; n<l_files.length; n++) {
            if(l_files[n].isDirectory()) {
                list_files(l_files[n]);
            }
            else {
                send_log("   file: " + l_files[n].getName());
            }
        }
    }

    //---------------------------------------------------------------------------------------------
    public void test(View view) throws IOException {
        //send_log("MainActivity: test");
        //super.test();

        tv_log.setText("");

        if(!isExternalStorageReadable()) {
            send_log("ERROR: external storage no readable");
            return;
        }

        String basePath = Environment.getExternalStorageDirectory().getAbsolutePath();
        //basePath = "/storage/emulated/0/Android/data/com.mendhak.gpslogger/files";
        send_log("basePath " + basePath);
        list_files(new File(basePath));

        //File file = new File("/storage/emulated/0/Android/data/com.mendhak.gpslogger/files");
    }

    //---------------------------------------------------------------------------------------------
}
