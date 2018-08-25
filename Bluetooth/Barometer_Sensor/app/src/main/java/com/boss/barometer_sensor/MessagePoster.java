package com.boss.barometer_sensor;

import android.widget.TextView;

public class MessagePoster implements Runnable {
    private TextView tv_temperature;
    private TextView tv_pressure;
    private TextView tv_atm;
    private TextView tv_altitude;
    private String message;
    public MessagePoster(TextView temperature,
                         TextView pressure,
                         TextView atm,
                         TextView altitude,
                         String message) {
        this.message = message;
        this.tv_temperature = temperature;
        this.tv_pressure = pressure;
        this.tv_atm = atm;
        this.tv_altitude = altitude;
    }

    public void run() {
        String[] ls = message.split("\\|");
        if(ls.length == 4) {
            String str_temperature = ls[0];
            String str_pressure = ls[1];
            String str_atm = ls[2];
            String str_altitude = ls[3];

            if(str_temperature.isEmpty())   str_temperature = "0";
            if(str_pressure.isEmpty())      str_pressure = "0";
            if(str_atm.isEmpty())           str_atm = "0";
            if(str_altitude.isEmpty())      str_altitude = "0";

            Float temperature = Float.parseFloat(str_temperature);
            Float pressure = Float.parseFloat(str_pressure) * 0.75006375541921f / 100.0f;
            Float atm = Float.parseFloat(str_atm);
            Float altitude = Float.parseFloat(str_altitude);
            
            tv_temperature.setText(Float.toString(temperature));
            tv_pressure.setText(Float.toString(pressure));
            tv_atm.setText(Float.toString(atm));
            tv_altitude.setText(Float.toString(altitude));
        }
    }

}
