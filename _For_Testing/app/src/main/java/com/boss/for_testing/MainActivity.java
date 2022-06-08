package com.boss.for_testing;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.WallpaperManager;
import android.content.BroadcastReceiver;
import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
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

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity  {
    private final String LOG_TAG = "States";

    public static final int REQUEST_CODE = (int) new Date().getTime();

    private final String s_log = "s_log";
    private final String s_current_tab = "s_current_tab";

    private static final int RECORD_REQUEST_CODE = 101;

    TextView tv_log;

    TabHost tabHost;
    Handler h_print;

    //---
    Button btn_test;

    public static final String TABLE_CONTACTS = "contacts";

    public static final String KEY_ID = "_id";
    public static final String KEY_NAME = "name";
    public static final String KEY_MAIL = "mail";

    SQLiteDatabase database;
    //---

    public class MessageReceiver extends BroadcastReceiver {
        public MessageReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            send_log(Color.RED, intent.getStringExtra("test"));
        }
    }
    MessageReceiver receiver;

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

        h_print = new Handler(Looper.getMainLooper(), new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
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
                return true;
            }
        });
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

        //TODO временный костыль
        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        //---

        database = getBaseContext().openOrCreateDatabase("app.db", MODE_PRIVATE, null);

        receiver = new MessageReceiver();

        requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, RECORD_REQUEST_CODE);

        requestPermission(Manifest.permission.READ_CONTACTS, RECORD_REQUEST_CODE);
        requestPermission(Manifest.permission.WRITE_CONTACTS, RECORD_REQUEST_CODE);

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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // if the result is capturing Image
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            try {
                Uri selectedImageURI = data.getData();
                send_log(Color.RED, getRealPathFromURI(selectedImageURI, this));
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
    /*
    надо не забыть в манифесте нужные права выставить
    звтем в OnCreate
        requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, RECORD_REQUEST_CODE);
    */
    public void save_wallpaper() {
        WallpaperManager wallpaperManager = WallpaperManager.getInstance(this);
        Drawable wallpaperDrawable = wallpaperManager.getDrawable();

        Bitmap bitmap = ((BitmapDrawable) wallpaperDrawable).getBitmap();

        try {
            String filename = "my_wallpaper.png";
            File sd = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

            File dest = new File(sd, filename);
            send_log(Color.RED, dest.toString());
            send_log(Color.RED, filename);

            FileOutputStream out = new FileOutputStream(dest);
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //---------------------------------------------------------------------------------------------
    void create_new_contact() {
        //---
        // https://habr.com/ru/post/130148/
        // добавление нового контакта
        ArrayList<ContentProviderOperation> op = new ArrayList<ContentProviderOperation>();

        /* Добавляем пустой контакт */
        op.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                .build());
        /* Добавляем данные имени */
        op.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, "Robert Smith")
                .build());
        /* Добавляем данные телефона */
        op.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, "11-22-33")
                .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
                .build());

        try {
            getContentResolver().applyBatch(ContactsContract.AUTHORITY, op);
        } catch (Exception e) {
            Log.e("Exception: ", e.getMessage());
        }
        //---
    }

    //---------------------------------------------------------------------------------------------
    // https://www.fandroid.info/urok-34-rabota-s-bazami-dannyh-sqlite-v-android/
    // https://metanit.com/java/android/14.5.php

    void add_sql_data(String name, String email) {
        ContentValues contentValues = new ContentValues();

        contentValues.put(KEY_NAME, name);
        contentValues.put(KEY_MAIL, email);

        database.insert(TABLE_CONTACTS, null, contentValues);
    }

    //---------------------------------------------------------------------------------------------
    void read_sql_data(String table_name) {
        Cursor cursor = database.query(table_name, null, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex(KEY_ID);
            int nameIndex = cursor.getColumnIndex(KEY_NAME);
            int emailIndex = cursor.getColumnIndex(KEY_MAIL);
            do {
                send_log(Color.RED,
                        "ID = " + cursor.getInt(idIndex) +
                        ", name = " + cursor.getString(nameIndex) +
                        ", email = " + cursor.getString(emailIndex));
            } while (cursor.moveToNext());
        } else
            send_log(Color.RED,"0 rows");

        cursor.close();
    }

    //---------------------------------------------------------------------------------------------
    void test_sql(SQLiteDatabase db, String table_name) {
        String KEY_ID = "_id";
        String KEY_NAME = "name";
        String KEY_MAIL = "mail";

        db.execSQL("CREATE TABLE IF NOT EXISTS " + table_name + "(" + KEY_ID
                + " integer primary key," + KEY_NAME + " text," + KEY_MAIL + " text" + ")");

        //---
        ContentValues contentValues = new ContentValues();

        contentValues.put(KEY_NAME, "XXX name");
        contentValues.put(KEY_MAIL, "XXX email");

        db.insert(table_name, null, contentValues);
        //---

        read_sql_data(table_name);
    }

    //---------------------------------------------------------------------------------------------
    void check_sql() {
        //add_sql_data("Name", "Email");
        //read_sql_data();

        test_sql(database, "XXX");
        //test_sql(database, "YYY");
    }

    //---------------------------------------------------------------------------------------------
    public void test(View view) {
        send_log(Color.RED, "test");

        //TODO test
        check_sql();

        //Date date = Calendar.getInstance().getTime();
        //@SuppressLint("SimpleDateFormat") DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
        //String filename = dateFormat.format(date) + ".jpg";
        //send_log(Color.RED, filename);

        //startService(new Intent(this, MyService.class));

        // https://stackoverflow.com/questions/9177212/creating-background-service-in-android
        //scheduleAlarm();

        //save_wallpaper();
    }

//    public void btn1_click(View view) {
//        send_log(Color.RED, "1");
//
//        Setting.set_x(5);
//    }
//
//    public void btn2_click(View view) {
//        send_log(Color.RED, "2");
//
//        send_log(Color.RED, "x = " + String.valueOf(Setting.get_x()));
//    }

    public void scheduleAlarm()
    {
        // Construct an intent that will execute the AlarmReceiver
        Intent intent = new Intent(getApplicationContext(), AlmasReceiver.class);
        // Create a PendingIntent to be triggered when the alarm goes off
        final PendingIntent pIntent = PendingIntent.getBroadcast(
                this, REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        // Setup periodic alarm every every half hour from this point onwards
        long firstMillis = System.currentTimeMillis(); // alarm is set right away
        AlarmManager alarm = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        // First parameter is the type: ELAPSED_REALTIME, ELAPSED_REALTIME_WAKEUP, RTC_WAKEUP
        // Interval can be INTERVAL_FIFTEEN_MINUTES, INTERVAL_HALF_HOUR, INTERVAL_HOUR, INTERVAL_DAY
        alarm.setRepeating(AlarmManager.RTC_WAKEUP, firstMillis, (long) (1000 * 10), pIntent);
    }
    //---------------------------------------------------------------------------------------------
}
