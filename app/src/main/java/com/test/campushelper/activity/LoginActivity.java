package com.test.campushelper.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.test.campushelper.R;
import com.test.campushelper.event.RefreshEvent;
import com.test.campushelper.model.User;
import com.test.campushelper.model.UserData;
import com.test.campushelper.model.UserModel;
import com.test.campushelper.utils.Constant;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMUserInfo;
import cn.bmob.newim.core.ConnectionStatus;
import cn.bmob.newim.listener.ConnectListener;
import cn.bmob.newim.listener.ConnectStatusChangeListener;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.LogInListener;

public class LoginActivity extends BaseActivity implements View.OnClickListener{

    private static final String TAG = "LoginActivity";
    private Button loginBtn,registerBtn;
    private EditText userNameET,passwordET;
    private String userName,pwd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentLayout(R.layout.activity_login);
        Bmob.initialize(this, Constant.BMOB_APPKEY);
        setTitle("登录");
        setBackArrow();
        init();
    }

    private void init() {
        loginBtn = findViewById(R.id.login);
        userNameET = findViewById(R.id.et_username);
        passwordET = findViewById(R.id.et_password);
        registerBtn = findViewById(R.id.btn_register);

        loginBtn.setOnClickListener(this);
        registerBtn.setOnClickListener(this);
    }
    @Override
    public void onClick(final View v) {
        switch (v.getId()){
            case R.id.login:
                //登录验证
                userName = userNameET.getText().toString();
                pwd = passwordET.getText().toString();
                if(TextUtils.isEmpty(userName)){
                    userNameET.setError("用户名不能为空");
                    break;
                }
                if (TextUtils.isEmpty(pwd)){
                    passwordET.setError("密码不能为空");
                    break;
                }
                final ProgressDialog progressDialog = new ProgressDialog(this);
                progressDialog.setTitle("提示");
                progressDialog.setMessage("正在登录...");
                progressDialog.setCancelable(true);
                progressDialog.show();
                UserModel.getInstance().login(userName, pwd, new LogInListener() {
                    @Override
                    public void done(Object o, BmobException e) {
                        if (e == null){
                            BmobQuery<UserData> queryData = new BmobQuery<UserData>();
                            queryData.addWhereEqualTo("userName",userName);
                            queryData.findObjects(new FindListener<UserData>() {
                                @Override
                                public void done(List<UserData> list, BmobException e) {
                                    if (e == null){
                                        Constant.curUser = list.get(0);
                                    }
                                }
                            });
                            MainActivity.isLogin = true;
                            final User user = BmobUser.getCurrentUser(User.class);
                            //TODO 连接：登录成功、注册成功或处于登录状态重新打开应用后执行连接IM服务器的操作
                            //判断用户是否登录，并且连接状态不是已连接，则进行连接操作
                            if (!TextUtils.isEmpty(user.getObjectId()) &&
                                    BmobIM.getInstance().getCurrentStatus().getCode() != ConnectionStatus.CONNECTED.getCode()) {
                                BmobIM.connect(user.getObjectId(), new ConnectListener() {
                                    @Override
                                    public void done(String uid, BmobException e) {
                                        if (e == null) {
                                            //TODO 会话：更新用户资料，用于在会话页面、聊天页面以及个人信息页面显示
                                            Log.d(TAG, "连接服务器成功！");
                                            BmobIM.getInstance().updateUserInfo(new BmobIMUserInfo(user.getObjectId(),
                                                            user.getUsername(), ""));
                                            EventBus.getDefault().post(new RefreshEvent());
                                            finish();
                                        } else {
                                            Toast.makeText(getBaseContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                                        }

                                    }
                                });
                                //TODO 连接：监听连接状态，可通过BmobIM.getInstance().getCurrentStatus()来获取当前的长连接状态
                                BmobIM.getInstance().setOnConnectStatusChangeListener(new ConnectStatusChangeListener() {
                                    @Override
                                    public void onChange(ConnectionStatus status) {
                                        Toast.makeText(getBaseContext(),status.getMsg() ,Toast.LENGTH_SHORT).show();
                                        Log.d(TAG, "onChange: "+BmobIM.getInstance().getCurrentStatus().getMsg());
                                    }
                                });
                            }
                        }else{
                            if (e.getErrorCode() == 101){
                                passwordET.setText("");
                                toast("用户名或密码不正确~");
                                progressDialog.dismiss();
                            }
                        }
                    }
                });
                break;
            case R.id.btn_register:
                startActivity(new Intent(this,RegisterActivity.class));
                break;
        }
    }
}
