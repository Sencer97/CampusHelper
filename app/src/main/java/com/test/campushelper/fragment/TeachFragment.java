package com.test.campushelper.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.test.campushelper.R;
import com.test.campushelper.activity.PublishActivity;
import com.test.campushelper.activity.TechActivity;
import com.test.campushelper.activity.ViewNoticesActivity;
import com.test.campushelper.utils.Constant;

import cn.bmob.v3.Bmob;

public class TeachFragment extends Fragment implements View.OnClickListener{

    private ViewGroup group;
    private LinearLayout ll_publish_homework,ll_exam,ll_class_switch,ll_course,
            ll_publish_project,ll_my_notice,
            ll_view_homework,ll_ask_online,ll_my_questions,ll_view_proj;
    private String role = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        group = (ViewGroup) inflater.inflate(R.layout.fragment_teach,container,false);
        Bmob.initialize(getContext(), Constant.BMOB_APPKEY);
        initView();
        return group;
    }

    @Override
    public void onResume() {
        super.onResume();
        role = Constant.curUser.getRole();
    }
    private void initView() {

        ll_publish_homework = group.findViewById(R.id.ll_publish_homework);
        ll_exam = group.findViewById(R.id.ll_exam_notice);
        ll_class_switch = group.findViewById(R.id.ll_class_switch);
        ll_course = group.findViewById(R.id.ll_course_question);
        ll_publish_project = group.findViewById(R.id.ll_tech);
        ll_my_notice = group.findViewById(R.id.ll_my_notices);

        ll_view_homework = group.findViewById(R.id.ll_view_homework);
        ll_ask_online = group.findViewById(R.id.ll_ask_online);
        ll_my_questions = group.findViewById(R.id.ll_my_questions);
        ll_view_proj = group.findViewById(R.id.ll_view_proj);

        ll_publish_homework.setOnClickListener(this);
        ll_exam.setOnClickListener(this);
        ll_class_switch.setOnClickListener(this);
        ll_course.setOnClickListener(this);
        ll_publish_project.setOnClickListener(this);
        ll_my_notice.setOnClickListener(this);
        ll_view_homework.setOnClickListener(this);
        ll_ask_online.setOnClickListener(this);
        ll_my_questions.setOnClickListener(this);
        ll_view_proj.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if(role==null){
            Constant.showToast(getContext(),"请先登录~");
            return;
        }
        switch (v.getId()){
            case R.id.ll_publish_homework:
                if (!role.equals(Constant.roles[1])){
                    //非教师
                    Constant.showToast(getContext(),"该版块功能仅对教师开放！");
                    break;
                }
                Intent homeworkIntent = new Intent(getContext(), PublishActivity.class);
                homeworkIntent.putExtra("title","发布作业");
                homeworkIntent.putExtra("hint","请输入作业内容...");
                startActivity(homeworkIntent);
                break;
            case R.id.ll_exam_notice:
                if (!role.equals(Constant.roles[1])){
                    //非教师
                    Constant.showToast(getContext(),"该版块功能仅对教师开放！");
                    break;
                }
                Intent examIntent = new Intent(getContext(), PublishActivity.class);
                examIntent.putExtra("title","考试通知");
                examIntent.putExtra("hint","请输入考试通知内容...");
                startActivity(examIntent);
                break;
            case R.id.ll_class_switch:
                if (!role.equals(Constant.roles[1])){
                    //非教师
                    Constant.showToast(getContext(),"该版块功能仅对教师开放！");
                    break;
                }
                Intent switchClassIntent = new Intent(getContext(), PublishActivity.class);
                switchClassIntent.putExtra("title","调课安排");
                switchClassIntent.putExtra("hint","请输入调课安排内容...");
                startActivity(switchClassIntent);
                break;

            case R.id.ll_course_question:
                if (!role.equals(Constant.roles[1])){
                    //非教师
                    Constant.showToast(getContext(),"该版块功能仅对教师开放！");
                    break;
                }
                //查看学生的提问  true--看所有
                Intent viewQuestionIntent = new Intent(getContext(), ViewNoticesActivity.class);
                viewQuestionIntent.putExtra("title","课程答疑");
                viewQuestionIntent.putExtra("lookAll",true);
                viewQuestionIntent.putExtra("showFab",false);
                viewQuestionIntent.putExtra("tag","student_questions");
                startActivity(viewQuestionIntent);
                break;

            case R.id.ll_tech:
                if (!role.equals(Constant.roles[1])){
                    //非教师
                    Constant.showToast(getContext(),"该版块功能仅对教师开放！");
                    break;
                }
                //直接一个列表 右下角一个添加按钮发布项目
                Intent techIntent = new Intent(getContext(), TechActivity.class);
                techIntent.putExtra("title","科教协同");
                techIntent.putExtra("showFab",true);
                techIntent.putExtra("lookAll",false);
                startActivity(techIntent);
                break;

            case R.id.ll_my_notices:
                if (!role.equals(Constant.roles[1])){
                    //非教师
                    Constant.showToast(getContext(),"该版块功能仅对教师开放！");
                    break;
                }
                //查看发布的各种通知列表
                Intent noticesIntent = new Intent(getContext(), ViewNoticesActivity.class);
                noticesIntent.putExtra("title","我的通知");
                //只看自己的通知
                noticesIntent.putExtra("lookAll",false);
                noticesIntent.putExtra("tag","teacher_notices");
                startActivity(noticesIntent);
                break;
            case R.id.ll_view_homework:
                if (!role.equals(Constant.roles[0])){
                    //非学生
                    Constant.showToast(getContext(),"该版块功能仅对学生开放！");
                    break;
                }
                //同上
                Intent viewNoticesIntent = new Intent(getContext(), ViewNoticesActivity.class);
                viewNoticesIntent.putExtra("title","查看通知");
                //可查看所有
                viewNoticesIntent.putExtra("lookAll",true);
                viewNoticesIntent.putExtra("showFab",false);
                viewNoticesIntent.putExtra("tag","teacher_notices");
                startActivity(viewNoticesIntent);
                break;
            case R.id.ll_ask_online:
                if (!role.equals(Constant.roles[0])){
                    //非学生
                    Constant.showToast(getContext(),"该版块功能仅对学生开放！");
                    break;
                }
                Intent askIntent = new Intent(getContext(), ViewNoticesActivity.class);
                askIntent.putExtra("title","我的提问");
                askIntent.putExtra("showFab",true);
                askIntent.putExtra("lookAll",false);
                askIntent.putExtra("tag","student_questions");
                startActivity(askIntent);
                break;
            case R.id.ll_my_questions:
//                if (!role.equals(Constant.roles[0])){
//                    //非学生
//                    Constant.showToast(getContext(),"该版块功能仅对学生开放！");
//                    break;
//                }
//                //查看我的提问列表
//                Intent questionIntent = new Intent(getContext(), ViewNoticesActivity.class);
//                questionIntent.putExtra("title","我的提问");
//                //只查看自己的
//                questionIntent.putExtra("lookAll",false);
//                questionIntent.putExtra("tag","student_questions");
//                startActivity(questionIntent);
                break;
            case R.id.ll_view_proj:
                if (!role.equals(Constant.roles[0])){
                    //非学生
                    Constant.showToast(getContext(),"该版块功能仅对学生开放！");
                    break;
                }
                Intent viewProjIntent = new Intent(getContext(), TechActivity.class);
                viewProjIntent.putExtra("lookAll",true);
                viewProjIntent.putExtra("showFab",false);
                viewProjIntent.putExtra("title","项目学习");
                startActivity(viewProjIntent);
                break;

        }
    }
}
