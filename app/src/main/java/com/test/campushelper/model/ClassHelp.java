package com.test.campushelper.model;


import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;

public class ClassHelp extends BmobObject{
    private String nickname;        //昵称
    private String time;            //发布时间
    private String depart;          //学院
    private String content;         //内容
    private String headUrl;         //头像链接
    private boolean hasPic;         //是否有图片
    private String tag;             //便于获取所有帮助对象
    private List<String> picUrls;  //保存的云图片文件url
    private List<String> favorList;   //点赞列表
    private List<CommentItem> replys; //评论列表
    public ClassHelp(){

    }
    public ClassHelp(String tag){
        this.tag = tag;
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
    public boolean isHasPic() {
        return hasPic;
    }

    public void setHasPic(boolean hasPic) {
        this.hasPic = hasPic;
    }

    public List<String> getFavorList() {
        return favorList;
    }

    public void setFavorList(List<String> favorList) {
        this.favorList = favorList;
    }

    public List<CommentItem> getReplys() {
        return replys;
    }

    public void setReplys(List<CommentItem> replys) {
        this.replys = replys;
    }
    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDepart() {
        return depart;
    }

    public void setDepart(String depart) {
        this.depart = depart;
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
}
