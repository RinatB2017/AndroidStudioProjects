package com.boss.test_sound;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        WaveGeneratorStackOverflow sound = new WaveGeneratorStackOverflow(440);
        sound.start();
    }

    public class WaveGeneratorStackOverflow {

        private float frequency = 1500;

        private final int numSamples = 8000;
        private final double sample[] = new double[numSamples];
        private final byte generatedSnd[] = new byte[2 * numSamples];
        private AudioTrack audioTrack;


        public WaveGeneratorStackOverflow (float freq) {
            frequency = freq;
            generateSound();
        }

        private void generateSound() {
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
                        generatedSnd.length, AudioTrack.MODE_STATIC);

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
