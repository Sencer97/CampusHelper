package com.test.campushelper.listener;

import cn.bmob.newim.listener.BmobListener1;
import cn.bmob.v3.exception.BmobException;


public abstract class UpdateCacheListener extends BmobListener1 {
    public abstract void done(BmobException e);

    @Override
    protected void postDone(Object o, BmobException e) {
        done(e);
    }
}
