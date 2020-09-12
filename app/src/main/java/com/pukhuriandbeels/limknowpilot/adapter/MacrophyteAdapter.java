package com.pukhuriandbeels.limknowpilot.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.pukhuriandbeels.limknowpilot.R;
import com.pukhuriandbeels.limknowpilot.model.Macrophyte;

import java.util.ArrayList;

public class MacrophyteAdapter extends BaseAdapter {
    private ArrayList<Macrophyte> macrophyteArrayList;
    private LayoutInflater layoutInflater;
    private Context context;

    //    private TextView serialNum, name, contactNum;
    private class MacrophyteViewHolder {
        ImageView imageView;
        TextView textView;

        MacrophyteViewHolder(){}
    }

    public MacrophyteAdapter(Context context, ArrayList<Macrophyte> macrophyteArrayList) {
        this.context = context;
        this.macrophyteArrayList = macrophyteArrayList;
    }

    @Override
    public int getCount() {
        return macrophyteArrayList.size();
    }

    @Override
    public Macrophyte getItem(int position) {
        return macrophyteArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.macrophyte_list_item, parent, false);
        }
        MacrophyteViewHolder macrophyteViewHolder = new MacrophyteViewHolder();
        macrophyteViewHolder.textView = convertView.findViewById(R.id.macrophyte_name);
        macrophyteViewHolder.imageView = convertView.findViewById(R.id.macrophyte_image);

        macrophyteViewHolder.textView.setText(macrophyteArrayList.get(position).getMacrophyteName());
        macrophyteViewHolder.imageView.setImageResource(macrophyteArrayList.get(position).getMacrophyteImageUri());

        return convertView;
    }
}
