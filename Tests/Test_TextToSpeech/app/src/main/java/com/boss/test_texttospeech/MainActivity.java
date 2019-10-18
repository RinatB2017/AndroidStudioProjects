package com.boss.test_texttospeech;

// Если этот код работает, его написал Александр Климов,
// а если нет, то не знаю, кто его писал.

// https://habr.com/ru/post/224685/

import java.util.Locale;

import android.app.Activity;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity implements
        TextToSpeech.OnInitListener {

    TextView logView;
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

        mButton = (Button) findViewById(R.id.button1);
        mButton.setEnabled(false);

        mButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                String text = "А Васька слушает да ест";
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
}

