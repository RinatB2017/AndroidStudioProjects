package luck.materialdesign.tabsnavigator.tabs;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import luck.materialdesign.tabsnavigator.R;

/**
 * Created by Edwin on 15/02/2015.
 */
public class Tab5 extends Fragment {
    View content;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.tab_5,container,false);
        content = v;

        return v;
    }

    public void append_text(String text) {
        TextView view = (TextView)content.findViewById(R.id.textView_5);
        if(view != null) {
            view.setText("Tab5 [" + text + "]");
        }
    }
}
