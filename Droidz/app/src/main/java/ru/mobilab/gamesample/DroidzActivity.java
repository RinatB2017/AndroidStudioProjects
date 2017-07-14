package ru.mobilab.gamesample;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

public class DroidzActivity extends Activity {
    /** Вызывается при создании activity. */

 private static final String TAG = DroidzActivity.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // запрос на отключение строки заголовка 
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // перевод приложения в полноэкранный режим
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // устанавливаем MainGamePanel как View
        setContentView(new MainGamePanel(this));
        Log.d(TAG, "View added");
    }

 @Override
 protected void onDestroy() {
  Log.d(TAG, "Destroying...");
  super.onDestroy();
 }

 @Override
 protected void onStop() {
  Log.d(TAG, "Stopping...");
  super.onStop();
 }
}
