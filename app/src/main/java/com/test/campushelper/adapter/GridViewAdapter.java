package com.test.campushelper.adapter;


import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.test.campushelper.R;
import com.test.campushelper.model.ClassHelp;

import java.net.URL;
import java.util.List;

public class GridViewAdapter extends BaseAdapter{
    private List<Uri> uriList;
    private Context context;
    private onItemDeleteListener mOnItemDeleteListener;
    private ClassHelp help;
    private boolean isNoDelete = false;

    public GridViewAdapter(Context context, ClassHelp help,boolean isNoDelete){
        this.context = context;
        this.help = help;
        this.isNoDelete = isNoDelete;
    }
    public GridViewAdapter(Context context,List<Uri> list){
        this.context = context;
        this.uriList = list;
    }
    public void setData(List<Uri> list){
        uriList = list;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (isNoDelete){
            return  help.getPicUrls().size();
        }else{
            return uriList.size();
        }
    }

    @Override
    public Object getItem(int position) {
        if (isNoDelete){
            return help.getPicUrls().get(position);
        }else {
            return uriList.get(position);
        }

    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * 删除按钮的监听接口
     */
    public interface onItemDeleteListener {
        void onDeleteClick(int position);

    }
    public void setOnItemDeleteClickListener(onItemDeleteListener mOnItemDeleteListener) {
        this.mOnItemDeleteListener = mOnItemDeleteListener;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView == null){
            viewHolder = new ViewHolder();
            //使用三个参数的打气筒
            convertView = LayoutInflater.from(context).inflate(R.layout.item_grid_pic,parent,false);
            viewHolder.iv_image = convertView.findViewById(R.id.img_photo);
            viewHolder.iv_delete = convertView.findViewById(R.id.img_delete);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

        //显示图片
        if (isNoDelete){
            Glide.with(context)
                    .load(help.getPicUrls().get(position))
                    .into(viewHolder.iv_image);
            viewHolder.iv_delete.setVisibility(View.GONE);
        }else{
            Glide.with(context)
                    .load(uriList.get(position))
                    .into(viewHolder.iv_image);
            Glide.with(context)
                    .load(R.drawable.cha)
                    .into(viewHolder.iv_delete);
        }
        viewHolder.iv_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnItemDeleteListener.onDeleteClick(position);
            }
        });

        return convertView;
    }
    class ViewHolder {
        ImageView iv_image;
        ImageView iv_delete;
    }


}
