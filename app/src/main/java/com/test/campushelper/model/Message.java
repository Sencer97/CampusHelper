package com.test.campushelper.model;


import java.util.Timer;

import cn.bmob.newim.bean.BmobIMMessage;
import cn.bmob.newim.bean.BmobIMTextMessage;

public class Message extends BmobIMMessage{
    public static final int TYPE_RECEIVED = 0;  //接收到的消息  布局在左边
    public static final int TYPE_SEND = 1;      //发出去的消息  右边
    private String nickName;
    private String content;
    private String time;
    private int headId;
    private int type;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
    public Message(){

    }
    public Message(String msg,String time,int type){
        this.content = msg;
        this.time = time;
        this.type = type;
    }
    public Message(String nickName, String message, String time, int headId ) {
        this.nickName = nickName;
        this.content = message;
        this.time = time;
        this.headId = headId;
    }

    public int getHeadId() {
        return headId;
    }

    public void setHeadId(int headId) {
        this.headId = headId;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }


}
