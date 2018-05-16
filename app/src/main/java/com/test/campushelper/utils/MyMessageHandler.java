package com.test.campushelper.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.test.campushelper.R;
import com.test.campushelper.activity.ChatActivity;
import com.test.campushelper.activity.MainActivity;
import com.test.campushelper.listener.UpdateCacheListener;
import com.test.campushelper.model.ChatRecord;
import com.test.campushelper.model.UserModel;

import org.greenrobot.eventbus.EventBus;

import java.util.List;
import java.util.Map;

import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMMessage;
import cn.bmob.newim.bean.BmobIMMessageType;
import cn.bmob.newim.bean.BmobIMUserInfo;
import cn.bmob.newim.event.MessageEvent;
import cn.bmob.newim.event.OfflineMessageEvent;
import cn.bmob.newim.listener.BmobIMMessageHandler;
import cn.bmob.newim.notification.BmobNotificationManager;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;


public class MyMessageHandler extends BmobIMMessageHandler {
    private static final String TAG = "MyMessageHandler";

    private Context context;

    public MyMessageHandler(Context context) {
        this.context = context;
    }

    @Override
    public void onMessageReceive(final MessageEvent event) {
        //在线消息

        executeMessage(event);
    }
    /**
     * 处理消息
     *
     * @param event
     */
    private void executeMessage(final MessageEvent event) {
        //检测用户信息是否需要更新
        UserModel.getInstance().updateUserInfo(event, new UpdateCacheListener() {
            @Override
            public void done(BmobException e) {
                BmobIMMessage msg = event.getMessage();
                if (BmobIMMessageType.getMessageTypeValue(msg.getMsgType()) == 0) {
                    //自定义消息类型：0
                    Intent pendingIntent = new Intent(context, MainActivity.class);
                    pendingIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    BmobIMUserInfo info = event.getFromUserInfo();
                    //解析extraMap
//                    Gson gson = new Gson();
//                    Map<String,Object> map = gson.fromJson(msg.getExtra(),Map.class);
//                    Log.d(TAG, "type: "+map.get("type"));
                    //这里可以是应用图标，也可以将聊天头像转成bitmap
                    Bitmap largeIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.logo);
                    BmobNotificationManager.getInstance(context).showNotification(largeIcon,
                            info.getName()+"评论了你的动态", msg.getContent(), "您的动态新评论！", pendingIntent);
                } else {
                    //SDK内部内部支持的消息类型
                    processSDKMessage(msg, event);
                }
            }
        });
    }
    /**
     * 处理SDK支持的消息
     *
     * @param msg
     * @param event
     */
    private void processSDKMessage(BmobIMMessage msg, MessageEvent event) {
        Log.d(TAG, "processSDKMessage: 在线消息"+msg.getContent());
        BmobIMUserInfo info = event.getFromUserInfo();
        if (BmobNotificationManager.getInstance(context).isShowNotification()) {
            //如果需要显示通知栏，SDK提供以下两种显示方式：
            Intent pendingIntent = new Intent(context, ChatActivity.class);
            pendingIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

            //TODO 消息接收：8.5、多个用户的多条消息合并成一条通知：有XX个联系人发来了XX条消息
//            BmobNotificationManager.getInstance(context).showNotification(event, pendingIntent);

            //TODO 消息接收：自定义通知消息：始终只有一条通知，新消息覆盖旧消息
            //点击后进入聊天界面
            pendingIntent.putExtra("title",info.getName());
            pendingIntent.putExtra("c", BmobIM.getInstance().startPrivateConversation(info, null));

            //这里可以是应用图标，也可以将聊天头像转成bitmap
            Bitmap largeIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.logo);
            BmobNotificationManager.getInstance(context).showNotification(largeIcon,
                    info.getName(), msg.getContent(), "您有一条新消息", pendingIntent);
            //接收离线消息 写入数据库
            addMessageToDB(info.getName(),msg);
        } else {
            //直接发送消息事件
            addMessageToDB(info.getName(),msg);
            EventBus.getDefault().post(event);
        }
    }

    public void addMessageToDB(String friendName, final BmobIMMessage msg){
        BmobQuery<ChatRecord> query = new BmobQuery<>();
        query.addWhereEqualTo("username",Constant.curUser.getUserName());
        query.addWhereEqualTo("friendName",friendName);
        query.findObjects(new FindListener<ChatRecord>() {
            @Override
            public void done(List<ChatRecord> list, BmobException e) {
                if (e == null) {
                    if (list.size() > 0) {
                        ChatRecord record = list.get(0);
                        List<BmobIMMessage> msgs = record.getChatRecord();
                        msgs.add(msg);
                        record.setChatRecord(msgs);
                        record.update(record.getId(), new UpdateListener() {
                            @Override
                            public void done(BmobException e) {
                                if(e == null){
                                    Log.d("ChatAc", "写入离线消息成功");
                                }else {
                                    Log.d("ChatAc", "更新离线消息失败---"+e.getMessage());
                                }
                            }
                        });
                    }
                }else {
                    Log.d("ChatAc", "写入离线消息失败---"+e.getMessage());
                }
            }
        });

    }

    @Override
        public void onOfflineReceive(final OfflineMessageEvent event) {
            //离线消息，每次connect的时候会查询离线消息，如果有，此方法会被调用
            Map<String, List<MessageEvent>> map = event.getEventMap();
            Log.d(TAG, "onOfflineReceive: "+"有" + map.size() + "个用户发来离线消息");
            //挨个检测下离线消息所属的用户的信息是否需要更新
            for (Map.Entry<String, List<MessageEvent>> entry : map.entrySet()) {
                List<MessageEvent> list = entry.getValue();
                int size = list.size();
                Log.d(TAG, "onOfflineReceive: "+"用户" + entry.getKey() + "发来" + size + "条消息");
                for (int i = 0; i < size; i++) {
                    //处理每条消息
                    executeMessage(list.get(i));
                }
            }
    }
}
