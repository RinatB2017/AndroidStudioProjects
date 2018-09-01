package com.boss.template;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
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
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.Random;

public class MainActivity extends AppCompatActivity {
    static final String LOG_TAG = "States";

    TextView tv_log;

    ImageButton imageButton;
    boolean flag = true;

    ToggleButton toggleButton;

    private ProgressDialog scanProgressDialog;
    Handler handler;

    //final Random random = new Random();

    //---------------------------------------------------------------------------------------------
    public void logging(String text) {
        Log.i(LOG_TAG, text);
        tv_log.append(text + "\n");
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv_log = (TextView) findViewById(R.id.logView);
        tv_log.setTextColor(Color.BLACK);

        SeekBar sb = (SeekBar)findViewById(R.id.seekBar);
        sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
                logging("pos = " + seekBar.getProgress());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,boolean fromUser) {
                // TODO Auto-generated method stub
                //logging("pos = " + progress);
            }
        });

        imageButton = (ImageButton)findViewById(R.id.imageButton);
        imageButton.setImageResource(R.drawable.power_off);
        imageButton.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                // меняем изображение на кнопке
                if (flag) {
                    imageButton.setImageResource(R.drawable.power_on);
                    logging("ON");
                }
                else {
                    // возвращаем первую картинку
                    imageButton.setImageResource(R.drawable.power_off);
                    logging("OFF");
                }
                flag = !flag;
            }
        });

        toggleButton = (ToggleButton)findViewById(R.id.toggleButton);
        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            if (isChecked) {
                    logging("ON");
                }
                else {
                    logging("OFF");
                }
            }
        });
    }

    //---------------------------------------------------------------------------------------------
    @Override
    protected void onStart() {
        super.onStart();
        logging("onStart()");
    }

    //---------------------------------------------------------------------------------------------
    @Override
    protected void onRestart() {
        super.onRestart();
        logging("onRestart()");
    }

    //---------------------------------------------------------------------------------------------
    @Override
    protected void onResume() {
        super.onResume();
        logging("onResume()");
    }

    //---------------------------------------------------------------------------------------------
    @Override
    protected void onPause() {
        super.onPause();
        logging("onPause()");
    }

    //---------------------------------------------------------------------------------------------
    @Override
    protected void onStop() {
        super.onStop();
        logging("onStop()");
    }

    //---------------------------------------------------------------------------------------------
    @Override
    protected void onDestroy() {
        super.onDestroy();
        logging("onDestroy()");
    }

    //---------------------------------------------------------------------------------------------
    public void click_test(View view) {
        logging("test");

        // https://javadevblog.com/dialogi-v-android-primer-raboty-s-progressdialog.html
        // http://developer.alexanderklimov.ru/android/java/random.php

        int minValue = 0;
        int maxValue = 100;

        scanProgressDialog = new ProgressDialog(MainActivity.this, R.style.DialogTheme);
        scanProgressDialog.setCancelable(true);
        scanProgressDialog.setCanceledOnTouchOutside(false);
        scanProgressDialog.setTitle("Scanning: " + minValue + " to " + maxValue);
        scanProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        scanProgressDialog.incrementProgressBy(1);
        scanProgressDialog.setProgress(minValue);
        scanProgressDialog.setMax(maxValue);
        scanProgressDialog.show();

        handler = new Handler() {
            public void handleMessage(Message msg) {
                // и обновляем идикатор, пока шкала не заполнится
                if (scanProgressDialog.getProgress() < scanProgressDialog.getMax()) {
                    //scanProgressDialog.setProgress(msg.what);
                    // обновляем индикаторы на 1 пункт за 100 милисекунд
                    scanProgressDialog.incrementProgressBy(1);
                    handler.sendEmptyMessageDelayed(0, 100);
                } else {
                    // когда шкала заполнилась, диалог пропадает
                    scanProgressDialog.dismiss();
                }
            }
        };
        // имитируем подключение к удаленному серверу
        // (ожидаем 10 секунд перед стартом обновления индикатора)
        handler.sendEmptyMessageDelayed(0, 10000);

        /*
        Thread t = new Thread(new Runnable() {
            public void run() {
                for (int i = 1; i <= 10; i++) {
                    // долгий процесс
                    try {
                        int n = random.nextInt(10) * 1000;
                        Thread.sleep(1000 + n);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    handler.sendEmptyMessage(i);
                    // пишем лог
                    Log.d(LOG_TAG, "i = " + i);
                }
            }
        });
        t.start();
        */

        logging("the end!");
    }

    //---------------------------------------------------------------------------------------------
    public void click_test2(View view) {
        logging("test");

        String title = "Выбор есть всегда";
        String message = "Выбери пищу";
        String button1String = "Вкусная пища";
        String button2String = "Здоровая пища";

        AlertDialog.Builder ad;
        final Context context;
        context = MainActivity.this;
        ad = new AlertDialog.Builder(context);
        ad.setTitle(title);  // заголовок
        ad.setMessage(message); // сообщение
        ad.setPositiveButton(button1String, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {
                Toast.makeText(context, "Вы сделали правильный выбор",
                        Toast.LENGTH_LONG).show();
            }
        });
        ad.setNegativeButton(button2String, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {
                Toast.makeText(context, "Возможно вы правы", Toast.LENGTH_LONG)
                        .show();
            }
        });
        ad.setCancelable(true);
        ad.setOnCancelListener(new DialogInterface.OnCancelListener() {
            public void onCancel(DialogInterface dialog) {
                Toast.makeText(context, "Вы ничего не выбрали",
                        Toast.LENGTH_LONG).show();
            }
        });
        ad.show();
    }

    //---------------------------------------------------------------------------------------------
}
