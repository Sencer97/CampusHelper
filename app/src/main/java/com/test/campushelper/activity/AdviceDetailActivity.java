package com.test.campushelper.activity;

import android.content.Context;
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

import com.test.campushelper.R;
import com.test.campushelper.adapter.CommentRecyclerAdapter;
import com.test.campushelper.model.Advice;
import com.test.campushelper.model.CommentItem;
import com.test.campushelper.model.Reply;
import com.test.campushelper.utils.Constant;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

public class AdviceDetailActivity extends BaseActivity {

    private TextView tv_title,tv_content,tv_time;
    private RecyclerView rv_comments;
    private CommentRecyclerAdapter commentRecyclerAdapter;   //评论列表和适配器
    private List<CommentItem> commentItemList;
    private Button sendBtn;                             //发送评论
    private EditText inputET;                           //输入评论内容
    private String commentStr;
    private boolean isCommentCurrent = true;            //是否评论当前帮助 还是评论recyclerItem
    private List<Reply> replyList;
    private int replyIndex = 0;
    private Advice advice;                              //意见
    private boolean isReplyToListItem = false;
    private String toName;
    private LinearLayout ll_advice_body;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentLayout(R.layout.activity_advice_detail);
        Bmob.initialize(this, Constant.BMOB_APPKEY);
        setTitle("意见详情");
        setBackArrow();
        initComments();
        init();

    }

    private void init() {
        tv_title = findViewById(R.id.tv_advice_title);
        tv_time = findViewById(R.id.tv_advice_time);
        tv_content = findViewById(R.id.tv_advice_content);
        ll_advice_body = findViewById(R.id.ll_advice_body);

        tv_content.setText(advice.getContent());
        tv_title.setText(advice.getTitle());
        tv_time.setText(advice.getTime());

        //评论区
        inputET = findViewById(R.id.et_comment_input);
        sendBtn = findViewById(R.id.btn_comment_send);

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

        ll_advice_body.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isCommentCurrent = true;
                inputET.setHint("说点什么吧...");
            }
        });
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //发送评论
                commentStr = inputET.getText().toString();
                if (commentStr.equals("")){
                    toast("评论不能为空哦~");
                    return;
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
                            }else{
                            }
                        }
                    });
                    commentItemList.set(replyIndex,selComment);
                    updateComments();
                    replyIndex = 0;
                    isCommentCurrent = true;
                    inputET.setText("");
                }

            }
        });
    }

    /**
     * 初始化评论列表
     */
    private void initComments() {
        advice = Constant.curAdvice;
        if(advice.getComments() != null){
            commentItemList = advice.getComments();
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
        comment.setCurNick(advice.getTagName());
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
        BmobQuery<Advice> query = new BmobQuery<>();
        query.addWhereEqualTo("id",advice.getId());
        query.findObjects(new FindListener<Advice>() {
            @Override
            public void done(List<Advice> list, BmobException e) {
                if(e==null){
                    if (list.size() > 0){
                        Advice adv = list.get(0);
                        adv.setValue("comments",commentItemList);
                        adv.update(adv.getId(), new UpdateListener() {
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
}
