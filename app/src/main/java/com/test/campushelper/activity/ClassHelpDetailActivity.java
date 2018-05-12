package com.test.campushelper.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.test.campushelper.R;
import com.test.campushelper.adapter.CommentRecyclerAdapter;
import com.test.campushelper.adapter.GridViewAdapter;
import com.test.campushelper.model.ClassHelp;
import com.test.campushelper.model.CommentItem;
import com.test.campushelper.model.Notice;
import com.test.campushelper.model.Reply;
import com.test.campushelper.utils.Constant;
import com.test.campushelper.view.ShowPicGridView;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import de.hdodenhof.circleimageview.CircleImageView;

public class ClassHelpDetailActivity extends BaseActivity implements View.OnClickListener{

    private ClassHelp classHelp;                        //当前发布的帮助
    private LinearLayout linearLayout,favorList;        //隐藏item中的赞、评论转发
    private ImageView iv_ignore;                        //隐藏删除图标
    private ShowPicGridView gridView;                   //图片列表和适配器
    private TextView tv_helper_name,tv_helper_time,tv_helper_depart,tv_helper_content;
    private CircleImageView civ_helper_head;              //评论者信息
    private RecyclerView rv_comments;
    private CommentRecyclerAdapter commentRecyclerAdapter;   //评论列表和适配器
    private List<CommentItem> commentItemList;
    private Button sendBtn;                             //发送评论
    private EditText inputET;                           //输入评论内容
    private String commentStr;
    private boolean isCommentCurrent = true;            //是否评论当前帮助 还是评论recyclerItem
    private List<Reply> replyList;
    private int replyIndex = 0;
    private String type;
    private Notice notice;     //通知
    private boolean isReplyToListItem = false;
    private String toName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentLayout(R.layout.activity_class_help_detail);
        Bmob.initialize(this, Constant.BMOB_APPKEY);
        setTitle(getIntent().getStringExtra("title"));
        setBackArrow();
        type = getIntent().getStringExtra("type");
        initComments();
        init();
    }

    /**
     * 初始化评论列表
     */
    private void initComments() {
        if (type.equals("classhelp")){
            //获得传来的ClassHelp对象
            classHelp = Constant.curHelp;
            if(classHelp.getComments() != null){
                commentItemList = classHelp.getComments();
            }
            else {
                commentItemList = new ArrayList<>();
            }
        }
        if (type.equals("notice")){
            notice = Constant.curNotice;
            if(notice.getComments() != null){
                commentItemList = notice.getComments();
            }
            else {
                commentItemList = new ArrayList<>();
            }
        }
    }

    /**
     * 更新评论列表，同时存入云数据库
     */
    private void refreshComments() {
        final CommentItem comment = new CommentItem();
        comment.setReplyTime(Constant.getCurTime());
        comment.setReplyContent(commentStr);
        comment.setReplyNick(Constant.curUser.getUserName());
        if (type.equals("classhelp")){
            comment.setCurNick(classHelp.getNickname());
            comment.setCurHead(Constant.curUser.getHeadUrl());
        }
        if (type.equals("notice")){
            comment.setCurNick(notice.getName());
            comment.setCurHead(Constant.curUser.getHeadUrl());
        }

        comment.setReplyList(new ArrayList<Reply>());
        commentItemList.add(comment);
        commentRecyclerAdapter.notifyDataSetChanged();
        //保存评论
        comment.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if(e==null){
                    String id = comment.getObjectId();
                    comment.setValue("id",id);
                    comment.update(id, new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                        }
                    });
                    Log.d("saveComment", "done: 保存评论成功！");
                }else{
                    Log.d("saveComment", "done: 保存评论失败！");
                }
            }
        });
        updateComments();
    }
    private void updateComments() {
        if (type.equals("classhelp")){
            BmobQuery<ClassHelp> query = new BmobQuery<>();
            query.addWhereEqualTo("id",classHelp.getId());
            query.findObjects(new FindListener<ClassHelp>() {
                @Override
                public void done(List<ClassHelp> list, BmobException e) {
                    if(e==null){
                        if (list.size() > 0){
                            ClassHelp help = list.get(0);
                            help.setValue("comments",commentItemList);
                            help.update(help.getId(), new UpdateListener() {
                                @Override
                                public void done(BmobException e) {
                                    if(e==null){
                                        Log.d("helpQuery", "done: 更新评论成功！");
                                    }else {
                                        Log.d("helpQuery", "error: 更新评论失败！");
                                    }
                                }
                            });
                        }
                    }else{
                        Log.d("helpQuery", "error: 找不到这条帮助。。。");
                    }
                }
            });
        }
        if (type.equals("notice")){
            BmobQuery<Notice> query = new BmobQuery<>();
            query.addWhereEqualTo("id",notice.getId());
            query.findObjects(new FindListener<Notice>() {
                @Override
                public void done(List<Notice> list, BmobException e) {
                    if(e==null){
                        if (list.size() > 0){
                            Notice notice = list.get(0);
                            notice.setValue("comments",commentItemList);
                            notice.update(notice.getId(), new UpdateListener() {
                                @Override
                                public void done(BmobException e) {
                                    if(e==null){
                                        Log.d("noticeQuery", "done: 更新评论成功！");
                                    }else {
                                        Log.d("noticeQuery", "error: 更新评论失败！");
                                    }
                                }
                            });
                        }
                    }else{
                        Log.d("noticeQuery", "error: 找不到这条帮助。。。");
                    }
                }
            });
        }
    }

    private void init() {

        linearLayout = findViewById(R.id.ll_util);
        favorList = findViewById(R.id.ll_favorlist);
        favorList.setVisibility(View.GONE);
        linearLayout.setVisibility(View.GONE);
        iv_ignore = findViewById(R.id.iv_ignore);
        iv_ignore.setVisibility(View.GONE);

        //显示帮助信息和图片
        civ_helper_head = findViewById(R.id.civ_help_head);
        tv_helper_name = findViewById(R.id.tv_help_nickname);
        tv_helper_time = findViewById(R.id.tv_help_time);
        tv_helper_depart = findViewById(R.id.tv_help_department);
        tv_helper_content = findViewById(R.id.tv_help_content);
        gridView = findViewById(R.id.gv_help_pic);

        //加载头像
        if (type.equals("classhelp")){
            Glide.with(this)
                    .load(classHelp.getHeadUrl())
                    .placeholder(R.drawable.ic_image_loading)
                    .error(R.drawable.ic_empty_picture)
                    .crossFade()
                    .into(civ_helper_head);
            tv_helper_name.setText(classHelp.getNickname());
            tv_helper_time.setText(classHelp.getTime());
            tv_helper_depart.setText(classHelp.getDepart());
            tv_helper_content.setText(classHelp.getContent());
            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    //TODO 查看大图
                    classHelp.getPicUrls();
                    Intent bigImageIntent = new Intent(ClassHelpDetailActivity.this, BigImagePagerActivity.class);
                    bigImageIntent.putStringArrayListExtra("picUrls",(ArrayList<String>) classHelp.getPicUrls());
                    bigImageIntent.putExtra("position",position);
                    startActivity(bigImageIntent);
                    overridePendingTransition(R.anim.fade_in,R.anim.fade_out);

                }
            });
            if (classHelp.isHasPic()){
                gridView.setVisibility(View.VISIBLE);
                gridView.setAdapter(new GridViewAdapter(this,classHelp.getPicUrls(),true));
            }else{
                gridView.setVisibility(View.GONE);
            }
        }
        if (type.equals("notice")){
            Glide.with(this)
                    .load(notice.getHeadUrl())
                    .placeholder(R.drawable.ic_image_loading)
                    .error(R.drawable.ic_empty_picture)
                    .crossFade()
                    .into(civ_helper_head);
            tv_helper_name.setText(notice.getName());
            tv_helper_time.setText(notice.getTime());
            tv_helper_depart.setText(notice.getDepart());
            tv_helper_content.setText(notice.getContent());

            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    //TODO 查看大图
                    Intent bigImageIntent = new Intent(ClassHelpDetailActivity.this, BigImagePagerActivity.class);
                    bigImageIntent.putStringArrayListExtra("picUrls",(ArrayList<String>) notice.getPicUrls());
                    bigImageIntent.putExtra("position",position);
                    startActivity(bigImageIntent);
                    overridePendingTransition(R.anim.fade_in,R.anim.fade_out);

                }
            });
            if (notice.isHasPic()){
                gridView.setVisibility(View.VISIBLE);
                gridView.setAdapter(new GridViewAdapter(this,notice.getPicUrls(),true));
            }else{
                gridView.setVisibility(View.GONE);
            }
        }

        //用户信息点击事件
        civ_helper_head.setOnClickListener(this);
        tv_helper_name.setOnClickListener(this);
        tv_helper_depart.setOnClickListener(this);
        tv_helper_content.setOnClickListener(this);

        //评论区
        inputET = findViewById(R.id.et_comment_input);
        sendBtn = findViewById(R.id.btn_comment_send);
        sendBtn.setOnClickListener(this);

        rv_comments = findViewById(R.id.rv_comments);
        //TODO 设置list点击事件
        AdapterView.OnItemClickListener listItemClickListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                isCommentCurrent = false;
                CommentItem comment = commentItemList.get(replyIndex);
                Reply reply = comment.getReplyList().get(position);
                toName = reply.getFrom();
                inputET.setHint("评论"+reply.getFrom()+":");
                //显示键盘
                Constant.showOrHideSoftKeyBoard(getBaseContext());
                isReplyToListItem = true;
            }
        };
        commentRecyclerAdapter = new CommentRecyclerAdapter(this,commentItemList,listItemClickListener);
        commentRecyclerAdapter.setOnItemClickListener(new CommentRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                isCommentCurrent = false;
                CommentItem comment = commentItemList.get(position);
                inputET.setHint("评论"+comment.getReplyNick()+":");
                //显示键盘
                Constant.showOrHideSoftKeyBoard(getBaseContext());
                replyIndex = position;
            }
        });
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rv_comments.setLayoutManager(layoutManager);
        rv_comments.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
        rv_comments.setAdapter(commentRecyclerAdapter);

    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            //点击头像、用户名  -->进入用户信息详情
            case R.id.civ_help_head:
            case R.id.tv_help_nickname:
            case R.id.tv_help_department:
                if(type.equals("classhelp")){
                    Intent intent = new Intent(this,FriendInfoActivity.class);
                    intent.putExtra("nickname",classHelp.getNickname());
                    intent.putExtra("isFriend",false);
                    startActivity(intent);
                }
                if (type.equals("notice")){
                    Intent intent = new Intent(this,FriendInfoActivity.class);
                    intent.putExtra("nickname",notice.getName());
                    intent.putExtra("isFriend",false);
                    startActivity(intent);
                }

                break;
            case R.id.tv_help_content:
                isCommentCurrent = true;
                if (type.equals("notice")){
                    inputET.setHint("评论"+notice.getName()+":");
                }
                if(type.equals("classhelp")){
                    inputET.setHint("评论"+classHelp.getNickname()+":");
                }
                break;
            case R.id.btn_comment_send:
                commentStr = inputET.getText().toString();
                if (commentStr.equals("")){
                    toast("评论不能为空哦~");
                    break;
                }
                //评论当前帮助
                if (isCommentCurrent){
                    refreshComments();
                    inputET.setText("");
                    toast("评论成功");
                    //隐藏键盘
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(v,InputMethodManager.SHOW_FORCED);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }else{
                    //评论item
//                    if(isReplyToListItem){
//                        break;
//                    }
                    CommentItem selComment = commentItemList.get(replyIndex);
                    if(isReplyToListItem){
                        Reply reply = new Reply(Constant.curUser.getUserName(),toName,commentStr);
                        commentRecyclerAdapter.addReply(replyIndex,reply);
                    }else{
                        Reply reply = new Reply(Constant.curUser.getUserName(),selComment.getReplyNick(),commentStr);
                        commentRecyclerAdapter.addReply(replyIndex,reply);
                    }
                    replyList = selComment.getReplyList();
                    selComment.setValue("replyList",replyList);
                    selComment.update(selComment.getObjectId(), new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if (e==null){
                                Log.d("replys", "done: 添加item回复成功~");
                            }else{
                                Log.d("replys", "error---->添加item的回复失败...."+e.getMessage());
                            }
                        }
                    });
                    commentItemList.set(replyIndex,selComment);
                    updateComments();
                    replyIndex = 0;
                    isCommentCurrent = true;
                    inputET.setText("");
                }
                break;

        }
    }
}
