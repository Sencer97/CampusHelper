package com.test.campushelper.base;

import android.app.Application;

import com.test.campushelper.utils.MyMessageHandler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import cn.bmob.newim.BmobIM;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobConfig;


public class MyApplication extends Application {

    private static MyApplication INSTANCE;

    public static MyApplication INSTANCE() {
        return INSTANCE;
    }

    private void setInstance(MyApplication app) {
        setBmobIMApplication(app);
    }

    private static void setBmobIMApplication(MyApplication a) {
        MyApplication.INSTANCE = a;
    }

        @Override
        public void onCreate() {
            super.onCreate();

            setInstance(this);
            // SDK初始化 并注册消息接收器
            if (getApplicationInfo().packageName.equals(getMyProcessName())){
                BmobIM.init(this);
                BmobIM.registerDefaultMessageHandler(new MyMessageHandler(this));
            }
            BmobConfig config =new BmobConfig.Builder(getBaseContext())
                    //请求超时时间（单位为秒）：默认15s
                    .setApplicationId("com.test.campushelper")
                    .setConnectTimeout(30)
                    //文件分片上传时每片的大小（单位字节），默认512*1024
                    .setUploadBlockSize(500*1024)
                    .build();
            Bmob.initialize(config);

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
