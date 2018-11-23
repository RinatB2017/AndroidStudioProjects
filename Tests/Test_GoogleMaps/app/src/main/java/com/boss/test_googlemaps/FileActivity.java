package com.boss.test_googlemaps;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.File;
import java.util.ArrayList;

public class FileActivity extends AppCompatActivity {
    String path;
    ListView listView;
    int pos = -1;
    String filename;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file);

        path = "/storage/emulated/0/Android/data/com.mendhak.gpslogger/files/";
        File file = new File(path);
        ArrayList<String> fileNames = list_files(file);

        // получаем экземпляр элемента ListView
        listView = (ListView)findViewById(R.id.listView);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                pos = position;
                filename = parent.getItemAtPosition(position).toString();
            }
        });

        // используем адаптер данных
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, fileNames);

        listView.setAdapter(adapter);
        listView.setSelection(1);
    }

    private ArrayList<String> list_files(File path) {
        ArrayList<String> list = new ArrayList<>();
        if (path == null) {
            //send_log("path == null");
            return list;
        }

        File[] l_files = path.listFiles();
        if (l_files == null) {
            //send_log("l_files == null");
            return list;
        }
        for (int n = 0; n < l_files.length; n++) {
            if (l_files[n].isDirectory()) {
                list_files(l_files[n]);
            } else {
                //send_log("   file: " + l_files[n].getName() + " size: " + l_files[n].length());
                list.add(l_files[n].getName());
            }
        }
        return list;
    }


    public void go_main(View view) {
        //Intent intent = new Intent(this, PhoneMainActivity.class);
        //startActivity(intent);

        if(pos >= 0) {
            Log.i("States", String.valueOf(pos));
            Log.i("States", filename);

            Intent intent = new Intent(getApplication(), MapsActivity.class);
            intent.putExtra("filename", path + filename);
            startActivity(intent);

        }
        else {
            Log.i("States", "ERROR");
        }
    }
}
