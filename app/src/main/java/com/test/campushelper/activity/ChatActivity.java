package com.test.campushelper.activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.github.library.bubbleview.BubbleTextView;
import com.test.campushelper.R;
import com.test.campushelper.adapter.MessageAdapter;
import com.test.campushelper.model.Message;
import com.test.campushelper.model.UserData;
import com.test.campushelper.utils.Constant;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.bean.BmobIMMessage;
import cn.bmob.newim.bean.BmobIMTextMessage;
import cn.bmob.newim.bean.BmobIMUserInfo;
import cn.bmob.newim.core.BmobIMClient;
import cn.bmob.newim.core.ConnectionStatus;
import cn.bmob.newim.event.MessageEvent;
import cn.bmob.newim.listener.ConnectListener;
import cn.bmob.newim.listener.ConversationListener;
import cn.bmob.newim.listener.MessageListHandler;
import cn.bmob.newim.listener.MessageSendListener;
import cn.bmob.newim.notification.BmobNotificationManager;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class ChatActivity extends BaseActivity implements MessageListHandler,View.OnClickListener{
    private static final String TAG = "ChatActivity";
    public static List<BmobIMMessage> msgList;
    public static MessageAdapter messageAdapter;
    public static RecyclerView chatRecyclerView;
    private EditText inputText;
    private Button sendBtn;
    private BubbleTextView msgLeftText;
    private BubbleTextView msgRightText;
    private String friendName = "";
    private boolean isConnect = false;
    private boolean isOpenConversation = false;
    private String msgContent = "",msgTime = "";
    private String uid = "";
    public static final int NEW_MESSAGE = 0x001;// 收到消息

    private BmobIMConversation mConversationManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentLayout(R.layout.activity_chat);
        Bmob.initialize(this, Constant.BMOB_APPKEY);
        friendName = getBundle().getString("title");
        setTitle(friendName);
        setBackArrow();
        BmobIMConversation conversationEntrance = (BmobIMConversation) getBundle().getSerializable("c");
        //TODO  根据会话入口获取消息管理，聊天页面
        mConversationManager = BmobIMConversation.obtain(BmobIMClient.getInstance(), conversationEntrance);

        init();
    }


    @Override
    protected void onResume() {
        //添加页面消息监听器
        BmobIM.getInstance().addMessageListHandler(this);
        // 有可能锁屏期间，在聊天界面出现通知栏，这时候需要清除通知
        BmobNotificationManager.getInstance(this).cancelNotification();
        super.onResume();
    }
    @Override
    protected void onPause() {
        //移除页面消息监听器
        BmobIM.getInstance().removeMessageListHandler(this);
        super.onPause();
    }
    /**
     *  消息发送监听器
     */
    public MessageSendListener messageSendListener = new MessageSendListener() {
        @Override
        public void onProgress(int value) {
            super.onProgress(value);
            //文件类型的消息才有进度值
        }
        @Override
        public void onStart(BmobIMMessage msg) {
            super.onStart(msg);
            messageAdapter.addMessage(msg);
        }
        @Override
        public void done(BmobIMMessage msg, BmobException e) {
//            messageAdapter.notifyItemInserted(msgList.size() - 1);
            messageAdapter.notifyDataSetChanged();
            chatRecyclerView.scrollToPosition(msgList.size() - 1);
            inputText.setText("");       //清空输入框
            if (e != null) {
                toast(e.getMessage());
            }
        }
    };

    private void sendMsg() {
        msgContent = inputText.getText().toString();
        if (TextUtils.isEmpty(msgContent.trim())){
            toast("消息不能为空，请输入！");
            return;
        }
        Log.d(TAG, "消息---"+msgContent);
        //获取当前时间
//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");// HH:mm:ss
//        Date date = new Date(System.currentTimeMillis());
//        msgTime = simpleDateFormat.format(date);
        BmobIMTextMessage msg = new BmobIMTextMessage();
        msg.setContent(msgContent);
        mConversationManager.sendMessage(msg, messageSendListener);
    }

    private void refreshMessage(BmobIMMessage msg) {
       // MyMessageHandler handler = new MyMessageHandler();
    }

    View.OnLongClickListener longClickListener = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            //长按弹出复制删除菜单
            return true;
        }
    };
    private void init() {

        inputText = findViewById(R.id.et_input);
        sendBtn = findViewById(R.id.btn_chat_send);
        msgList = new ArrayList<>();
//        msgList.add(new Message( "Hello","2018-3-18 12:12", 0));
//        msgList.add(new Message( "Hey,man","2018-3-18 12:15", 1));
//        msgList.add(new Message( "Are you ok?","2018-3-18 12:20", 0));
//        msgList.add(new Message( "Okey!","2018-3-18 13:12", 1));
        chatRecyclerView = findViewById(R.id.rv_message);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        chatRecyclerView.setLayoutManager(layoutManager);
        messageAdapter = new MessageAdapter(msgList);
        chatRecyclerView.setAdapter(messageAdapter);

        //实现复制删除
        LinearLayout layout = (LinearLayout) getLayoutInflater().inflate(R.layout.item_chat_msg,null);
        msgLeftText = layout.findViewById(R.id.tv_msg_left);
        msgRightText = layout.findViewById(R.id.tv_msg_right);
        msgLeftText.setOnLongClickListener(longClickListener);
        msgRightText.setOnLongClickListener(longClickListener);
        sendBtn.setOnClickListener(this);
    }

    //发送文本
    @Override
    public void onClick(View v) {

        if (BmobIM.getInstance().getCurrentStatus().getCode() != ConnectionStatus.CONNECTED.getCode()) {
            toast("尚未连接IM服务器");
            return;
        }
        sendMsg();
    }

    @Override
    public void onMessageReceive(List<MessageEvent> list) {
        Log.d(TAG, "onMessageReceive: 当前会话收到消息--" +list.size());
        for (int i = 0; i < list.size(); i++) {
            addMessage2Chat(list.get(i));
        }
    }
    /**
     * 添加消息到聊天界面
     * @param messageEvent
     */
    private void addMessage2Chat(MessageEvent messageEvent) {
        BmobIMMessage msg = messageEvent.getMessage();
        if(mConversationManager != null && messageEvent != null &&
                mConversationManager.getConversationId().equals(messageEvent.getConversation().getConversationId())
                && !msg.isTransient()){
            //且不为暂态消息
            //未添加 更新
            if(messageAdapter.findPosition(msg) < 0){
                messageAdapter.addMessage(msg);
                mConversationManager.updateReceiveStatus(msg);
            }

        }
    }
}
