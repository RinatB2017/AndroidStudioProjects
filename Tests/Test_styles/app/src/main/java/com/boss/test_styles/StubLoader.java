package com.boss.test_styles;

import android.content.Context;
import android.os.SystemClock;
import androidx.loader.content.AsyncTaskLoader;

public class StubLoader extends AsyncTaskLoader<Integer> {

   public StubLoader(Context context) {
       super(context);
   }

   @Override
   protected void onStartLoading() {
       super.onStartLoading();
       forceLoad();
   }

   @Override
   public Integer loadInBackground() {
       // emulate long-running operation
       SystemClock.sleep(2000);
       return 50;
   }
}
