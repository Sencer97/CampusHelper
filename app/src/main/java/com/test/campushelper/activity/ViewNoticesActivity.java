package com.test.campushelper.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.test.campushelper.R;
import com.test.campushelper.adapter.ClassHelpAdapter;
import com.test.campushelper.adapter.NoticesAdapter;
import com.test.campushelper.model.Notice;
import com.test.campushelper.utils.Constant;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class ViewNoticesActivity extends BaseActivity {

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView rv_notices;
    private NoticesAdapter adapter;
    private List<Notice> noticeList = new ArrayList<>();
    private boolean isRefreshing = false;
    private FloatingActionButton addFab;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentLayout(R.layout.activity_view_notices);
        Bmob.initialize(this, Constant.BMOB_APPKEY);
        setTitle(getIntent().getStringExtra("title"));
        setBackArrow();
        init();
        refreshData();
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshData();
    }
    public void refreshData() {
        BmobQuery<Notice> query = new BmobQuery<>();
        query.addWhereEqualTo("tag",getIntent().getStringExtra("tag"));
        if (!getIntent().getBooleanExtra("lookAll",true)){
            //如果是教师  则增加只看当前的通知
            query.addWhereEqualTo("name",Constant.curUser.getUserName());
        }
        query.setLimit(50);       //默认返回10条
        query.findObjects(new FindListener<Notice>() {
            @Override
            public void done(List<Notice> list, BmobException e) {
                if (e==null){
                    if (list.size() != 0){
                        noticeList.clear();
                        adapter.notifyDataSetChanged();
                        for (Notice notice: list) {
                            adapter.addData(0,notice);
                        }
                        adapter.notifyDataSetChanged();
                        Log.d("queryTeacherNoticeItem", "找到"+list.size()+"条数据");
                    }
                }else {
                    Log.d("queryTeacherNoticeItem", "error "+e.getMessage());
                }
            }
        });
        rv_notices.scrollToPosition(0);
    }
    private void init() {
        swipeRefreshLayout = findViewById(R.id.srl_view_notices);
        rv_notices = findViewById(R.id.rv_notices);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rv_notices.setLayoutManager(layoutManager);
        //测试item
        adapter = new NoticesAdapter(this,noticeList);
        rv_notices.setAdapter(adapter);
        rv_notices.setItemAnimator(new DefaultItemAnimator());
        adapter.setOnItemClickListener(new ClassHelpAdapter.OnItemClickListener(){
            @Override
            public void onItemClick(View view, final int position) {
                switch (view.getId()){
                    //点击头像、用户名  -->进入用户信息详情
                    case R.id.civ_notice_head:
                    case R.id.tv_notice_name:
                    case R.id.tv_notice_department:
                        Intent intent = new Intent(getBaseContext(),FriendInfoActivity.class);
                        intent.putExtra("nickname",noticeList.get(position).getName());
                        intent.putExtra("isFriend",false);
                        startActivity(intent);
                        break;
                    //点击内容、时间、评论 -->  进入通知详情
                    case R.id.tv_notice_content:
                    case R.id.tv_notice_time:
                        Intent detailIntent = new Intent(getBaseContext(),ClassHelpDetailActivity.class);
                        //通过静态对象传值
                        Constant.curNotice = noticeList.get(position);
                        detailIntent.putExtra("type","notice");
                        detailIntent.putExtra("title","详情");
                        startActivity(detailIntent);
                        break;
                }
            }
        });

        //下拉刷新
        swipeRefreshLayout.setColorSchemeColors(Color.GREEN,Color.BLUE,Color.RED);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (!isRefreshing) {
                    isRefreshing = true;
                    //刷新完更新数据   在handler中完成
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            swipeRefreshLayout.setRefreshing(false);
                            isRefreshing = false;
                            //执行刷新
                            refreshData();
                            toast("刷新成功~");
                        }
                    }, 2000);   //转圈圈2秒
                }
            }
        });

        addFab = findViewById(R.id.fab_addNotice);
        addFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent askIntent = new Intent(getBaseContext(), PublishActivity.class);
                askIntent.putExtra("title","在线提问");
                askIntent.putExtra("hint","随手说出你的学习困惑...");
                startActivity(askIntent);
            }
        });
        if (!getIntent().getBooleanExtra("showFab",true)){
            addFab.setVisibility(View.GONE);
        }

    }
}
