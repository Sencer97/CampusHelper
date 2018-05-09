package com.test.campushelper.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.test.campushelper.R;
import com.test.campushelper.model.User;
import com.test.campushelper.model.UserData;
import com.test.campushelper.utils.Constant;

import java.util.List;

import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.bean.BmobIMUserInfo;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import de.hdodenhof.circleimageview.CircleImageView;

public class FriendInfoActivity extends BaseActivity implements View.OnClickListener{

    private static final String TAG = "FriendInfoActivity";
    private CircleImageView head;
    private TextView tv_name,tv_sex,tv_school,tv_depart,tv_major,tv_sign;
    private Button deleteBtn,chatBtn;
    private String name,sex,school,depart,major,sign;
    private boolean isFriend = true;
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
        head = findViewById(R.id.civ_friend_head);
        tv_name = findViewById(R.id.tv_friend_nickname);
        tv_sex = findViewById(R.id.tv_friend_sex);
        tv_school = findViewById(R.id.tv_friend_school);
        tv_depart = findViewById(R.id.tv_friend_depart);
        tv_major = findViewById(R.id.tv_friend_major);
        tv_sign = findViewById(R.id.tv_friend_sign);

        Intent intent = getIntent();
        name = intent.getStringExtra("nickname");
        isFriend = intent.getBooleanExtra("isFriend",true);
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
                    depart = list.get(0).getDepart();
                    tv_sex.setText(sex);
                    tv_school.setText(school);
                    tv_depart.setText(depart);
                    tv_major.setText(major);
                    tv_sign.setText(sign);

                    //使用Glide加载头像
                    Glide.with(getBaseContext())
                            .load(list.get(0).getHeadUrl())
                            .placeholder(R.drawable.ic_image_loading)
                            .error(R.drawable.ic_empty_picture)
                            .crossFade()
                            .into(head);
                }
            }
        });

        deleteBtn = findViewById(R.id.btn_delete);
        chatBtn = findViewById(R.id.btn_chat);

        deleteBtn.setOnClickListener(this);
        chatBtn.setOnClickListener(this);

        if (!isFriend){
            deleteBtn.setVisibility(View.GONE);
        }
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
