package com.test.campushelper.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.test.campushelper.R;
import com.test.campushelper.event.FinishEvent;
import com.test.campushelper.model.UserModel;
import com.test.campushelper.utils.Constant;

import org.greenrobot.eventbus.EventBus;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.LogInListener;

public class RegisterActivity extends BaseActivity {
    private static final String TAG = "RegisterActivity";
    private Button registerBtn;
    private EditText et_user,et_password,et_confirm_pwd;
    private String userName,pwd,confirmPwd,role;
    private Spinner spinner;
    private ArrayAdapter<CharSequence> adapter;
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
        role = Constant.roles[0];
        registerBtn = findViewById(R.id.register);
        et_user = findViewById(R.id.et_username);
        et_password = findViewById(R.id.et_password);
        et_confirm_pwd = findViewById(R.id.et_password_confirm);
        spinner = findViewById(R.id.spinner);
        adapter = ArrayAdapter.createFromResource(this,R.array.roles,android.R.layout.simple_spinner_dropdown_item);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                role = Constant.roles[position];
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        spinner.setAdapter(adapter);
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userName = et_user.getText().toString();
                pwd = et_password.getText().toString();
                confirmPwd = et_confirm_pwd.getText().toString();
                if (TextUtils.isEmpty(userName)) {
                    et_user.setError("用户名不能为空");
                    return;
                }
                if (TextUtils.isEmpty(pwd)) {
                    et_password.setError("密码不能为空");
                    return;
                }
                if (!pwd.equals(confirmPwd)) {
                    et_confirm_pwd.setError("密码不一致！");
                    et_confirm_pwd.setText("");
                    return;
                }
                UserModel.getInstance().register(userName, pwd, confirmPwd,
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
                        },role);

            }
        });

    }


}
