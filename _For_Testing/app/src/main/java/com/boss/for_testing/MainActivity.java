package com.boss.for_testing;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
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

import org.xmlpull.v1.XmlPullParser;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    //private final int RECORD_REQUEST_CODE = 101;
    private static final int REQUEST_GALLERY = 0;

    private String LOG_TAG = "States";

    private final String s_log = "s_log";
    private final String s_current_tab = "s_current_tab";

    TextView tv_log;

    TabHost tabHost;
    Handler h_print;

    //---
    Button btn_test;

    SoundPool soundPool;
    int explosionId = -1;
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

                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    String c_text = "<font color=#" + Integer.toHexString(color).substring(2) + ">" + text + "</font><br>";
                    tv_log.append(Html.fromHtml(c_text, Html.FROM_HTML_MODE_LEGACY));
                } else {
                    tv_log.append(text + "\n");
                }

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
        Display display = (Display) findViewById(R.id.display);
        for(int n=0; n<10; n++) {
            display.set_color(n, n, Color.RED);
        }
        display.redraw();
        //---

        //TODO временный костыль
        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
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

        //---
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            soundPool = new SoundPool.Builder()
                    .setMaxStreams(10)
                    .build();
        } else {
            soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 1);
        }

        try {
            AssetManager assetManager = getAssets();
            AssetFileDescriptor descriptor = assetManager.openFd("health.wav");
            explosionId = soundPool.load(descriptor, 1);
        } catch (IOException e) {
            send_log(Color.RED, e.getMessage());
        }
        //---
    }

    //---------------------------------------------------------------------------------------------
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // if the result is capturing Image
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            try {
                Uri selectedImageURI = data.getData();
                send_log(Color.RED, getRealPathFromURI(selectedImageURI, this)); //TODO
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //---------------------------------------------------------------------------------------------
    public String getRealPathFromURI(Uri contentURI, Activity context) {
        String[] projection = { MediaStore.Images.Media.DATA };
        @SuppressWarnings("deprecation")
        Cursor cursor = context.managedQuery(contentURI, projection, null,
                null, null);
        if (cursor == null)
            return null;
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        if (cursor.moveToFirst()) {
            String s = cursor.getString(column_index);
            // cursor.close();
            return s;
        }
        // cursor.close();
        return null;
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
    private void parseWithXmlPullParser() {
        try {
            XmlPullParser xmlPullParser = getResources().getXml(R.xml.config_store);
            while (xmlPullParser.getEventType() != XmlPullParser.END_DOCUMENT) {

                switch (xmlPullParser.getEventType()) {
                    case XmlPullParser.START_DOCUMENT: {
                        if (BuildConfig.DEBUG) {
                            Log.d(LOG_TAG, "START_DOCUMENT");
                        }
                        break;
                    }
                    // начало тега
                    case XmlPullParser.START_TAG: {
                        if (BuildConfig.DEBUG) {
                            Log.d(LOG_TAG, "START_TAG: имя тега = "
                                    + xmlPullParser.getName()
                                    + ", уровень = "
                                    + xmlPullParser.getDepth()
                                    + ", число атрибутов = "
                                    + xmlPullParser.getAttributeCount());
                        }

                        if (xmlPullParser.getName().equals("string")) {
                            if (xmlPullParser.getAttributeValue(null, "name").equals("SSID")) {
                                Log.d(LOG_TAG, xmlPullParser.nextText());
                                break;
                            }

                            if (xmlPullParser.getAttributeValue(null, "name").equals("PreSharedKey")) {
                                Log.d(LOG_TAG, xmlPullParser.nextText());
                                break;
                            }
                        }

                        if (xmlPullParser.getName().equals("boolean")) {
                            if (xmlPullParser.getAttributeValue(null, "name").equals("HiddenSSID")) {
                                Log.d(LOG_TAG, xmlPullParser.getAttributeValue(null, "value"));
                                break;
                            }
                        }
                        break;
                    }
                    // конец тега
                    case XmlPullParser.END_TAG:
                        if (BuildConfig.DEBUG) {
                            Log.d("LOG_TAG", "END_TAG: имя тега = " + xmlPullParser.getName());
                        }

                        break;
                    // содержимое тега
                    case XmlPullParser.TEXT:
                        if (BuildConfig.DEBUG) {
                            Log.d("LOG_TAG", "текст = " + xmlPullParser.getText());
                        }
                        break;

                    default:
                        break;
                }
                xmlPullParser.next();
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    //---------------------------------------------------------------------------------------------
    public void test(View view) {
        send_log(Color.RED, "test");

        //clear_cache();

        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select File"), REQUEST_GALLERY);
    }

    public void get_running_processes() {
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
    }

    public void get_imei() {
        String android_id = Settings.Secure.getString(this.getContentResolver(),
                Settings.Secure.ANDROID_ID);

        send_log(Color.RED, "android_id: " + android_id);

        TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        if(telephonyManager != null) {
            String imei = "none";
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                imei = telephonyManager.getImei();
            } else {
                imei = telephonyManager.getDeviceId();
            }
            send_log(Color.RED, "IMEI: " + imei);
        }
    }

    public void btn1_click(View view) {
        send_log(Color.RED, "1");
        //throw new RuntimeException("Усё пропало!");

        Setting.set_x(5);

        /*
        try {
            // получаем входной поток
            InputStream ims = getAssets().open("splash_screen.png");
            // загружаем как Drawable
            Drawable d = Drawable.createFromStream(ims, null);
            // выводим картинку в ImageView
            ImageView mImage = (ImageView)findViewById(R.id.asset_image);
            mImage.setImageDrawable(d);
        }
        catch(IOException ex) {
            send_log(Color.RED, ex.getMessage());
        }
        */
    }

    public void btn2_click(View view) {
        send_log(Color.RED, "2");

        send_log(Color.RED, "x = " + String.valueOf(Setting.get_x()));
    }

    public void clear_cache() {
        // http://qaru.site/questions/248377/android-clear-cache-of-all-apps
        PackageManager  pm = getPackageManager();
        // Get all methods on the PackageManager
        Method[] methods = pm.getClass().getDeclaredMethods();
        for (Method m : methods) {
            if (m.getName().equals("freeStorage")) {
                // Found the method I want to use
                try {
                    long desiredFreeStorage = 8 * 1024 * 1024 * 1024; // Request for 8GB of free space
                    m.invoke(pm, desiredFreeStorage , null);
                } catch (Exception e) {
                    // Method invocation failed. Could be a permission problem
                }
                break;
            }
        }
    }

    //---------------------------------------------------------------------------------------------
}
