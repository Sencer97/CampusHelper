package com.test.campushelper.adapter;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.test.campushelper.R;
import com.test.campushelper.model.Advice;
import com.test.campushelper.model.Match;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdviceAdapter extends RecyclerView.Adapter<AdviceAdapter.AdviceHolder>{
    private List<Advice> adviceList;
    private List<Match> matchList;
    private Context context;
    private OnItemClickListener mItemClickListener;
    private boolean isMatch;

    public AdviceAdapter(Context context, List<Advice> adviceList) {
        this.adviceList = adviceList;
        this.context = context;
    }

    public AdviceAdapter(Context context, List<Match> matchList,boolean isMatch){
        this.matchList = matchList;
        this.context = context;
        this.isMatch = isMatch;
    }

    @Override
    public AdviceHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_advice,parent,false);
        AdviceHolder holder = new AdviceHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(AdviceHolder holder, final int position) {
        if(isMatch){
            Match match = matchList.get(position);
            holder.tv_title.setText(match.getTitle());
            holder.tv_time.setText(match.getTime());
            holder.tv_content.setText(match.getContent());
            Glide.with(context)
                    .load(match.getHeadUrl())
                    .placeholder(R.drawable.ic_image_loading)
                    .error(R.drawable.ic_empty_picture)
                    .crossFade()
                    .into(holder.head);

        }else{
            Advice advice = adviceList.get(position);
            holder.tv_title.setText(advice.getTitle());
            holder.tv_time.setText(advice.getTime());
            holder.tv_content.setText(advice.getContent());
        }
        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItemClickListener.onItemClick(v,position);
            }
        };
        if (mItemClickListener != null){
            holder.itemView.setOnClickListener(clickListener);
        }

    }


    @Override
    public int getItemCount() {
        if(isMatch){
            return matchList.size();
        }else{
            return adviceList.size();
        }
    }

    public void addData(int i, Advice advice) {
        adviceList.add(i,advice);
        notifyItemInserted(i);
        notifyItemRangeChanged(i,adviceList.size());
    }

    class AdviceHolder extends RecyclerView.ViewHolder{
        private TextView tv_title,tv_time,tv_content;
        private CircleImageView head;
        public AdviceHolder(View itemView) {
            super(itemView);
            tv_title = itemView.findViewById(R.id.tv_advice_title);
            tv_time = itemView.findViewById(R.id.tv_advice_time);
            tv_content = itemView.findViewById(R.id.tv_advice_content);
            head = itemView.findViewById(R.id.civ_advice_head);
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
    public void setOnItemClickListener(AdviceAdapter.OnItemClickListener itemClickListener) {
        this.mItemClickListener = itemClickListener;
    }
}
