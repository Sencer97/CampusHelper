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

import java.util.List;

public class GridViewAdapter extends BaseAdapter{
    private List<Uri> uriList;
    private List<String> urlList;
    private Context context;
    private onItemDeleteListener mOnItemDeleteListener;
    private boolean isNoDelete = false;

    public GridViewAdapter(Context context ,List<String> urlList,boolean isNoDelete){
        this.context = context;
        this.urlList = urlList;
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
            return urlList.size();
        }else {
            return uriList.size();
        }
    }

    @Override
    public Object getItem(int position) {
        if (isNoDelete){
            return urlList.get(position);
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
                    .load(urlList.get(position))
                    .placeholder(R.drawable.ic_image_loading)
                    .error(R.drawable.ic_empty_picture)
                    .crossFade()
                    .into(viewHolder.iv_image);
            viewHolder.iv_delete.setVisibility(View.GONE);
        }else{
            Glide.with(context)
                    .load(uriList.get(position))
                    .placeholder(R.drawable.ic_image_loading)
                    .error(R.drawable.ic_empty_picture)
                    .crossFade()
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
