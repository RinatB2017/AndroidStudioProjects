package com.boss.newmoonlight;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

public class Memory {

    private Context context;

    public Memory(Context context) {
        this.context = context;
    }

    //---------------------------------------------------------------------------------------------
    public String get_string(String name) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getString(name, "");
    }

    //---------------------------------------------------------------------------------------------
    public void set_string(String name, String value) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putString(name, value);
        editor.apply();
    }

    //---------------------------------------------------------------------------------------------
    public String get_string_value(String name) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getString(name, "");
    }

    //---------------------------------------------------------------------------------------------
    public boolean get_boolean_value(String name) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getBoolean(name, false);
    }

    //---------------------------------------------------------------------------------------------
    public void set_boolean_value(String name, boolean value) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putBoolean(name, value);
        editor.apply();
    }

    //---------------------------------------------------------------------------------------------
    public boolean get_int_value(String name, int def_value, int value) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        if (sp.contains(name)) {
            return false;
        }
        value = sp.getInt(name, def_value);
        return true;
    }

    //---------------------------------------------------------------------------------------------
    public boolean set_int_value(String name, int value) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(name, value);
        editor.apply();
        return true;
    }
    //---------------------------------------------------------------------------------------------

}
