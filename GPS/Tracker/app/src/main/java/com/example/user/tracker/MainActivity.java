package com.example.user.tracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private TextView logView;
    private final String TAG = "States";

    DBHelper dbHelper;

    //---------------------------------------------------------------------------------------------
    void logging(String text) {
        logView.append(text + "\n");
        Log.v(TAG, text);
    }

    //---------------------------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        logView = (TextView)findViewById(R.id.logView);

        // создаем объект для создания и управления версиями БД
        dbHelper = new DBHelper(this);
    }

    //---------------------------------------------------------------------------------------------
    public void test(View view) {
        // создаем объект для данных
        ContentValues cv = new ContentValues();

        // подключаемся к БД
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        logging("Test");

        Cursor c = db.rawQuery("select * from sqlite_master where type = 'table'", null);
        if (c.moveToFirst()) {
            logging("found...");
            do {
                logging(c.getString(0) + " " + c.getString(1));
            } while (c.moveToNext());
        } else {
            logging("0 rows");
        }
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
                    + "id integer primary key autoincrement,"
                    + "name text,"
                    + "email text" + ");");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }

    //---------------------------------------------------------------------------------------------
}
