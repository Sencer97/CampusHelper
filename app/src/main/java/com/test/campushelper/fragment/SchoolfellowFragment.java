package com.test.campushelper.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.test.campushelper.R;
import com.test.campushelper.activity.AddressBookActivity;
import com.test.campushelper.activity.RecruitActivity;
import com.test.campushelper.utils.Constant;

import cn.bmob.v3.Bmob;

public class SchoolfellowFragment extends Fragment implements View.OnClickListener{

    private LinearLayout ll_recruit,ll_book;
    private String role = "";
    private ViewGroup group;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        group = (ViewGroup) inflater.inflate(R.layout.fragment_schoolfellow,container,false);
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
        role = Constant.curUser.getRole();
        ll_recruit = group.findViewById(R.id.ll_fellow_recruit);
        ll_book = group.findViewById(R.id.ll_fellow_book);

        ll_book.setOnClickListener(this);
        ll_recruit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(role == null ){
            Constant.showToast(getContext(),"请先登录~");
            return;
        }
        switch (v.getId()){
            case R.id.ll_fellow_recruit:

                Intent recruitIntent = new Intent(getContext(), RecruitActivity.class);
                if (!role.equals(Constant.roles[3])){
                    //非校友
                    recruitIntent.putExtra("showFab",false);
                }
                startActivity(recruitIntent);
                break;
            case R.id.ll_fellow_book:

                Intent fellowIntent = new Intent(getContext(), AddressBookActivity.class);
                if (!role.equals(Constant.roles[3])){
                    //非校友
                    fellowIntent.putExtra("showFab",false);
                }
                startActivity(fellowIntent);
                break;
        }
    }
}
