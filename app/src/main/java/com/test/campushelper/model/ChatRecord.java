package com.test.campushelper.model;


import java.util.List;

import cn.bmob.newim.bean.BmobIMMessage;
import cn.bmob.v3.BmobObject;

public class ChatRecord extends BmobObject{
    private String id;
    private List<BmobIMMessage> chatRecord;
    private String username;
    private String friendName;

    public ChatRecord(){}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<BmobIMMessage> getChatRecord() {
        return chatRecord;
    }

    public void setChatRecord(List<BmobIMMessage> chatRecord) {
        this.chatRecord = chatRecord;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFriendName() {
        return friendName;
    }

    public void setFriendName(String friendName) {
        this.friendName = friendName;
    }
}
