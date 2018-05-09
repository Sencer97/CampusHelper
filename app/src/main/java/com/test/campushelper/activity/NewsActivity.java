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
import com.test.campushelper.model.News;
import com.test.campushelper.utils.Constant;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class NewsActivity extends BaseActivity {

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView rv_news;
    private NewsAdapter adapter;
    private FloatingActionButton addNewsFab;
    private List<News> newsList = new ArrayList<>();
    private boolean isRefreshing = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentLayout(R.layout.activity_news);
        Bmob.initialize(this, Constant.BMOB_APPKEY);
        setTitle(getIntent().getStringExtra("title"));
        setBackArrow();
        init();

    }
    @Override
    public void onResume() {
        super.onResume();
        refreshData();
    }
    public void refreshData() {
        BmobQuery<News> query = new BmobQuery<>();
        if (!getIntent().getBooleanExtra("lookAll",true)){
            //如果是学院管理员  则增加只看当前的通知
            query.addWhereEqualTo("name",Constant.curUser.getUserName());
        }
        query.setLimit(50);       //默认返回10条
        query.findObjects(new FindListener<News>() {
            @Override
            public void done(List<News> list, BmobException e) {
                if (e==null){
                    if (list.size() != 0){
                        newsList.clear();
                        adapter.notifyDataSetChanged();
                        for (News n: list) {
                            adapter.addData(0,n);
                        }
                        adapter.notifyDataSetChanged();
                        Log.d("queryNewsItem", "找到"+list.size()+"条数据");
                    }
                }else {
                    Log.d("queryNewsItem", "error "+e.getMessage());
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
        adapter = new NewsAdapter(this,newsList);
        rv_news.setAdapter(adapter);
        rv_news.setItemAnimator(new DefaultItemAnimator());
        adapter.setOnItemClickListener(new NewsAdapter.OnItemClickListener(){
            @Override
            public void onItemClick(View view, final int position) {
                //直接进入建议详情
                Constant.curNews = newsList.get(position);
                startActivity(new Intent(getBaseContext(),NewsDetailActivity.class));
            }
        });

        //下拉刷新
        swipeRefreshLayout = findViewById(R.id.srl_news);
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

        addNewsFab = findViewById(R.id.fab_addNews);
        addNewsFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(NewsActivity.this,PublishNewsActivity.class));
            }
        });
        if (!getIntent().getBooleanExtra("showFab",true)){
            addNewsFab.setVisibility(View.GONE);
        }


    }
}
