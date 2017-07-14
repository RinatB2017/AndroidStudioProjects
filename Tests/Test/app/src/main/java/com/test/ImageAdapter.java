package com.test;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

/**
 * Created by boss on 10.07.17.
 */

public class ImageAdapter extends BaseAdapter {

    private Context mContext;

    public int getCount() {
        return 0;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView = new ImageView(mContext);;

        return  imageView;
    }

    public Object getItem(int position) {
        return 0;
    }

    public long getItemId(int position) {
        return position;
    }
}
