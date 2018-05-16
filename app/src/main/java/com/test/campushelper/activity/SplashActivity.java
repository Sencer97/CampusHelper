package com.test.campushelper.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.test.campushelper.R;

public class SplashActivity extends AppCompatActivity {

    private ImageView logo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);      //去掉标题栏
        setContentView(R.layout.activity_splash);
        init();
    }

    private void init() {
        logo = findViewById(R.id.iv_splash_logo);
        //logo缩放动画
        Animation animation = AnimationUtils.loadAnimation(this,R.anim.dialog_enter);
        logo.setAnimation(animation);
        logo.startAnimation(animation);
        splashActivity(3);

    }
    /**
     * 延迟多少秒进入主界面
     * @param second 秒
     */
    private void splashActivity(int second) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }, 1000*second);
    }
}
