package com.boss.test_circularseekbar;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends Activity {
    CircularSeekBar circularSeekbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        circularSeekbar = new CircularSeekBar(this);
        circularSeekbar.setMaxProgress(100);
        circularSeekbar.setProgress(100);
        setContentView(circularSeekbar);
        circularSeekbar.invalidate();

        circularSeekbar.setSeekBarChangeListener(new CircularSeekBar.OnSeekChangeListener() {

            @Override
            public void onProgressChange(CircularSeekBar view, int newProgress) {
                Log.i("Welcome", "Progress:" + view.getProgress() + "/" + view.getMaxProgress());
            }
        });
    }
}
