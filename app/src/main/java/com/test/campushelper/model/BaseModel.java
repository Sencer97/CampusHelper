package com.test.campushelper.model;


import android.content.Context;

import com.test.campushelper.base.MyApplication;

public abstract class BaseModel {

    public int CODE_NULL=1000;

    public static int CODE_NOT_EQUAL=1001;
    public int CODE_SAME = 1111;
    public int CODE_SUCCESS = 1;
    public int CODE_ERROR = 0;
    public static final int DEFAULT_LIMIT=20;

    public Context getContext(){
        return MyApplication.INSTANCE();
    }
}
