package com.test.campushelper.fragment;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.test.campushelper.R;
import com.test.campushelper.activity.ClassHelpDetailActivity;
import com.test.campushelper.activity.ClassHelpPublishActivity;
import com.test.campushelper.activity.FriendInfoActivity;
import com.test.campushelper.adapter.ClassHelpAdapter;
import com.test.campushelper.adapter.GridViewAdapter;
import com.test.campushelper.model.ClassHelp;
import com.test.campushelper.utils.Constant;
import com.test.campushelper.view.ShowPicGridView;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;
import jahirfiquitiva.libs.fabsmenu.FABsMenu;
import jahirfiquitiva.libs.fabsmenu.TitleFAB;

public class ClassmatesFragment extends Fragment implements View.OnClickListener{
    private ViewGroup group;
    private RecyclerView rv_help;
    private List<ClassHelp> list_help = new ArrayList<>();
    private FABsMenu fabsMenu;
    private TitleFAB fabStudy, fabFond ,fabPy ,fabOther;
    private ClassHelpAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private boolean isRefreshing = false;
    private List<String> favorList;
    private ClassHelp classHelp;
    private ShowPicGridView gridView;
    private GridViewAdapter gridViewAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {
        group = (ViewGroup) inflater.inflate(R.layout.fragment_classmates,container,false);
        //用到Bmob时一定要记得先初始化
        Bmob.initialize(getContext(), Constant.BMOB_APPKEY);
        initView();
        refreshData();
        return group;
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshData();
    }

    private void initView() {
        rv_help = group.findViewById(R.id.rv_classhelp);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        rv_help.setLayoutManager(layoutManager);
        //测试item
        adapter = new ClassHelpAdapter(getContext(),list_help);
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

                        Intent intent = new Intent(getContext(),FriendInfoActivity.class);
                        intent.putExtra("nickname",list_help.get(position).getNickname());
                        intent.putExtra("isFriend",false);
                        Log.d("friendname", "onItemClick: "+list_help.get(position).getNickname());
                        startActivity(intent);

                        break;
                        //点击内容、时间、评论 -->  进入帮助详情
                        case R.id.tv_help_content:
                        case R.id.tv_help_time:
                        case R.id.ll_help_comment:
                            Intent detailIntent = new Intent(getContext(), ClassHelpDetailActivity.class);
//                            detailIntent.putExtra("id",list_help.get(position).getId());
                            //通过静态对象传值
                            detailIntent.putExtra("title","帮助详情");
                            detailIntent.putExtra("type","classhelp");
                            Constant.curHelp = list_help.get(position);
                            startActivity(detailIntent);
                            break;
                        case R.id.iv_ignore:
                            //删除
                            AlertDialog dialog = new AlertDialog.Builder(getContext()).setTitle("提示")
                                    .setMessage("确定删除该动态吗？")
                                    .setPositiveButton("是的", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            adapter.deleteData(position);
                                        }
                                    })
                                    .setNegativeButton("不",null)
                                    .show();
                            break;
                        case R.id.ll_help_favor:
//                            Toast.makeText(getContext(),"点赞",Toast.LENGTH_SHORT).show();
                            addFavor(position);
                            break;
                        case R.id.ll_help_share:
//                            Toast.makeText(getContext(),"转发一下",Toast.LENGTH_SHORT).show();
                            Constant.share(getContext(),"转发到","来自「校园帮」的分享："+list_help.get(position).getContent());
                            break;
//                    case R.id.gv_help_pic:
//                        classHelp = list_help.get(position);
//                        break;
                }
            }
        });

        fabsMenu = group.findViewById(R.id.fabs_menu);
        fabsMenu.attachToRecyclerView(rv_help);
        fabStudy = group.findViewById(R.id.fab_menu_study);
        fabFond = group.findViewById(R.id.fab_menu_fond);
        fabPy = group.findViewById(R.id.fab_menu_py);
        fabOther = group.findViewById(R.id.fab_menu_other);
        fabStudy.setOnClickListener(this);
        fabFond.setOnClickListener(this);
        fabPy.setOnClickListener(this);
        fabOther.setOnClickListener(this);

        //下滑刷新
        swipeRefreshLayout = group.findViewById(R.id.swl_classhelp);
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
                            Toast.makeText(getContext(),"刷新成功~",Toast.LENGTH_SHORT).show();
                        }
                    }, 2000);   //转圈圈2秒
                }
            }
        });
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
        query.addWhereEqualTo("tag","help");
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
//        rv_help.scrollToPosition(0);
    }

    @Override
    public void onClick(View v) {
        Intent publicIntent = new Intent(getContext(),ClassHelpPublishActivity.class);
        switch (v.getId()){
            case R.id.fab_menu_study:
                publicIntent.putExtra("hint","随手说出你的学习困惑，或者分享你的学习经验...");
                startActivity(publicIntent);
                break;
            case R.id.fab_menu_fond:
                publicIntent.putExtra("hint","拾金不昧是优良的传统美德...");
                startActivity(publicIntent);
                break;
            case R.id.fab_menu_py:
                publicIntent.putExtra("hint","闲置的物品快亮出来交易吧...");
                startActivity(publicIntent);
                break;
            case R.id.fab_menu_other:
                publicIntent.putExtra("hint","我有酒你有故事吗...");
                startActivity(publicIntent);
                break;
        }
    }
}
