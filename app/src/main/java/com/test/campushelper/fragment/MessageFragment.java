package com.test.campushelper.fragment;


import android.graphics.Color;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.test.campushelper.R;
import com.test.campushelper.model.Message;
import com.test.campushelper.adapter.MyRecyclerAdapter;
import com.test.campushelper.utils.Constant;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import cn.bmob.v3.Bmob;

public class MessageFragment extends Fragment implements View.OnClickListener{
    private View group;
    private TextView tabChat,tabNotify;
    private FloatingActionButton fabAdd;
    private RecyclerView rv_chat,rv_notify;
    private MyRecyclerAdapter chatAdapter;
    private List<Message> data;
    private SwipeRefreshLayout swipeRefreshLayout;
    private boolean isRefreshing = false;
    private boolean isChatTab = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        group =  inflater.inflate(R.layout.fragment_message,container,false);
        Bmob.initialize(getContext(), Constant.BMOB_APPKEY);
        init();
        return group;
    }

    private void init() {
        data = new ArrayList<>();
        data.add(new Message("Sencer","hahahaha","12:12",R.drawable.head));
        data.add(new Message("aaa","hahahaha","12:12",R.drawable.head));
        data.add(new Message("bbb","hahahaha","12:12",R.drawable.head));
        data.add(new Message("ccc","hahahaha","12:12",R.drawable.head));

        tabChat = group.findViewById(R.id.tab_chat);
        tabNotify = group.findViewById(R.id.tab_notify);
        fabAdd = group.findViewById(R.id.fab_addFriend);
        rv_chat = group.findViewById(R.id.rv_chat);
        rv_notify = group.findViewById(R.id.rv_notify);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        rv_chat.setLayoutManager(layoutManager);
        chatAdapter = new MyRecyclerAdapter(getContext(),data);
        rv_chat.setAdapter(chatAdapter);
        rv_chat.setItemAnimator(new DefaultItemAnimator());
        chatAdapter.setOnItemClickListener(new MyRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int p) {
                    Message m = data.get(p);

                    Snackbar.make(view,"你点击了----"+m.getContent(),Snackbar.LENGTH_SHORT).show();
            }
        });
        chatAdapter.setOnItemLongClickListener(new MyRecyclerAdapter.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(View view, int position) {
                Message m = data.get(position);
                chatAdapter.deleteData(position);
                Snackbar.make(view,"你删除了----"+m.getNickName(),Snackbar.LENGTH_SHORT).show();
            }
        });

        //下滑刷新
        swipeRefreshLayout = group.findViewById(R.id.swl_chat);
        swipeRefreshLayout.setColorSchemeColors(Color.GREEN,Color.BLUE,Color.RED);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                if (!isRefreshing) {
                    isRefreshing = true;
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            swipeRefreshLayout.setRefreshing(false);
                            isRefreshing = false;
                            if(isChatTab){
                                //执行刷新
                                chatAdapter.addData(0,new Message("ccc","hahahaha","12:12",R.drawable.head));
                                Snackbar.make(getView(), "聊天栏刷新成功~", Snackbar.LENGTH_SHORT).show();
                            }else{
                                Snackbar.make(getView(), "通知栏刷新成功~", Snackbar.LENGTH_SHORT).show();
                            }
                        }
                    }, 2000);   //转圈圈2秒
                    //刷新完更新数据   在handler中完成
                }
            }
        });

        tabNotify.setOnClickListener(this);
        tabChat.setOnClickListener(this);
        fabAdd.setOnClickListener(this);

    }
    public void resetTextColor(){
        tabChat.setTextColor(getResources().getColor(R.color.text_normal));
        tabNotify.setTextColor(getResources().getColor(R.color.text_normal));
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tab_chat:
                isChatTab = true;
                resetTextColor();
                tabChat.setTextColor(getResources().getColor(R.color.colorPrimary));
                rv_chat.setVisibility(View.VISIBLE);
                rv_notify.setVisibility(View.GONE);
                Snackbar.make(fabAdd,"聊天",Snackbar.LENGTH_SHORT).show();
                break;
            case R.id.tab_notify:
                isChatTab = false;
                resetTextColor();
                tabNotify.setTextColor(getResources().getColor(R.color.colorPrimary));
                rv_chat.setVisibility(View.GONE);
                rv_notify.setVisibility(View.VISIBLE);
                Snackbar.make(fabAdd,"通知",Snackbar.LENGTH_SHORT).show();
                break;
            case R.id.fab_addFriend:
                chatAdapter.addData(0,new Message("ccc","hahahaha","12:12",R.drawable.head));
                Snackbar.make(fabAdd,"添加好友",Snackbar.LENGTH_SHORT).show();
                break;
        }
    }
}
