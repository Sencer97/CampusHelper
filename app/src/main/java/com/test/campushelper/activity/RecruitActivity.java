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
import com.test.campushelper.adapter.NewsAdapter;
import com.test.campushelper.adapter.RecruitAdapter;
import com.test.campushelper.model.RecruitInfo;
import com.test.campushelper.utils.Constant;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * 招聘内推
 */
public class RecruitActivity extends BaseActivity {

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView rv_news;
    private RecruitAdapter adapter;
    private FloatingActionButton addNewsFab;
    private List<RecruitInfo> recruitInfoListList = new ArrayList<>();
    private boolean isRefreshing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentLayout(R.layout.activity_news);
        Bmob.initialize(this, Constant.BMOB_APPKEY);
        setTitle("招聘内推");
        setBackArrow();
        init();

    }
    @Override
    public void onResume() {
        super.onResume();
        refreshData();
    }
    public void refreshData() {
        BmobQuery<RecruitInfo> query = new BmobQuery<>();
        //显示所有招聘信息
        query.setLimit(50);       //默认返回10条
        query.findObjects(new FindListener<RecruitInfo>() {
            @Override
            public void done(List<RecruitInfo> list, BmobException e) {
                if (e==null){
                    if (list.size() != 0){
                        recruitInfoListList.clear();
                        adapter.notifyDataSetChanged();
                        for (RecruitInfo r: list) {
                            adapter.addData(0,r);
                        }
                        adapter.notifyDataSetChanged();
                        Log.d("queryRecruitItem", "找到"+list.size()+"条数据");
                    }
                }else {
                    Log.d("queryRecruitItem", "error "+e.getMessage());
                }
            }
        });
        rv_news.scrollToPosition(0);
    }
    private void init() {
        rv_news = findViewById(R.id.rv_news);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rv_news.setLayoutManager(layoutManager);
        //测试item
        adapter = new RecruitAdapter(this, recruitInfoListList);
        rv_news.setAdapter(adapter);
        rv_news.setItemAnimator(new DefaultItemAnimator());
        adapter.setOnItemClickListener(new NewsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, final int position) {
                //直接进入招聘详情详情
                Constant.curRecruit = recruitInfoListList.get(position);
                startActivity(new Intent(getBaseContext(), RecruitDetailActivity.class));
            }
        });

        //下拉刷新
        swipeRefreshLayout = findViewById(R.id.srl_news);
        swipeRefreshLayout.setColorSchemeColors(Color.GREEN, Color.BLUE, Color.RED);
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

        addNewsFab = findViewById(R.id.fab_addNews);
        addNewsFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent publishRecruit = new Intent(RecruitActivity.this, PublishNewsActivity.class);
                publishRecruit.putExtra("title","发布招聘内推");
                startActivity(publishRecruit);
            }
        });
        if (!getIntent().getBooleanExtra("showFab", true)) {
            addNewsFab.setVisibility(View.GONE);
        }
    }
}
