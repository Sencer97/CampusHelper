package com.test.campushelper.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.test.campushelper.R;
import com.test.campushelper.adapter.ListViewAdapter;
import com.test.campushelper.model.Friend;
import com.test.campushelper.model.UserData;
import com.test.campushelper.utils.Constant;
import com.test.campushelper.view.WordsNavigation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

public class MyfriendsActivity extends BaseActivity implements WordsNavigation.onWordsChangeListener {
    private static final String TAG = "MyFriendsActivity";
    private ListView listView;
    private ListViewAdapter adapter;
    private TextView tv_index;
    private WordsNavigation words;
    private List<Friend> list = new ArrayList<>();
    private FloatingActionButton addFriendFab;
    private int clickIndex = -1;
    public static boolean isDelete = false;
    public static boolean isAdd = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentLayout(R.layout.activity_myfriends);
        Bmob.initialize(this, Constant.BMOB_APPKEY);
        setTitle("好友列表");
        setBackArrow();
        initData();
        init();
    }

    private void initData() {
        List<Friend> tmp = Constant.curUser.getFriendList();
        Log.d(TAG, "initData: "+tmp);
        for (Friend f:tmp) {
            Log.d(TAG, "initData: "+f.getName());
            list.add(new Friend(f.getName(),f.getHeadIcon()));
        }
        //对集合排序
        if(list.size() > 0){
            Collections.sort(list);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        List<Friend> tmpList = Constant.curUser.getFriendList();
        if(isDelete){
            for(int i=0;i < tmpList.size();i++){
                if(tmpList.get(i).getName().equals(list.get(clickIndex).getName())){
                    tmpList.remove(i);
                }
            }
            Log.d(TAG, "删除后的好友列表: ");
            for (Friend f:Constant.curUser.getFriendList()) {
                Log.d(TAG, "好友: "+f.getName());
            }
            /**
             * 更新数据库中的好友列表
             */
            BmobQuery<UserData> queryUser = new BmobQuery<>();
            queryUser.addWhereEqualTo("userName",Constant.curUser.getUserName());
            queryUser.findObjects(new FindListener<UserData>() {
                @Override
                public void done(List<UserData> list, BmobException e) {
                    if (e == null){
                        UserData userData = list.get(0);
                        userData.setValue("friendList",Constant.curUser.getFriendList());
                        userData.update(new UpdateListener() {
                            @Override
                            public void done(BmobException e) {
                                if(e == null){
                                    Toast.makeText(getBaseContext(),"删除成功~",Toast.LENGTH_SHORT).show();
                                }else{
                                    Toast.makeText(getBaseContext(),"网络异常~",Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }
            });
            list.remove(clickIndex);
            adapter.notifyDataSetChanged();
            isDelete = false;
        }
        if (isAdd){
            Log.d(TAG, "添加后的好友列表: ");
            for (Friend f:Constant.curUser.getFriendList()) {
                Log.d(TAG, "好友: "+f.getName());
            }
            list.add(new Friend(tmpList.get(tmpList.size()-1).getName(),tmpList.get(tmpList.size()-1).getHeadIcon()));
            adapter.notifyDataSetChanged();
            isAdd = false;
        }
    }
    private void init() {
        listView = findViewById(R.id.list_friends);
        tv_index = findViewById(R.id.tv_index);
        words = findViewById(R.id.words_nav);
        addFriendFab = findViewById(R.id.fab_addFriend);
        addFriendFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getBaseContext(),AddFriendActivity.class));
                Snackbar.make(v,"添加好友",Snackbar.LENGTH_SHORT).show();
            }
        });

        words.setOnWordsChangeListener(this);
        adapter = new ListViewAdapter(getBaseContext(),list);
        listView.setAdapter(adapter);
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if(list.size() == 0){
                }else{
                    words.setTouchIndex(list.get(firstVisibleItem).getFirstLetter());
                }
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                clickIndex = position;
                Intent intent = new Intent(getBaseContext(),FriendInfoActivity.class);
                intent.putExtra("nickname",list.get(position).getName());
                startActivity(intent);
            }
        });
    }

    @Override
    public void wordsChange(String word) {
        updateWord(word);
        updateListView(word);
    }
    /**
     * 更新中央的字母提示
     * @param word 首字母
     */
    private void updateWord(String word) {
        Handler handler = new Handler();
        tv_index.setText(word);
        tv_index.setVisibility(View.VISIBLE);
        //清空之前的所有消息
        handler.removeCallbacksAndMessages(null);
        //500ms后让tv隐藏
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                tv_index.setVisibility(View.GONE);
            }
        }, 500);
    }
    /**
     * @param words 首字母
     */
    private void updateListView(String words) {
        for (int i = 0; i < list.size(); i++) {
            String headerWord = list.get(i).getFirstLetter();
            //将手指按下的字母与列表中相同字母开头的项找出来
            if (words.equals(headerWord)) {
                //将列表选中哪一个
                listView.setSelection(i);
                //找到开头的一个即可
                return;
            }
        }
    }
}
