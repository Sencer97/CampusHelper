package com.test.campushelper.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.test.campushelper.R;
import com.test.campushelper.adapter.AddressBookListAdapter;
import com.test.campushelper.model.Fellow;
import com.test.campushelper.utils.Constant;
import com.test.campushelper.view.WordsNavigation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

public class AddressBookActivity extends BaseActivity implements WordsNavigation.onWordsChangeListener {

    private ListView listView;
    private List<Fellow> fellowList = new ArrayList<>();
    private AddressBookListAdapter adapter;
    private EditText et_name;
    private EditText et_mobile;
    private FloatingActionButton addBtn;
    private LinearLayout ll_add;        //添加对话框布局
    private Fellow fellow;
    private TextView tv_word_index;
    private WordsNavigation wordsNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentLayout(R.layout.activity_address_book);
        Bmob.initialize(this, Constant.BMOB_APPKEY);
        setTitle("校友通讯录");
        setBackArrow();
        init();
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshData();
    }
    /**
     * 初始化通讯录
     */
    private void refreshData() {
        BmobQuery<Fellow> query = new BmobQuery<>();
//        query.addWhereEqualTo("tag","fellow");
        query.setLimit(50);       //默认返回10条
        query.findObjects(new FindListener<Fellow>() {
            @Override
            public void done(List<Fellow> list, BmobException e) {
                if(e==null){
                    if(list.size() > 0){
                        fellowList.clear();
                        for (Fellow f : list) {
                            fellowList.add(f);
                        }
                        //对集合排序
                        if(fellowList.size() > 0){
                            Collections.sort(fellowList);
                            adapter.notifyDataSetChanged();
                        }
                    }
                }else {
                    Log.d("initFellows", "error: "+e.getMessage());
                }
            }
        });
}

    private void init() {
        listView = findViewById(R.id.lv_abk_fellows);
        adapter = new AddressBookListAdapter(this,fellowList);
        listView.setAdapter(adapter);
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if(fellowList.size() == 0){
                }else{
                    wordsNavigation.setTouchIndex(fellowList.get(firstVisibleItem).getFirstLetter());
                }
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                final String name = fellowList.get(position).getName();
                final String phone = fellowList.get(position).getMobile();

                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(AddressBookActivity.this);
                builder.setTitle(name+":"+phone);
                builder.setNegativeButton("取消",null);
                String[] strings = new String[]{"添加到手机联系人","复制手机号"};
                builder.setItems(strings, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case 0:
                                //添加到手机联系人
                                addContact(name,phone);
                                break;
                            case 1:
                                //复制手机号
                                //获取剪贴板管理器
                                ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                                // 创建普通字符型ClipData
                                ClipData mClipData = ClipData.newPlainText("Label", phone);
                                // 将ClipData内容放到系统剪贴板里。
                                cm.setPrimaryClip(mClipData);
                                toast("复制成功");
                                break;
                        }
                    }
                });
                builder.show();
            }
        });

        addBtn = findViewById(R.id.fab_addFellow);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ll_add = (LinearLayout) getLayoutInflater().inflate(R.layout.dialog_add_fellow, null);
                et_name = ll_add.findViewById(R.id.et_fellow_name);
                et_mobile = ll_add.findViewById(R.id.et_fellow_mobile);
                //弹出添加对话框
                AlertDialog.Builder addDialog = new AlertDialog.Builder(AddressBookActivity.this).setTitle("添加校友")
                        .setView(ll_add)
                        .setPositiveButton("添加", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String fellowName = et_name.getText().toString();
                                String phoneNum = et_mobile.getText().toString();
                                if(TextUtils.isEmpty(fellowName)){
                                    et_name.setError("请输入名字");
                                    return;
                                }
                                if(TextUtils.isEmpty(phoneNum)){
                                    et_name.setError("请输入手机号");
                                    return;
                                }
                                fellow = new Fellow(fellowName,phoneNum);
                                fellow.setTag("fellow");
                                fellow.save(new SaveListener<String>() {
                                    @Override
                                    public void done(String s, BmobException e) {
                                        if(e==null){
                                            fellow.setId(fellow.getObjectId());
                                            fellow.update(fellow.getId(), new UpdateListener() {
                                                @Override
                                                public void done(BmobException e) {

                                                }
                                            });
                                            refreshData();
                                            toast("添加成功");
                                        }else{
                                            Log.d("addFellow", "error--添加失败！！！"+e.getMessage());
                                        }
                                    }
                                });
                            }
                        });
                addDialog.show();
            }
        });
        if (!getIntent().getBooleanExtra("showFab", true)) {
            addBtn.setVisibility(View.GONE);
        }

        wordsNavigation = findViewById(R.id.words_nav_abk);
        tv_word_index = findViewById(R.id.tv_word_index);
        wordsNavigation.setOnWordsChangeListener(this);
    }

    /**
     * 添加联系人
     * @param name
     * @param phone
     */
    private void addContact(String name, String phone) {
        Intent intent = new Intent(Intent.ACTION_INSERT);
        intent.setType("vnd.android.cursor.dir/person");
        intent.setType("vnd.android.cursor.dir/contact");
        intent.setType("vnd.android.cursor.dir/raw_contact");
        // 添加姓名
        intent.putExtra(ContactsContract.Intents.Insert.NAME, name);
        // 添加手机
        intent.putExtra(ContactsContract.Intents.Insert.PHONE_TYPE, Phone.TYPE_MOBILE);
        intent.putExtra(ContactsContract.Intents.Insert.PHONE, phone);
        startActivity(intent);
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
        tv_word_index.setText(word);
        tv_word_index.setVisibility(View.VISIBLE);
        //清空之前的所有消息
        handler.removeCallbacksAndMessages(null);
        //500ms后让tv隐藏
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                tv_word_index.setVisibility(View.GONE);
            }
        }, 500);
    }
    /**
     * @param words 首字母
     */
    private void updateListView(String words) {
        for (int i = 0; i < fellowList.size(); i++) {
            String headerWord = fellowList.get(i).getFirstLetter();
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
