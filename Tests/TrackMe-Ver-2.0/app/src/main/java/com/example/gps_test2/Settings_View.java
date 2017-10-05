package com.example.gps_test2;

import java.util.Timer;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class Settings_View extends ListActivity
{
		//TimePicker pickerTime;
		
	 	
		
		Timer rescheduleTimer= new Timer("ResettingTimer",true);
		timerImp my_timer_task = new timerImp();
		//timerImp timer3= timerImp.getInstance();
		public void onCreate(Bundle icicle)
		{
			super.onCreate(icicle);
			String[] values = new String[] { "Set Timers", "Send Sms?"};
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
	        android.R.layout.simple_list_item_1, values){ 
	            public boolean isEnabled(int position) 
	            { 
		            if(MainActivity.TrackMeStarted == true &&  position == 0)
		            {
		                return false;
		            }
		            return true;
	            } 
			};      
			setListAdapter(adapter);
	  }
		  protected void onListItemClick(ListView l, View v, int position, long id) {
			    String item = (String) getListAdapter().getItem(position);
			    if(item.equalsIgnoreCase("Set Timers"))
			    {	
			    	//Toast.makeText(this, "Set Timers", Toast.LENGTH_SHORT).show();
			    	
			    	final CharSequence[] items={"10 Mins","30 Mins","60 Mins"};
			    	
			    	AlertDialog.Builder builder=new AlertDialog.Builder(this);
			    	builder.setIcon(R.drawable.sms);
			    	builder.setTitle("Set Interval");
			    	//builder.setMessage("This interval determines the timegap while sending SMS to the selected contacts");
			    	builder.setPositiveButton("Set", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							if(Constants.settings_val == 1000)
							{	
								//timer3.cancel();
								//MainActivity.repeat.cancel();
								//MainActivity.repeat.purge();
								MainActivity.seletedOption = 0;
								Constants.settings_val = 1000;
								//rescheduleTimer.scheduleAtFixedRate(my_timer_task, 0 , Constants.settings_val);
								//repeatAgain.scheduleAtFixedRate(timer3, 1 ,Constants.settings_val);
								Toast.makeText(getBaseContext(), "10  Selected", Toast.LENGTH_SHORT).show();
							}
							else if(Constants.settings_val == 1000)
							{
								//timer3.cancel();
								
								//MainActivity.repeat.cancel();
								//MainActivity.repeat.purge();
								MainActivity.seletedOption = 1;
								MainActivity.repeat.cancel();
								Constants.settings_val = 3000;
								//rescheduleTimer.scheduleAtFixedRate(my_timer_task, 0 , Constants.settings_val);
								//repeatAgain.scheduleAtFixedRate(timer3, 1 ,Constants.settings_val);
								Toast.makeText(getBaseContext(), "30 secs Selected", Toast.LENGTH_SHORT).show();
							} 
							else if(Constants.settings_val == 1000)
							{
								//timer3.cancel();
								//MainActivity.repeat.cancel();
								//MainActivity.repeat.purge();
								MainActivity.seletedOption = 2;
								Constants.settings_val = 6000;
								//rescheduleTimer.scheduleAtFixedRate(my_timer_task, 0 , Constants.settings_val);
								Toast.makeText(getBaseContext(), "60 Mins Selected", Toast.LENGTH_SHORT).show();
							}
							
							
						}
					});
			    
			    	builder.setSingleChoiceItems(items,MainActivity.seletedOption, new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub	
							
							if("10 Mins".equals(items[which]))
							{	
								Constants.settings_val = 1000;
								Toast.makeText(getBaseContext(), "10  Mins Touched", Toast.LENGTH_SHORT).show();
							}
							else if("30 Mins".equals(items[which]))
							{
								Constants.settings_val = 3000;
								Toast.makeText(getBaseContext(), "30 Mins Touched", Toast.LENGTH_SHORT).show();
							} 
							else if("60 Mins".equals(items[which]))
							{
								Constants.settings_val = 6000;
								Toast.makeText(getBaseContext(), "60 Mins Touched", Toast.LENGTH_SHORT).show();
							}
							
					
						}
					});
			    	builder.show();
			    	
			    }
			    if(item.equalsIgnoreCase("Send Sms?"))
			    {
			    	Toast.makeText(this, "Send Sms", Toast.LENGTH_SHORT).show();
			    }
			  }
	}