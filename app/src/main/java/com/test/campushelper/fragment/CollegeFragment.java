package com.test.campushelper.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.test.campushelper.R;
import com.test.campushelper.activity.AdviceActivity;
import com.test.campushelper.activity.JoinActivity;
import com.test.campushelper.activity.NewsActivity;
import com.test.campushelper.utils.Constant;

import cn.bmob.v3.Bmob;

public class CollegeFragment extends Fragment implements View.OnClickListener{
    private LinearLayout ll_notice,ll_match,ll_box;
    private LinearLayout ll_view_news,ll_join,ll_advise;
    private ViewGroup group;
    private String role = "";
    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {
        group = (ViewGroup) inflater.inflate(R.layout.fragment_college,container,false);
        Bmob.initialize(getContext(), Constant.BMOB_APPKEY);
        init();
        return group;
    }

    @Override
    public void onResume() {
        super.onResume();
        role = Constant.curUser.getRole();
    }

    private void init() {
        ll_notice = group.findViewById(R.id.ll_college_notices);
        ll_match = group.findViewById(R.id.ll_college_match);
        ll_box = group.findViewById(R.id.ll_college_advise_box);
        ll_view_news = group.findViewById(R.id.ll_college_view_notices);
        ll_join = group.findViewById(R.id.ll_college_join);
        ll_advise = group.findViewById(R.id.ll_college_advise);

        ll_notice.setOnClickListener(this);
        ll_match.setOnClickListener(this);
        ll_box.setOnClickListener(this);
        ll_view_news.setOnClickListener(this);
        ll_join.setOnClickListener(this);
        ll_advise.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
//        if(role==null){
//            Constant.showToast(getContext(),"请先登录~");
//            return;
//        }
        switch (v.getId()){
            case R.id.ll_college_notices:
//             if (!role.equals(Constant.roles[2])){
//                    //非学院管理员
//                    Constant.showToast(getContext(),"该版块功能仅对学院管理员开放！");
//                    break;
//                }
                Intent newsIntent = new Intent(getContext(), NewsActivity.class);
                newsIntent.putExtra("title","新闻公告");
                newsIntent.putExtra("showFab",true);
                newsIntent.putExtra("lookAll",false);
                startActivity(newsIntent);
                break;
            case R.id.ll_college_match:
//                if (!role.equals(Constant.roles[2])){
//                    //非学院管理员
//                    Constant.showToast(getContext(),"该版块功能仅对学院管理员开放！");
//                    break;
//                }
                Intent matchIntent = new Intent(getContext(), JoinActivity.class);
                matchIntent.putExtra("title","竞赛活动");
                matchIntent.putExtra("showFab",true);
                matchIntent.putExtra("lookAll",false);
                startActivity(matchIntent);
                break;
            case R.id.ll_college_advise_box:
//                if (!role.equals(Constant.roles[2])){
//                    //非学院管理员
//                    Constant.showToast(getContext(),"该版块功能仅对学院管理员开放！");
//                    break;
//                }
                Intent boxIntent = new Intent(getContext(),AdviceActivity.class);
                boxIntent.putExtra("title","意见箱");
                boxIntent.putExtra("showFab",false);
                boxIntent.putExtra("lookAll",true);
                startActivity(boxIntent);
                break;
            case R.id.ll_college_view_notices:
//                if (!role.equals(Constant.roles[0])){
//                    //非学生
//                    Constant.showToast(getContext(),"该版块功能仅对学生开放！");
//                    break;
//                }
                Intent viewNewsIntent = new Intent(getContext(), NewsActivity.class);
                viewNewsIntent.putExtra("title","新闻公告");
                viewNewsIntent.putExtra("showFab",false);
                viewNewsIntent.putExtra("lookAll",true);
                startActivity(viewNewsIntent);

                break;
            case R.id.ll_college_join:
//                if (!role.equals(Constant.roles[0])){
//                    //非学生
//                    Constant.showToast(getContext(),"该版块功能仅对学生开放！");
//                    break;
//                }
                Intent joinIntent = new Intent(getContext(), JoinActivity.class);
                joinIntent.putExtra("title","我报名的活动");
                joinIntent.putExtra("showFab",false);
                joinIntent.putExtra("lookAll",true);
                startActivity(joinIntent);
                break;
            case R.id.ll_college_advise:
//                if (!role.equals(Constant.roles[0])){
//                    //非学生
//                    Constant.showToast(getContext(),"该版块功能仅对学生开放！");
//                    break;
//                }
                Intent pushAdviceIntent = new Intent(getContext(),AdviceActivity.class);
                pushAdviceIntent.putExtra("title","我的意见");
                pushAdviceIntent.putExtra("showFab",true);
                //只看自己的
                pushAdviceIntent.putExtra("lookAll",false);
                startActivity(pushAdviceIntent);
                break;
        }
    }

}
