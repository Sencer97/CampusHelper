package com.test.campushelper.utils;

import com.test.campushelper.model.UserData;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by 14685 on 2018/3/10.
 */

public class Constant {
    public static String BMOB_APPKEY = "4597e0340f34f81e828ecf9dc0851431";
    public static UserData curUser = new UserData();

    public static String getCurTime(){
        //获取当前时间
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");// HH:mm:ss
        Date date = new Date(System.currentTimeMillis());
        String time = simpleDateFormat.format(date);
        return time;
    }
}
