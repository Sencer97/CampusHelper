package com.test.campushelper.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.test.campushelper.R;
import com.test.campushelper.adapter.MessageAdapter;
import com.test.campushelper.model.ChatRecord;
import com.test.campushelper.model.UserData;
import com.test.campushelper.utils.Constant;
import com.test.campushelper.utils.WrapContentLinearLayoutManager;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.bean.BmobIMMessage;
import cn.bmob.newim.bean.BmobIMTextMessage;
import cn.bmob.newim.core.BmobIMClient;
import cn.bmob.newim.core.ConnectionStatus;
import cn.bmob.newim.event.MessageEvent;
import cn.bmob.newim.listener.MessageListHandler;
import cn.bmob.newim.listener.MessageSendListener;
import cn.bmob.newim.notification.BmobNotificationManager;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

public class ChatActivity extends BaseActivity implements MessageListHandler,View.OnClickListener{
    private static final String TAG = "ChatActivity";
    public static List<BmobIMMessage> msgList = new ArrayList<>();
    public static MessageAdapter messageAdapter;
    public static RecyclerView chatRecyclerView;
    private EditText inputText;
    private Button sendBtn;
    private String friendHead = "";         //好友头像url
    private String friendName = "";         //好友名字--标题
    private String msgContent = "";         //消息内容

    private BmobIMConversation mConversationManager;

    private ChatRecord chatRecord;       //用户的聊天记录

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentLayout(R.layout.activity_chat);
        Bmob.initialize(this, Constant.BMOB_APPKEY);
        friendName = getIntent().getStringExtra("title");
        setTitle(friendName);
        setBackArrow();
        //TODO  根据会话入口获取消息管理，聊天页面
        BmobIMConversation conversationEntrance = (BmobIMConversation) getIntent().getSerializableExtra("c");
        mConversationManager = BmobIMConversation.obtain(BmobIMClient.getInstance(), conversationEntrance);
        init();
    }

    @Override
    protected void onResume() {
        //添加页面消息监听器
        BmobIM.getInstance().addMessageListHandler(this);
        BmobNotificationManager.getInstance(this).cancelNotification();
        loadRecord();
        super.onResume();
    }

    /**
     * 载入聊天记录
     */
    private void loadRecord() {
        BmobQuery<ChatRecord> query = new BmobQuery<>();
        query.addWhereEqualTo("username",Constant.curUser.getUserName());
        query.addWhereEqualTo("friendName",friendName);
        query.findObjects(new FindListener<ChatRecord>() {
            @Override
            public void done(List<ChatRecord> list, BmobException e) {
                if(e==null){
                    if(list.size() > 0){
                        chatRecord = list.get(0);
                        msgList.clear();
                        for (BmobIMMessage msg: chatRecord.getChatRecord()) {
                            messageAdapter.addMessage(msg);
                        }
                        chatRecyclerView.scrollToPosition(msgList.size()-1);
                    }else{
                        chatRecord = new ChatRecord();
                        chatRecord.setUsername(Constant.curUser.getUserName());
                        chatRecord.setFriendName(friendName);
                        chatRecord.setChatRecord(new ArrayList<BmobIMMessage>());
                        chatRecord.save(new SaveListener<String>() {
                            @Override
                            public void done(String s, BmobException e) {
                                if(e==null){
                                    chatRecord.setId(chatRecord.getObjectId());
                                    chatRecord.update(chatRecord.getObjectId(),new UpdateListener() {
                                        @Override
                                        public void done(BmobException e) {
                                            Log.d(TAG, "done: "+"新增聊天");
                                        }
                                    });
                                }
                            }
                        });
                    }
                }
            }
        });
    }

    public void updateRecord(){
        chatRecord.setChatRecord(msgList);
        chatRecord.update(chatRecord.getId(), new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if(e==null){
                    Log.d(TAG, "done: 消息数"+msgList.size());
                }
            }
        });
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
        }
        @Override
        public void done(BmobIMMessage msg, BmobException e) {
            messageAdapter.addMessage(msg);
            chatRecyclerView.scrollToPosition(msgList.size() - 1);
            Log.d(TAG, "--------发消息---------");
            Log.d(TAG, "消息内容---"+msg.getContent());
            Log.d(TAG, "fromUID: "+msg.getFromId());
            Log.d(TAG, "toUID: "+msg.getToId());
            Log.d(TAG, "用户头像: "+msg.getBmobIMUserInfo().getAvatar());
            Log.d(TAG, "--------发消息---------");
            inputText.setText("");       //清空输入框

            updateRecord();

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
        BmobIMTextMessage msg = new BmobIMTextMessage();
        msg.setContent(msgContent);
        mConversationManager.sendMessage(msg, messageSendListener);
    }


    private void init() {

        BmobQuery<UserData> friendData = new BmobQuery<>();
        friendData.addWhereEqualTo("userName",friendName);
        friendData.findObjects(new FindListener<UserData>() {
            @Override
            public void done(List<UserData> list, BmobException e) {
                if(e==null){
                    if(list.size() > 0){
                        friendHead = list.get(0).getHeadUrl();
                        chatRecyclerView = findViewById(R.id.rv_message);
                        chatRecyclerView.setLayoutManager(new WrapContentLinearLayoutManager(getBaseContext(), LinearLayoutManager.VERTICAL, false));
                        messageAdapter = new MessageAdapter(getBaseContext(),msgList,friendHead);
                        chatRecyclerView.setAdapter(messageAdapter);
                        messageAdapter.setOnItemClickListener(new MessageAdapter.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, final int position) {
                                        //长按弹出复制删除菜单
                                        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(ChatActivity.this);
                                        String[] strings = new String[]{"复制消息","删除"};
                                        builder.setItems(strings, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                switch (which){
                                                    case 0:
                                                        //获取剪贴板管理器
                                                        ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                                                        // 创建普通字符型ClipData
                                                        ClipData mClipData = ClipData.newPlainText("Label",msgList.get(position).getContent());
                                                        // 将ClipData内容放到系统剪贴板里。
                                                        cm.setPrimaryClip(mClipData);
                                                        toast("复制成功");
                                                        break;
                                                    case 1:
                                                        //删除消息
                                                        messageAdapter.deleteMessage(position);
                                                        updateRecord();
                                                        break;
                                                }
                                            }
                                        });
                                        builder.show();
                            }
                        });
                    }
                }else{
                    Log.d("ChatAc", "加载头像error------"+e.getMessage());
                }
            }
        });

        inputText = findViewById(R.id.et_input);
        sendBtn = findViewById(R.id.btn_chat_send);
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
//            Log.d(TAG, "onMessageReceive: "+list.get(i).getMessage().getContent());

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
                chatRecyclerView.scrollToPosition(msgList.size()-1);
                updateRecord();
            }
            Log.d(TAG, "=========收到消息=======");
            Log.d(TAG, "消息内容---"+msg.getContent());
            Log.d(TAG, "fromUID: "+msg.getFromId());
            Log.d(TAG, "toUID: "+msg.getToId());
            Log.d(TAG, "用户名: "+msg.getBmobIMUserInfo().getName());
            Log.d(TAG, "用户头像: "+msg.getBmobIMUserInfo().getAvatar());
            Log.d(TAG, "=========收到消息=======");
        }
    }
}
