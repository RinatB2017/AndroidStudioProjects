package com.boss.image;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    static final String LOG_TAG = "States";
    static final int GALLERY_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btn_test = (Button)findViewById(R.id.btn_test);
        btn_test.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                GifAnimationDrawable gif;
                ImageView ivGif = (ImageView) findViewById(R.id.imageView);

                try {
                    gif = new GifAnimationDrawable(getResources().openRawResource(R.raw.download));
                    gif.setOneShot(false);

                    ivGif.setImageDrawable(gif);
                    gif.setVisible(true, true);
                } catch (Resources.NotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        Button button = (Button)findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, GALLERY_REQUEST);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        Bitmap bitmap;
        ImageView imageView = (ImageView) findViewById(R.id.imageView);

        switch(requestCode) {
            case GALLERY_REQUEST:
                if(resultCode == RESULT_OK){
                    Uri selectedImage = imageReturnedIntent.getData();
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
//                        imageView.setImageBitmap(bitmap);

                        int w = bitmap.getWidth();
                        int h = bitmap.getHeight();

                        if(h == 32) {
                            if(false) {
                                String message = "width  = " + w + "\n" +
                                        "height = " + h;
                                show_message("Info", message);
                            }

                            Bitmap b_dst = create_bitmap(bitmap);
                            if(b_dst != null) {
                                imageView.setImageBitmap(b_dst);
                            }
                        } else {
                            show_message("Error", "Bad file");
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
        }
    }

    private Bitmap create_bitmap(Bitmap b_src) {
        int w = b_src.getWidth();
        int h = b_src.getHeight();

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        int temp = size.x / 32;

        Bitmap b_dest = Bitmap.createBitmap(h * temp,
                w * temp,
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(b_dest);

        Paint mPaint;
        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.FILL);

        for(int y=0; y<h; y++) {
            for(int x=0; x<w; x++) {
                int color = b_src.getPixel(x, y);

                mPaint.setColor(color);
                canvas.drawRect(y * temp, x * temp,
                        y * temp + temp, x * temp + temp,
                        mPaint);
            }
        }

        return b_dest;
    }

    private void show_message(String title, String message) {
        AlertDialog.Builder ad;
        ad = new AlertDialog.Builder(this);
        ad.setTitle(title);     // заголовок
        ad.setMessage(message); // сообщение
        ad.setIcon(R.drawable.ic_launcher_background);
        ad.setCancelable(true);
        ad.setNegativeButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {
                dialog.cancel();
            }
        });
        ad.show();
    }
}
