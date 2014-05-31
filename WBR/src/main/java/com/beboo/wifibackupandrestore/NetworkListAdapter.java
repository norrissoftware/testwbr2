package com.beboo.wifibackupandrestore;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.SimpleAdapter;
import android.content.res.Resources;
import android.widget.TextView;

public class NetworkListAdapter extends SimpleAdapter {
    private LayoutInflater mInflater;

    private int[] colors = new int[]{0x00808080, 0x00606060};
    //private int[] colors = new int[] { 0x30FF0000, 0x300000FF };

    private Context context;

    private List<Map<String,String>> items;

    private String[] from;

    private int[] to;


    public NetworkListAdapter(Context context, List<Map<String, String>> items, int resource, String[] from, int[] to) {
        super(context, items, resource, from, to);
        this.context = context;
        this.items = items;
        this.from = from;
        this.to = to;
    }

    @Override
    public Object getItem(int position) {
        return super.getItem(position);
    }

    private void initRow(View view, int position) {
        Map<String, String> item = items.get(position);
        for (int i = 0; i < from.length; i++) {
            int viewElementId = to[i];
            String value = item.get(from[i]);
            TextView viewItem = (TextView)view.findViewById(viewElementId);
            viewItem.setText(value);
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        int layoutID = 0;

        if (position % 2 == 0) {
            // left
            layoutID = R.layout.left_row;
        }
        else {
            // left
            layoutID = R.layout.right_row;
        }

        View rowView = inflater.inflate(layoutID, parent, false);

        initRow(rowView,position);

        return rowView;
    }
}
