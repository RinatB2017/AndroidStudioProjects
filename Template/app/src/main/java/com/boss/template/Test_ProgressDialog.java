package com.boss.template;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;

public class Test_ProgressDialog {
    ProgressDialog scanProgressDialog;
    Handler handler;

    public void run(Context context) {
        // https://javadevblog.com/dialogi-v-android-primer-raboty-s-progressdialog.html
        // http://developer.alexanderklimov.ru/android/java/random.php

        int minValue = 0;
        int maxValue = 100;

        scanProgressDialog = new ProgressDialog(context, R.style.DialogTheme);
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
    }
}
