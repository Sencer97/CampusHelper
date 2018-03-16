package com.test.campushelper.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.test.campushelper.R;
import com.test.campushelper.model.User;
import com.test.campushelper.utils.Constant;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class LoginActivity extends BaseActivity implements View.OnClickListener{

    private static final String TAG = "LoginActivity";
    //    @BindView(R.id.iv_wechat_login)
    ImageView WeChatLogin;
//    @BindView(R.id.iv_qq_login)
    ImageView QQLogin;
    private Button loginBtn,forgetPwdBtn,registerBtn;
    private EditText userNameET,passwordET;
    private String userName,pwd;
    public static final String action = "jason.broadcast.action";

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
        WeChatLogin = findViewById(R.id.iv_wechat_login);
        QQLogin = findViewById(R.id.iv_qq_login);
        loginBtn = findViewById(R.id.login);
        userNameET = findViewById(R.id.et_username);
        passwordET = findViewById(R.id.et_password);
        forgetPwdBtn = findViewById(R.id.btn_forgot_password);
        registerBtn = findViewById(R.id.btn_register);

        WeChatLogin.setOnClickListener(this);
        QQLogin.setOnClickListener(this);
        loginBtn.setOnClickListener(this);
        forgetPwdBtn.setOnClickListener(this);
        registerBtn.setOnClickListener(this);
    }
    @Override
    public void onClick(final View v) {
        switch (v.getId()){
            case R.id.iv_wechat_login:
                Snackbar.make(v,"接入微信登录",Snackbar.LENGTH_SHORT).show();
                break;
            case R.id.iv_qq_login:
                Snackbar.make(v,"接入QQ登录",Snackbar.LENGTH_SHORT).show();
                break;
            case R.id.login:
                //登录验证
                userName = userNameET.getText().toString();
                pwd = passwordET.getText().toString();
                if(userName.equals("")){
                    userNameET.setError("用户名不能为空");
                }else if (pwd.equals("")){
                    passwordET.setError("密码不能为空");
                }else{
                    BmobQuery<User> queryUser = new BmobQuery<User>();
                    queryUser.addWhereEqualTo("userName",userName);
                    BmobQuery<User> queryPWd = new BmobQuery<User>();
                    queryPWd.addWhereEqualTo("password",pwd);
                    List<BmobQuery<User>> queries = new ArrayList<BmobQuery<User>>();
                    queries.add(queryUser);
                    queries.add(queryPWd);
                    BmobQuery<User> query = new BmobQuery<User>();
                    query.and(queries);
                    query.findObjects(new FindListener<User>() {
                        @Override
                        public void done(List<User> list, BmobException e) {
                            if(e == null){
                                if (list.size()>0){
                                    Snackbar.make(v,"登录成功！2s后返回主界面",Snackbar.LENGTH_SHORT).show();
//                                    LinearLayout head = (LinearLayout) getLayoutInflater().inflate(R.layout.drawer_nav_header,null);
//                                    Button btn = head.findViewById(R.id.btn_login);
//                                    TextView nickName = head.findViewById(R.id.tv_username);
//                                    btn.setVisibility(View.GONE);
//                                    nickName.setVisibility(View.VISIBLE);
//                                    nickName.setText(userName);
                                    Intent intent = new Intent(action);
                                    intent.putExtra("name",userName);
                                    sendBroadcast(intent);
                                    MainActivity.isLogin = true;
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            finish();
                                        }
                                    },2000);
                                }else{
                                    Snackbar.make(v,"用户名或密码错误！",Snackbar.LENGTH_SHORT).show();
                                    MainActivity.isLogin = false;
                                }
                            }else{
                                MainActivity.isLogin = false;
                                Snackbar.make(v,"登录失败！请查看网络连接~",Snackbar.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                break;
            case R.id.btn_forgot_password:
                Snackbar.make(v,"忘记密码~",Snackbar.LENGTH_SHORT).show();
                break;
            case R.id.btn_register:
                startActivity(new Intent(this,RegisterActivity.class));
                break;
        }
    }
}
