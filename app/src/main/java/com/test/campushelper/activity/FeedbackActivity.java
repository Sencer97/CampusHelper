package com.test.campushelper.activity;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.test.campushelper.R;
import com.test.campushelper.model.Feedback;
import com.test.campushelper.utils.Constant;

import java.util.regex.Pattern;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

public class FeedbackActivity extends BaseActivity implements View.OnClickListener{

    private EditText contentET,emailET;
    private Button submitBtn;
    private String text,email;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentLayout(R.layout.activity_feedback);
        Bmob.initialize(this, Constant.BMOB_APPKEY);
        setTitle("意见反馈");
        setBackArrow();
        initView();
    }

    private void initView() {
        contentET = findViewById(R.id.et_advice);
        emailET = findViewById(R.id.et_mail);
        submitBtn = findViewById(R.id.submit_btn);
        submitBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.submit_btn:
                //提交反馈
                text = contentET.getText().toString();
                email = emailET.getText().toString();
                Feedback feedbackObj = new Feedback(text,email);
                if(text.equals("")){
                    contentET.setError("亲~反馈不能为空哦！");
                }else if (!Pattern.matches("^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$",email)){
                    emailET.setError("请输入有效的邮箱~");

                }else{
                    feedbackObj.save(new SaveListener<String>() {
                        @Override
                        public void done(String s, BmobException e) {
                            if(e == null){
                                Toast.makeText(getBaseContext(),"反馈成功！",Toast.LENGTH_SHORT).show();
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        finish();
                                    }
                                },2000);
                            }else {
                                Toast.makeText(getBaseContext(),"反馈失败！请查看网络连接~",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                break;
        }
    }
}
