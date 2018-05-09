package com.test.campushelper.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.test.campushelper.R;
import com.test.campushelper.model.CommentItem;
import com.test.campushelper.model.TechProject;
import com.test.campushelper.utils.Constant;

import java.util.ArrayList;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

public class PublishProjectActivity extends BaseActivity {

    private EditText et_proj_name,et_proj_goal,et_proj_need,et_proj_period,et_proj_course,et_proj_credit,et_reason;
    private String name,goal,need,period,course,credit,reason;
    private Button publishBtn;
    private TechProject project = new TechProject();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentLayout(R.layout.activity_publish_project);
        Bmob.initialize(this, Constant.BMOB_APPKEY);
        setTitle("发布项目");
        setBackArrow();
        init();

    }
    private void init() {
        et_proj_name = findViewById(R.id.et_proj_name);
        et_proj_goal = findViewById(R.id.et_proj_goal);
        et_proj_need = findViewById(R.id.et_proj_need);
        et_proj_period = findViewById(R.id.et_proj_period);
        et_proj_course = findViewById(R.id.et_proj_rep_course);
        et_proj_credit = findViewById(R.id.et_proj_rep_credit);
        et_reason = findViewById(R.id.et_proj_rep_reason);
        publishBtn = findViewById(R.id.btn_proj_publish);
        publishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = et_proj_name.getText().toString();
                goal = et_proj_goal.getText().toString();
                need = et_proj_need.getText().toString();
                period = et_proj_period.getText().toString();
                course = et_proj_course.getText().toString();
                credit = et_proj_credit.getText().toString();
                reason = et_reason.getText().toString();
                if (TextUtils.isEmpty(name)){
                    et_proj_name.setError("请输入项目名");
                    return;
                }
                if (TextUtils.isEmpty(goal)){
                    et_proj_goal.setError("请输入项目目标");
                    return;
                }
                if (TextUtils.isEmpty(need)){
                    et_proj_need.setError("请输入项目需求");
                    return;
                }
                if (TextUtils.isEmpty(period)){
                    et_proj_period.setError("请输入项目周期");
                    return;
                }
                if (TextUtils.isEmpty(course)){
                    et_proj_course.setError("请输入可替换课程");
                    return;
                }
                if (TextUtils.isEmpty(credit)){
                    et_proj_credit.setError("请输入可替换学分");
                    return;
                }
                if (TextUtils.isEmpty(reason)){
                    et_reason.setError("请输入可替换理由");
                    return;
                }
                final ProgressDialog progressDialog = new ProgressDialog(PublishProjectActivity.this);
                progressDialog.setTitle("提示");
                progressDialog.setMessage("帮助发布中...");
                progressDialog.setCancelable(true);
                progressDialog.show();
                //发布帮助上传到云
                project.setName(Constant.curUser.getUserName());
                project.setDepart(Constant.curUser.getDepart());
                project.setTime(Constant.getCurTime());
                project.setTag("tech");
                project.setComments(new ArrayList<CommentItem>());
                project.setHeadUrl(Constant.curUser.getHeadUrl());
                project.setProjName(name);
                project.setProjGoal(goal);
                project.setProjNeed(need);
                project.setProjPeriod(period);
                project.setReplacedCourse(course);
                project.setReplacedCredit(credit);
                project.setReplaceReason(reason);
                project.save(new SaveListener<String>() {
                    @Override
                    public void done(String s, BmobException e) {
                        if (e==null){
                            String id = project.getObjectId();
                            project.setValue("id",id);
                            project.update(id, new UpdateListener() {
                                @Override
                                public void done(BmobException e) {
                                }
                            });
                            toast("发布成功！");
                            finish();
                        }else{
                            toast("发布失败！");
                        }
                        progressDialog.dismiss();
                    }
                });


            }
        });


    }
}
