package com.test.campushelper.fragment;


import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.test.campushelper.R;
import com.test.campushelper.utils.Constant;

import cn.bmob.v3.Bmob;

public class CollegeFragment extends Fragment{
    private ViewGroup group;
    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {
        group = (ViewGroup) inflater.inflate(R.layout.fragment_college,container,false);
        Bmob.initialize(getContext(), Constant.BMOB_APPKEY);
        return group;
    }
}
