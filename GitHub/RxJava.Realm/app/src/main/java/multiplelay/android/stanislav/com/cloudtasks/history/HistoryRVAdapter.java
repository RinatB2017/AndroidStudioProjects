package multiplelay.android.stanislav.com.cloudtasks.history;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;



import java.util.ArrayList;
import java.util.List;

import multiplelay.android.stanislav.com.cloudtasks.R;

/**
 * Created by LasVegas on 11.04.2017.
 */

public class HistoryRVAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private List<String> mListHistoryList = new ArrayList<>();
    private LayoutInflater mInflater;

    public HistoryRVAdapter(Context context){
        this.mInflater = LayoutInflater.from(context);
    }

    public void setList(List<String> list){
        mListHistoryList = list;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = mInflater.inflate(R.layout.task_layout, parent, false);

        return new HistoryRVAdapter.HistoryItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder baseHolder, int position) {

        if (baseHolder instanceof HistoryItemViewHolder) {
            HistoryItemViewHolder holder = (HistoryItemViewHolder) baseHolder;
            holder.TVtask.setText(mListHistoryList.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return mListHistoryList.size();
    }

    public void setListHistoryList(List<String> mListHistoryList) {
        this.mListHistoryList.clear();
        this.mListHistoryList = mListHistoryList;
        notifyDataSetChanged();
    }

    private class HistoryItemViewHolder extends RecyclerView.ViewHolder  {

        private TextView TVtask;

        public HistoryItemViewHolder(View view) {
            super(view);

            TVtask = (TextView) view.findViewById(R.id.tv_title_task);
        }
    }
}