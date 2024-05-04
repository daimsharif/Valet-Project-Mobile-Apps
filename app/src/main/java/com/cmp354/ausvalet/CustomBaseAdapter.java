package com.cmp354.ausvalet;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class CustomBaseAdapter extends BaseAdapter {

    Context context;
    ArrayList<String> names;
    ArrayList<String> ids;
    ArrayList<String> points;

    LayoutInflater inflater;

    public CustomBaseAdapter(Context context, ArrayList<String> names, ArrayList<String> ids, ArrayList<String> points){
        this.context = context;
        this.names = names;
        this.ids = ids;
        this.points = points;

        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return names.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup parent) {
        view = inflater.inflate(R.layout.activity_item, null);
        TextView tv_itemName = view.findViewById(R.id.tv_itemName);
        TextView tv_itemID = view.findViewById(R.id.tv_itemID);
        TextView tv_itemPoints = view.findViewById(R.id.tv_itemPoints);

        tv_itemName.setText(names.get(i));
        tv_itemID.setText(ids.get(i));
        tv_itemPoints.setText(points.get(i));

        return view;
    }
}
