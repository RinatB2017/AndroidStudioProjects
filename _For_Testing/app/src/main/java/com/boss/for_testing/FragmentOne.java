package com.boss.for_testing;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

public class FragmentOne extends Fragment {
    Button btn_1;
    Button btn_2;
    Button btn_3;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        btn_1 = new Button(getActivity());
        btn_2 = new Button(getActivity());
        btn_3 = new Button(getActivity());

        btn_1.setText("1");
        btn_2.setText("2");
        btn_3.setText("3");

        btn_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) getActivity()).send_log("1");
            }
        });

        btn_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) getActivity()).send_log("2");
            }
        });

        btn_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) getActivity()).send_log("3");
            }
        });

        LinearLayout ll = new LinearLayout(getActivity());
        ll.addView(btn_1);
        ll.addView(btn_2);
        ll.addView(btn_3);

        return ll;
    }
}
