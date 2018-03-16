package com.test.campushelper.fragment;


import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.test.campushelper.R;

public class SchoolfellowFragment extends Fragment{
    private ViewGroup group;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        group = (ViewGroup) inflater.inflate(R.layout.fragment_schoolfellow,container,false);
        return group;
    }
}
