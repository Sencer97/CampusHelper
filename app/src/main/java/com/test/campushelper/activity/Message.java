package com.test.campushelper.activity;


import de.hdodenhof.circleimageview.CircleImageView;

public class Message {
    private String nickName;
    private String message;
    private String time;

    private int headId;

    public Message(String nickName, String message, String time, int headId) {
        this.nickName = nickName;
        this.message = message;
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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }


}
