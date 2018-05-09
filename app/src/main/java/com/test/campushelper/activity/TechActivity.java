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
import com.test.campushelper.adapter.ProjectsAdapter;
import com.test.campushelper.model.TechProject;
import com.test.campushelper.utils.Constant;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * 科教协同
 */
public class TechActivity extends BaseActivity {

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView rv_projects;
    private ProjectsAdapter adapter;
    private FloatingActionButton addProFab;
    private List<TechProject> projList = new ArrayList<>();
    private boolean isRefreshing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentLayout(R.layout.activity_tech);
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
        BmobQuery<TechProject> query = new BmobQuery<>();
        query.addWhereEqualTo("tag","tech");
        if(!getIntent().getBooleanExtra("lookAll",true)){
            query.addWhereEqualTo("name",Constant.curUser.getUserName());
        }
        query.setLimit(50);       //默认返回10条
        query.findObjects(new FindListener<TechProject>() {
            @Override
            public void done(List<TechProject> list, BmobException e) {
                if (e==null){
                    if (list.size() != 0){
                        projList.clear();
                        adapter.notifyDataSetChanged();
                        for (TechProject proj: list) {
                            adapter.addData(0,proj);
                        }
                        adapter.notifyDataSetChanged();
                        Log.d("queryProjectItem", "找到"+list.size()+"条数据");
                    }
                }else {
                    Log.d("queryProjectItem", "error "+e.getMessage());
                }
            }
        });
        rv_projects.scrollToPosition(0);
    }
    private void init() {
        rv_projects = findViewById(R.id.rv_projects);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rv_projects.setLayoutManager(layoutManager);
        //测试item
        adapter = new ProjectsAdapter(this,projList);
        rv_projects.setAdapter(adapter);
        rv_projects.setItemAnimator(new DefaultItemAnimator());
        adapter.setOnItemClickListener(new ProjectsAdapter.OnItemClickListener(){
            @Override
            public void onItemClick(View view, final int position) {
                switch (view.getId()){
                    //点击头像、用户名  -->进入用户信息详情
                    case R.id.civ_tech_head:
                    case R.id.tv_tech_name:
                    case R.id.tv_tech_department:
                        Intent intent = new Intent(getBaseContext(),FriendInfoActivity.class);
                        intent.putExtra("nickname",projList.get(position).getName());
                        intent.putExtra("isFriend",false);
                        startActivity(intent);
                        break;
                    //点击内容、时间、评论 -->  进入项目详情
                    case R.id.ll_project_body:
                        Intent detailIntent = new Intent(getBaseContext(),ProjectDetailActivity.class);
                        //通过静态对象传值
                        Constant.curProject = projList.get(position);
                        startActivity(detailIntent);
                        break;
                }
            }
        });

        //下拉刷新
        swipeRefreshLayout = findViewById(R.id.srl_view_projects);
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


        addProFab = findViewById(R.id.fab_addProject);
        addProFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(TechActivity.this,PublishProjectActivity.class));
            }
        });
        if (!getIntent().getBooleanExtra("showFab",true)){
            addProFab.setVisibility(View.GONE);
        }

    }
}
