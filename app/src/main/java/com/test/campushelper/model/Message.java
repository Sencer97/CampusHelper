package com.test.campushelper.model;


import cn.bmob.v3.BmobObject;

public class Message extends BmobObject{

    private String id;
    private String fromName;    //消息发送方
    private String toName;      //消息接收方
    private String content;     //内容
    private String time;        //时间

    public Message(){}

    public String getFromName() {
        return fromName;
    }

    public void setFromName(String fromName) {
        this.fromName = fromName;
    }

    public String getToName() {
        return toName;
    }

    public void setToName(String toName) {
        this.toName = toName;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
