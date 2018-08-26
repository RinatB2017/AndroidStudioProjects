package com.boss.fragment_template;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class SecondFragment extends Fragment implements android.view.View.OnClickListener {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.second_fragment,
                container, false);

        final TextView log = (TextView)view.findViewById(R.id.logView2);

        Button btn2 = (Button) view.findViewById(R.id.button_second);
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                log.append("2\n");
            }
        });

        return view;
    }

    @Override
    public void onClick(View v) {
        // implements your things
    }
}
