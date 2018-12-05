package com.boss.test_sound;

import android.graphics.Color;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    static final String LOG_TAG = "States";
    static final int FREQ = 20000;
    WaveGeneratorStackOverflow sound;

    EditText et_freq;
    Button btn_on;
    Button btn_off;

    TextView tv_log;
    Handler h_print;

    public void send_log(String text) {
        if (text == null) {
            return;
        }
        Message msg = new Message();
        msg.obj = text;
        h_print.sendMessage(msg);
    }

    void init_log() {
        tv_log = (TextView) findViewById(R.id.logView);
        tv_log.setTextColor(Color.BLACK);

        h_print = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                String text = (String) msg.obj;
                Log.i(LOG_TAG, text);
                tv_log.append(text + "\n");
            }
        };
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init_log();

        et_freq = (EditText)findViewById(R.id.et_freq);
        btn_on = (Button)findViewById(R.id.btn_on);
        btn_off = (Button)findViewById(R.id.btn_off);

        et_freq.setText(String.valueOf(FREQ));
        sound = new WaveGeneratorStackOverflow(FREQ);
    }

    public void sound_on(View view) {
        String temp = et_freq.getText().toString();
        if(temp.isEmpty()) {
            send_log("freq is emtpy");
            return;
        }

        int value = Integer.valueOf(temp);

        sound.set_freq(value);
        sound.start();
    }

    public void sound_off(View view) {
        if(sound == null) {
            send_log("sound is NULL");
            return;
        }
        sound.stop();
    }

    public class WaveGeneratorStackOverflow {
        private float frequency = 0;

        private final int numSamples = 44100;
        private final double sample[] = new double[numSamples];
        private final byte generatedSnd[] = new byte[2 * numSamples];
        private AudioTrack audioTrack;

        public WaveGeneratorStackOverflow (float freq) {
            frequency = freq;
            generateSound();
        }

        public void set_freq(float freq) {
            stop();
            frequency = freq;
            generateSound();
        }

        public void generateSound() {
            for (int i = 0; i < numSamples; ++i) {
                sample[i] = Math.sin(2 * Math.PI * i / (numSamples/frequency));
            }
            int idx = 0;
            for (final double dVal : sample) {
                final short val = (short) ((dVal * 32767));
                generatedSnd[idx++] = (byte) (val & 0x00ff);
                generatedSnd[idx++] = (byte) ((val & 0xff00) >>> 8);
            }
        }

        public void start() {
            if (audioTrack == null) {
                audioTrack = new AudioTrack(
                        AudioManager.STREAM_MUSIC,
                        numSamples,
                        AudioFormat.CHANNEL_OUT_MONO,
                        AudioFormat.ENCODING_PCM_16BIT,
                        generatedSnd.length,
                        AudioTrack.MODE_STATIC);

                audioTrack.write(generatedSnd, 0, generatedSnd.length);
                audioTrack.setLoopPoints(0, generatedSnd.length/2, -1);

                audioTrack.play();
            }
        }

        public void stop() {
            if (audioTrack != null) {
                audioTrack.stop();
                audioTrack.release();
                audioTrack = null;
            }
        }

    }
}
