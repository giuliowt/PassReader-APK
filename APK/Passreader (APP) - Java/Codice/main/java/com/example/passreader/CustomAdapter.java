package com.example.passreader;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import androidx.annotation.NonNull;

import java.util.ArrayList;


public class CustomAdapter extends ArrayAdapter<Values> {

    private Context context;

    public CustomAdapter(Context context, int resource, ArrayList<Values> objects) {
        super(context, resource, objects);
        this.context = context;
    }



    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String value1 = getItem(position).getValue1();
        String value2 = getItem(position).getValue2();

        LayoutInflater inflater = LayoutInflater.from(context);
        convertView = inflater.inflate(R.layout.activity_custom_item, parent, false);

        TextView first = convertView.findViewById(R.id.value1);
        TextView second = convertView.findViewById(R.id.value2);

        first.setText(value1);
        second.setText(value2);

        return convertView;
    }
}
