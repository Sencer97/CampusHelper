package com.test.campushelper.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.library.bubbleview.BubbleTextView;
import com.test.campushelper.R;
import com.test.campushelper.model.Message;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import cn.bmob.newim.bean.BmobIMMessage;
import cn.bmob.v3.BmobUser;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 *  聊天界面的适配器
 */

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ChatViewHolder> {
    private List<BmobIMMessage> msgList;
    private OnItemClickListener mItemClickListener;
    //接收的发送类型
    private final int TYPE_RECEIVE_TXT = 0;
    private final int TYPE_SEND_TXT = 1;
    private String curUID = "";

    public MessageAdapter(List<BmobIMMessage> list){
        this.msgList = list;
        curUID = BmobUser.getCurrentUser().getObjectId();
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
    @Override
    public ChatViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
         View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_msg,parent,false);
         return  new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ChatViewHolder holder, final int position) {
        //获取当前时间
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");// HH:mm:ss
        BmobIMMessage msg = msgList.get(position);
        if(!msg.getFromId().equals(curUID)){
            //收到消息，显示左边布局 右边隐藏
            holder.leftLayout.setVisibility(View.VISIBLE);
            holder.rightLayout.setVisibility(View.GONE);
            holder.chatTime.setText(simpleDateFormat.format(msg.getCreateTime()));
            holder.leftChatMsg.setText(msg.getContent());
        } else {
            //发出消息，显示右边布局，左边隐藏
            holder.leftLayout.setVisibility(View.GONE);
            holder.rightLayout.setVisibility(View.VISIBLE);
            holder.chatTime.setText(simpleDateFormat.format(msg.getCreateTime()));
            holder.rightChatMsg.setText(msg.getContent());
        }
        if(mItemClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mItemClickListener.onItemClick(holder.itemView,position);
                }
            });
        }

    }
    public int findPosition(BmobIMMessage message) {
        int index = this.getItemCount();
        int position = -1;
        while(index-- > 0) {
            if(message.equals(this.getItem(index))) {
                position = index;
                break;
            }
        }
        return position;
    }
    public void addMessage(BmobIMMessage message) {
        msgList.addAll(Arrays.asList(message));
        notifyDataSetChanged();
    }
    /**获取消息
     * @param position
     * @return
     */
    public BmobIMMessage getItem(int position){
        return this.msgList == null?null:(position >= this.msgList.size()?null:this.msgList.get(position));
    }
    @Override
    public int getItemCount() {
        return msgList.size();
    }


    public class  ChatViewHolder extends RecyclerView.ViewHolder {
        private TextView chatTime;
        private RelativeLayout leftLayout;
        private RelativeLayout rightLayout;
        private CircleImageView leftChatHeader;
        private CircleImageView rightChatHeader;
        private BubbleTextView leftChatMsg;
        private BubbleTextView rightChatMsg;
        public  ChatViewHolder(View itemView) {
            super(itemView);
            chatTime = itemView.findViewById(R.id.tv_chat_time);
            leftLayout = itemView.findViewById(R.id.ll_msg_left);
            rightLayout = itemView.findViewById(R.id.ll_msg_right);
            leftChatHeader = itemView.findViewById(R.id.civ_chat_left);
            rightChatHeader = itemView.findViewById(R.id.civ_chat_right);
            leftChatMsg = itemView.findViewById(R.id.tv_msg_left);
            rightChatMsg = itemView.findViewById(R.id.tv_msg_right);
        }
    }

}
