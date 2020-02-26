package com.example.boss.test_imageswitcher;

import android.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.app.ActionBar.LayoutParams;
import android.widget.ViewSwitcher.ViewFactory;

public class MainActivity extends AppCompatActivity {

    private ImageSwitcher sw;
    private Button b1,b2;
    int index = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        b1 = (Button) findViewById(R.id.btn_prev);
        b2 = (Button) findViewById(R.id.btn_next);

        sw = (ImageSwitcher) findViewById(R.id.imageSwitcher);
        sw.setFactory(new ViewFactory() {
            @Override
            public View makeView() {
                ImageView myView = new ImageView(getApplicationContext());
                myView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                myView.setLayoutParams(new ImageSwitcher.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
                return myView;
            }
        });

        sw.setImageResource(R.drawable.pic_1);

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // prev image
                if(index > 0) index--;
                else index = 3;
                draw_image(index);
            }
        });

        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // next image
                if(index < 3) index++;
                else index = 0;
                draw_image(index);
            }
        });

        sw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dlg = new AlertDialog.Builder(MainActivity.this);
                dlg.setTitle("My Dialog");
                dlg.setMessage("pic_" + index);
                dlg.setNegativeButton("ОК", null);
                dlg.show();
            }
        });
    }

    private void draw_image(int index)
    {
        switch (index)
        {
            case 0:
                sw.setImageResource(R.drawable.pic_1);
                break;
            case 1:
                sw.setImageResource(R.drawable.pic_2);
                break;
            case 2:
                sw.setImageResource(R.drawable.pic_3);
                break;
            case 3:
                sw.setImageResource(R.drawable.pic_4);
                break;
            default:
                break;
        }
    }
}
