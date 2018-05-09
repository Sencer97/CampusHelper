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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.test.campushelper.R;
import com.test.campushelper.adapter.CommentRecyclerAdapter;
import com.test.campushelper.model.CommentItem;
import com.test.campushelper.model.Reply;
import com.test.campushelper.model.TechProject;
import com.test.campushelper.utils.Constant;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import de.hdodenhof.circleimageview.CircleImageView;

public class ProjectDetailActivity extends BaseActivity implements View.OnClickListener{
    private TechProject project;                        //当前发布的项目
    private TextView tv_tech_name,tv_tech_time,tv_tech_depart;
    private TextView tv_proj_name,tv_proj_goal,tv_proj_need,tv_proj_peroid,tv_proj_course,tv_proj_credit,tv_proj_reason;
    private CircleImageView civ_project_head;           //发布者头像
    private RecyclerView rv_comments;
    private CommentRecyclerAdapter commentRecyclerAdapter;   //评论列表和适配器
    private List<CommentItem> commentItemList;
    private Button sendBtn;                             //发送评论
    private EditText inputET;                           //输入评论内容
    private String commentStr;
    private boolean isCommentCurrent = true;            //是否评论当前帮助 还是评论recyclerItem
    private List<Reply> replyList;
    private int replyIndex = 0;
    private LinearLayout ll_proj_body;

    private boolean isReplyToListItem = false;
    private String toName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentLayout(R.layout.activity_project_detail);
        Bmob.initialize(this, Constant.BMOB_APPKEY);
        setTitle("项目详情");
        setBackArrow();
        initComments();
        init();
    }

    /**
     * 初始化评论列表
     */
    private void initComments() {
            project = Constant.curProject;
        if(project.getComments() != null){
            commentItemList = project.getComments();
        }
        else {
            commentItemList = new ArrayList<>();
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
        comment.setCurNick(project.getName());
        comment.setCurHead(Constant.curUser.getHeadUrl());
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
            BmobQuery<TechProject> query = new BmobQuery<>();
            query.addWhereEqualTo("id",project.getId());
            query.findObjects(new FindListener<TechProject>() {
                @Override
                public void done(List<TechProject> list, BmobException e) {
                    if(e==null){
                        if (list.size() > 0){
                            TechProject proj = list.get(0);
                            proj.setValue("comments",commentItemList);
                            proj.update(proj.getId(), new UpdateListener() {
                                @Override
                                public void done(BmobException e) {
                                    if(e==null){
                                        Log.d("projectQuery", "done: 更新评论成功！");
                                    }else {
                                        Log.d("projectQuery", "error: 更新评论失败！");
                                    }
                                }
                            });
                        }
                    }else{
                        Log.d("projectQuery", "error: 找不到这条项目。。。");
                    }
                }
            });
    }
    private void init() {
        civ_project_head = findViewById(R.id.civ_tech_head);
        tv_tech_name = findViewById(R.id.tv_tech_name);
        tv_tech_time = findViewById(R.id.tv_tech_time);
        tv_tech_depart = findViewById(R.id.tv_tech_department);
        tv_proj_name = findViewById(R.id.tv_tech_proj_name);
        tv_proj_goal = findViewById(R.id.tv_tech_proj_goal);
        tv_proj_need = findViewById(R.id.tv_tech_proj_need);
        tv_proj_peroid = findViewById(R.id.tv_tech_proj_period);
        tv_proj_course = findViewById(R.id.tv_tech_proj_replaced_course);
        tv_proj_credit = findViewById(R.id.tv_tech_proj_replaced_credit);
        tv_proj_reason = findViewById(R.id.tv_tech_proj_replaced_reason);
        ll_proj_body = findViewById(R.id.ll_project_body);

        //初始化项目信息
        Glide.with(this)
                .load(project.getHeadUrl())
                .placeholder(R.drawable.ic_image_loading)
                .error(R.drawable.ic_empty_picture)
                .crossFade()
                .into(civ_project_head);
        tv_tech_name.setText(project.getName());
        tv_tech_time.setText(project.getTime());
        tv_tech_depart.setText(project.getDepart());
        tv_proj_name.setText(project.getProjName());
        tv_proj_goal.setText(project.getProjGoal());
        tv_proj_need.setText(project.getProjNeed());
        tv_proj_peroid.setText(project.getProjPeriod());
        tv_proj_course.setText(project.getReplacedCourse());
        tv_proj_credit.setText(project.getReplacedCredit());
        tv_proj_reason.setText(project.getReplaceReason());

        //初始化评论区
        //用户信息点击事件
        civ_project_head.setOnClickListener(this);
        tv_tech_name.setOnClickListener(this);
        tv_tech_depart.setOnClickListener(this);
        ll_proj_body.setOnClickListener(this);

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
        switch (v.getId()) {
            //点击头像、用户名  -->进入用户信息详情
            case R.id.civ_help_head:
            case R.id.tv_help_nickname:
            case R.id.tv_help_department:

                Intent intent = new Intent(this,FriendInfoActivity.class);
                intent.putExtra("nickname",project.getName());
                intent.putExtra("isFriend",false);
                startActivity(intent);
                break;
            case R.id.ll_project_body:
                isCommentCurrent = true;
                inputET.setHint("评论"+project.getName()+":");
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

                    CommentItem selComment = commentItemList.get(replyIndex);
                    if(isReplyToListItem){
                        Reply reply = new Reply(Constant.curUser.getUserName(),toName,commentStr);
                        commentRecyclerAdapter.addReply(replyIndex,reply);
                    }else{
                        Reply reply = new Reply(Constant.curUser.getUserName(),selComment.getReplyNick(),commentStr);
                        commentRecyclerAdapter.addReply(replyIndex,reply);
                    }
                    replyList = selComment.getReplyList();
                    for (Reply r:replyList) {
                        Log.d("replys", "回复列表："+r.toString());
                    }
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
