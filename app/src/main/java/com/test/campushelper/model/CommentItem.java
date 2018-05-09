package com.test.campushelper.model;


import java.io.Serializable;
import java.util.List;

import cn.bmob.v3.BmobObject;

public class CommentItem extends BmobObject implements Serializable{
    private String id;
    private String replyNick;         //回复者的昵称
    private String replyTime;         //回复时间
    private String replyHead;         //回复者的头像
    private String curNick;           //当前用户的昵称
    private String curHead;           //当前用户头像
    private String replyContent;      //评论内容
    private List<Reply> replyList;   //该评论下的回复列表

    public CommentItem(){

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Reply> getReplyList() {
        return replyList;
    }
    public void setReplyList(List<Reply> replyList) {
        this.replyList = replyList;
    }

    public String getReplyNick() {
        return replyNick;
    }

    public void setReplyNick(String replyNick) {
        this.replyNick = replyNick;
    }

    public String getReplyTime() {
        return replyTime;
    }

    public void setReplyTime(String replyTime) {
        this.replyTime = replyTime;
    }

    public String getReplyHead() {
        return replyHead;
    }

    public void setReplyHead(String replyHead) {
        this.replyHead = replyHead;
    }

    public String getCurNick() {
        return curNick;
    }

    public void setCurNick(String curNick) {
        this.curNick = curNick;
    }

    public String getCurHead() {
        return curHead;
    }

    public void setCurHead(String curHead) {
        this.curHead = curHead;
    }

    public String getReplyContent() {
        return replyContent;
    }

    public void setReplyContent(String replyContent) {
        this.replyContent = replyContent;
    }
}
