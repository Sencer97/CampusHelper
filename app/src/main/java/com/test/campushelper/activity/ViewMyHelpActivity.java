package com.test.campushelper.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.test.campushelper.R;
import com.test.campushelper.adapter.ClassHelpAdapter;
import com.test.campushelper.model.ClassHelp;
import com.test.campushelper.utils.Constant;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

public class ViewMyHelpActivity extends BaseActivity {
    private RecyclerView rv_help;
    private List<ClassHelp> list_help = new ArrayList<>();
    private ClassHelpAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private boolean isRefreshing = false;
    private List<String> favorList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentLayout(R.layout.activity_view_my_help);
        Bmob.initialize(this, Constant.BMOB_APPKEY);
        setTitle("我的帮助");
        setBackArrow();
        init();

    }
    @Override
    public void onResume() {
        super.onResume();
        refreshData();
    }
    /**
     * 点赞
     */
    private void addFavor(int position) {
        ClassHelp help = list_help.get(position);
        if(help.getFavorList()!=null){
            favorList = help.getFavorList();
        }else{
            favorList = new ArrayList<>();
        }
        //已点赞则取消
        if (favorList.contains(Constant.curUser.getUserName())){
            favorList.remove(Constant.curUser.getUserName());
        }else{
            favorList.add(Constant.curUser.getUserName());
        }
        help.setValue("favorList",favorList);
        help.update(help.getId(), new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if(e==null){
                    Log.d("favor", "done: 点赞成功~");
                }else{
                    Log.d("favor", "error: 点赞失败~>>>>"+e.getMessage());
                }
            }
        });
        adapter.notifyDataSetChanged();
    }

    public void refreshData() {
        BmobQuery<ClassHelp> query = new BmobQuery<>();
        query.addWhereEqualTo("nickname",Constant.curUser.getUserName());
        query.setLimit(50);       //默认返回10条
        query.findObjects(new FindListener<ClassHelp>() {
            @Override
            public void done(List<ClassHelp> list, BmobException e) {
                if (e==null){
                    if (list.size() != 0){
                        list_help.clear();
                        adapter.notifyDataSetChanged();
                        for (ClassHelp help: list) {
                            adapter.addData(0,help);
                        }
                        adapter.notifyDataSetChanged();
                        Log.d("queryHelpItem", "找到"+list.size()+"条数据");
                    }
                }else {
                    Log.d("queryHelpItem", "error "+e.getMessage());
                }
            }
        });
    }
    private void init() {
        rv_help = findViewById(R.id.rv_classhelp);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rv_help.setLayoutManager(layoutManager);
        //测试item
        adapter = new ClassHelpAdapter(this,list_help);
        rv_help.setAdapter(adapter);
        rv_help.setItemAnimator(new DefaultItemAnimator());
        adapter.setOnItemClickListener(new ClassHelpAdapter.OnItemClickListener(){
            @Override
            public void onItemClick(View view, final int position) {
                switch (view.getId()){
                    //点击头像、用户名  -->进入用户信息详情
                    case R.id.civ_help_head:
                    case R.id.tv_help_nickname:
                    case R.id.tv_help_department:

                        Intent intent = new Intent(ViewMyHelpActivity.this,FriendInfoActivity.class);
                        intent.putExtra("nickname",list_help.get(position).getNickname());
                        intent.putExtra("isFriend",false);
                        Log.d("friendname", "onItemClick: "+list_help.get(position).getNickname());
                        startActivity(intent);

                        break;
                    //点击内容、时间、评论 -->  进入帮助详情
                    case R.id.tv_help_content:
                    case R.id.tv_help_time:
                    case R.id.ll_help_comment:
                        Intent detailIntent = new Intent(ViewMyHelpActivity.this, ClassHelpDetailActivity.class);
//                            detailIntent.putExtra("id",list_help.get(position).getId());
                        //通过静态对象传值
                        detailIntent.putExtra("title","帮助详情");
                        detailIntent.putExtra("type","classhelp");
                        Constant.curHelp = list_help.get(position);
                        startActivity(detailIntent);
                        break;
                    case R.id.iv_ignore:
                        //删除
                        AlertDialog dialog = new AlertDialog.Builder(ViewMyHelpActivity.this).setTitle("提示")
                                .setMessage("确定删除该帮助吗？")
                                .setPositiveButton("是的", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        ClassHelp help = new ClassHelp();
                                        help.setObjectId(list_help.get(position).getId());
                                        Log.d("deleteHelp", "点击的id----》 "+list_help.get(position).getId());
                                        help.delete(new UpdateListener() {
                                            @Override
                                            public void done(BmobException e) {
                                                if(e == null){
                                                    list_help.remove(position);
                                                    adapter.notifyItemRemoved(position);
                                                    adapter.notifyItemRangeChanged(position,list_help.size());
                                                    toast("删除成功！");
                                                }else{
                                                    Log.d("deleteHelp", "delete-error---> "+e.getMessage());
                                                }
                                            }
                                        });


                                        //adapter.deleteData(position);
                                    }
                                })
                                .setNegativeButton("不",null)
                                .show();
                        break;
                    case R.id.ll_help_favor:
                        addFavor(position);
                        break;
                    case R.id.ll_help_share:
                        Constant.share(ViewMyHelpActivity.this,"转发到","来自「校园帮」的分享："+list_help.get(position).getContent());
                        break;
                }
            }
        });

        //下滑刷新
        swipeRefreshLayout = findViewById(R.id.swl_classhelp);
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

    }
}
