package com.boss.test_texttospeech;

// Если этот код работает, его написал Александр Климов,
// а если нет, то не знаю, кто его писал.

// https://habr.com/ru/post/224685/

import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

public class MainActivity extends Activity implements
        TextToSpeech.OnInitListener, SeekBar.OnSeekBarChangeListener {

    TextView logView;
    EditText e_text;
    SeekBar volume_bar;
    AudioManager audioManager;
    private Button mButton;
    private TextToSpeech mTTS;

    //---------------------------------------------------------------------------------------------
    void logging(String text) {
        logView.append(text + "\n");
    }
    //---------------------------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        logView = (TextView)findViewById(R.id.logView);
        mTTS = new TextToSpeech(this, this);

        volume_bar = (SeekBar)findViewById(R.id.volume);
        volume_bar.setOnSeekBarChangeListener(this);

        e_text = (EditText)findViewById(R.id.editText);
        e_text.setText("А Васька слушает да ест");

        mButton = (Button) findViewById(R.id.button1);
        mButton.setEnabled(false);

        mButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                String text = e_text.getText().toString();
                logging(text);
                int res = mTTS.speak(text, TextToSpeech.QUEUE_FLUSH, null);
                logging("res = " + res);
            }
        });
    }

    //---------------------------------------------------------------------------------------------
    @Override
    public void onInit(int status) {
        // TODO Auto-generated method stub
        if (status == TextToSpeech.SUCCESS) {

            Locale locale = new Locale("ru");

            int result = mTTS.setLanguage(locale);
            //int result = mTTS.setLanguage(Locale.getDefault());

            audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            float maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
            float curVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);

            volume_bar.setMax((int)(maxVolume + 0.5f));
            volume_bar.setProgress((int)(curVolume + 0.5f));

            logging("maxVolume " + maxVolume);
            logging("curVolume " + curVolume);

            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                logging("Извините, этот язык не поддерживается");
            } else {
                mButton.setEnabled(true);
            }

        } else {
            logging("Ошибка!");
        }
    }

    //---------------------------------------------------------------------------------------------
    @Override
    public void onDestroy() {
        // Don't forget to shutdown mTTS!
        if (mTTS != null) {
            mTTS.stop();
            mTTS.shutdown();
        }
        super.onDestroy();
    }

    //---------------------------------------------------------------------------------------------
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        logging("volume " + progress);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
    }

    //---------------------------------------------------------------------------------------------
    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    //---------------------------------------------------------------------------------------------
    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    //---------------------------------------------------------------------------------------------
}

