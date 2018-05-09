package com.test.campushelper.model;

import java.util.List;

import cn.bmob.v3.BmobObject;

public class Advice extends BmobObject{
    private String id;
    private String title;
    private String content;
    private String time;
    private String tagName;
    private String tag;             //便于获取所有对象
    private List<CommentItem> comments; //评论列表

    public Advice(){}

    public Advice(String title, String content, String time, String tagName) {
        this.title = title;
        this.content = content;
        this.time = time;
        this.tagName = tagName;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public List<CommentItem> getComments() {
        return comments;
    }

    public void setComments(List<CommentItem> comments) {
        this.comments = comments;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }



}
