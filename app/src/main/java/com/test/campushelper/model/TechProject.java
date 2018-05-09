package com.test.campushelper.model;

import java.util.List;

import cn.bmob.v3.BmobObject;



public class TechProject extends BmobObject {
    private String id;              //用于更新的id
    private String name;            //昵称
    private String time;            //发布时间
    private String headUrl;         //头像链接
    private String content;         //内容
    private String tag;             //便于获取所有对象
    private List<CommentItem> comments; //评论列表
    private String depart;            //学院
    private String projName;          //项目名
    private String projNeed;          //项目需求
    private String projGoal;          //项目目标
    private String projPeriod;        //项目周期
    private String replacedCourse;     //可替代的课程
    private String replacedCredit;     //可替代学分数
    private String replaceReason;      //替代理由

    public TechProject(){};
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDepart() {
        return depart;
    }

    public void setDepart(String depart) {
        this.depart = depart;
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

    public String getHeadUrl() {
        return headUrl;
    }

    public void setHeadUrl(String headUrl) {
        this.headUrl = headUrl;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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

    public String getProjName() {
        return projName;
    }

    public void setProjName(String projName) {
        this.projName = projName;
    }

    public String getProjNeed() {
        return projNeed;
    }

    public void setProjNeed(String projNeed) {
        this.projNeed = projNeed;
    }

    public String getProjGoal() {
        return projGoal;
    }

    public void setProjGoal(String projGoal) {
        this.projGoal = projGoal;
    }

    public String getProjPeriod() {
        return projPeriod;
    }

    public void setProjPeriod(String projPeriod) {
        this.projPeriod = projPeriod;
    }

    public String getReplacedCourse() {
        return replacedCourse;
    }

    public void setReplacedCourse(String replacedCourse) {
        this.replacedCourse = replacedCourse;
    }

    public String getReplacedCredit() {
        return replacedCredit;
    }

    public void setReplacedCredit(String replacedCredit) {
        this.replacedCredit = replacedCredit;
    }

    public String getReplaceReason() {
        return replaceReason;
    }

    public void setReplaceReason(String replaceReason) {
        this.replaceReason = replaceReason;
    }
}
