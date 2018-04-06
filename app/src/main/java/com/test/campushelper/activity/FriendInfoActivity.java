package com.test.campushelper.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.test.campushelper.R;
import com.test.campushelper.model.User;
import com.test.campushelper.model.UserData;
import com.test.campushelper.utils.Constant;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.bean.BmobIMUserInfo;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class FriendInfoActivity extends BaseActivity implements View.OnClickListener{

    private static final String TAG = "FriendInfoActivity";
    private TextView tv_name,tv_sex,tv_school,tv_major,tv_sign;
    private Button deleteBtn,chatBtn;
    private String name,sex,school,major,sign;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentLayout(R.layout.activity_friend_info);
        Bmob.initialize(this, Constant.BMOB_APPKEY);
        setBackArrow();
        setTitle("好友资料");
        init();
    }

    private void init() {
        tv_name = findViewById(R.id.tv_friend_nickname);
        tv_sex = findViewById(R.id.tv_friend_sex);
        tv_school = findViewById(R.id.tv_friend_school);
        tv_major = findViewById(R.id.tv_friend_major);
        tv_sign = findViewById(R.id.tv_friend_sign);

        Intent intent = getIntent();
        name = intent.getStringExtra("nickname");
        tv_name.setText(name);
        // 查找数据库
        BmobQuery<UserData> query = new BmobQuery<>();
        query.addWhereEqualTo("userName",name);
        query.findObjects(new FindListener<UserData>() {
            @Override
            public void done(List<UserData> list, BmobException e) {
                if (e == null){
                    sex = list.get(0).getSex();
                    school = list.get(0).getSchool();
                    major = list.get(0).getMajor();
                    sign = list.get(0).getSignature();

                    tv_sex.setText(sex);
                    tv_school.setText(school);
                    tv_major.setText(major);
                    tv_sign.setText(sign);
                }
            }
        });

        deleteBtn = findViewById(R.id.btn_delete);
        chatBtn = findViewById(R.id.btn_chat);

        deleteBtn.setOnClickListener(this);
        chatBtn.setOnClickListener(this);
    }
    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_delete:
                AlertDialog dialog = new AlertDialog.Builder(this).setTitle("提醒")
                        .setMessage("确定要删除好友："+name)
                        .setPositiveButton("是的", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                MyfriendsActivity.isDelete = true;
                                finish();
                            }
                        })
                        .setNegativeButton("不了",null)
                        .show();
                break;
            case R.id.btn_chat:
                User user = BmobUser.getCurrentUser(User.class);
                BmobIMUserInfo info = new BmobIMUserInfo(user.getObjectId(), user.getUsername(), "");
                //TODO 会话： 创建一个常态会话入口，好友聊天
                BmobIMConversation conversationEntrance = BmobIM.getInstance().startPrivateConversation(info, null);
                Bundle bundle = new Bundle();
                bundle.putString("title",name);
                bundle.putSerializable("c", conversationEntrance);
                startActivity(ChatActivity.class,bundle);
                break;
        }
    }
}
