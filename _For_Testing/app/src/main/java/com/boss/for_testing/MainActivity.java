package com.boss.for_testing;

import android.Manifest;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final int RECORD_REQUEST_CODE = 101;
    static final String LOG_TAG = "States";

    private static final String s_log = "s_log";
    private static final String s_current_tab = "s_current_tab";

    TextView tv_log;

    TabHost tabHost;
    Handler h_print;

    //---
    Button btn_test;
    //---

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
    public void send_log(int color, String text) {
        if (text == null) {
            return;
        }
        Message msg = new Message();
        msg.arg1 = color;
        msg.obj = text;
        h_print.sendMessage(msg);
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
                int color = msg.arg1;
                Log.i(LOG_TAG, text);

                String c_text = "<font color=#" + Integer.toHexString(color).substring(2) + ">" + text + "</font><br>";
                tv_log.append(Html.fromHtml(c_text, Html.FROM_HTML_MODE_LEGACY));

                //---
                Toast toast = Toast.makeText(getBaseContext(),
                        text,
                        Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.BOTTOM, 0, 0);
                toast.show();
                //---
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
        savedInstanceState.putString(s_log, tv_log.getText().toString());
        savedInstanceState.putInt(s_current_tab, tabHost.getCurrentTab());
        super.onSaveInstanceState(savedInstanceState);
    }

    //---------------------------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init_log();

        btn_test = (Button) findViewById(R.id.btn_test);

        //---
        // Получаем экземпляр элемента Spinner
        //spinner = (Spinner)findViewById(R.id.spinner);

        // Настраиваем адаптер
        //ArrayAdapter<?> adapter =
        //        ArrayAdapter.createFromResource(this, R.array.colors, android.R.layout.simple_spinner_item);
        //adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Вызываем адаптер
        //spinner.setAdapter(adapter);

        //btn_set_color = (Button)findViewById(R.id.btn_set_color);
        //---

        //TODO временный костыль
        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        //---

        //---
        ColorPicker cp = (ColorPicker)findViewById(R.id.main_view);
        cp.set_new_size(400, 400);

        MagicCircle mc1 = (MagicCircle)findViewById(R.id.magic_circle_1);
        MagicCircle mc2 = (MagicCircle)findViewById(R.id.magic_circle_2);
        MagicCircle mc3 = (MagicCircle)findViewById(R.id.magic_circle_3);
        MagicCircle mc4 = (MagicCircle)findViewById(R.id.magic_circle_4);

        mc1.set_new_size(150, 150);
        mc2.set_new_size(150, 150);
        mc3.set_new_size(150, 150);
        mc4.set_new_size(150, 150);

        mc1.set_fontsize(50);
        mc2.set_fontsize(50);
        mc3.set_fontsize(50);
        mc4.set_fontsize(50);
        //---

        init_tabs();

        if (savedInstanceState != null) {
            String temp = savedInstanceState.getString(s_log);
            if (temp != null) {
                if (!temp.isEmpty()) {
                    tv_log.setText(temp);
                }
            }

            int current_tab = savedInstanceState.getInt(s_current_tab);
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

    public class UpdateColor implements ColorPickerDialog.OnColorChangedListener {
        public void colorChanged(int color) {
            //ShowColor.setBackgroundColor(color);
            //show the color value

            send_log(Color.RED, "color = " + String.valueOf(color));
        }
    }

    //---------------------------------------------------------------------------------------------
    public void test(View view) {
//        ColorPickerDialog dlg = new ColorPickerDialog(MainActivity.this, new UpdateColor(), Color.GREEN);
//        dlg.show();

        /*
        // Get running processes
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningProcesses = manager.getRunningAppProcesses();
        if (runningProcesses != null && runningProcesses.size() > 0) {
            // Set data to the list adapter
            setListAdapter(new ListAdapter(this, runningProcesses));
        } else {
            // In case there are no processes running (not a chance :))
            Toast.makeText(getApplicationContext(), "No application is running", Toast.LENGTH_LONG).show();
        }
        */

//        int x = Mem.x;
//        int y = Mem.y;
//        send_log(Color.RED, "x " + String.valueOf(x));
//        send_log(Color.RED, "y " + String.valueOf(y));

//         send_log(Color.BLACK, "test");
// 
//         send_log(Color.RED,   "red");
//         send_log(Color.GREEN, "green");
//         send_log(Color.BLUE,  "blue");
// 
//         send_log(Color.BLACK, "the end");
    }

    //---------------------------------------------------------------------------------------------
    /*
    public void set_color(View view) {
        String color_str = spinner.getSelectedItem().toString();
        send_log(Color.BLACK, spinner.getSelectedItem().toString());
        switch (color_str)
        {
            case "BLACK":
                btn_set_color.setBackgroundColor(Color.BLACK);
                break;
            case "DKGRAY":
                btn_set_color.setBackgroundColor(Color.DKGRAY);
                break;
            case "GRAY":
                btn_set_color.setBackgroundColor(Color.GRAY);
                break;
            case "LTGRAY":
                btn_set_color.setBackgroundColor(Color.LTGRAY);
                break;
            case "WHITE":
                btn_set_color.setBackgroundColor(Color.WHITE);
                break;
            case "RED":
                btn_set_color.setBackgroundColor(Color.RED);
                break;
            case "GREEN":
                btn_set_color.setBackgroundColor(Color.GREEN);
                break;
            case "BLUE":
                btn_set_color.setBackgroundColor(Color.BLUE);
                break;
            case "YELLOW":
                btn_set_color.setBackgroundColor(Color.YELLOW);
                break;
            case "CYAN":
                btn_set_color.setBackgroundColor(Color.CYAN);
                break;
            case "MAGENTA":
                btn_set_color.setBackgroundColor(Color.MAGENTA);
                break;

            default:
                send_log(Color.RED, "unknown color [" + color_str + "]");
                break;
        }
    }
    */

    //---------------------------------------------------------------------------------------------
}
