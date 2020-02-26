package com.example.boss.test_draw2d;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);

        Draw2D draw2D = new Draw2D(this);
        setContentView(draw2D);
    }
}
