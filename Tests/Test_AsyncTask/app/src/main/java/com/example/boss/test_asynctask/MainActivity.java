package com.example.boss.test_asynctask;

    import android.app.Activity;
    import android.os.AsyncTask;
    import android.os.Bundle;
    import android.support.v7.app.AppCompatActivity;
    import android.util.Log;
    import android.view.View;
    import android.widget.Button;
    import android.widget.ProgressBar;
    import android.widget.TextView;

    import java.util.concurrent.TimeUnit;

public class MainActivity extends Activity {

    private static TextView mInfoTextView;
    private static Button mStartButton;
    private static ProgressBar mHorizontalProgressBar;

    CatTask catTask;

    private static final String TAG = "States";
    private static final int max_floor = 25;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mInfoTextView = (TextView) findViewById(R.id.textViewInfo);

        mStartButton = (Button) findViewById(R.id.buttonStart);
        mHorizontalProgressBar = (ProgressBar) findViewById(R.id.progressBar);

        mHorizontalProgressBar.setMax(max_floor);

        catTask = (CatTask) getLastNonConfigurationInstance();
        if (catTask == null) {
            catTask = new CatTask();
        }
        // передаем в MyTask ссылку на текущее MainActivity
        catTask.link(this);
    }

    public Object onRetainNonConfigurationInstance() {
        // удаляем из CatTask ссылку на старое MainActivity
        catTask.unLink();
        return catTask;
    }

    public void onClick(View view) {
        //CatTask
        catTask = new CatTask();
        catTask.execute();
    }

    static class CatTask extends AsyncTask<Void, Integer, Void> {

        MainActivity activity;

        // получаем ссылку на MainActivity
        void link(MainActivity act) {
            activity = act;
        }

        // обнуляем ссылку
        void unLink() {
            activity = null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mInfoTextView.setText("Полез на крышу");
            mStartButton.setEnabled(false);
        }

        @Override
        protected Void doInBackground(Void... params) {

            try {
                int counter = 0;

                for (int i = 0; i < max_floor; i++) {
                    getFloor(counter);
                    publishProgress(++counter);
                }
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            mInfoTextView.setText("Залез");
            mStartButton.setEnabled(true);
            mHorizontalProgressBar.setProgress(0);
        }

        @Override
        protected void onProgressUpdate(final Integer... values) {
            super.onProgressUpdate(values);

            mInfoTextView.setText("Этаж: " + values[0]);
            mHorizontalProgressBar.setProgress(values[0]);

            Log.v(TAG, "Этаж: " + values[0] + " progress: " + mHorizontalProgressBar.getProgress());
        }

        private void getFloor(int floor) throws InterruptedException {
            TimeUnit.SECONDS.sleep(1);
        }
    }
}
