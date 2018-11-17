package com.boss.for_testing;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    static final String LOG_TAG = "States";

    private static final int RECORD_REQUEST_CODE = 101;

    TextView tv_log;

    TabHost tabHost;
    Handler h_print;

    //---
    Button btn_test;
    TextView textViewInfo;
    //---

    String path;

    //---------------------------------------------------------------------------------------------
    public void send_log(String text) {
        if (text == null) {
            return;
        }
        Message msg = new Message();
        msg.obj = text;
        h_print.sendMessage(msg);
    }

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
    private void requestSmsPermission(String permission) {
        //String permission = Manifest.permission.RECEIVE_SMS;
        int grant = ContextCompat.checkSelfPermission(this, permission);
        if (grant != PackageManager.PERMISSION_GRANTED) {
            String[] permission_list = new String[1];
            permission_list[0] = permission;
            ActivityCompat.requestPermissions(this, permission_list, 1);
        }
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
        switch (item.getItemId()) {
            case R.id.action_clear_log:
                tv_log.setText("");
                break;

            case R.id.about:
                showAbout();
                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //---------------------------------------------------------------------------------------------
    void init_log() {
        tv_log = (TextView) findViewById(R.id.logView);
        tv_log.setTextColor(Color.BLACK);

        h_print = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                String text = (String) msg.obj;
                Log.i(LOG_TAG, text);
                tv_log.append(text + "\n");
            }
        };
    }

    //---------------------------------------------------------------------------------------------
    void init_tabs()
    {
        tabHost = (TabHost) findViewById(R.id.tabHost);

        tabHost.setup();

        TabHost.TabSpec tabSpec;

        tabSpec = tabHost.newTabSpec("tab_main");
        tabSpec.setContent(R.id.tab_main);
        tabSpec.setIndicator(getString(R.string.main));
        tabHost.addTab(tabSpec);

        tabSpec = tabHost.newTabSpec("tab_log");
        tabSpec.setContent(R.id.tab_log);
        tabSpec.setIndicator(getString(R.string.log));
        tabHost.addTab(tabSpec);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.weight = 1;

        TabWidget tabWidget = tabHost.getTabWidget();
        for (int i = 0; i < tabHost.getTabWidget().getChildCount(); i++) {
            //tabHost.getTabWidget().getChildAt(i).setBackgroundColor(Color.parseColor("#CCCCCC"));
            tabHost.getTabWidget().getChildAt(i).setLayoutParams(layoutParams);
            TextView textView = (TextView) tabWidget.getChildAt(i).findViewById(android.R.id.title);
            textView.setTextColor(Color.BLACK);
        }
    }

    //---------------------------------------------------------------------------------------------
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putString("log", tv_log.getText().toString());
        savedInstanceState.putInt("current_tab", tabHost.getCurrentTab());
        savedInstanceState.putString("info", textViewInfo.getText().toString());
        super.onSaveInstanceState(savedInstanceState);
    }

    //---------------------------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init_log();

        requestSmsPermission(Manifest.permission.RECEIVE_SMS);

        //---
        btn_test = (Button) findViewById(R.id.btn_test);
        textViewInfo = (TextView) findViewById(R.id.textViewInfo);
        //---

        init_tabs();

        requestPermission(Manifest.permission.READ_EXTERNAL_STORAGE, RECORD_REQUEST_CODE);

        if (savedInstanceState != null) {
            String temp = savedInstanceState.getString("log");
            if (temp != null) {
                if (!temp.isEmpty()) {
                    tv_log.setText(temp);
                }
            }
            String info = savedInstanceState.getString("info");
            if(info != null) {
                textViewInfo.setText(info);
            }
            int current_tab = savedInstanceState.getInt("current_tab");
            tabHost.setCurrentTab(current_tab);
        } else {
            Bundle bundle = new Bundle();
            getIntent().putExtras(bundle);
        }
    }

    //---------------------------------------------------------------------------------------------
    @Override
    protected void onStart() {
        super.onStart();
        //send_log("onStart()");
    }

    //---------------------------------------------------------------------------------------------
    @Override
    protected void onRestart() {
        super.onRestart();
        //send_log("onRestart()");
    }

    //---------------------------------------------------------------------------------------------
    @Override
    protected void onResume() {
        super.onResume();
        //send_log("onResume()");

        Bundle bundle = getIntent().getExtras(); //new Bundle();
        bundle.putBoolean("flag_is_running", false);
        getIntent().putExtras(bundle);
    }

    //---------------------------------------------------------------------------------------------
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
    }

    //---------------------------------------------------------------------------------------------
    @Override
    protected void onPause() {
        super.onPause();
        //send_log("onPause()");
    }

    //---------------------------------------------------------------------------------------------
    @Override
    protected void onStop() {
        super.onStop();
        //send_log("onStop()");
    }

    //---------------------------------------------------------------------------------------------
    @Override
    protected void onDestroy() {
        super.onDestroy();
        //send_log("onDestroy()");
    }

    //---------------------------------------------------------------------------------------------
    void showAbout() {
        // Inflate the about message contents
        View messageView = getLayoutInflater().inflate(R.layout.about, null, false);

        // When linking text, force to always use default color. This works
        // around a pressed color state bug.
        TextView textView = (TextView) messageView.findViewById(R.id.about_credits);
        int defaultColor = textView.getTextColors().getDefaultColor();
        textView.setTextColor(defaultColor);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //builder.setIcon(R.drawable.app_icon);
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setTitle(R.string.app_name);
        builder.setView(messageView);
        builder.setCancelable(false);
        builder.setNegativeButton("ОК",
                new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
        builder.create();
        builder.show();
    }

    //---------------------------------------------------------------------------------------------
    public void test(View view) {
        send_log("test");

        //textViewInfo.setText("Hello");

        //Intent intent = new Intent(this, FileExplore.class);
        //startActivity(intent);

        path = "/storage/emulated/0/Android/data/com.mendhak.gpslogger/files/";
        File file = new File(path);
        ArrayList<String> fileNames = list_files(file);

        // получаем экземпляр элемента ListView
        ListView listView = (ListView)findViewById(R.id.listView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                send_log("itemClick: position = " + position + ", id = " + id);
                send_log(path + parent.getItemAtPosition(position).toString());
            }
        });

        // используем адаптер данных
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, fileNames);

        listView.setAdapter(adapter);

        send_log("the end");
    }

    private ArrayList<String> list_files(File path) {
        ArrayList<String> list = new ArrayList<>();
        if (path == null) {
            send_log("path == null");
            return list;
        }

        File[] l_files = path.listFiles();
        if (l_files == null) {
            send_log("l_files == null");
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

    void send_email(String text) {
        Intent i = new Intent(Intent.ACTION_SENDTO);
        i.setType("message/rfc822");
        i.setData(Uri.parse("mailto:"));
        i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"test@gmail.com"});
        i.putExtra(Intent.EXTRA_SUBJECT, "Subject");
        i.putExtra(Intent.EXTRA_TEXT   , "body of email");
        try {
            startActivity(Intent.createChooser(i, "Send mail..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
        }
    }

    //---------------------------------------------------------------------------------------------
}
