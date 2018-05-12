package com.test.campushelper.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.test.campushelper.R;
import com.test.campushelper.adapter.AttendAdapter;
import com.test.campushelper.model.Attender;
import com.test.campushelper.model.Match;
import com.test.campushelper.utils.Constant;

import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

public class MatchDetailActivity extends BaseActivity {
    private TextView tv_title,tv_content;
    private ListView listView;
    private List<Attender> attendList;
    private AttendAdapter attendAdapter;
    private FloatingActionButton joinFab;
    private Match match;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentLayout(R.layout.activity_match_detail);
        Bmob.initialize(this, Constant.BMOB_APPKEY);
        setTitle("竞赛活动详情");
        setBackArrow();
        initData();
        init();

    }
    /**
     * 初始化列表
     */
    private void initData() {
        match = Constant.curMatch;
        attendList = match.getAtteners();
    }

    private void init() {
        tv_title = findViewById(R.id.tv_match_detail_name);
        tv_content = findViewById(R.id.tv_match_detail_content);

        tv_content.setText(match.getContent());
        tv_title.setText(match.getTitle());

        listView = findViewById(R.id.lv_attenders);
        attendAdapter = new AttendAdapter(this,attendList);
        listView.setAdapter(attendAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //点击进入报名者信息详情
                Intent intent = new Intent(getBaseContext(),FriendInfoActivity.class);
                intent.putExtra("nickname",attendList.get(position).getName());
                intent.putExtra("isFriend",false);
                startActivity(intent);
            }
        });
        joinFab = findViewById(R.id.fab_attend);
        joinFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO 报名--- 判断是否报名了 是则提醒已经报名了。
                for(int i=0;i<match.getAtteners().size();i++){
                    if(Constant.curUser.getUserName().equals(match.getAtteners().get(i).getName())){
                        toast("您已经报名成功了！");
                        return;
                    }
                }
                final Attender attender = new Attender();
                attender.setName(Constant.curUser.getUserName());
                attender.setTime(Constant.getCurTime());
                attender.setHeadUrl(Constant.curUser.getHeadUrl());
                attendList.add(attender);
                attendAdapter.notifyDataSetChanged();
                match.setAtteners(attendList);
                match.update(match.getId(), new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        if(e==null){
                            toast("报名成功！");

                        }else {
                            toast("报名失败！"+e.getMessage());
                        }
                    }
                });

            }
        });
        if (!getIntent().getBooleanExtra("showFab",true)){
            joinFab.setVisibility(View.GONE);
        }


    }
}
