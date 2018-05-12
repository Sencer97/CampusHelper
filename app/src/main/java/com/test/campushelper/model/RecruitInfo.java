package com.test.campushelper.model;


import java.util.List;

import cn.bmob.v3.BmobObject;

public class RecruitInfo extends BmobObject{
    private String id;              //用于更新的id
    private String name;            //昵称
    private String time;            //发布时间
    private String content;         //内容
    private String headUrl;         //头像链接
    private boolean hasPic;         //是否有图片
    private String tag;             //便于获取所有对象
    private List<String> picUrls;   //保存的云图片文件url
    private List<CommentItem> comments; //评论列表
    private String title;           //招聘标题

    public RecruitInfo(){}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getHeadUrl() {
        return headUrl;
    }

    public void setHeadUrl(String headUrl) {
        this.headUrl = headUrl;
    }

    public boolean isHasPic() {
        return hasPic;
    }

    public void setHasPic(boolean hasPic) {
        this.hasPic = hasPic;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public List<String> getPicUrls() {
        return picUrls;
    }

    public void setPicUrls(List<String> picUrls) {
        this.picUrls = picUrls;
    }

    public List<CommentItem> getComments() {
        return comments;
    }

    public void setComments(List<CommentItem> comments) {
        this.comments = comments;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
