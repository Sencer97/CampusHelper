package com.test.campushelper.adapter;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.test.campushelper.R;
import com.test.campushelper.model.CommentItem;
import com.test.campushelper.model.Reply;
import com.test.campushelper.utils.Constant;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.Bmob;
import de.hdodenhof.circleimageview.CircleImageView;

public class CommentRecyclerAdapter extends RecyclerView.Adapter<CommentRecyclerAdapter.CommentHolder>{

    private List<CommentItem> commentList;
    private OnItemClickListener mItemClickListener;     //recyclerItem
    private AdapterView.OnItemClickListener onListItemClickListener;    //listItem
    private List<Reply> replys;
    private Context context;
    private ReplyAdapter replyAdapter;
    public CommentRecyclerAdapter(){}

    public CommentRecyclerAdapter(Context context,List<CommentItem> commentList){
        this.context = context;
        this.commentList = commentList;
        Bmob.initialize(context, Constant.BMOB_APPKEY);
    }

    public CommentRecyclerAdapter(Context context, List<CommentItem> commentList, AdapterView.OnItemClickListener listener){
        this.context = context;
        this.commentList = commentList;
        this.onListItemClickListener = listener;
        Bmob.initialize(context, Constant.BMOB_APPKEY);
    }
    @Override
    public CommentHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_comment,parent,false);
        CommentHolder holder = new CommentHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(CommentHolder holder,final int position) {
        CommentItem commentItem = commentList.get(position);
        Log.d("commentItem", "onBindViewHolder: "+commentItem.getObjectId());
        //使用Glide加载头像
        Glide.with(context)
                .load(commentItem.getCurHead())
                .placeholder(R.drawable.ic_image_loading)
                .error(R.drawable.ic_empty_picture)
                .crossFade()
                .into(holder.civ_head);
        holder.tv_commment_name.setText(commentItem.getReplyNick());
        holder.tv_comment_time.setText(commentItem.getReplyTime());
        holder.tv_comment_content.setText(commentItem.getReplyContent());
        if(commentItem.getReplyList()==null){
            replys = new ArrayList<>();
            holder.listView.setVisibility(View.GONE);
            Log.d("replys", "onBindViewHolder: replysList == null");
        }else {
            replys = commentItem.getReplyList();
            holder.listView.setVisibility(View.VISIBLE);
        }
        replyAdapter = new ReplyAdapter(context,replys);
        holder.listView.setAdapter(replyAdapter);

        //listview设置item回调
        holder.listView.setOnItemClickListener(onListItemClickListener);
        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItemClickListener.onItemClick(v,position);
            }
        };
        if (mItemClickListener!=null){
            holder.itemView.setOnClickListener(clickListener);
        }

    }

    public void setOnListItemClick(CommentHolder holder, int recyclerItemPos, AdapterView.OnItemClickListener listener) {
        this.onListItemClickListener = listener;
        holder.listView.setOnItemClickListener(listener);
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }
    class CommentHolder extends RecyclerView.ViewHolder{
        private CircleImageView civ_head;
        private TextView tv_commment_name,tv_comment_time,tv_comment_content;
        private ListView listView;
        public CommentHolder(View itemView) {
            super(itemView);
            civ_head = itemView.findViewById(R.id.civ_comment_head);
            tv_commment_name = itemView.findViewById(R.id.tv_comment_nickname);
            tv_comment_time = itemView.findViewById(R.id.tv_comment_time);
            tv_comment_content = itemView.findViewById(R.id.tv_comment_content);
            listView = itemView.findViewById(R.id.lv_replys);
        }
    }

    /**
     * 添加评论
     * @param comment
     */
    public void addComment(CommentItem comment){
        commentList.add(comment);
        notifyDataSetChanged();
    }

    /**
     * 评论下添加回复
     */
    public void addReply(int position,Reply reply){
        CommentItem commentItem = commentList.get(position);
//        if (commentItem.getReplyList()==null){
//            replys = new ArrayList<>();
//        }else{
            replys = commentItem.getReplyList();
//        }
        replys.add(reply);
        replyAdapter.notifyDataSetChanged();
        notifyDataSetChanged();
        Log.d("replys", "addReply: 添加回复");
        for (Reply r:replys) {
            Log.d("replys", "addReply: "+r);
        }
    }





    //点击item回调接口
    /**
     *  recycler
     * item的回调接口
     */
    public interface OnItemClickListener{
        void onItemClick(View view, int position);
    }
    //定义一个设置点击监听器的方法
    public void setOnItemClickListener(CommentRecyclerAdapter.OnItemClickListener itemClickListener) {
        this.mItemClickListener = itemClickListener;
    }

//    public interface OnListItemClickListener{
//        void onListItemClick(View view, int position);
//    }
//    /**
//     * listItem
//     * @param itemClickListener
//     */
//    //定义一个设置点击监听器的方法
//    public void setOnListItemClickListener(OnListItemClickListener itemClickListener) {
//        this.onListItemClickListener = itemClickListener;
//    }

}
