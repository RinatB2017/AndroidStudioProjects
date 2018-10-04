package com.boss.armor;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class BluetoothName {

    //---------------------------------------------------------------------------------------------
    static public String get_name(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getString("device_name", "HC-05");
    }
    //---------------------------------------------------------------------------------------------
    static public void set_name(Context context, String name) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putString("device_name", name);
        editor.apply();
    }
    //---------------------------------------------------------------------------------------------
    static public String get_mac(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getString("device_mac", "00:14:02:10:09:04");
    }
    //---------------------------------------------------------------------------------------------
    static public void set_mac(Context context, String name) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putString("device_mac", name);
        editor.apply();
    }
    //---------------------------------------------------------------------------------------------

}
