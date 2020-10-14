package com.pukhuriandbeels.limknowpilot.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.pukhuriandbeels.limknowpilot.R;
import com.pukhuriandbeels.limknowpilot.model.Macrophyte;

import java.util.List;

public class MacrophyteAdapter extends BaseAdapter {
    private List<Macrophyte> macrophyteArrayList;
    private LayoutInflater layoutInflater;
    private Context context;

    public MacrophyteAdapter(Context context, List<Macrophyte> macrophyteArrayList) {
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
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.macrophyte_list_item, parent, false);
        }
        MacrophyteViewHolder macrophyteViewHolder = new MacrophyteViewHolder();

        macrophyteViewHolder.textView = convertView.findViewById(R.id.macrophyte_name);
        macrophyteViewHolder.imageView = convertView.findViewById(R.id.macrophyte_image);

        if (macrophyteArrayList.get(position).isInvasiveSpecies()) {
            macrophyteViewHolder.textView.setText(macrophyteArrayList.get(position).getMacrophyteName() + " " + "\n(Invasive Species)");
        } else {
            macrophyteViewHolder.textView.setText(macrophyteArrayList.get(position).getMacrophyteName());
        }

        if (macrophyteArrayList.get(position).getMacrophyteImageURL() != null) {
            Uri uri = Uri.parse(macrophyteArrayList.get(position).getMacrophyteImageURL());
            Glide.with(context).load(uri).error(R.drawable.sample_macrophyte).into(macrophyteViewHolder.imageView);
        }
        return convertView;
    }

    //    private TextView serialNum, name, contactNum;
    private class MacrophyteViewHolder {
        ImageView imageView;
        TextView textView;

        MacrophyteViewHolder() {
        }
    }

}
