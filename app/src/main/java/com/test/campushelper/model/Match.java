package com.test.campushelper.model;


import java.util.List;

import cn.bmob.v3.BmobObject;

public class Match extends BmobObject{
    private String id;
    private String title;
    private String content;
    private String time;
    private String tagName;
    private String tag;             //便于获取所有对象
    private String headUrl;
    private List<Attender> atteners; //报名列表

    public Match(){}


    public String getHeadUrl() {
        return headUrl;
    }

    public void setHeadUrl(String headUrl) {
        this.headUrl = headUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public List<Attender> getAtteners() {
        return atteners;
    }

    public void setAtteners(List<Attender> atteners) {
        this.atteners = atteners;
    }
}
