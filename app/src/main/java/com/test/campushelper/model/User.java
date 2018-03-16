package com.test.campushelper.model;

import cn.bmob.v3.BmobObject;

/**
 * 账号类
 */

public class User extends BmobObject {
    private String userName;
    private String password;
    private String id;

    public User(String id,String userName, String password) {
        this.id = id;
        this.userName = userName;
        this.password = password;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
