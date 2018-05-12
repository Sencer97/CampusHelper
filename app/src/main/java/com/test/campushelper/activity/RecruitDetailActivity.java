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
import com.test.campushelper.model.CommentItem;
import com.test.campushelper.model.RecruitInfo;
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

public class RecruitDetailActivity extends BaseActivity implements View.OnClickListener{

    private RecruitInfo recruitInfo;
    private TextView tv_recruit_title,tv_recruit_content,tv_recruit_time,tv_recruit_name;
    private RecyclerView rv_comments;
    private CommentRecyclerAdapter commentRecyclerAdapter;   //评论列表和适配器
    private List<CommentItem> commentItemList;
    private Button sendBtn;                             //发送评论
    private EditText inputET;                           //输入评论内容
    private String commentStr;
    private boolean isCommentCurrent = true;            //是否评论当前帮助 还是评论recyclerItem
    private List<Reply> replyList;
    private int replyIndex = 0;
    private LinearLayout ll_recruit_body;
    private ShowPicGridView gridView;                   //图片列表和适配器
    private boolean isReplyToListItem = false;
    private String toName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentLayout(R.layout.activity_recruit_detail);
        Bmob.initialize(this, Constant.BMOB_APPKEY);
        setTitle("招聘信息详情");
        setBackArrow();
        initData();
        init();

    }

    private void initData() {
        recruitInfo = Constant.curRecruit;
        if(recruitInfo.getComments() != null){
            commentItemList = recruitInfo.getComments();
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
        comment.setCurNick(recruitInfo.getName());
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
        BmobQuery<RecruitInfo> query = new BmobQuery<>();
        query.addWhereEqualTo("id",recruitInfo.getId());
        query.findObjects(new FindListener<RecruitInfo>() {
            @Override
            public void done(List<RecruitInfo> list, BmobException e) {
                if(e==null){
                    if (list.size() > 0){
                        RecruitInfo r = list.get(0);
                        r.setValue("comments",commentItemList);
                        r.update(r.getId(), new UpdateListener() {
                            @Override
                            public void done(BmobException e) {
                                if(e==null){
                                    Log.d("recruitQuery", "done: 更新评论成功！");
                                }else {
                                    Log.d("recruitQuery", "error: 更新评论失败！"+e.getMessage());
                                }
                            }
                        });
                    }
                }else{
                    Log.d("recruitQuery", "error: 找不到这条记录。。。");
                }
            }
        });
    }

    private void init() {
        tv_recruit_title = findViewById(R.id.tv_recruit_title);
        tv_recruit_content = findViewById(R.id.tc_recruit_content);
        tv_recruit_time = findViewById(R.id.tv_recruit_time);
        tv_recruit_name = findViewById(R.id.tv_recruit_name);

        tv_recruit_content.setText(recruitInfo.getContent());
        tv_recruit_title.setText(recruitInfo.getTitle());
        tv_recruit_time.setText(recruitInfo.getTime());
        tv_recruit_name.setText(recruitInfo.getName());

        ll_recruit_body = findViewById(R.id.ll_recruit_body);
        gridView = findViewById(R.id.gv_recruit_pic);

        rv_comments = findViewById(R.id.rv_comments);

        //评论区
        inputET = findViewById(R.id.et_comment_input);
        sendBtn = findViewById(R.id.btn_comment_send);
        ll_recruit_body.setOnClickListener(this);
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
            case R.id.ll_recruit_body:
                isCommentCurrent = true;
                inputET.setHint("评论"+recruitInfo.getName()+":");
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
