package com.setscreenbrightness_android_examples.com;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.provider.Settings;

public class MainActivity extends Activity {

	SeekBar seekbar;
	TextView progress;
	Context context;
	int Brightness;

    TextView log;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        seekbar = (SeekBar)findViewById(R.id.seekBar1);
        progress = (TextView)findViewById(R.id.textView1);
        context = getApplicationContext();

        log = (TextView)findViewById(R.id.logView);

        log.append("Brightness = " + String.valueOf(Settings.System.getInt(context.getContentResolver(),Settings.System.SCREEN_BRIGHTNESS,0)));

        //Getting Current screen brightness.
        Brightness = Settings.System.getInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, 0);
        
        //Setting up current screen brightness to seekbar;
        seekbar.setProgress(Brightness);
        
      //Setting up current screen brightness to TextView;
        progress.setText("Screen Brightness : " + Brightness);
        
        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
               
                progress.setText("Screen Brightness : " + i);

                //Changing Brightness on seekbar movement.
                if(i >= 0 && i <= 255) {
    	            Settings.System.putInt(context.getContentResolver(),Settings.System.SCREEN_BRIGHTNESS,i);
    	        }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        
    }
}
