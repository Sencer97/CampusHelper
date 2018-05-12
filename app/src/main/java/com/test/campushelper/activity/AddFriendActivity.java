package com.test.campushelper.activity;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.test.campushelper.R;
import com.test.campushelper.model.Friend;
import com.test.campushelper.model.UserData;
import com.test.campushelper.utils.Constant;

import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;
import de.hdodenhof.circleimageview.CircleImageView;

public class AddFriendActivity extends BaseActivity implements OnClickListener{
    private static final String TAG = "AddFriendActivity";
    private EditText et_search;
    private Button searchBtn,addBtn;
    private CircleImageView friendHeader;
    private TextView tv_friendName;
    private RelativeLayout resLayout;
    private String input;
    private String friendHeadUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentLayout(R.layout.activity_add_friend);
        Bmob.initialize(this, Constant.BMOB_APPKEY);
        setTitle("添加好友");
        setBackArrow();
        init();
    }

    private void init() {
        et_search = findViewById(R.id.et_search);
        searchBtn = findViewById(R.id.btn_search);
        addBtn = findViewById(R.id.btn_add);
        resLayout = findViewById(R.id.rl_search_result);
        friendHeader = findViewById(R.id.civ_result_head);
        tv_friendName = findViewById(R.id.tv_result_name);
        searchBtn.setOnClickListener(this);
        addBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_search:
                //TODO 数据库查询用户
                input = et_search.getText().toString();
                BmobQuery<UserData> query = new BmobQuery<>();
                query.addWhereEqualTo("userName",input);
                query.findObjects(new FindListener<UserData>() {
                    @Override
                    public void done(List<UserData> list, BmobException e) {
                        if (e == null){
                            if (list.size() > 0){
                                UserData user = list.get(0);
                                tv_friendName.setText(user.getUserName());
                                friendHeadUrl = user.getHeadUrl();
                                Glide.with(getBaseContext())
                                        .load(friendHeadUrl)
                                        .placeholder(R.drawable.ic_image_loading)
                                        .error(R.drawable.ic_empty_picture)
                                        .crossFade()
                                        .into(friendHeader);
                                resLayout.setVisibility(View.VISIBLE);
                            }else {
                                Toast.makeText(getBaseContext(),"查无此人....",Toast.LENGTH_SHORT).show();
                            }
                        }else {
                            Toast.makeText(getBaseContext(),"error: 网络异常...",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                break;
            case R.id.btn_add:
                //TODO 发送加好友请求  待对方确认添加    添加完更新数据库中的的好友列表
                Friend friend = new Friend();
                String name = tv_friendName.getText().toString();
                friend.setName(name);
                friend.setHeadIcon(friendHeadUrl);
                Constant.curUser.getFriendList().add(friend);
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
                                    if (e == null){
                                        MyfriendsActivity.isAdd = true;
                                        Toast.makeText(getBaseContext(),"添加成功~",Toast.LENGTH_SHORT).show();
                                    }else {
                                        Toast.makeText(getBaseContext(),"系统错误~添加失败！",Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    }
                });


                break;
        }
    }
}
