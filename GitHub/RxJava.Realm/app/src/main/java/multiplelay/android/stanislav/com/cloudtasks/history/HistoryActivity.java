package multiplelay.android.stanislav.com.cloudtasks.history;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import multiplelay.android.stanislav.com.cloudtasks.R;
import multiplelay.android.stanislav.com.cloudtasks.db.DBservise;
import multiplelay.android.stanislav.com.cloudtasks.db.models.TaskRealModel;

public class HistoryActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private HistoryRVAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        Realm.init(this);
        DBservise dBservise = new DBservise();

        mAdapter = new HistoryRVAdapter(getBaseContext());

        dBservise.getAll(TaskRealModel.class)
                .subscribe(taskRealModels -> {
                    List<String> historyList = new ArrayList<String>();

                    for (TaskRealModel model: taskRealModels) {
                        historyList.add(model.getTitle());
                    }
                    mAdapter.setList(historyList);
                });

        mRecyclerView = (RecyclerView) findViewById(R.id.rv_content);

        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.setAdapter(mAdapter);
    }
}
