package com.test.campushelper.adapter;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.test.campushelper.R;
import com.test.campushelper.activity.BigImagePagerActivity;
import com.test.campushelper.model.ClassHelp;
import com.test.campushelper.view.ShowPicGridView;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * 同学帮适配器
 */
public class ClassHelpAdapter extends RecyclerView.Adapter<ClassHelpAdapter.HelpHolder>{
    private List<ClassHelp> helpList;
    private Context context;
    private ClassHelpAdapter.OnItemClickListener mItemClickListener;

    public ClassHelpAdapter(Context context,List<ClassHelp> list){
        this.context = context;
        this.helpList = list;
    }

    @Override
    public HelpHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_classhelp,parent,false);
        HelpHolder holder = new HelpHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final HelpHolder holder, final int position) {
        final ClassHelp classHelp = helpList.get(position);
        //使用Glide加载头像
        Glide.with(context)
                .load(classHelp.getHeadUrl())
                .placeholder(R.drawable.ic_image_loading)
                .error(R.drawable.ic_empty_picture)
                .crossFade()
                .into(holder.head);
        holder.nickname.setText(classHelp.getNickname());
        holder.time.setText(classHelp.getTime());
        holder.depart.setText(classHelp.getDepart());
        holder.content.setText(classHelp.getContent());
        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItemClickListener.onItemClick(v,position);
            }
        };
        if (mItemClickListener != null){
            holder.ll_favor.setOnClickListener(clickListener);
            holder.ll_comment.setOnClickListener(clickListener);
            holder.ll_transmit.setOnClickListener(clickListener);

            holder.head.setOnClickListener(clickListener);
            holder.nickname.setOnClickListener(clickListener);
            holder.depart.setOnClickListener(clickListener);

            holder.content.setOnClickListener(clickListener);
            holder.time.setOnClickListener(clickListener);
            holder.ignore.setOnClickListener(clickListener);
//            holder.gridView.setOnClickListener(clickListener);
            holder.gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    //TODO 查看大图
                    Intent bigImageIntent = new Intent(context, BigImagePagerActivity.class);
                    bigImageIntent.putStringArrayListExtra("picUrls",(ArrayList<String>) classHelp.getPicUrls());
                    bigImageIntent.putExtra("position",position);
                    Activity activity = (Activity) context;
                    activity.startActivity(bigImageIntent);
                    activity.overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
                }
            });
        }
        //图片列表
        if (classHelp.isHasPic()){
            holder.gridView.setVisibility(View.VISIBLE);
            holder.gridView.setAdapter(new GridViewAdapter(context,classHelp.getPicUrls(),true));
        }else{
            holder.gridView.setVisibility(View.GONE);
        }

        //点赞列表
        List<String> favor = classHelp.getFavorList();
        String str = "";
        if (favor.size()>0){
            for (String s: favor) {
                str+=s+"、";
            }
            str = str.substring(0,str.length()-1);
            str +=" 觉得很赞";
            holder.favorlist.setText(str);
            holder.ll_favorList.setVisibility(View.VISIBLE);
        }else{
            holder.ll_favorList.setVisibility(View.GONE);
        }

        //显示没有更多了
        if(position == helpList.size()-1){
            holder.nomore.setVisibility(View.VISIBLE);
        }else{
            holder.nomore.setVisibility(View.GONE);
        }
    }


    @Override
    public int getItemCount() {
        return helpList.size();
    }

    class HelpHolder extends RecyclerView.ViewHolder{
        private CircleImageView head;
        private TextView nickname;
        private TextView time;
        private TextView depart;
        private TextView content;
        private ShowPicGridView gridView;
        private TextView favorlist;
        private ImageView ignore;                               //不看该动态
        private LinearLayout ll_favor,ll_comment,ll_transmit;   //点赞、评论、转发
        private LinearLayout ll_favorList;
        private TextView nomore;

        public HelpHolder(View itemView) {
            super(itemView);
            head = itemView.findViewById(R.id.civ_help_head);
            nickname = itemView.findViewById(R.id.tv_help_nickname);
            time = itemView.findViewById(R.id.tv_help_time);
            depart = itemView.findViewById(R.id.tv_help_department);
            content = itemView.findViewById(R.id.tv_help_content);
            gridView = itemView.findViewById(R.id.gv_help_pic);
            favorlist = itemView.findViewById(R.id.tv_favorlist);
            ignore = itemView.findViewById(R.id.iv_ignore);
            ll_comment = itemView.findViewById(R.id.ll_help_comment);
            ll_favor = itemView.findViewById(R.id.ll_help_favor);
            ll_transmit = itemView.findViewById(R.id.ll_help_share);
            nomore = itemView.findViewById(R.id.tv_nomore_help);
            ll_favorList = itemView.findViewById(R.id.ll_favorlist);
        }
    }
    /**
     * 添加数据
     * @param position
     * @param help item
     */
    public void addData(int position,ClassHelp help){
        helpList.add(position,help);
        notifyItemInserted(position);
        notifyItemRangeChanged(position,helpList.size());
    }

    public void clearData(){
        helpList.clear();
        notifyDataSetChanged();
    }

    public void deleteData(int position){
        helpList.remove(position);
        notifyItemRemoved(position);
//        notifyDataSetChanged();
        notifyItemRangeChanged(position,helpList.size());
    }


    /**
     *
     * item的回调接口
     */
    public interface OnItemClickListener{
        void onItemClick(View view, int position);
    }
    //定义一个设置点击监听器的方法
    public void setOnItemClickListener(ClassHelpAdapter.OnItemClickListener itemClickListener) {
        this.mItemClickListener = itemClickListener;
    }

}
