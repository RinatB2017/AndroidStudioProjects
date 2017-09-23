package com.guidedmeditationtreks.binaural;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.ToggleButton;
import java.io.InputStream;

public class MainActivity extends Activity {

	//Class variables
	private EditText frequencyCarrierInput;
	private EditText frequencyBeatInput;
	private TextView displayCarrierFrequency;
	private TextView displayBeatFrequency;
	private RadioGroup radioBeatGroup;
	private ToggleButton startStop;
	private boolean isDataChanged = true, isCarrierValid = true, isBeatValid = true;
	private BeatsEngine wave;

	//Refresh waveform if user changes frequencies
	View.OnFocusChangeListener focusChangeListener = new View.OnFocusChangeListener() {
		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			if (isDataChanged && startStop.isActivated()){
				refresh();
				attemptStartWave();
			}
		}
	};
	TextWatcher textWatcherCarrier = new TextWatcher() {

		public void afterTextChanged(Editable s)
		{
			isCarrierValid = isDataChanged = false;
			if( frequencyCarrierInput.getText().toString().length() == 0 )
				frequencyCarrierInput.setError( "Carrier Frequency is required!" );
			else {
				try {
					float carrierFrequency = Float.parseFloat(frequencyCarrierInput.getText().toString());
					if (carrierFrequency < 20)
						frequencyCarrierInput.setError( "Carrier Frequency must be greater than 20!" );
					else if (carrierFrequency > 1200)
						frequencyCarrierInput.setError( "Carrier Frequency must be less than 1200!" );
					else {
						isCarrierValid = true;
						if (isBeatValid) {
							isDataChanged = true;
						}
					}
				}catch (NumberFormatException ex) {
					frequencyCarrierInput.setError( "Carrier Frequency is required!" );
				}
			}
		}

		public void beforeTextChanged(CharSequence s, int start,
								  int count, int after) {
		}

		public void onTextChanged(CharSequence s, int start,
							  int before, int count) {
		}

	};

	TextWatcher textWatcherBeat = new TextWatcher() {

		public void afterTextChanged(Editable s)
		{
			isBeatValid = isDataChanged = false;
			if( frequencyBeatInput.getText().toString().length() == 0 )
				frequencyBeatInput.setError( "Beat Frequency is required!" );
			else {
				float carrierFrequency;
				try {
					carrierFrequency= Float.parseFloat(frequencyBeatInput.getText().toString());
					int selectedId = radioBeatGroup.getCheckedRadioButtonId();
					RadioButton selectedButton = (RadioButton) findViewById(selectedId);
					double minVal = getString(R.string.binaural_radio).equals(selectedButton.getText()) ?  0 : .5;
					if (carrierFrequency < minVal)
						frequencyBeatInput.setError(String.format("Beat Frequency must be greater than %d1!", minVal ));
					else if (carrierFrequency > 1200)
						frequencyBeatInput.setError( "Beat Frequency must be less than 1200!" );
					else {
						isBeatValid = true;
						if (isCarrierValid)
						{
							isDataChanged = true;
						}
					}
				} catch (NumberFormatException ex)
				{
					frequencyBeatInput.setError( "Beat Frequency is required!" );
				}
			}
		}

		public void beforeTextChanged(CharSequence s, int start,
									  int count, int after) {
		}

		public void onTextChanged(CharSequence s, int start,
								  int before, int count) {
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// займем весь экран
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION           // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN                // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE);
		//---

		setContentView(R.layout.main_activity);
		initializeView();
	}

	private void initializeView() {
		frequencyCarrierInput = (EditText) findViewById(R.id.etCarrierFrequency);
		frequencyBeatInput = (EditText) findViewById(R.id.etBeatFrequency);
		radioBeatGroup = (RadioGroup) findViewById(R.id.radioBeatType);
		startStop = (ToggleButton) findViewById(R.id.btnPlay);
		displayCarrierFrequency = (TextView) findViewById(R.id.tvCarrierFrequency);
		displayBeatFrequency = (TextView) findViewById(R.id.tvBeatFrequency);

		//By default, and when someone changes anything, denote that data has been updated
		isDataChanged = true;
		frequencyCarrierInput.addTextChangedListener(textWatcherCarrier);
		frequencyCarrierInput.setOnFocusChangeListener(focusChangeListener);
		frequencyBeatInput.addTextChangedListener(textWatcherBeat);
		frequencyBeatInput.setOnFocusChangeListener(focusChangeListener);
		radioBeatGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

					public void onCheckedChanged(RadioGroup group, int checkedId) {
						if (checkedId == R.id.radioIsochronic)
						{
							float beatFrequency = Float.parseFloat(frequencyBeatInput.getText().toString());
							if (beatFrequency < .5) {
								frequencyBeatInput.setText(".5");
							}
						}
						isDataChanged = true;
					}
				});

		//Linkify the title using our internal version of linkify
		TextView gmt = (TextView) findViewById(R.id.gmtTitle);
		String scheme = getString(R.string.url);
		Linkify.addLinks(gmt, getString(R.string.gmt), scheme);

		//Prepare noise track
		InputStream inputStream = getResources().openRawResource(R.raw.pinkwave);
	}

	//Play button clicked
	public void clickPlay(View v) {
		togglePlay();
	}

	private void togglePlay() {
		startStop.setActivated(!startStop.isActivated());

		if (startStop.isActivated()) {
			if (isDataChanged) {
				refresh();
			}
			//wave.start();
			attemptStartWave();
		} else {
			wave.stop();
		}
	}

	//if user goes too fast
	private void attemptStartWave() {
		if (!wave.getIsPlaying()) {
			wave.start();
			startStop.setActivated(true);
			startStop.setChecked(true);
		} else {
			startStop.setActivated(false);
			startStop.setChecked(false);
		}
	}

	public void runCountdown(View v) {
		int countdownLength = Integer.parseInt((String)v.getTag()) * 1000 * 60;

		if (!startStop.isActivated())
		{
			startStop.setChecked(true);
			togglePlay();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	//Refresh the tones
	private void refresh() {
		float carrierFrequency, beatFrequency;
		int selectedId = radioBeatGroup.getCheckedRadioButtonId();
		RadioButton selectedButton = (RadioButton) findViewById(selectedId);
		carrierFrequency = Float.parseFloat(frequencyCarrierInput.getText().toString());
		beatFrequency = Float.parseFloat(frequencyBeatInput.getText().toString());

		if (wave != null) {
			wave.release();
		}

		wave = getString(R.string.binaural_radio).equals(selectedButton.getText())
				? new Binaural(carrierFrequency, beatFrequency)
				: new Isochronic(carrierFrequency, beatFrequency);

		isDataChanged = false;

		displayCarrierFrequency.setText(
				getString(R.string.binaural_radio).equals(selectedButton.getText())
						? String.format("%.2f / %.2f", carrierFrequency + beatFrequency / 2, carrierFrequency - beatFrequency / 2)
						: String.valueOf(carrierFrequency)
		);
		displayBeatFrequency.setText(String.valueOf(beatFrequency));
	}

    public void f_Focus(View view) {
		frequencyCarrierInput.setText("263");
		frequencyBeatInput.setText("24");
		if (isDataChanged && startStop.isActivated()){
			refresh();
			attemptStartWave();
		}
    }

	public void f_Creativity(View view) {
		frequencyCarrierInput.setText("180");
		frequencyBeatInput.setText("10.6");
		if (isDataChanged && startStop.isActivated()){
			refresh();
			attemptStartWave();
		}
	}

	public void f_Relaxation(View view) {
		frequencyCarrierInput.setText("136.1");
		frequencyBeatInput.setText("6");
		if (isDataChanged && startStop.isActivated()){
			refresh();
			attemptStartWave();
		}
	}

	public void f_Sleep(View view) {
		frequencyCarrierInput.setText("141.27");
		frequencyBeatInput.setText("3.4");
		if (isDataChanged && startStop.isActivated()){
			refresh();
			attemptStartWave();
		}
	}

	public void f_Chanting(View view) {
		frequencyCarrierInput.setText("432");
		frequencyBeatInput.setText("4.5");
		if (isDataChanged && startStop.isActivated()){
			refresh();
			attemptStartWave();
		}
	}

	public void f_Intuition(View view) {
		frequencyCarrierInput.setText("211.44");
		frequencyBeatInput.setText("5.5");
		if (isDataChanged && startStop.isActivated()){
			refresh();
			attemptStartWave();
		}
	}

	public void f_Astral_Travel(View view) {
		frequencyCarrierInput.setText("140");
		frequencyBeatInput.setText("6.3");
		if (isDataChanged && startStop.isActivated()){
			refresh();
			attemptStartWave();
		}
	}

	public void f_Healing(View view) {
		frequencyCarrierInput.setText("150");
		frequencyBeatInput.setText("8.0");
		if (isDataChanged && startStop.isActivated()){
			refresh();
			attemptStartWave();
		}
	}

	public void f_Alpha(View view) {
		frequencyCarrierInput.setText("306.9");
		frequencyBeatInput.setText("9.3");
		if (isDataChanged && startStop.isActivated()){
			refresh();
			attemptStartWave();
		}
	}

	public void f_Intelligence(View view) {
		frequencyCarrierInput.setText("492");
		frequencyBeatInput.setText("13.0");
		if (isDataChanged && startStop.isActivated()){
			refresh();
			attemptStartWave();
		}
	}

	public void f_Euphoria(View view) {
		frequencyCarrierInput.setText("210.42");
		frequencyBeatInput.setText("20.0");
		if (isDataChanged && startStop.isActivated()){
			refresh();
			attemptStartWave();
		}
	}
}
