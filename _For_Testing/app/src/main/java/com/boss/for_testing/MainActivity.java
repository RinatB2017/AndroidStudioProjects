package com.boss.for_testing;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity
        implements View.OnTouchListener {
    private static final int RECORD_REQUEST_CODE = 101;
    static final String LOG_TAG = "States";

    private static final String s_log = "s_log";
    private static final String s_current_tab = "s_current_tab";
    private static final String s_info = "s_info";

    TextView tv_log;

    TabHost tabHost;
    Handler h_print;

    //---
    Button btn_test;
    TextView textViewInfo;

    private Paint mPaint;
    Canvas c_bitmap;
    ImageView main_view;
    Bitmap bitmap;
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

            case R.id.add_icon:
                addShortcut();
                break;

            case R.id.remove_icon:
                removeShortcut();
                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //---------------------------------------------------------------------------------------------
    private void addShortcut() {
        send_log(Color.BLACK, "addShortcut");

        requestPermission(Manifest.permission.INSTALL_SHORTCUT, RECORD_REQUEST_CODE);

        //Adding shortcut for MainActivity
        //on Home screen
        Intent shortcutIntent = new Intent(getApplicationContext(),
                MainActivity.class);

        shortcutIntent.setAction(Intent.ACTION_MAIN);

        Intent addIntent = new Intent();
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "HelloWorldShortcut");
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
                Intent.ShortcutIconResource.fromContext(getApplicationContext(),
                        R.mipmap.ic_launcher));

        addIntent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
        getApplicationContext().sendBroadcast(addIntent);
    }

    //---------------------------------------------------------------------------------------------
    private void removeShortcut() {
        send_log(Color.BLACK, "removeShortcut");

        requestPermission(Manifest.permission.UNINSTALL_SHORTCUT, RECORD_REQUEST_CODE);

        //Deleting shortcut for MainActivity
        //on Home screen
        Intent shortcutIntent = new Intent(getApplicationContext(),
                MainActivity.class);
        shortcutIntent.setAction(Intent.ACTION_MAIN);

        Intent addIntent = new Intent();
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "HelloWorldShortcut");

        addIntent.setAction("com.android.launcher.action.UNINSTALL_SHORTCUT");
        getApplicationContext().sendBroadcast(addIntent);
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

                //tv_log.append(text + "\n");

                String c_text = "<font color=#" + Integer.toHexString(color).substring(2) + ">" + text + "</font><br>";
                tv_log.append(Html.fromHtml(c_text));
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
        savedInstanceState.putString(s_info, textViewInfo.getText().toString());
        super.onSaveInstanceState(savedInstanceState);
    }

    //---------------------------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init_log();

        btn_test = (Button) findViewById(R.id.btn_test);
        textViewInfo = (TextView) findViewById(R.id.textViewInfo);

        main_view = (ImageView) findViewById(R.id.main_view);
        main_view.setOnTouchListener(this);

        //TODO временный костыль
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        //---
        Display display = getWindowManager().getDefaultDisplay();
        Point p = new Point();
        int s = 600;
        display.getSize(p);
        int w = p.x;
        int h = p.y;
        if(w < h)
            s = w;
        else
            s = h;

        int width  = s;
        int heigth = s;
        bitmap = Bitmap.createBitmap(width, heigth, Bitmap.Config.ARGB_8888);
        main_view.setImageBitmap(bitmap);

        c_bitmap = new Canvas(bitmap);
        mPaint = new Paint();

        final int[] colors = new int[] { Color.RED, Color.CYAN, Color.MAGENTA, Color.YELLOW };
        Shader shader = new SweepGradient(width / 2, heigth / 2, colors, null);
        mPaint.setShader(shader);
        c_bitmap.drawCircle(width / 2, heigth / 2, width / 2, mPaint);

        //c_bitmap.drawRect(0, 0, width, heigth, mPaint);
        c_bitmap.drawCircle(width / 2, heigth / 2, width / 2, mPaint);
        //---

        init_tabs();

        if (savedInstanceState != null) {
            String temp = savedInstanceState.getString(s_log);
            if (temp != null) {
                if (!temp.isEmpty()) {
                    tv_log.setText(temp);
                }
            }

            String info = savedInstanceState.getString(s_info);
            if(info != null) {
                textViewInfo.setText(info);
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

    //---------------------------------------------------------------------------------------------
    public void test(View view) {
        send_log(Color.BLACK, "test");

        send_log(Color.RED,   "red");
        send_log(Color.GREEN, "green");
        send_log(Color.BLUE,  "blue");

        send_log(Color.BLACK, "the end");
    }

    //---------------------------------------------------------------------------------------------
    @Override
    public boolean onTouch(View v, MotionEvent event) {

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            float x = event.getX();
            float y = event.getY();

            int pixelColor = bitmap.getPixel((int)x, (int)y);
            int pixelRed = Color.red(pixelColor);
            int pixelGreen = Color.green(pixelColor);
            int pixelBlue = Color.blue(pixelColor);

            send_log(Color.BLACK,   "R " + String.valueOf(pixelRed));
            send_log(Color.BLACK,   "G " + String.valueOf(pixelGreen));
            send_log(Color.BLACK,   "B " + String.valueOf(pixelBlue));
        }

        return false;
    }

    //---------------------------------------------------------------------------------------------
}
