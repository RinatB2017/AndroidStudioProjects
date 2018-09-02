package com.boss.test_styles;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class Page3 extends AppCompatActivity {

    private TextView logView;

    void logging(String text) {
        logView.append(text + "\n");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page3);

        logView = (TextView)findViewById(R.id.log_page3);

        logging("onCreate");
    }

    public void page_1(View view) {
        Intent intent = new Intent(this, Page1.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public void page_2(View view) {
        Intent intent = new Intent(this, Page2.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public void page_main(View view) {
        Intent intent = new Intent(this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
