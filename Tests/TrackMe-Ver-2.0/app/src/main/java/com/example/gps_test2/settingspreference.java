package com.example.gps_test2;


 
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.widget.Toast;
 
public class settingspreference extends PreferenceActivity{
	
	static SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.context);
	public static Boolean prefCheckBox = sharedPreferences.getBoolean("Send_Chk", false);
	public static String prefList = sharedPreferences.getString("PREF_LIST", "no selection");
	
	
	@SuppressWarnings("deprecation")
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        addPreferencesFromResource(R.xml.settings);
        
        //Toast.makeText(MainActivity.context, "Preferences"+prefList, Toast.LENGTH_LONG).show();
        
    }
	
	
}