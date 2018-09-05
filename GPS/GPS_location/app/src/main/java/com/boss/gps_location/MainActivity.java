package com.boss.gps_location;

import java.util.Date;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private TextView tvEnabledGPS;
    private TextView tvStatusGPS;
    private TextView tvLocationGPS;

    private TextView logView;
    private final String TAG = "States";

    private TextView cntRecord;
    int counter = 0;

    private static final int RECORD_REQUEST_CODE = 101;

    private LocationManager locationManager;

    ContentValues cv;
    SQLiteDatabase db;

    DBHelper dbHelper;

    //---------------------------------------------------------------------------------------------
    protected void requestPermission(String permissionType, int requestCode) {
        int permission = ContextCompat.checkSelfPermission(this,
                permissionType);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{permissionType},
                    requestCode );
        }
    }
    //private static final int RECORD_REQUEST_CODE = 101;
    //requestPermission(Manifest.permission.ACCESS_FINE_LOCATION, RECORD_REQUEST_CODE);
    //---

    //---------------------------------------------------------------------------------------------
    void logging(String text) {
        logView.append(text + "\n");
        Log.v(TAG, text);
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
            case R.id.action_geolocation:
                onClickLocationSettings(MainActivity.this);
                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //---------------------------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvEnabledGPS = (TextView) findViewById(R.id.tvEnabledGPS);
        tvStatusGPS = (TextView) findViewById(R.id.tvStatusGPS);
        tvLocationGPS = (TextView) findViewById(R.id.tvLocationGPS);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        logView = (TextView)findViewById(R.id.logView);

        cntRecord = (TextView)findViewById(R.id.cntRecord);
        cntRecord.setText("count record: " + String.valueOf(counter));

        requestPermission(Manifest.permission.ACCESS_FINE_LOCATION, RECORD_REQUEST_CODE);

        // создаем объект для создания и управления версиями БД
        dbHelper = new DBHelper(this);

        // создаем объект для данных
        cv = new ContentValues();

        // подключаемся к БД
        db = dbHelper.getWritableDatabase();
    }

    //---------------------------------------------------------------------------------------------
    @Override
    protected void onResume() {
        super.onResume();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            logging("onResume: error permission");
            return;
        }
        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                1000,
                1,
                locationListener);
        checkEnabled();
    }

    //---------------------------------------------------------------------------------------------
    public void update(View view) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            logging("onResume: error permission");
            return;
        }
        logging("update");
        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                1000,
                1,
                locationListener);
        checkEnabled();
        logging("OK");
    }


    //---------------------------------------------------------------------------------------------
    @Override
    protected void onPause() {
        super.onPause();
        locationManager.removeUpdates(locationListener);
    }

    //---------------------------------------------------------------------------------------------
    private LocationListener locationListener = new LocationListener() {

        @Override
        public void onLocationChanged(Location location) {
            showLocation(location);
        }

        @Override
        public void onProviderDisabled(String provider) {
            checkEnabled();
        }

        @Override
        public void onProviderEnabled(String provider) {
            checkEnabled();
            if (ActivityCompat.checkSelfPermission(getParent(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(getParent(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                logging("onProviderEnabled: error permission");
                return;
            }
            showLocation(locationManager.getLastKnownLocation(provider));
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            if (provider.equals(LocationManager.GPS_PROVIDER)) {
                tvStatusGPS.setText("Status: " + String.valueOf(status));
            }
        }
    };

    //---------------------------------------------------------------------------------------------
    private void showLocation(Location location) {
        if (location == null) {
            logging("showLocation: location is NULL");
            return;
        }
        if (location.getProvider().equals(LocationManager.GPS_PROVIDER)) {
            tvLocationGPS.setText(formatLocation(location));
        }
    }

    //---------------------------------------------------------------------------------------------
    private String formatLocation(Location location) {
        if (location == null) {
            logging("formatLocation: location is NULL");
            return "";
        }

        save_data(location.getLatitude(),
                location.getLongitude(),
                new Date(location.getTime()));

        return String.format(
                "Coordinates: \nlat = %1$.4f\nlon = %2$.4f\ntime = %3$tF %3$tT",
                location.getLatitude(),
                location.getLongitude(),
                new Date(location.getTime()));
    }

    //---------------------------------------------------------------------------------------------
    private void checkEnabled() {
        tvEnabledGPS.setText("Enabled: "
                + locationManager
                .isProviderEnabled(LocationManager.GPS_PROVIDER));
    }

    //---------------------------------------------------------------------------------------------
    public void onClickLocationSettings(MainActivity view) {
        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
    };

    //---------------------------------------------------------------------------------------------
    public void test(View view) {

        logging("Test");

        /*
        // получение имен таблиц
        Cursor c = db.rawQuery("select * from sqlite_master where type = 'table'", null);
        if (c.moveToFirst()) {
            logging("found...");
            do {
                logging(c.getString(0) + " " + c.getString(1));
            } while (c.moveToNext());
        } else {
            logging("0 rows");
        }
        */

        // делаем запрос всех данных из таблицы mytable, получаем Cursor
        Cursor c = db.query("mytable", null, null, null, null, null, null);

        // ставим позицию курсора на первую строку выборки
        // если в выборке нет строк, вернется false
        if (c.moveToFirst()) {

            // определяем номера столбцов по имени в выборке
            int id_date_time = c.getColumnIndex("date_time");
            int id_latitude = c.getColumnIndex("latitude");
            int id_longitude = c.getColumnIndex("longitude");

            do {
                // получаем значения по номерам столбцов и пишем все в лог
                logging("date_time = " + c.getString(id_date_time));
                logging("latitude  = " + c.getDouble(id_latitude));
                logging("longitude = " + c.getDouble(id_longitude));
                // переход на следующую строку
                // а если следующей нет (текущая - последняя), то false - выходим из цикла
            } while (c.moveToNext());
        } else
            logging("0 rows");
        c.close();
    }

    //---------------------------------------------------------------------------------------------
    void save_data(double latitude,
                   double longitude,
                   Date dt) {
        String dt_str = dt.toString();

        // делаем запрос всех данных из таблицы mytable, получаем Cursor
        Cursor c = db.query("mytable", null, null, null, null, null, null);

        cv.put("date_time", dt_str);
        cv.put("latitude",  latitude);
        cv.put("longitude", longitude);
        // вставляем запись и получаем ее ID
        long rowID = db.insert("mytable", null, cv);
        if(rowID < 0) {
            logging("Error: ID = " + rowID);
        }
        else {
            counter++;
        }
        cntRecord.setText("count record: " + String.valueOf(counter) + " rowID: " + String.valueOf(rowID));
    }

    //---------------------------------------------------------------------------------------------
    void drop_table(String table_name) {
        logging("drop table");
        db.execSQL("DROP TABLE IF EXISTS " + table_name +";");
    }

    //---------------------------------------------------------------------------------------------
    void create_table() {
        logging("create table");
        // создаем таблицу с полями
        db.execSQL("create table mytable ("
                + "date_time text,"
                + "latitude float,"
                + "longitude float" + ");");
    }
    //---------------------------------------------------------------------------------------------
    public void drop(View view) {
        drop_table("mytable");
    }

    //---------------------------------------------------------------------------------------------
    public void create(View view) {
        create_table();
    }

    //---------------------------------------------------------------------------------------------
    class DBHelper extends SQLiteOpenHelper {

        public DBHelper(Context context) {
            // конструктор суперкласса
            super(context, "myDB", null, 1);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            logging("--- onCreate database ---");
            // создаем таблицу с полями
            db.execSQL("create table mytable ("
                    + "date_time text,"
                    + "latitude float,"
                    + "longitude float" + ");");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }

    //---------------------------------------------------------------------------------------------

}
