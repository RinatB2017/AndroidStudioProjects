package com.example.boss.test_asynctask;

    import android.os.AsyncTask;
    import android.os.Bundle;
    import android.support.v7.app.AppCompatActivity;
    import android.view.View;
    import android.widget.Button;
    import android.widget.ProgressBar;
    import android.widget.TextView;

    import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private TextView mInfoTextView;
    private ProgressBar mProgressBar;
    private Button mStartButton;
    private ProgressBar mHorizontalProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mInfoTextView = (TextView) findViewById(R.id.textViewInfo);

        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mStartButton = (Button) findViewById(R.id.buttonStart);
        mHorizontalProgressBar = (ProgressBar) findViewById(R.id.progressBar);

        mProgressBar.setMax(14);
    }


    public void onClick(View view) {
        CatTask catTask = new CatTask();
        catTask.execute();
    }

    class CatTask extends AsyncTask<Void, Integer, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mInfoTextView.setText("Полез на крышу");
            mStartButton.setVisibility(View.INVISIBLE);
        }

        @Override
        protected Void doInBackground(Void... params) {

            try {
                int counter = 0;

                for (int i = 0; i < 14; i++) {
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
            mStartButton.setVisibility(View.VISIBLE);
            mHorizontalProgressBar.setProgress(0);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);

            mInfoTextView.setText("Этаж: " + values[0]);
            mHorizontalProgressBar.setProgress(values[0]);
        }

        private void getFloor(int floor) throws InterruptedException {
            TimeUnit.SECONDS.sleep(1);
        }
    }
}