package com.boss.for_testing;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.widget.Toast;

public class Test_AlertDialog {
    static final String LOG_TAG = "States";

    public void run(final Context context) {
        String title = "Заголовок";
        String message = "Выберите вариант";
        String button1String = "Да";
        String button2String = "Нет";

        AlertDialog.Builder ad;
        ad = new AlertDialog.Builder(context);
        ad.setTitle(title);     // заголовок
        ad.setMessage(message); // сообщение
        ad.setPositiveButton(button1String, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {
                Log.i(LOG_TAG, "Вы выбрали ДА");
            }
        });
        ad.setNegativeButton(button2String, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {
                Log.i(LOG_TAG, "Вы выбрали НЕТ");
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
}
