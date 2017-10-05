package com.example.gps_test2;

import java.util.List;
import java.util.Timer;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("ShowToast")
public class MainActivity extends Activity implements LocationListener  {
	
	Settings_View settings_View;
	
	//public static long settings_val=10000;
	TextView textView1 ; 
	TextView textView2 = null; 
	Button button1;
	boolean clickme = false;
	//timerImp timer2= timerImp.getInstance();
	timerImp timer2 = new timerImp();
	public static Boolean TrackMeStarted = false;
	public static Timer repeat= new Timer("timer_rep",true);
	public static String final_parsed_location;
	public static Context context;
	public static int seletedOption = 2;
	public int flag = 0;
	String locationProvider = LocationManager.GPS_PROVIDER;
	String locationProvider_net = LocationManager.NETWORK_PROVIDER;
	public boolean location_callback_flag = false;
    @SuppressWarnings("unused")
	@Override
  
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getApplicationContext();
        setContentView(R.layout.activity_main); 
        final ImageButton button1 = (ImageButton)findViewById(R.id.button1);
        ConnectivityManager connectivityManager= (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
    	boolean connected = false;
    	//LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
    	NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        connected = networkInfo != null && networkInfo.isAvailable() && networkInfo.isConnected();
       // button1.setClickable(false);
    	
    	if(!connected)
    	{
    		Log.d("Saurav", "Data connection is not available");	
    		flag = 1;
    		onCreateDialog(savedInstanceState).show();
    		return;
    	}
    		Log.d("Saurav", "Data connection is available");
    		//showing the location at textview logic start
    		if(flag == 1)
        		return;
        	LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        	if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
        	{
        		Toast.makeText(getApplicationContext(), "GPS is Enabled in your device", Toast.LENGTH_SHORT).show();
        		Location lastKnownLocation = locationManager.getLastKnownLocation(locationProvider);
        	}
        	else if(locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
        	{
        		Toast.makeText(getApplicationContext(), "Forcing device to fetch location from network", Toast.LENGTH_SHORT).show();
        		Location lastKnownLocation = locationManager.getLastKnownLocation(locationProvider_net);
        	}
        	else
        	{
        		Toast.makeText(getApplicationContext(), "Gps is Disabled and Application will terminate", Toast.LENGTH_LONG);
        		Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        		startActivity(intent);
        		return;
        	}
        	
        	//Location lastKnownLocation = locationManager.getLastKnownLocation(locationProvider);
            
            TextView textView1 = (TextView)findViewById(R.id.textView1);
            //textView1.setText("Last KnownLocation"+"\nLat:"+String.valueOf(lastKnownLocation.getLatitude())+"\n Long:"+String.valueOf(lastKnownLocation.getLongitude()));
        	//request for location updates
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
        	{
        		locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0,this);
        	}
        	else if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
        	{
        		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0,this);
        	}
            //task running to send sms every min for now ()
            //if we not got location changed callback then don't forward the sms
    	      while(location_callback_flag)
    	      {
    	    	  return;
    	      }
    		//showing the location at textview logic end
    	      
    	      
    	  	//putting all sms sending logic in send button
    			button1.setOnClickListener(new OnClickListener() {
    				
    				@Override
    				public void onClick(View v) {
    					// TODO Auto-generated method stub
    					Toast.makeText(context, "Tracking Started", Toast.LENGTH_SHORT).show();
    					TrackMeStarted = true;
    					SendSms();	
    					button1.setEnabled(false);
    				}
    			});
    			
        
    }
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder
		.setCancelable(false)
		.setMessage("Application requires data connection for operating!")
		.setTitle("Internet Conenction")
		.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Intent intent = new Intent(Intent.ACTION_MAIN);
				intent.addCategory(Intent.CATEGORY_HOME);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
			}
		});
		
		return builder.create();
				}
    protected void SendSms()
    {
    	//Button button1 = (Button)findViewById(R.id.button1);
    
    	//super.onStart();
        repeat.scheduleAtFixedRate(timer2, 1000,Integer.valueOf(settingspreference.prefList));        
        //if network is enabled
    
    }
    
    ConnectivityManager connectivityManager;
    NetworkInfo wifiInfo, mobileInfo;
    boolean connected = false;
    public boolean isOnline(Context con) {
        try {
            connectivityManager = (ConnectivityManager) con.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        connected = networkInfo != null && networkInfo.isAvailable() &&
                networkInfo.isConnected();
        return connected;


        } catch (Exception e) {
            System.out.println("CheckConnectivity Exception: " + e.getMessage());
            Log.v("connectivity", e.toString());
        }
        return connected;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    //code to respond when options inside settings is selected
    public boolean onOptionsItemSelected(MenuItem item) {
        @SuppressWarnings("unused")
		Intent intent;
		// Handle presses on the action bar items
        switch (item.getItemId()) {
        case R.id.menu_settings:
           intent = new Intent(this, settingspreference.class);
           startActivity(intent);
        	return true;
            case R.id.menu_share:
                share_app();
                return true;
            case R.id.about_app:
            	Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/kodered/TrackMe-Ver-2.0/blob/master/README.md"));
            	startActivity(i); 
            default:
                return super.onOptionsItemSelected(item);
        }
    }

	private void share_app() {
		//message to send when sharing the app in twitter
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("text/plain");
		intent.putExtra(android.content.Intent.EXTRA_TEXT, "Just a location sharing app!" +
				"\n Find the Github Source at: https://github.com/kodered/TrackMe-Ver-2.0");
		startActivity(intent); 
		
	}

	@Override
	public void onLocationChanged(Location location) {
		//button1.setClickable(true);
		location_callback_flag = true;
		// TODO Auto-generated method stub
		Geocoder g1= new Geocoder(getApplicationContext());
		List<Address> locale= null;
		 String add = null;
		try
		{
			locale = g1.getFromLocation(location.getLatitude(), location.getLongitude(), 3);
		}
		catch(Exception e)
		{
		Log.d("Saurav_log", "Cant fetch address");
		}
	/*	finally
		{
		 Log.d("Saurav_log", "Finally Exiting");
		}*/
		   TextView textView2 = (TextView)findViewById(R.id.textView2);
		   if(locale != null)
		   {
			  add =locale.get(0).getAddressLine(0);
		   }
		//textView2.setText("From GPS: Lat:"+String.valueOf(location.getLatitude())+"Lon:"+String.valueOf(location.getLongitude()));		
		   textView2.setText("From GPS:Actual Address \n"+add+locale.get(0).getLocality()+" \n"+locale.get(0).getCountryName()+" \n"+locale.get(0).getSubLocality());
		   final_parsed_location = add+locale.get(0).getLocality()+" \n"+locale.get(0).getCountryName()+" \n"+locale.get(0).getSubLocality();
		   
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}

	
    
}

