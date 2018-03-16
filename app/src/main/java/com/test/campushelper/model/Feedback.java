package com.test.campushelper.model;

import cn.bmob.v3.BmobObject;

/**
 * 意见反馈类
 */

public class Feedback extends BmobObject {
    private String text;
    private String email;
    public Feedback (){

    }
    public Feedback (String text,String email){
        this.text = text;
        this.email = email;
    }
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
