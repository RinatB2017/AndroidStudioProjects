package com.boss.fragment_template;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class FirstFragment extends Fragment implements android.view.View.OnClickListener {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.first_fragment,
                container, false);

        final TextView log = (TextView)view.findViewById(R.id.logView1);

        Button btn1 = (Button) view.findViewById(R.id.button_first);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                log.append("1\n");
            }
        });

        return view;
    }

    @Override
    public void onClick(View v) {
        // implements your things
    }
}
