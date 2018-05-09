package com.test.campushelper.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.test.campushelper.R;
import com.test.campushelper.model.Reply;

import java.util.List;

public class ReplyAdapter extends BaseAdapter{
    private List<Reply> replys;
    private LayoutInflater inflater;
    public ReplyAdapter(){}
    public ReplyAdapter(Context context,List<Reply> replys) {
        this.replys = replys;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return replys.size();
    }

    @Override
    public Object getItem(int position) {
        return replys.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null){
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.item_reply, parent,false);
            holder.tv_from = (TextView) convertView.findViewById(R.id.tv_reply_from);
            holder.tv_to = (TextView) convertView.findViewById(R.id.tv_reply_to);
            holder.tv_content = (TextView) convertView.findViewById(R.id.tv_reply_content);
            holder.tv_text = convertView.findViewById(R.id.tv_text);
            holder.tv_quot = convertView.findViewById(R.id.tv_quot);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        Reply reply = replys.get(position);
        holder.tv_from.setText(reply.getFrom());
        holder.tv_to.setText(reply.getTo());
        holder.tv_content.setText(reply.getContent());
        holder.tv_text.setText("回复");
        holder.tv_quot.setText(":");
        return convertView;
    }



    private class ViewHolder{
        private TextView tv_from,tv_text,tv_to,tv_quot,tv_content;
    }
}
