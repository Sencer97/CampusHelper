package com.test.campushelper.model;



public class CommentItem {
    private String replyNick;   //回复者的昵称
    private String replyTime;   //回复时间
    private String replyHead;   //回复者的头像
    private String curNick;     //当前用户的昵称
    private String curHead;     //当前用户头像
    private String replyContent;    //评论内容

    public CommentItem(){

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
