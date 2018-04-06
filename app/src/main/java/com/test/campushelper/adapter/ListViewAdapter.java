package com.test.campushelper.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.test.campushelper.R;
import com.test.campushelper.model.Friend;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * 好友列表适配器
 */

public class ListViewAdapter extends BaseAdapter{
    private List<Friend> list;
    private LayoutInflater inflater;

    public ListViewAdapter(Context context, List<Friend> list) {
        this.list = list;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.item_friend, parent,false);
            holder.catalog = (TextView) convertView.findViewById(R.id.tv_catalog);
            holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            holder.headIcon = (CircleImageView) convertView.findViewById(R.id.civ_head);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        String word = list.get(position).getFirstLetter();
        holder.catalog.setText(word);
        holder.tv_name.setText(list.get(position).getName());
        holder.headIcon.setImageResource(R.drawable.head);
        //将相同字母开头的合并在一起
        if (position == 0) {
            //第一个是一定显示的
            holder.catalog.setVisibility(View.VISIBLE);
        } else {
            //后一个与前一个对比,判断首字母是否相同，相同则隐藏
            String headerWord = list.get(position - 1).getFirstLetter();
            if (word.equals(headerWord)) {
                holder.catalog.setVisibility(View.GONE);
            } else {
                holder.catalog.setVisibility(View.VISIBLE);
            }
        }
        return convertView;
    }

    private class ViewHolder {
        private TextView catalog;     //字母索引
        private TextView tv_name;     //好友名字
        private CircleImageView headIcon;
        private String headUrl;
    }
}
