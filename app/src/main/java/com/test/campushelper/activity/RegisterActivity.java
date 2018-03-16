package com.test.campushelper.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.test.campushelper.R;
import com.test.campushelper.model.User;
import com.test.campushelper.model.UserData;
import com.test.campushelper.utils.Constant;

import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

public class RegisterActivity extends BaseActivity {

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
                if(userName.equals("")){
                    et_user.setError("用户名不能为空");
                    return;
                }else if(pwd.equals("")){
                    et_password.setError("密码不能为空");
                    return;
                }else if(!pwd.equals(confirmPwd)){
                    et_confirm_pwd.setError("密码不一致！");
                    et_confirm_pwd.setText("");
                    return;
                }else{
                    //先查询是否已注册
                    BmobQuery<User> query = new BmobQuery<User>();
                    query.addWhereEqualTo("userName",userName);
                    query.findObjects(new FindListener<User>() {
                        @Override
                        public void done(final List<User> list, BmobException e) {
                            if(e == null){
                                if(list.size()>0){
                                    Toast.makeText(getBaseContext(),"用户名已存在！请更换...",Toast.LENGTH_SHORT).show();
                                }else{
                                    final User user = new User("",userName,pwd);
                                    user.save(new SaveListener<String>() {
                                        @Override
                                        public void done(String s, BmobException e) {
                                            if(e == null){
                                                String userID = user.getObjectId();
                                                user.setValue("id",userID);
                                                user.update(userID, new UpdateListener() {
                                                    @Override
                                                    public void done(BmobException e) {
                                                    }
                                                });
                                                Toast.makeText(getBaseContext(),"注册成功！",Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                    //创建账户的同时 创建个人信息数据对象
                                    final UserData userData = new UserData("",userName,"","","","",""
                                    ,"","","");
                                    userData.save(new SaveListener<String>() {
                                        @Override
                                        public void done(String s, BmobException e) {
                                            if(e == null){
                                                String userDataID = userData.getObjectId();
                                                userData.setValue("id",userDataID);
                                                userData.update(userDataID, new UpdateListener() {
                                                    @Override
                                                    public void done(BmobException e) {
                                                    }
                                                });
                                            }
                                        }
                                    });
                                    finish();
                                }
                            }else{
                                Toast.makeText(getBaseContext(),"注册失败！请查看网络连接~",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }

            }
        });

    }


}
