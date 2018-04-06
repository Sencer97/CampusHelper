package com.test.campushelper.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.test.campushelper.R;
import com.test.campushelper.model.Message;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 *  聊天和通知列表适配器
 */

public class MyRecyclerAdapter extends RecyclerView.Adapter <MyRecyclerAdapter.MyHolder>{

    private Context context;
    private List<Message> list;
    private OnItemClickListener mItemClickListener;
    private OnItemLongClickListener mItemLongClickListener;

    public MyRecyclerAdapter(Context context, List<Message> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_message,parent,false);
        MyHolder holder = new MyHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final MyHolder holder, final int position) {
        final Message message = list.get(position);
        holder.headView.setImageResource(message.getHeadId());
        holder.nickName.setText(message.getNickName());
        holder.text.setText(message.getContent());
        holder.time.setText(message.getTime());
        if(mItemClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mItemClickListener.onItemClick(holder.itemView,position);
//                    int pos = holder.getAdapterPosition();
//                    Message m = list.get(pos);
//                    Toast.makeText(context, "你点击了" + holder.nickName.getText().toString(), Toast.LENGTH_SHORT).show();

                    //TODO 进入聊天界面

                }
            });
        }
        if(mItemLongClickListener != null){
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    mItemLongClickListener.onItemLongClick(holder.itemView,position);
                    return  true;
                }
            });
        }

    }

    /**
     * 添加数据
     * @param position
     * @param message item
     */
    public void addData(int position,Message message){
        list.add(position,message);
        notifyItemInserted(position);
        notifyItemRangeChanged(position,list.size());
    }

    public void deleteData(int position){
        list.remove(position);
        notifyItemRemoved(position);
//        notifyDataSetChanged();
        notifyItemRangeChanged(position,list.size());
    }
    /**
     *
     * item的回调接口
     */
    public interface OnItemClickListener{
        void onItemClick(View view,int position);
    }
    //定义一个设置点击监听器的方法
    public void setOnItemClickListener(OnItemClickListener itemClickListener) {
        this.mItemClickListener = itemClickListener;
    }

    /**
     * item长按回调接口
     */
    public interface OnItemLongClickListener{
        void onItemLongClick(View view,int position);
    }
    public void setOnItemLongClickListener(OnItemLongClickListener itemLongClickListener){
        this.mItemLongClickListener = itemLongClickListener;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
    class MyHolder extends RecyclerView.ViewHolder{
        private CircleImageView headView;
        private TextView nickName;
        private TextView text;
        private TextView time;
        public MyHolder(View itemView) {
            super(itemView);
            headView = itemView.findViewById(R.id.iv_head);
            nickName = itemView.findViewById(R.id.tv_user);
            text = itemView.findViewById(R.id.tv_message);
            time = itemView.findViewById(R.id.tv_time);
        }
    }
}
