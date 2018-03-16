package com.test.campushelper.base;

import android.app.Application;

import com.test.campushelper.utils.MyMessageHandler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import cn.bmob.newim.BmobIM;


public class MyApplication extends Application {
        @Override
        public void onCreate() {
            super.onCreate();
            // SDK初始化 并注册消息接收器
            if (getApplicationInfo().packageName.equals(getMyProcessName())){
                BmobIM.init(this);
                BmobIM.registerDefaultMessageHandler(new MyMessageHandler());
            }

        }
    /**
     * 获取当前运行的进程名
     * @return
     */
    public static String getMyProcessName() {
        try {
            File file = new File("/proc/" + android.os.Process.myPid() + "/" + "cmdline");
            BufferedReader mBufferedReader = new BufferedReader(new FileReader(file));
            String processName = mBufferedReader.readLine().trim();
            mBufferedReader.close();
            return processName;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
