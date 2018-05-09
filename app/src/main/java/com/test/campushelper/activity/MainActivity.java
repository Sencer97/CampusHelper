package com.test.campushelper.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.test.campushelper.R;
import com.test.campushelper.event.RefreshEvent;
import com.test.campushelper.fragment.ClassmatesFragment;
import com.test.campushelper.fragment.CollegeFragment;
import com.test.campushelper.fragment.MessageFragment;
import com.test.campushelper.fragment.SchoolfellowFragment;
import com.test.campushelper.fragment.TeachFragment;
import com.test.campushelper.utils.Constant;
import com.test.campushelper.view.BottomNavigationViewHelper;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.newim.BmobIM;
import cn.bmob.newim.event.MessageEvent;
import cn.bmob.newim.event.OfflineMessageEvent;
import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,View.OnClickListener{
    private static final String TAG = "MainActivity";
//    @BindView(R.id.toolbar)
    private Toolbar toolbar;
//    @BindView(R.id.drawer)
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    //碎片页面
    private List<Fragment> fragments;
    private TeachFragment teachFragment;
    private ClassmatesFragment classmatesFragment;
    private MessageFragment messageFragment;
    private CollegeFragment collegeFragment;
    private SchoolfellowFragment schoolFragment;

//    @BindView(R.id.view_pager_main)
    private ViewPager viewPager;
//    @BindView(R.id.bottom_navigation_bar)
    private BottomNavigationView bottomNavigationView;
    private MenuItem menuItem;

//    @BindView(R.id.nav_view)
    private NavigationView navigationView;

    private CircleImageView headIcon;
    private LinearLayout nav_header;
    private Button loginBtn;
    private TextView nickName;

    //是否登录
    public static boolean isLogin = false;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.bottom_navigation_teach:
                    changePage("师生帮",0);
                    return true;
                case R.id.bottom_navigation_classmates:
                    changePage("同学帮",1);
                    return true;
                case R.id.bottom_navigation_message:
                    changePage("消息",2);
                    return true;
                case R.id.bottom_navigation_college:
                    changePage("学院帮",3);
                    return true;
                case R.id.bottom_navigation_schoolfellow:
                    changePage("校友帮",4);
                    return true;
            }
            return false;
        }
    };

    /**
     * 修改标题 和 页面
     * @param title 标题
     * @param pos 页面索引
     */
    public void changePage(String title,int pos){
        toolbar.setTitle(title);
        viewPager.setCurrentItem(pos);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        ButterKnife.bind(this);
        initView();
//        IntentFilter filter = new IntentFilter(LoginActivity.action);
//        registerReceiver(broadcastReceiver, filter);
    }
    //用广播实现传递用户名
//    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            nickName.setText(intent.getExtras().getString("name"));
//        }
//    };
    public void initView() {
        toolbar = findViewById(R.id.toolbar);
        drawerLayout = findViewById(R.id.drawer);
        viewPager = findViewById(R.id.view_pager_main);
        bottomNavigationView = findViewById(R.id.bottom_navigation_bar);
        navigationView = findViewById(R.id.nav_view);


        //加载碎片页面
        teachFragment = new TeachFragment();
        classmatesFragment = new ClassmatesFragment();
        messageFragment = new MessageFragment();
        collegeFragment = new CollegeFragment();
        schoolFragment = new SchoolfellowFragment();
        fragments = new ArrayList<Fragment>();
        fragments.add(teachFragment);
        fragments.add(classmatesFragment);
        fragments.add(messageFragment);
        fragments.add(collegeFragment);
        fragments.add(schoolFragment);
        //添加适配器和监听器
        viewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return fragments.get(position);
            }
            @Override
            public int getCount() {
                return fragments.size();
            }
        });
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }
            @Override
            public void onPageSelected(int position) {
                if (menuItem != null) {
                    menuItem.setChecked(false);
                } else {
                    bottomNavigationView.getMenu().getItem(0).setChecked(false);
                }
                bottomNavigationView.getMenu().getItem(position).setChecked(true);
                menuItem = bottomNavigationView.getMenu().getItem(position);
                switch (position){
                    case 0:
                        toolbar.setTitle("师生帮");
                        break;
                    case 1:
                        toolbar.setTitle("同学帮");
                        break;
                    case 2:
                        toolbar.setTitle("消息");
                        break;
                    case 3:
                        toolbar.setTitle("学院帮");
                        break;
                    case 4:
                        toolbar.setTitle("校友帮");
                        break;
                }
            }
            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        //底部导航栏监听
        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        //toolbar配置
        toolbar.setTitle("校园帮");
        toolbar.setTitleTextColor(Color.parseColor("#ffffff"));
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        drawerToggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.app_name,R.string.app_name){
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);

            }
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };
        drawerToggle.syncState();
        drawerLayout.setDrawerListener(drawerToggle);

        //抽屉事件监听
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setItemIconTintList(null);

        View headerView = navigationView.getHeaderView(0);
        nav_header = headerView.findViewById(R.id.nav_head);
        nav_header.setOnClickListener(this);
        loginBtn = nav_header.findViewById(R.id.btn_login);
        nickName = nav_header.findViewById(R.id.tv_username);
        headIcon = nav_header.findViewById(R.id.iv_userhead);

    }

    @Override
    protected void onResume() {
        super.onResume();
         if(isLogin){
             nickName.setText(Constant.curUser.getUserName());
             nickName.setVisibility(View.VISIBLE);
             loginBtn.setVisibility(View.GONE);
             //使用Glide加载头像
             Glide.with(this)
                     .load(Constant.curUser.getHeadUrl())
                     .placeholder(R.drawable.ic_image_loading)
                     .error(R.drawable.ic_empty_picture)
                     .crossFade()
                     .into(headIcon);



        }else{
            nickName.setVisibility(View.GONE);
            loginBtn.setVisibility(View.VISIBLE);
            headIcon.setImageResource(R.drawable.head);
        }
    }
    /**
     * 注册消息接收事件
     *
     * @param event
     */
    //TODO 消息接收：通知有在线消息接收
    @Subscribe
    public void onEventMainThread(MessageEvent event) {

    }

    /**
     * 注册离线消息接收事件
     *
     * @param event
     */
    //TODO 消息接收：通知有离线消息接收
    @Subscribe
    public void onEventMainThread(OfflineMessageEvent event) {

    }

    /**
     * 注册自定义消息接收事件
     *
     * @param event
     */
    //TODO 消息接收：通知有自定义消息接收
    @Subscribe
    public void onEventMainThread(RefreshEvent event) {

    }
    @Override
    public void onStart() {
    super.onStart();
    EventBus.getDefault().register(this);
}

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
//        unregisterReceiver(broadcastReceiver);
        isLogin = false;
        BmobIM.getInstance().disConnect();
        BmobIM.getInstance().clear();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Intent intent = new Intent();
        switch (item.getItemId()){
            case R.id.nav_personal:      //个人中心
                if (isLogin){
                    Intent userIntent = new Intent(this,UserCenterActivity.class);
//                    userIntent.putExtra("nick",nickName.getText());
                    startActivity(userIntent);
                }else{
                    Toast.makeText(getBaseContext(),"请先登录~",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.nav_relation:     //与我有关
                if (isLogin){
                    intent.setClass(this,RelativeActivity.class);
                    startActivity(intent);
                }else{
                    Toast.makeText(getBaseContext(),"请先登录~",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.nav_friends:      //我的好友
                intent.setClass(this,MyfriendsActivity.class);
                startActivity(intent);
                if (isLogin){
                }else{
                  //Toast.makeText(getBaseContext(),"请先登录~",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.nav_about:
                intent.setClass(this,AboutActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_feedback:
                intent.setClass(this,FeedbackActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_exit:
                AlertDialog dialog = new AlertDialog.Builder(this).setTitle("提示")
                      .setMessage("确定要退出应用？")
                      .setPositiveButton("是的", new DialogInterface.OnClickListener() {
                          @Override
                          public void onClick(DialogInterface dialog, int which) {
                              finish();
                          }
                      })
                      .setNegativeButton("不了",null)
                        .show();
                break;
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.nav_head:
                if (isLogin){
                    Intent userIntent = new Intent(this,UserCenterActivity.class);
                    userIntent.putExtra("nick",nickName.getText());
                    startActivity(userIntent);
                }else{
                    startActivity(new Intent(this,LoginActivity.class));
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

}
