package com.test.campushelper.adapter;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.test.campushelper.R;
import com.test.campushelper.activity.BigImagePagerActivity;
import com.test.campushelper.model.News;
import com.test.campushelper.view.ShowPicGridView;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsHolder>{

    private List<News> newsList;
    private Context context;
    private OnItemClickListener mItemClickListener;

    public NewsAdapter(Context context,List<News> list){
        this.context = context;
        this.newsList = list;
    }

    @Override
    public NewsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_notices_or_qusetions,parent,false);
        NewsHolder holder = new NewsHolder(view);
        return holder;

    }

    @Override
    public void onBindViewHolder(NewsHolder holder, final int position) {
        final News news = newsList.get(position);
        //使用Glide加载头像
        Glide.with(context)
                .load(news.getHeadUrl())
                .placeholder(R.drawable.ic_image_loading)
                .error(R.drawable.ic_empty_picture)
                .crossFade()
                .into(holder.head);
        holder.title.setText(news.getTitle());
        holder.time.setText(news.getTime());
        holder.depart.setText(news.getName());
        holder.content.setText(news.getContent());
        //图片列表
        if (news.isHasPic()){
            holder.gridView.setVisibility(View.VISIBLE);
            holder.gridView.setAdapter(new GridViewAdapter(context,news.getPicUrls(),true));
        }else{
            holder.gridView.setVisibility(View.GONE);
        }
        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItemClickListener.onItemClick(v,position);
            }
        };
        if (mItemClickListener != null){
            holder.head.setOnClickListener(clickListener);
            holder.title.setOnClickListener(clickListener);
            holder.depart.setOnClickListener(clickListener);
            holder.content.setOnClickListener(clickListener);
            holder.time.setOnClickListener(clickListener);
            holder.gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    //TODO 查看大图
                    Intent bigImageIntent = new Intent(context, BigImagePagerActivity.class);
                    bigImageIntent.putStringArrayListExtra("picUrls",(ArrayList<String>) news.getPicUrls());
                    bigImageIntent.putExtra("position",position);
                    Activity activity = (Activity) context;
                    activity.startActivity(bigImageIntent);
                    activity.overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return newsList.size();
    }

    public void addData(int i, News news) {
        newsList.add(i,news);
        notifyItemInserted(i);
        notifyItemRangeChanged(i,newsList.size());
    }

    class NewsHolder extends RecyclerView.ViewHolder{
        private CircleImageView head;
        private TextView title;
        private TextView time;
        private TextView depart;
        private TextView content;
        private ShowPicGridView gridView;
        public NewsHolder(View itemView) {
            super(itemView);
            head = itemView.findViewById(R.id.civ_notice_head);
            title = itemView.findViewById(R.id.tv_notice_name);
            time = itemView.findViewById(R.id.tv_notice_time);
            depart = itemView.findViewById(R.id.tv_notice_department);
            content = itemView.findViewById(R.id.tv_notice_content);
            gridView = itemView.findViewById(R.id.gv_notice_pic);
        }
    }

    /**
     *
     * item的回调接口
     */
    public interface OnItemClickListener{
        void onItemClick(View view, int position);
    }
    //定义一个设置点击监听器的方法
    public void setOnItemClickListener(NewsAdapter.OnItemClickListener itemClickListener) {
        this.mItemClickListener = itemClickListener;
    }
}
