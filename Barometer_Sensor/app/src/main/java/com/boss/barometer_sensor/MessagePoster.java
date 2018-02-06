package com.boss.barometer_sensor;

import android.widget.TextView;

public class MessagePoster implements Runnable {
    private TextView textView;
    private String message;
    public MessagePoster(TextView textView, String message) {
        this.textView = textView;
        this.message = message;
    }

public void run() {
//        String[] ls = message.split("|");
//        for (String str: ls) {
//            textView.append(str + "\n");
//        }
        textView.append(message + "\n");
    }
}
