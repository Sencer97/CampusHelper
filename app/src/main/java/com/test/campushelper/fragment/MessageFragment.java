package com.test.campushelper.fragment;


import android.content.DialogInterface;
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
import com.test.campushelper.adapter.MyRecyclerAdapter;
import com.test.campushelper.model.Message;
import com.test.campushelper.utils.Constant;
import com.test.campushelper.utils.WrapContentLinearLayoutManager;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

public class MessageFragment extends Fragment{
    private View group;
    private RecyclerView rv_chat;
    private MyRecyclerAdapter msgAdapter;
    private List<Message> msgList = new ArrayList<>();
    private SwipeRefreshLayout swipeRefreshLayout;
    private boolean isRefreshing = false;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        group =  inflater.inflate(R.layout.fragment_message,container,false);
        Bmob.initialize(getContext(), Constant.BMOB_APPKEY);
        refreshMsg();
        init();
        return group;
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshMsg();
    }

    //获取数据库中的消息通知
    private void refreshMsg() {
        if (BmobUser.getCurrentUser() == null){
            msgList.clear();
            msgAdapter.notifyDataSetChanged();
            Toast.makeText(getContext(),"请先登录",Toast.LENGTH_SHORT).show();
            return;
        }else{
            BmobQuery<Message> query = new BmobQuery<>();
            query.addWhereEqualTo("toName",Constant.curUser.getUserName());
            query.findObjects(new FindListener<Message>() {
                @Override
                public void done(List<Message> list, BmobException e) {
                    if(e==null){
                        if(list.size()>0){
                            msgList.clear();
                            for(Message message:list){
                                msgAdapter.addData(0,message);
                            }
                        }
                    }else{
                        Log.d("msgFragment", "error--消息通知"+e.getMessage());
                    }
                }
            });
        }
    }
    private void init() {

        rv_chat = group.findViewById(R.id.rv_message);
        rv_chat.setLayoutManager(new WrapContentLinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        msgAdapter = new MyRecyclerAdapter(getContext(),msgList);
        rv_chat.setAdapter(msgAdapter);
        rv_chat.setItemAnimator(new DefaultItemAnimator());
        msgAdapter.setOnItemClickListener(new MyRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, final int p) {
                final Message m = msgList.get(p);
                    //删除
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getContext());
                String[] strings = new String[]{"删除"};
                builder.setItems(strings, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case 0:
                                msgAdapter.deleteData(p);
                                m.delete(m.getId(), new UpdateListener() {
                                    @Override
                                    public void done(BmobException e) {
                                       if(e==null){
                                           Log.d("deleteMsg", "done: 删除成功");
                                       }else{
                                           Log.d("deleteMsg", "删除失败---"+e.getMessage());
                                       }
                                    }
                                });
                                break;
                        }
                    }
                });
                builder.show();

            }
        });

        //下滑刷新
        swipeRefreshLayout = group.findViewById(R.id.swl_msg);
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
                            //执行刷新
                            refreshMsg();
                        }
                    }, 2000);   //转圈圈2秒
                }
            }
        });
    }
}
