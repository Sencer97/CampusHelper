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
import com.test.campushelper.model.RecruitInfo;
import com.test.campushelper.view.ShowPicGridView;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class RecruitAdapter extends RecyclerView.Adapter<RecruitAdapter.RecruitHolder>{
    private List<RecruitInfo> recruitInfoList;
    private Context context;
    private NewsAdapter.OnItemClickListener mItemClickListener;

    public RecruitAdapter(Context context,List<RecruitInfo> list){
        this.context = context;
        this.recruitInfoList = list;
    }

    @Override
    public RecruitAdapter.RecruitHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_notices_or_qusetions,parent,false);
        RecruitAdapter.RecruitHolder holder = new RecruitAdapter.RecruitHolder(view);
        return holder;

    }

    @Override
    public void onBindViewHolder(RecruitAdapter.RecruitHolder holder, final int position) {
        final RecruitInfo recruitInfo = recruitInfoList.get(position);
        //使用Glide加载头像
        Glide.with(context)
                .load(recruitInfo.getHeadUrl())
                .placeholder(R.drawable.ic_image_loading)
                .error(R.drawable.ic_empty_picture)
                .crossFade()
                .into(holder.head);
        holder.title.setText(recruitInfo.getTitle());
        holder.time.setText(recruitInfo.getTime());
        holder.name.setText(recruitInfo.getName());
        holder.content.setText(recruitInfo.getContent());
        //图片列表
        if (recruitInfo.isHasPic()){
            holder.gridView.setVisibility(View.VISIBLE);
            holder.gridView.setAdapter(new GridViewAdapter(context,recruitInfo.getPicUrls(),true));
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
            holder.content.setOnClickListener(clickListener);
            holder.time.setOnClickListener(clickListener);
            holder.gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    //TODO 查看大图
                    Intent bigImageIntent = new Intent(context, BigImagePagerActivity.class);
                    bigImageIntent.putStringArrayListExtra("picUrls",(ArrayList<String>) recruitInfo.getPicUrls());
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
        return recruitInfoList.size();
    }

    public void addData(int i, RecruitInfo r) {
        recruitInfoList.add(i,r);
        notifyItemInserted(i);
        notifyItemRangeChanged(i,recruitInfoList.size());
    }

    class RecruitHolder extends RecyclerView.ViewHolder{
        private CircleImageView head;
        private TextView title;
        private TextView time;
        private TextView content;
        private TextView name;
        private ShowPicGridView gridView;
        public RecruitHolder(View itemView) {
            super(itemView);
            head = itemView.findViewById(R.id.civ_notice_head);
            title = itemView.findViewById(R.id.tv_notice_name);
            time = itemView.findViewById(R.id.tv_notice_time);
            content = itemView.findViewById(R.id.tv_notice_content);
            name = itemView.findViewById(R.id.tv_notice_department);
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
