package com.example.noteapp.ui.notes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.noteapp.R;

public class CustomSpinnerAdapter extends ArrayAdapter<String> {
    private Context context;
    private String[] items;

    public CustomSpinnerAdapter(@NonNull Context context, String[] items) {
        super(context, R.layout.spinner_item, items);
        this.context = context;
        this.items = items;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.spinner_item, parent, false);
        }
        TextView textView = convertView.findViewById(R.id.spinner_item_text);
        textView.setText(items[position]);
        return convertView;
    }



    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.spinner_item, parent, false);
        }
        TextView textView = convertView.findViewById(R.id.spinner_item_text);
        textView.setText(items[position]);
        return convertView;
    }

}
