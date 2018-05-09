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
import com.test.campushelper.adapter.AdviceAdapter;
import com.test.campushelper.model.Advice;
import com.test.campushelper.utils.Constant;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class AdviceActivity extends BaseActivity{

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView rv_advice;
    private AdviceAdapter adapter;
    private FloatingActionButton addAdviceFab;
    private List<Advice> adviceList = new ArrayList<>();
    private boolean isRefreshing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentLayout(R.layout.activity_advice);
        Bmob.initialize(this, Constant.BMOB_APPKEY);
        setTitle(getIntent().getStringExtra("title"));
        setBackArrow();
        init();

    }
    private void init() {
        rv_advice = findViewById(R.id.rv_my_advice);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rv_advice.setLayoutManager(layoutManager);
        //测试item
        adapter = new AdviceAdapter(this,adviceList);
        rv_advice.setAdapter(adapter);
        rv_advice.setItemAnimator(new DefaultItemAnimator());
        adapter.setOnItemClickListener(new AdviceAdapter.OnItemClickListener(){
            @Override
            public void onItemClick(View view, final int position) {
                //直接进入建议详情
                Constant.curAdvice = adviceList.get(position);
                startActivity(new Intent(getBaseContext(),AdviceDetailActivity.class));
            }
        });

        //下拉刷新
        swipeRefreshLayout = findViewById(R.id.srl_view_my_advice);
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

        addAdviceFab = findViewById(R.id.fab_addAdvice);
        addAdviceFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdviceActivity.this,PublishAdviceActivity.class);
                intent.putExtra("title","提交意见");
                startActivity(intent);
            }
        });
        if (!getIntent().getBooleanExtra("showFab",true)){
            addAdviceFab.setVisibility(View.GONE);
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        refreshData();
    }

    private void refreshData() {
        BmobQuery<Advice> query = new BmobQuery<>();
        if (!getIntent().getBooleanExtra("lookAll",true)){
            query.addWhereEqualTo("tagName",Constant.curUser.getUserName());
        }else{
            //学院可查看所有意见
            query.addWhereEqualTo("tag","tag");
        }
        query.setLimit(50);       //默认返回10条
        query.findObjects(new FindListener<Advice>() {
            @Override
            public void done(List<Advice> list, BmobException e) {
                if (e==null){
                    if (list.size() != 0){
                        adviceList.clear();
                        adapter.notifyDataSetChanged();
                        for (Advice a: list) {
                            adapter.addData(0,a);
                        }
                        adapter.notifyDataSetChanged();
                        Log.d("adviceQuery", "done: 找到"+list.size()+"条数据");
                    }
                }else {
                    Log.d("adviceQuery", "error: "+e.getMessage());
                }
            }
        });

    }



}
