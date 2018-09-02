package com.boss.test_styles;

import android.content.Intent;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    final String TAG = "States";
    TextView log_main;

    void logging(String text) {
        Log.v(TAG, text);
        log_main.append(text + "\n");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        log_main = (TextView)findViewById(R.id.log_main);

        for(int n=0; n<100; n++) {
            logging("onCreate " + String.valueOf(n));
        }

        getSupportLoaderManager().initLoader(R.id.loader_id, Bundle.EMPTY, new StubLoaderCallbacks());
    }

    public void page_1(View view) {
        Intent intent = new Intent(this, Page1.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public void page_2(View view) {
        Intent intent = new Intent(this, Page2.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public void page_3(View view) {
        Intent intent = new Intent(this, Page3.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public void page_4(View view) {
        logging("NO page_4");
    }

    public void page_5(View view) {
        logging("NO page_5");
    }

    public void page_6(View view) {
        logging("NO page_6");
    }

    public void test(View view) {
        //MyLog.i(TAG, "Info");
        //MyLog.d(TAG, "Debug");
        //MyLog.v(TAG, "Verbose");
        //MyLog.e(TAG, "Error");

        logging("TEST");
    }

    private class StubLoaderCallbacks implements LoaderManager.LoaderCallbacks<Integer> {

        @Override
        public Loader<Integer> onCreateLoader(int id, Bundle args) {
            if (id == R.id.loader_id) {
                return new StubLoader(MainActivity.this);
            }
            return null;
        }

        @Override
        public void onLoadFinished(Loader<Integer> loader, Integer data) {
            if (loader.getId() == R.id.loader_id) {
                Toast.makeText(MainActivity.this, getString(R.string.load_finished) + " " + String.valueOf(data), Toast.LENGTH_SHORT).show();
            }
        }


        @Override
        public void onLoaderReset(Loader<Integer> loader) {
            // Do nothing
        }
    }
}
