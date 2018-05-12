package com.test.campushelper.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.test.campushelper.model.Advice;
import com.test.campushelper.model.ClassHelp;
import com.test.campushelper.model.Match;
import com.test.campushelper.model.News;
import com.test.campushelper.model.Notice;
import com.test.campushelper.model.RecruitInfo;
import com.test.campushelper.model.TechProject;
import com.test.campushelper.model.UserData;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by 14685 on 2018/3/10.
 */

public class Constant {
    public static String BMOB_APPKEY = "4597e0340f34f81e828ecf9dc0851431";
    public static UserData curUser = new UserData();
    public static ClassHelp curHelp = new ClassHelp();
    public static Notice curNotice = new Notice();
    public static TechProject curProject = new TechProject();
    public static Advice curAdvice = new Advice();
    public static News curNews= new News();
    public static Match curMatch = new Match();
    public static RecruitInfo curRecruit = new RecruitInfo();
    public static String[] roles = {"student","teacher","departAdmin","schoolfellow"};
    public static Toast toast;

    public static String getCurTime() {
        //获取当前时间
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd HH:mm");// HH:mm:ss
        Date date = new Date(System.currentTimeMillis());
        String time = simpleDateFormat.format(date);
        return time;
    }

    /**
     * 显示或隐藏键盘
     *
     * @param context
     */
    public static void showOrHideSoftKeyBoard(Context context) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }

    /**
     * 保存图片到本地
     * @param context
     * @param bmp
     * @return
     */
    public static String saveImageToGallery(Context context, Bitmap bmp) {
        // 首先保存图片
        String storePath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "CampusHelper";
        File appDir = new File(storePath);
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        String fileName = System.currentTimeMillis()+ ".jpg";
        File file = new File(appDir, fileName);

        try {
            FileOutputStream fos = new FileOutputStream(file);
            //通过io流的方式来压缩保存图片
            boolean isSuccess = bmp.compress(Bitmap.CompressFormat.JPEG, 60, fos);
            fos.flush();
            fos.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return storePath+"/"+fileName;
    }

    /**
     * 分享
     * @param context
     * @param msg
     * @param title
     */
    public static void share(Context context,String title,String msg){
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT,msg);
        intent.setType("text/plain");
        context.startActivity(Intent.createChooser(intent,title));
    }

    /**
     * 底部微提醒
     * @param view
     * @param str
     */
    public static void snack(View view,String str){
        Snackbar snackbar = null;
        if(snackbar == null){
            snackbar = Snackbar.make(view,"",Snackbar.LENGTH_SHORT);
        }
        snackbar.setText(str);
        snackbar.show();
    }
    /**
     * Toast 提醒 避免多次弹出已有的toast
     */
    public static void showToast(Context context, String content) {
        if (toast == null) {
            toast = Toast.makeText(context, content, Toast.LENGTH_SHORT);
        } else {
            toast.setText(content);
        }
        toast.show();
    }
}
