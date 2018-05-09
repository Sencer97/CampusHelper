package com.test.campushelper.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.test.campushelper.R;
import com.test.campushelper.model.Attender;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AttendAdapter extends BaseAdapter{
    private List<Attender> attendList;
    private LayoutInflater inflater;
    private Context context;
    public AttendAdapter(Context context, List<Attender> list) {
        this.attendList = list;
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return attendList.size();
    }

    @Override
    public Object getItem(int position) {
        return attendList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        AttendAdapter.ViewHolder holder;
        if (convertView == null) {
            holder = new AttendAdapter.ViewHolder();
            convertView = inflater.inflate(R.layout.item_attender, parent,false);
            holder.tv_time = (TextView) convertView.findViewById(R.id.tv_attend_time);
            holder.tv_name = (TextView) convertView.findViewById(R.id.tv_attend_nickname);
            holder.headIcon = (CircleImageView) convertView.findViewById(R.id.civ_attend_head);
            convertView.setTag(holder);
        } else {
            holder = (AttendAdapter.ViewHolder) convertView.getTag();
        }
        Attender attend = attendList.get(position);
        holder.tv_time.setText(attend.getTime());
        holder.tv_name.setText(attend.getName());
        Glide.with(context)
                .load(attend.getHeadUrl())
                .placeholder(R.drawable.ic_image_loading)
                .error(R.drawable.ic_empty_picture)
                .crossFade()
                .into(holder.headIcon);
        return convertView;

    }

    private class ViewHolder {
        private TextView tv_time;
        private TextView tv_name;
        private CircleImageView headIcon;
    }
}
