package com.boss.image;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

public class MessageBox {

    public static void info(Context context, String title, String message) {
        AlertDialog.Builder ad;
        ad = new AlertDialog.Builder(context);
        ad.setTitle(title);     // заголовок
        ad.setMessage(message); // сообщение
        ad.setIcon(R.drawable.ic_launcher_background);
        ad.setCancelable(true);
        ad.setNegativeButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {
                dialog.cancel();
            }
        });
        ad.show();
    }

    public static void warning(Context context, String title, String message) {
        AlertDialog.Builder ad;
        ad = new AlertDialog.Builder(context);
        ad.setTitle(title);     // заголовок
        ad.setMessage(message); // сообщение
        ad.setIcon(R.drawable.ic_launcher_background);
        ad.setCancelable(true);
        ad.setNegativeButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {
                dialog.cancel();
            }
        });
        ad.show();
    }

    public static void critical(Context context, String title, String message) {
        AlertDialog.Builder ad;
        ad = new AlertDialog.Builder(context);
        ad.setTitle(title);     // заголовок
        ad.setMessage(message); // сообщение
        ad.setIcon(R.drawable.ic_launcher_background);
        ad.setCancelable(true);
        ad.setNegativeButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {
                dialog.cancel();
            }
        });
        ad.show();
    }

//    public static AlertDialog question(Context context, String title, String message) {
//        AlertDialog.Builder ad;
//        ad = new AlertDialog.Builder(context);
//        ad.setTitle(title);     // заголовок
//        ad.setMessage(message); // сообщение
//        ad.setIcon(R.drawable.ic_launcher_background);
//        ad.setCancelable(true);
//        ad.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int arg1) {
//                dialog.cancel();
//            }
//        });
//        ad.setNegativeButton("No", new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int arg1) {
//                dialog.dismiss();
//            }
//        });
//        return ad.create();
//    }
}
