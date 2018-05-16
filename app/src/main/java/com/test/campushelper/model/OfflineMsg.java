package com.test.campushelper.model;

import cn.bmob.newim.bean.BmobIMExtraMessage;


/**
 * 自定义消息
 */
public class OfflineMsg extends BmobIMExtraMessage {

    private String type;
    public OfflineMsg() {
    }

    @Override
    public String getMsgType() {
        return "0";
    }

    @Override
    public boolean isTransient() {
        return true;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
