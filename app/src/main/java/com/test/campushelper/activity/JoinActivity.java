package com.test.campushelper.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.test.campushelper.R;
import com.test.campushelper.adapter.AdviceAdapter;
import com.test.campushelper.model.Attender;
import com.test.campushelper.model.Match;
import com.test.campushelper.utils.Constant;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

public class JoinActivity extends BaseActivity {
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView rv_matches;
    private AdviceAdapter adapter;
    private FloatingActionButton addActiFab;
    private List<Match> matchList = new ArrayList<>();
    private boolean isRefreshing = false;
    private MenuItem menuItem;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentLayout(R.layout.activity_join);
        setTitle(getIntent().getStringExtra("title"));
        setBackArrow();
        setToolBarMenu(R.menu.advice_menu);
        Bmob.initialize(this, Constant.BMOB_APPKEY);
        init();

    }

    @Override
    public void onResume() {
        super.onResume();
        refreshData();
    }

    private void refreshData() {
        BmobQuery<Match> query = new BmobQuery<>();
        if (!getIntent().getBooleanExtra("lookAll",true)){
            query.addWhereEqualTo("tagName",Constant.curUser.getUserName());
        }else{
            //学生可查看所有竞赛活动
            query.addWhereEqualTo("tag","tag");
        }
        query.setLimit(50);       //默认返回10条
        query.findObjects(new FindListener<Match>() {
            @Override
            public void done(List<Match> list, BmobException e) {
                if (e==null){
                    if (list.size() != 0){
                        matchList.clear();
                        adapter.notifyDataSetChanged();
                        for (Match m: list) {
                            matchList.add(0,m);
                            adapter.notifyItemInserted(0);
                            adapter.notifyItemRangeChanged(0,matchList.size());
                        }
                        adapter.notifyDataSetChanged();
                        Log.d("matchQuery", "done: 找到"+list.size()+"条数据");

                        if(getIntent().getStringExtra("title").equals("我报名的活动")){
                            //学生筛选自己报名的活动---遍历每个活动看是否报名
                            matchList.clear();
                            for (Match match: list ) {
                                for(int i=0;i<match.getAtteners().size();i++){
                                    if(Constant.curUser.getUserName().equals(match.getAtteners().get(i).getName())){
                                        matchList.add(match);
                                        adapter.notifyDataSetChanged();
                                        Log.d("matchQuery", "报名了--"+match.getTitle());
                                    }
                                }
                            }
                            //设置长按取消报名
                            adapter.setOnItemLongClickListener(new AdviceAdapter.OnItemLongClickListener() {
                                @Override
                                public void onItemLongClick(View view, final int position) {
                                    //确认取消报名，同时更新数据库的报名列表
                                    AlertDialog dialog = new AlertDialog.Builder(JoinActivity.this).setTitle("提示")
                                            .setMessage("确定要取消报名吗？")
                                            .setPositiveButton("是的", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    Match match = matchList.get(position);
                                                    //遍历报名列表 删除我
                                                    List<Attender> attenders = match.getAtteners();
                                                    for(int i=0;i<match.getAtteners().size();i++){
                                                        if(Constant.curUser.getUserName().equals(match.getAtteners().get(i).getName())){
                                                            Log.d("matchQuery", "删除报名--- "+match.getAtteners().get(i).getName());
                                                            attenders.remove(i);
                                                            break;
                                                        }
                                                    }
                                                    match.setAtteners(attenders);
                                                    match.update(match.getId(), new UpdateListener() {
                                                        @Override
                                                        public void done(BmobException e) {
                                                            if(e==null){
                                                                Log.d("matchQuery", "done: 取消成功");
                                                            }else {
                                                                Log.d("matchQuery", "error--- "+e.getMessage());
                                                            }

                                                        }
                                                    });
                                                    matchList.remove(position);
                                                    adapter.notifyItemRemoved(position);
                                                    adapter.notifyItemRangeChanged(position,matchList.size());
                                                    toast("取消成功！");
                                                }
                                            })
                                            .setNegativeButton("不了",null)
                                            .show();


                                }
                            });

                        }
                    }
                }else {
                    Log.d("matchQuery", "error: "+e.getMessage());
                }
            }
        });

    }
    private void init() {
        rv_matches = findViewById(R.id.rv_joined_acti);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rv_matches.setLayoutManager(layoutManager);

        adapter = new AdviceAdapter(this,matchList,true);
        rv_matches.setAdapter(adapter);
        rv_matches.setItemAnimator(new DefaultItemAnimator());
        adapter.setOnItemClickListener(new AdviceAdapter.OnItemClickListener(){
            @Override
            public void onItemClick(View view, final int position) {
                //直接进入活动详情
                Constant.curMatch = matchList.get(position);
                Intent intent = new Intent(getBaseContext(),MatchDetailActivity.class);
                if(getIntent().getStringExtra("title").equals("竞赛活动")){
                    //是学院
                    intent.putExtra("showFab",false);
                }
                startActivity(intent);
            }
        });

        //下拉刷新
        swipeRefreshLayout = findViewById(R.id.srl_view_acti);
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

        addActiFab = findViewById(R.id.fab_addActi);
        addActiFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(JoinActivity.this,PublishAdviceActivity.class);
                intent.putExtra("title","发布竞赛活动");
                startActivity(intent);
            }
        });
        if (!getIntent().getBooleanExtra("showFab",true)){
            addActiFab.setVisibility(View.GONE);
        }


    }
    /** 创建菜单 */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.advice_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_more:
                //调到查看所有活动竞赛界面
                Intent joinIntent = new Intent(getBaseContext(), JoinActivity.class);
                joinIntent.putExtra("title","所有活动");
                joinIntent.putExtra("showFab",false);
                joinIntent.putExtra("lookAll",true);
                startActivity(joinIntent);
                break;


        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.menu_advice_ok).setVisible(false);
        if(getIntent().getStringExtra("title").equals("竞赛活动")){
            menu.findItem(R.id.menu_more).setVisible(false);
        }
        return super.onPrepareOptionsMenu(menu);
    }
}
