package com.boss.http_client;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

public class FragmentOne extends Fragment {

    SeekBar spin;
    String caption;
    String ip;
    int port = 15000;
    int pwm_value = 0;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        Bundle bundle = getArguments();
        caption = bundle.getString("caption");
        ip = bundle.getString("ip");
        port = bundle.getInt("port");
        pwm_value = bundle.getInt("pwm_value");

        spin = new SeekBar(getActivity());
        spin.setMax(1023);
        spin.setProgress(pwm_value);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        TextView tv_caption = new TextView(getActivity());
        tv_caption.setText(caption);
        tv_caption.setTextColor(Color.BLACK);

        Button btn_ON = new Button(getActivity());
        btn_ON.setText("I");
        btn_ON.setMinimumWidth(0);
        btn_ON.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //String temp = host_str + "/gpio/1";
                Data data = new Data();
                data.ip = ip;
                data.port = port;
                data.pwm_value = 1023;
                spin.setProgress(1023);
                ((MainActivity) getActivity()).exec_task(data);
            }
        });

        Button btn_OFF = new Button(getActivity());
        btn_OFF.setText("0");
        btn_OFF.setMinimumWidth(0);
        btn_OFF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //String temp = host_str + "/gpio/0";
                Data data = new Data();
                data.ip = ip;
                data.port = port;
                data.pwm_value = 0;
                spin.setProgress(0);
                ((MainActivity) getActivity()).exec_task(data);
            }
        });

        ((MainActivity) getActivity()).send_log("value " + String.valueOf(pwm_value));
        spin.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
                //String temp = host_str + "/pwm/" + String.valueOf(seekBar.getProgress());
                Data data = new Data();
                data.ip = ip;
                data.port = port;
                data.pwm_value = seekBar.getProgress();
                spin.setProgress(seekBar.getProgress());
                ((MainActivity) getActivity()).exec_task(data);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,boolean fromUser) {
                // TODO Auto-generated method stub
                //String temp = host_str + "/pwm/" + String.valueOf(seekBar.getProgress());
                //logging(temp);
                //new ProgressTask().execute(temp);
            }
        });

        int width = 100;
        int height = 100;
        LinearLayout.LayoutParams lp_caption = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        lp_caption.gravity = Gravity.CENTER_VERTICAL;

        LinearLayout.LayoutParams lp_pwm_panel = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                height);

        LinearLayout.LayoutParams lp_spin = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        lp_spin.weight = 1;
        lp_spin.gravity = Gravity.CENTER_VERTICAL;

        LinearLayout.LayoutParams lp_btn = new LinearLayout.LayoutParams(
                width,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        LinearLayout pwm_panel = new LinearLayout(getActivity());
        pwm_panel.setLayoutParams(lp_pwm_panel);
        pwm_panel.setOrientation(LinearLayout.HORIZONTAL);
        pwm_panel.addView(tv_caption, lp_caption);
        pwm_panel.addView(spin,     lp_spin);
        pwm_panel.addView(btn_ON,   lp_btn);
        pwm_panel.addView(btn_OFF,  lp_btn);
        //pwm_panel.addView(btn_PWM);

        LinearLayout ll = new LinearLayout(getActivity());
        ll.setLayoutParams(layoutParams);
        ll.setOrientation(LinearLayout.VERTICAL);
        //ll.setOrientation(LinearLayout.HORIZONTAL);
        ll.addView(pwm_panel, layoutParams);

        return ll;
    }
}
