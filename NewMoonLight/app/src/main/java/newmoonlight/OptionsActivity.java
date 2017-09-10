package newmoonlight;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class OptionsActivity extends AppCompatActivity {

    final String LOG_TAG = "States";

    Button btn_apply;
    Button btn_cancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);

        btn_apply = (Button)findViewById(R.id.btn_apply);
        btn_cancel = (Button)findViewById(R.id.btn_cancel);

        View.OnClickListener apply = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(LOG_TAG, "apply");

                f_run_MainActivity();
            }
        };
        View.OnClickListener cancel = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(LOG_TAG, "cancel");

                f_run_MainActivity();
            }
        };

        btn_apply.setOnClickListener(apply);
        btn_cancel.setOnClickListener(cancel);
    }

    void f_run_MainActivity() {
        Intent intent = new Intent(this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
