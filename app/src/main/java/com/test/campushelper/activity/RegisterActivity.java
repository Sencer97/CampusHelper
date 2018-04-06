package com.test.campushelper.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.test.campushelper.R;
import com.test.campushelper.event.FinishEvent;
import com.test.campushelper.model.Friend;
import com.test.campushelper.model.User;
import com.test.campushelper.model.UserData;
import com.test.campushelper.model.UserModel;
import com.test.campushelper.utils.Constant;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.LogInListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

public class RegisterActivity extends BaseActivity {
    private static final String TAG = "RegisterActivity";

    private Button registerBtn;
    private EditText et_user,et_password,et_confirm_pwd;
    private String userName,pwd,confirmPwd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentLayout(R.layout.activity_register);
        Bmob.initialize(this, Constant.BMOB_APPKEY);
        setTitle("注册");
        setBackArrow();
        init();
    }
    private void init() {
        registerBtn = findViewById(R.id.register);
        et_user = findViewById(R.id.et_username);
        et_password = findViewById(R.id.et_password);
        et_confirm_pwd = findViewById(R.id.et_password_confirm);
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userName = et_user.getText().toString();
                pwd = et_password.getText().toString();
                confirmPwd = et_confirm_pwd.getText().toString();
                UserModel.getInstance().register(userName, pwd, confirmPwd,
                        et_user, et_password, et_confirm_pwd,
                        new LogInListener() {
                            @Override
                            public void done(Object o, BmobException e) {
                                if (e == null) {
                                    EventBus.getDefault().post(new FinishEvent());
                                    toast("注册成功！");
                                    finish();
                                }else {
                                    Log.d(TAG, "注册失败....");
                                    toast(e.getMessage() + "(" + e.getErrorCode() + ")");
                                }
                            }
                        });

            }
        });

    }


}
